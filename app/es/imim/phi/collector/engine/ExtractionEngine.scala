/*   
     Collector is a tool for obtaining bioactivity data from the Open PHACTS platform.
     Copyright (C) 2013 UPF
     Contributed by Manuel Pastor(manuel.pastor@upf.edu) and Oriol López-Massaguer(oriol.lopez@upf.edu). 
 
    This file is part of Collector.

    Collector is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Collector is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Collector.  If not, see <http://www.gnu.org/licenses/>.
   
*/

package es.imim.phi.collector.engine

import es.imim.phi.collector.compounds._
import es.imim.phi.collector.ops.lda_api._

import slick.lifted.TableQuery
import slick.lifted.ProvenShape
import slick.driver.PostgresDriver.simple._
import slick.jdbc.JdbcBackend.Database.dynamicSession
import slick.jdbc.{ StaticQuery => Q }
import slick.jdbc.StaticQuery.interpolation
import slick.jdbc.GetResult
import slick.backend._

import java.util.Properties
import java.io.FileInputStream
import java.sql.ResultSet
import scala.collection.immutable.HashMap
import scala.collection.immutable.HashSet
import org.apache.commons.lang3.StringUtils
import java.text.SimpleDateFormat
import java.util.TreeSet
import scala.collection.JavaConversions._
import play.Play
import play.api.Logger
import java.sql.Timestamp
import java.sql.DriverManager
import es.imim.phi.collector.compounds._
import es.imim.phi.collector.model.database_eTOXOPS
import play.api.libs.json.Json
import play.api.libs.json._

object ExtractionEngine {
  val logger = Logger
  var dbUser: String = ""
  var dbPassword: String = ""
  var dbURL: String = ""
  var OPSAPIURL = ""
  var exportDataDir = ""
  //var fieldMap = new java.util.HashMap[String, String]
  var sourceQuery = ""
  //var filtering_id = ""
  var appBasePath = ""
  var externalToolsBasePath = ""
  var inputDatadir = ""
  var appKey = ""
  var appId = ""
  var opsAPI: OPSLDAScala = null
  var cachedapi: Boolean = false
  var bucketAPISize = -1
  var version = "1.4.8"
  var CDKit = true

  def initEngine(home_path: String) = {
    Logger.info("Initializing collector " + this.version)
    Logger.info("Application path " + home_path)
    appBasePath = home_path
    externalToolsBasePath = appBasePath + "/external_tools"
    this.readProperties()

  }

  def changeOPSAPIURL(opsapiurl: String) = {
    this.opsAPI = new OPSLDAScala(opsapiurl, ExtractionEngine.appKey, ExtractionEngine.appId, ExtractionEngine.dbURL, ExtractionEngine.dbUser, ExtractionEngine.dbPassword, ExtractionEngine.cachedapi)
  }

  def readProperties() {
    Logger.info("Reading Properties")
    val defaultProps = FileUtils.readPropertiesFile("conf/collector.properties")
    //ExtractionEngine.dbURL = defaultProps.getProperty("dbURL")
    //ExtractionEngine.dbURL="jdbc:"+sys.env("DATABASE_URL")

    //val dbUri = new java.net.URI(System.getenv("DATABASE_URL"))
    //val username = dbUri.getUserInfo().split(":")(0)
    //val password = dbUri.getUserInfo().split(":")(0)

    //ExtractionEngine.dbURL = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath()

    ExtractionEngine.dbURL = System.getenv("JDBC_DATABASE_URL")

    database_eTOXOPS.db = Database.forURL(ExtractionEngine.dbURL)
    Class.forName("org.postgresql.Driver")    
    var con = DriverManager.getConnection(ExtractionEngine.dbURL)
    database_eTOXOPS.sqlConnection = con

    //
    //String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

    //return DriverManager.getConnection(dbUrl, username, password);

    //ExtractionEngine.dbUser = defaultProps.getProperty("dbUser")
    Logger.info("dbUser: " + ExtractionEngine.dbUser)
    //ExtractionEngine.dbPassword = defaultProps.getProperty("dbPassword")
    Logger.info("dbPassword: " + ExtractionEngine.dbPassword)
    ExtractionEngine.OPSAPIURL = defaultProps.getProperty("opsLDA_API_URL")
    Logger.info("OPS API URL: " + ExtractionEngine.OPSAPIURL)
    //ExtractionEngine.exportDataDir = appBasePath + defaultProps.getProperty("exportDatadir")
    ExtractionEngine.exportDataDir = "logs/"
    Logger.info("export data dir: " + ExtractionEngine.exportDataDir)
    ExtractionEngine.inputDatadir = appBasePath + defaultProps.getProperty("inputDatadir")
    Logger.info("input data dir: " + ExtractionEngine.inputDatadir)
    ExtractionEngine.appId = defaultProps.getProperty("appId")
    Logger.info("AppId: " + ExtractionEngine.appId)
    ExtractionEngine.appKey = defaultProps.getProperty("appKey")
    Logger.info("AppKey: " + ExtractionEngine.appKey)
    ExtractionEngine.cachedapi = if (defaultProps.getProperty("cachedapi") == "true") true else false
    Logger.info("Cached Api: " + ExtractionEngine.cachedapi)
    ExtractionEngine.bucketAPISize = defaultProps.getProperty("bucketAPISize").toInt
    Logger.info("Bucket API size: " + ExtractionEngine.bucketAPISize)
    ExtractionEngine.CDKit = defaultProps.getProperty("toolkitSDF") match {
      case "RDKit" => false
      case _       => true
    }

    Logger.info("CDKit: " + ExtractionEngine.CDKit)

    this.opsAPI = new OPSLDAScala(ExtractionEngine.OPSAPIURL, ExtractionEngine.appKey, ExtractionEngine.appId, ExtractionEngine.dbURL, ExtractionEngine.dbUser, ExtractionEngine.dbPassword, ExtractionEngine.cachedapi)
  }

  def computeSDF(jobExecutionId: String) = {
    var i = 0
    database_eTOXOPS.db withDynSession {
      database_eTOXOPS.GetRAWDataJobExecutionForSDF(jobExecutionId.toInt).foreach {
        case (jobExecutionId, job_data_raw_id, cs_id, smiles, sdf2d) => {
          i = i + 1
          if ((i % 100) == 0) println("SDF computing : " + i)
          var sdf = CompoundUtil.getSDFFromSmiles(smiles, ExtractionEngine.CDKit)
          val q1 = for (msg <- database_eTOXOPS.job_data_raw_for_sdf if msg.job_data_raw_id === job_data_raw_id)
            yield (msg.sdf2d)
          q1.update(Option(sdf))
        }
      }
    }
  }

  //  def getDataSDF(jobExecutionId: String, job_series_id: Int) = {
  //    var fieldMapLDASDF = new java.util.HashMap[String, String]
  //    val fieldMapTypeSDF = new java.util.HashMap[String, String]
  //    fieldMapLDASDF.put("relation", "relation")
  //    fieldMapLDASDF.put("standard_units", "standard_units")
  //    fieldMapLDASDF.put("standard_value", "standard_value")
  //    fieldMapLDASDF.put("activity_type", "activity_type")
  //    fieldMapLDASDF.put("inchi", "inchi")
  //    fieldMapLDASDF.put("inchikey", "inchikey")
  //    fieldMapLDASDF.put("smiles", "smiles")
  //    fieldMapLDASDF.put("job_execution_id", "job_execution_id")
  //    fieldMapTypeSDF.put("relation", "STRING")
  //    fieldMapTypeSDF.put("standard_units", "STRING")
  //    fieldMapTypeSDF.put("standard_value", "FLOAT")
  //    fieldMapTypeSDF.put("activity_type", "STRING")
  //    fieldMapTypeSDF.put("inchi", "STRING")
  //    fieldMapTypeSDF.put("inchikey", "STRING")
  //    fieldMapTypeSDF.put("smiles", "STRING")
  //    fieldMapTypeSDF.put("job_execution_id", "INTEGER")
  //    val q = "SELECT activities_id, activities_series_id, relation, standard_units," +
  //      "       standard_value, activity_type, inchi, inchikey, smiles, data," +
  //      "    sdf2d, sdf3d" +
  //      "  FROM activities where activities_series_id=" + job_series_id + " ;"
  //
  //    val rs = database_eTOXOPS.doQuerySQL(q)
  //    var acts = new scala.collection.mutable.LinkedList[Map[String, String]]()
  //    while (rs.next()) {
  //      val data = rs.getString("data")
  //      val js = Json.parse(data)
  //      val jso = js.as[JsObject]
  //      val flds = jso.fields
  //      val dict = for ((f, v) <- flds)
  //        yield ({
  //        val v1 = v.as[JsString].value
  //        f -> v1
  //      })
  //      val dict2 = dict.toMap
  //      acts = acts :+ dict2
  //    }
  //    Logger.info("Activities obtained: " + acts.size)
  //    database_eTOXOPS.MoveLDAResults2SQL(acts, jobExecutionId, database_eTOXOPS.sqlConnection, "job_data_raw")
  //  }

  //  def executeExtraction_old(jobExecutionId: String, basket: Map[String, String]) = {
  //    Logger.info("Basket:")
  //    val target_cwiki = basket("target_cwiki")
  //    Logger.info("Query for target id new id: " + target_cwiki)
  //    var res = opsAPI.GetPharmacologyByTargetLDA(target_cwiki)
  //    println("Activities obtained: " + res.size.toString())
  //    
  //  }

  def executeExtraction(jobExecutionId: String, basket: Map[String, String]) = {
    Logger.info("Basket:")
    val target_cwiki = basket("target_cwiki")
    Logger.info("Query for target id new id: " + target_cwiki)
    var res = opsAPI.GetPharmacologyByTargetLDA_new(target_cwiki, jobExecutionId, database_eTOXOPS.sqlConnection, "job_data_raw")

  }

  def obtainInitialData(jobId: Int, jobExecutionId: String) = {
    Logger.info("Obtaining parameters")
    Logger.info("OPS case")
    Logger.info("Executing extraction")
    executeExtraction(jobExecutionId, getJobParameterValues(jobId))
    val rs = database_eTOXOPS.doQuerySQL("update job_execution set job_execution_finish_extraction_date=CURRENT_TIMESTAMP where job_execution_id=" + jobExecutionId)
    computeSDF(jobExecutionId)
  }

  def executejob(jobId: Int) = {
    Logger.info("Executing job: " + jobId)
    var jobExecutionId = ""
    try {
      jobExecutionId = insertJobExecution(jobId)
      Logger.info("JobExecutionId inserted:" + jobExecutionId)
      database_eTOXOPS.RefreshJobExecutionStatistics(jobExecutionId)
      obtainInitialData(jobId, jobExecutionId)
      Logger.info("Obtaining filters")
      val filters = getFilters(jobId)
      Logger.info("Executing filtering")
      executeFiltering(jobExecutionId, filters)
      val r = database_eTOXOPS.doQuerySQL("update job_execution set job_execution_finish_filtering_date=CURRENT_TIMESTAMP,job_execution_status='OK' where job_execution_id=" + jobExecutionId)

      database_eTOXOPS.RefreshJobExecutionStatistics(jobExecutionId)
      database_eTOXOPS.refreshMaterializedView
      Logger.info("Succesful execution jobId:" + jobId + " jobExecutionId:" + jobExecutionId)
      "{\"success\": true, \"jobexecutionid\":" + jobExecutionId.toString() + "}"
    } catch {
      case e: Exception => {
        Logger.info("error")
        Logger.info("Finalised execution jobId:" + jobId + " jobExecutionId:" + jobExecutionId)
        println(e.getMessage())
        println(e.getStackTraceString)
        database_eTOXOPS.doQuerySQL("update job_execution set job_execution_finish_filtering_date=CURRENT_TIMESTAMP,job_execution_status='Error' where job_execution_id=" + jobExecutionId)
        //database_eTOXOPS.RefreshJobExecutionStatistics(jobExecutionId)
        "{success: false}"
      }
    }
  }

  def insertJobExecution(jobId: Int): String = {
    var resultset = database_eTOXOPS.doQuerySQLInsert(
      "insert into job_execution (job_id, job_execution_init_date,job_execution_status) VALUES (" + jobId + ",CURRENT_TIMESTAMP,'Running')")
    resultset.next()
    resultset.getString("job_execution_id")
  }

  def getJobParameterValues(jobId: Int) = {
    var queryGetParametersValues: String = " SELECT " +
      "  job_parameter_value.job_parameter_value,  " +
      "  job_parameter_value.job_parameter_field   " +
      " FROM " +
      "		public.job_parameter_value,  " +
      "		public.job " +
      " WHERE " +
      " 	job_parameter_value.job_extraction_id = job.job_extraction_id " +
      " 	AND job_id=" + jobId + ";"
    var parametersValues = database_eTOXOPS.doQuerySQL(queryGetParametersValues)

    var basket = Map[String, String]()
    while (parametersValues.next()) {
      basket += parametersValues.getString("job_parameter_field") -> parametersValues.getString("job_parameter_value")
    }
    parametersValues.close()
    basket
  }

  def getFilters(jobId: Int) = {
    var queryGetFilters: String = " SELECT   job.job_id,  job.job_filtering_id,  filter.filter_id,  filter.filter_description,   filter.filter_class,  job_filtering_filter.curation_order " +
      " FROM   public.filter,  public.job,   public.job_filtering_filter WHERE   job_filtering_filter.filter_id = filter.filter_id AND  job_filtering_filter.job_filtering_id = job.job_filtering_id " +
      " AND job_id=" + jobId + " ORDER BY curation_order ;"

    Logger.info("Query get filters: " + queryGetFilters)
    var filters = database_eTOXOPS.doQuerySQL(queryGetFilters)
    var filtersList = scala.collection.mutable.LinkedList[Tuple4[String, CompoundFilter, String, String]]()
    while (filters.next()) {
      var filter: CompoundFilter = Class.forName(filters.getString("filter_class")).newInstance.asInstanceOf[CompoundFilter]
      val filtering_id = filters.getString("job_filtering_id")
      val t = (filters.getString("filter_id"), filter, filtering_id, filters.getString("filter_description"))
      filtersList = filtersList :+ t
      Logger.info("Filter: " + filters.getString("filter_description"))
    }
    filters.close()
    filtersList
  }

  def executeFiltering(jobExecutionId: String, filters: scala.collection.mutable.LinkedList[Tuple4[String, CompoundFilter, String, String]]) =
    {
      Logger.debug("Doing filtering job_id: " + jobExecutionId)
      Logger.info("Filters: " + filters)
      database_eTOXOPS.db withDynSession {
        //val q = database_eTOXOPS.GetRAWDataJobExecution(jobExecutionId.toInt)
        val q = database_eTOXOPS.GetRAWDataJobExecutionId(jobExecutionId.toInt)
        println("Filters: " + filters.size)
        val fnames = filters.map(_._4)
        var mapini = fnames.map(v => (v, 0)).toMap
        var i = 0
        q.foreach {

          //case (job_execution_id, job_data_raw_id, assay_id, target_id, molecule_id, cs_id, relation, standard_units, standard_value, activity_type, inchi, inchikey, smiles, ro5violations, target_organism, pmid, full_mwt, compoundpreflabel, assayDescription, targetTitle, activity_id) =>
          case (job_data_raw_id, activity_type, smiles) =>
            val compoundSimple = new CompoundSimple(activity_type, smiles.getOrElse(""))
            //var activity = new Activity(job_execution_id, job_data_raw_id, activity_type, smiles.getOrElse(""))
            //var compound = new Compound(activity)
            i = i + 1
            if ((i % 1000) == 0) println("Filtering : " + i)

            if ((i % 1000) == 0) System.gc()

            for ((filter_id, filter, filtering_id, fname) <- filters) {
              //var filterPass = filter.filterPass(compound)
              val filter = CompoundsFilters.filters(fname)
              var filterPass = filter(compoundSimple)
              if (filterPass) {
                val v = mapini(fname)
                val prev = (mapini - fname)
                mapini = prev + (fname -> (v + 1))
              }
              var query = "insert into job_data_filtered (job_filtering_id,filter_id,job_execution_id,job_data_raw_id,filter_passed) " +
                "VALUES (" + filtering_id + "," + filter_id + "," + jobExecutionId + "," + job_data_raw_id + "," + filterPass + ")"

              try {
                //var resultset = database_eTOXOPS.doQuerySQLInsert(query)

                var statement = database_eTOXOPS.sqlConnection.createStatement()
                Logger.debug("doQuerySQLInsert: " + query)
                statement.execute(query)
                statement.close()
              } catch {
                case e: Exception => {
                  println(e.getMessage())
                  //Logger.info("Failed execution jobExecutionId:" + jobExecutionId)
                  //Logger.debug("Doing filtering job_id: " + jobExecutionId)
                  //Logger.info("Filters: " + filters)
                  Logger.info(query)
                  //Logger.info(e.getMessage())
                  //Logger.info(e.getStackTraceString)
                }
              }
            }
        }

        println("Fnames " + fnames.size)
        for ((f, v) <- mapini) println("Filter: " + f + " / " + v)

      }
    }

  def insertStep(jobExecutionId: Int, filter_id: Int, newDataSeriesId: Int, stepId: Int) = {
    database_eTOXOPS.InsertStep(jobExecutionId, filter_id, newDataSeriesId, stepId)
  }

  //  def executeFiltering_v14(jobExecutionId : String, initialDataSeries : Int, filters : scala.collection.mutable.LinkedList[Tuple3[String, CompoundFilter, String]]) = {
  //    Logger.debug("Doing filtering job_id: "+jobExecutionId)
  //    Logger.info("Filters: "+filters)
  //    Logger.info("Initial data series: "+initialDataSeries)
  //    var currentDataSeries = initialDataSeries
  //    var newDataSeriesId = 0
  //    var stepId = 1
  //    database_eTOXOPS.db withDynSession {
  //      for ((filter_id, filter, _) <- filters) {
  //        newDataSeriesId = CompoundDataSeries.FilterDataSeries(currentDataSeries, filter)
  //        insertStep(jobExecutionId.toInt, filter_id.toInt, newDataSeriesId, stepId)
  //        stepId += 1
  //      }
  //    }
  //  }
}
  



