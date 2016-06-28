/*   
     Collector is a tool for obtaining bioactivity data from the Open PHACTS platform.
     Copyright (C) 2013 UPF
     Contributed by Manuel Pastor(manuel.pastor@upf.edu) and Oriol L. Massaguer(olopez@imim.es). 
 
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

package controllers

import es.imim.phi.collector.engine._
import es.imim.phi.collector.model._
import play.api.libs.json._
import play.api.mvc._
import play.api.Logger
import es.imim.phi.collector.engine.exportData
import play.api.libs.iteratee.Iteratee
import play.libs.Akka

//import scala.slick.lifted.Column
//import scala.slick.lifted.TypeMapper
//import scala.slick.session.Database
//import scala.slick.driver.PostgresDriver.simple._
//import scala.slick.driver.ExtendedProfile
//import scala.slick.driver.ExtendedDriver
//import scala.slick.session.Session
//import Database.threadLocalSession
//import scala.slick.jdbc.{ GetResult, StaticQuery => Q }

import slick.driver.PostgresDriver.simple.Query
import slick.jdbc.{ StaticQuery => Q }
import scala.slick.jdbc.StaticQuery.interpolation
import scala.slick.jdbc.GetResult
import slick.driver.PostgresDriver.simple._
import slick.jdbc.JdbcBackend.Database.dynamicSession

import play.api.libs.concurrent.Execution.Implicits._
import java.net.URLEncoder
import javax.net.ssl.SSLContext
import play.Play
import scala.concurrent.Future
import es.imim.phi.collector.model.database_eTOXOPS
import es.imim.phi.collector.model.db2JSON
//import padeldescriptor.PaDELDescriptorApp
import play.api.libs.iteratee.Enumerator
//import es.imim.phi.collector.compounds.CompoundUtil

object Application extends Controller {
  //  val s = System.load("/opt/collector/lib/libGraphMolWrap.so")
  //  val app = PaDELDescriptorApp.getApplication()
  var logger = play.api.Logger
  //Logger.info("Home: " + System.getenv("COLLECTOR_HOME"))
  ExtractionEngine.initEngine(System.getenv("COLLECTOR_HOME"))

  def parseJsonFilters(jsonfilters: String) = {
    Logger.info("Parsing filters")
    val pairs = Json.parse(jsonfilters) match {
      case jsa: JsArray =>
        {
          for (pair <- jsa.value) yield {
            val key = (pair \ "property").as[String]
            val value = (pair \ "value").as[String]
            (key -> value)
          }
        }
      case _ => Seq()
    }

    Logger.info(pairs.toString())
    pairs.toMap[String, String]
  }

  def getFileResultFromFile(localfilename: String, filename: String) = {
    Logger.info("Result!")
    val file = new java.io.File(localfilename)
    val fileContent: Enumerator[Array[Byte]] = Enumerator.fromFile(file)
    Logger.info("Result!")
    SimpleResult(
      header = ResponseHeader(200,
        Map(
          CONTENT_LENGTH -> file.length.toString,
          "Content-Disposition" -> ("attachment; filename=" + filename))),
      body = fileContent)
  }

  def main =
    Action { request =>
      Ok(views.html.main("Collector is ready", "localhost"))
    }

  def jobnew = Action { request =>
    {
      Logger.info("Action new Job")
      val params = request.body.asFormUrlEncoded.get
      Logger.info("Job Parameters: " + params)
      database_eTOXOPS.CreateJob(params("job_description")(0), params("protein_uri")(0), params("target_label")(0), params("protocol_id")(0))
      Ok("{success: true}")
    }
  }
  //
  //  def jobnew2(job_description: String, protein_uri: String, target_label: String, protocol_id: String) = Action { request =>
  //    {
  //      Logger.info("Action new Job2")
  //      Logger.info("Job description: " + job_description)
  //      Logger.info("Protein URI: " + protein_uri)
  //      Logger.info("Target label: " + target_label)
  //      Logger.info("Protocol id: " + protocol_id)
  //      Ok(database_eTOXOPS.CreateJob(job_description, protein_uri, target_label, protocol_id))
  //    }
  //  }

  def jobsAll(page: Int, start: Int, limit: Int) = Action {
    Logger.info("Action get all jobs info")
    Ok(db2JSON.getJobsAllJSON)
  }

  //  def jobsAll2 = Action {
  //    Ok(db2JSON.getJobsAllJSON2)
  //  }

  def jobexecutionsAll(filter: String) = Action {
    
    Logger.info("Action get job info")
    println("Action get job info")
    Logger.info("Filter: " + filter)
    val filterparameters = parseJsonFilters(filter)
    //Logger.info(filterparameters.toString())
    Logger.info("Job id:" + filterparameters("job_id"))
    Ok(db2JSON.getJobExecutionsJSON(filterparameters("job_id").toInt))
  }

  //  def jobexecutionsAll2(job_id: String) = Action {
  //    Ok(db2JSON.getJobExecutionsJSON2(job_id.toInt))
  //  }

  def jobdatadetailed(page: Int, start: Int, limit: Int, filter: String, filtered: Boolean) = Action {
    val filterparameters = parseJsonFilters(filter)
    Logger.info("Action get job execution info job execution id:" + filterparameters("job_execution_id"))
    val js = db2JSON.getJobDataDetailedJSON(filterparameters("job_execution_id").toInt, limit, start, filtered)
    Ok(js)
  }

  def jobdataexport(job_execution_id: Int, filtered: Boolean, agregated: Boolean, format: String) = Action {
    Logger.info("Action Download job execution info job execution id:" + job_execution_id)
    Logger.info(if (filtered) "Filtered" else "Not Filtered")
    Logger.info(if (agregated) "Compounds" else "Activities")
    Logger.info(format)
    val file = exportData.exportexecutiondata(job_execution_id, filtered, agregated, format)
    getFileResultFromFile(file._1, file._2)
  }

  def jobExecAsync(job_id: Int) = Action{
    val res: Future[String] = scala.concurrent.Future { ExtractionEngine.executejob(job_id) }
    val futureResult: Future[SimpleResult] = res.map { resst => Ok("PI value computed: " + resst) }
    //futureResult
    Ok("")
  }

  def jobExec(job_id: Int) = Action {
    Logger.info("Action exec job: " + job_id)
    val res = ExtractionEngine.executejob(job_id)
    Ok(res)
  }

  def jobDelete(job_id: Int) = Action {
    Logger.info("Action delete job")
    Logger.info("Job id: " + job_id)
    var deleteq = database_eTOXOPS.job.where(_.job_id === job_id)
    Logger.debug(deleteq.deleteStatement)
    database_eTOXOPS.db withDynSession { deleteq.delete }
    Ok("{success: true}")
  }

  def jobExecutionDelete(job_execution_id: Int) = Action {
    Logger.info("Action delete job execution: " + job_execution_id)
    val deleteq = database_eTOXOPS.job_execution.where(_.job_execution_id === job_execution_id)
    Logger.debug(deleteq.deleteStatement)
    //Akka.system.scheduler.scheduleOnce(10 milliseconds) {
    database_eTOXOPS.db withDynSession { deleteq.delete }
    //}
    Ok("{success: true}")
  }

  def getJobStatistics(page: Int, start: Int, limit: Int, filter: String) = Action {
    val filterparameters = parseJsonFilters(filter)

    Logger.info("Action job statistics: ")
    Logger.info("job execution id: " + filterparameters("job_execution_id"))
    database_eTOXOPS.db withDynSession {
      //val l = database_eTOXOPS.GetStatisticsForJobExecutionOldId(filterparameters("job_execution_id").toInt)
      //Logger.info("Job Statistics: \n" + Json.toJson(l))
      val (l,js) = database_eTOXOPS.GetStatisticsForJobExecutionIdJSON(filterparameters("job_execution_id").toInt)
      Ok("{success: true, total: " + l.size + ",jobstatistics:" + js + "}")
      //Ok("")
    }
  }

  def getJobStatisticsHistogram(page: Int, start: Int, limit: Int, filter: String) = Action {
    Logger.info("Action job execution histogram statistics")
    val filterparameters = parseJsonFilters(filter)
    val job_execution_id = filterparameters("job_execution_id")
    Logger.info("Job execution id:" + job_execution_id)
    database_eTOXOPS.db withDynSession {
      val l = database_eTOXOPS.GetJobExecutionDataForHistogram(job_execution_id.toInt)
      Logger.info("JSON Histogram Statistics:")
      Logger.info("{success: true,total: " + l.size + ", jobstatistics:" + Json.toJson(l) + " }")
      Ok("{success: true, total: " + l.size + ",jobstatisticshistogram:" + Json.toJson(l) + "}")
    }
  }
  def getURLForCBNFiltered(job_execution_id: Int) = getURLForCBN(job_execution_id, true)
  def getURLForCBNRAW(job_execution_id: Int) = getURLForCBN(job_execution_id, false)

  def getURLForCBN(job_execution_id: Int, filtered: Boolean) = Action {
    Logger.info("Action get URLs for CBN")
    //val cbnurls = database_eTOXOPS.db withDynSession { database_eTOXOPS.GetURLForCBN(job_execution_id) }   "
    val sql = if (filtered)
      "select cs_id from job_data_filtered_vw where job_execution_id=" + job_execution_id + " limit " + ExtractionEngine.numcompoundsCBN.toInt
    else
      "select cs_id from job_data_vw where job_execution_id=" + job_execution_id + " limit " + ExtractionEngine.numcompoundsCBN.toInt
    println(sql)
    val rs = database_eTOXOPS.doQuerySQL(sql)
    var lurls = scala.collection.mutable.LinkedList[String]()
    while (rs.next) {
      println(rs.getString(1))
      val ur = rs.getString(1)
      lurls = lurls ++ List(ur)
      //lurls += ur
      //lurls=lurlsF+rs.getString(1)
    }
    rs.close()
    println("end query")
    println(lurls.size)
    val cbnurls = ExtractionEngine.URLCBN + lurls.toSet.take(ExtractionEngine.numcompoundsCBN.toInt).mkString(",")
    Logger.debug(cbnurls)
    println(cbnurls)
    Ok("{success: true, url:\"" + cbnurls + "\"}")
  }

  def testCDK = Action {
    val comp = es.imim.phi.collector.compounds.CompoundUtil
    val sdf = comp.getSDFFromSMiles_CDK("CCCHC")
    Ok("{sdf:" + sdf + "}")
  }
  
    def testRDKit = Action {
    val comp = es.imim.phi.collector.compounds.CompoundUtil
    
    println(models.chemistry.CompoundUtil.getMWfromSMILES("CCC"))
    val sdf = comp.getSDFFromSMiles_RDKit("CCCC")
    Ok("{sdf:" + sdf + "}")
  }

  def getFilters(page: Int, start: Int, limit: Int, filter_string: String) = Action {
    Logger.info("Action get Filters")
    database_eTOXOPS.db withDynSession {
      Ok(database_eTOXOPS.getFiltersForString(filter_string: String))
    }
  }

  def getProtocolsForString(page: Int, start: Int, limit: Int, protocol_string: String) = Action {
    Logger.info("Action get Protocols for string: " + protocol_string)
    val protocols = database_eTOXOPS.GetFilteringProtocolForString(protocol_string: String)
    Logger.info("protocols obtained \n" + protocols)
    database_eTOXOPS.db withDynSession { Ok(protocols) }
  }

  def getAllProtocols(page: Int, start: Int, limit: Int) = Action {
    Logger.info("Action get all protocols")

    val protocols = db2JSON.getProtocolsAllJSON
    Logger.info("protocols obtained \n" + protocols)
    database_eTOXOPS.db withDynSession { Ok(protocols.toString()) }
  }

  def protocolnew = Action {
    request =>
      {
        println("New protocol: \n" + request.body.toString())
        Logger.info("Action new protocol")
        val b = request.body
        val js = b.asJson.get
        Logger.info("new protocol json: " + js)
        val filters = js match {
          case jsa: JsArray =>
            {
              Logger.debug("Array parsed")
              for (e <- jsa.value)
                yield (e \ "filter_description")
            }
          case jso: JsObject => {
            val j1 = jso \ "filter_description"
            List(j1)
          }
          case _ => {
            Logger.debug("Failed to parse array")
            Seq()
          }
        }
        val lf = filters.toList.map(js => js.as[String])
        database_eTOXOPS.CreateProtocol(lf.mkString(" + "), lf)
        Ok("{success: true}")
      }
  }

}
