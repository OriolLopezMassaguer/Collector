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

package es.imim.phi.collector.model
import play.api.Logger
import es.imim.phi.collector.engine._

//import scala.slick.lifted._
//import scala.slick.session.Database
//import scala.slick.driver.PostgresDriver.simple._
//import scala.slick.driver._
//import scala.slick.jdbc._

//import scala.slick.jdbc.{ StaticQuery => Q }

//import slick.driver.PostgresDriver._
import slick.lifted.ProvenShape
import slick.lifted.TableQuery
import slick.driver.PostgresDriver.simple._
import slick.jdbc.JdbcBackend.Database.dynamicSession
import slick.jdbc.{ StaticQuery => Q }
import slick.jdbc.StaticQuery.interpolation
import slick.jdbc.GetResult
import slick.backend._

import java.sql.Statement
import java.io.PrintStream
import java.sql.ResultSet
import java.sql.Connection
import collection.JavaConversions._
import scala.runtime.ZippedTraversable3.zippedTraversable3ToTraversable
import play.api.libs.json.Json
import play.api.libs.json._
import controllers.Application
import es.imim.phi.collector.compounds.CompoundFilter
//import scala.slick.driver.BasicProfile.SimpleQL.Table

object database_eTOXOPS {
  var db: Database = null
  var sqlConnection: java.sql.Connection = null
  db

  class Job(tag: Tag) extends Table[(Int, String, Int, Int)](tag, "job") {
    def job_id = column[Int]("job_id", O.AutoInc)
    def job_description = column[String]("job_description")
    def job_extraction_id = column[Int]("job_extraction_id")
    def job_filtering_id = column[Int]("job_filtering_id")
    //def job_series_id = column[Int]("job_series_id")
    def * = (job_id, job_description, job_extraction_id, job_filtering_id)
    //~ job_series_id
  }

  val job = TableQuery[Job]

  class Job_extraction(tag: Tag) extends Table[(Int, String)](tag, "job_extraction") {
    def job_extraction_id = column[Int]("job_extraction_id", O.PrimaryKey)
    def job_extraction_description = column[String]("job_extraction_description")
    def * = (job_extraction_id, job_extraction_description)
  }

  val job_extraction = TableQuery[Job_extraction]

  class Job_parameter(tag: Tag) extends Table[(Int, String, String)](tag, "job_parameter") {
    def job_parameter_id = column[Int]("job_parameter_id", O.PrimaryKey)
    def job_parameterSQL = column[String]("job_parameterSQL")
    def job_parameterSQLType = column[String]("job_parameterSQLType")
    def * = (job_parameter_id, job_parameterSQL, job_parameterSQLType)
  }

  val job_parameter = TableQuery[Job_parameter]

  class Job_parameter_value(tag: Tag) extends Table[(Int, Int, String)](tag, "job_parameter_value") {
    def job_extraction_id = column[Int]("job_extraction_id", O.PrimaryKey)
    def job_parameter_id = column[Int]("job_parameter_id")
    def job_parameter_value = column[String]("job_parameter_value")
    def * = (job_extraction_id, job_parameter_id, job_parameter_value)
  }

  val job_parameter_value = TableQuery[Job_parameter_value]

  class Job_filtering(tag: Tag) extends Table[(Int, String)](tag, "job_filtering") {
    def job_filtering_id = column[Int]("job_filtering_id", O.PrimaryKey, O.AutoInc)
    def job_filtering_description = column[String]("job_filtering_description")
    def * = (job_filtering_id, job_filtering_description)
  }

  val job_filtering = TableQuery[Job_filtering]

  class Filter(tag: Tag) extends Table[(Int, String, String)](tag, "filter") {
    def filter_id = column[Int]("filter_id", O.PrimaryKey)
    def filter_description = column[String]("filter_description")
    def filter_class = column[String]("filter_class")
    def * = (filter_id, filter_description, filter_class)
  }

  val filter = TableQuery[Filter]

  class Job_filtering_filter(tag: Tag) extends Table[(Int, Int, Int)](tag, "job_filtering_filter") {
    def job_filtering_id = column[Int]("job_filtering_id", O.PrimaryKey)
    def filter_id = column[Int]("filter_id", O.PrimaryKey)
    def curation_order = column[Int]("curation_order")
    def * = (job_filtering_id, filter_id, curation_order)
  }

  val job_filtering_filter = TableQuery[Job_filtering_filter]

  class Job_execution(tag: Tag) extends Table[(Int, Int, Option[java.sql.Timestamp], Option[java.sql.Timestamp], Option[java.sql.Timestamp], String)](tag, "job_execution") {
    def job_execution_id = column[Int]("job_execution_id", O.PrimaryKey)
    def job_id = column[Int]("job_id")
    def job_execution_init_date = column[Option[java.sql.Timestamp]]("job_execution_init_date")
    def job_execution_finish_extraction_date = column[Option[java.sql.Timestamp]]("job_execution_finish_extraction_date")
    def job_execution_finish_filtering_date = column[Option[java.sql.Timestamp]]("job_execution_finish_filtering_date")
    def job_execution_status = column[String]("job_execution_status")
    def * = (job_execution_id, job_id, job_execution_init_date, job_execution_finish_extraction_date, job_execution_finish_filtering_date, job_execution_status)
  }

  val job_execution = TableQuery[Job_execution]

  class Job_execution_step(tag: Tag) extends Table[(Int, Int, Int, Int)](tag, "job_execution_step") {
    def job_execution_id = column[Int]("job_execution_id")
    def filter_id = column[Int]("filter_id")
    def job_step_id = column[Int]("job_step_id")
    def activities_series_id = column[Int]("activities_series_id")
    def * = (job_execution_id, filter_id, job_step_id, activities_series_id)
  }

  val job_execution_step = TableQuery[Job_execution_step]

  class Job_data_filtered(tag: Tag) extends Table[(Int, Int, Int, Int, Boolean)](tag, "job_data_filtered") {
    def job_filtering_id = column[Int]("job_filtering_id", O.PrimaryKey)
    def filter_id = column[Int]("filter_id", O.PrimaryKey)
    def job_data_raw_id = column[Int]("job_data_raw_id", O.PrimaryKey)
    def job_execution_id = column[Int]("job_execution_id")
    def filter_passed = column[Boolean]("filter_passed")
    def * = (job_filtering_id, filter_id, job_execution_id, job_data_raw_id, filter_passed)
  }

  val job_data_filtered = TableQuery[Job_data_filtered]

  class Job_data_raw_for_sdf(tag: Tag) extends Table[(Int, Int, Option[String], Option[String], Option[String])](tag, "job_data_raw") {
    def job_execution_id = column[Int]("job_execution_id")
    def job_data_raw_id = column[Int]("job_data_raw_id", O.PrimaryKey)
    def cs_id = column[Option[String]]("cs_id")
    def smiles = column[Option[String]]("smiles")
    def sdf2d = column[Option[String]]("sdf2d")
    def * = (job_execution_id, job_data_raw_id, cs_id, smiles, sdf2d)
  }

  val job_data_raw_for_sdf = TableQuery[Job_data_raw_for_sdf]

  class Job_data_raw(tag: Tag) extends Table[(Int, Int, Option[String], Option[String], Option[String], Option[String], Option[String], Option[String], Option[String], Option[String], Option[String], Option[String], Option[String], Option[String], Option[String], Option[String], Option[Float], Option[String], Option[String], Option[String], Option[String])](tag, "job_data_raw") {
    def job_execution_id = column[Int]("job_execution_id")
    def job_data_raw_id = column[Int]("job_data_raw_id", O.PrimaryKey) // This is the primary key column
    def assay_id = column[Option[String]]("assay_id")
    def target_id = column[Option[String]]("target_id")
    def molecule_id = column[Option[String]]("molecule_id")
    def cs_id = column[Option[String]]("cs_id")
    def relation = column[Option[String]]("relation")
    def standard_units = column[Option[String]]("standard_units")
    def standard_value = column[Option[String]]("standard_value")
    def activity_type = column[Option[String]]("activity_type")
    def inchi = column[Option[String]]("inchi")
    def inchikey = column[Option[String]]("inchikey")
    def smiles = column[Option[String]]("smiles")
    def ro5violations = column[Option[String]]("ro5violations")
    def target_organism = column[Option[String]]("target_organism")
    def pmid = column[Option[String]]("pmid")
    def full_mwt = column[Option[Float]]("full_mwt")
    def compoundpreflabel = column[Option[String]]("compound_pref_label")
    def assayDescription = column[Option[String]]("assay_description")
    def targetTitle = column[Option[String]]("target_title")
    def activity_id = column[Option[String]]("activity_id")
    def * = (job_execution_id, job_data_raw_id, assay_id, target_id, molecule_id, cs_id, relation, standard_units, standard_value, activity_type, inchi, inchikey, smiles, ro5violations, target_organism, pmid, full_mwt, compoundpreflabel, assayDescription, targetTitle, activity_id)
  }

  val job_data_raw = TableQuery[Job_data_raw]

  class Job_data_raw_cc(tag: Tag) extends Table[es.imim.phi.collector.compounds.Activity](tag, "job_data_raw") {
    def job_execution_id = column[Int]("job_execution_id")
    def job_data_raw_id = column[Int]("job_data_raw_id", O.PrimaryKey) // This is the primary key column
    def activity_type = column[String]("activity_type")
    def smiles = column[String]("smiles")
    //def target_cwiki = column[String]("target_cwiki")
    //def cs_id = column[String]("cs_id")
    //def relation = column[String]("relation")
    //def standard_units = column[String]("standard_units")
    //def standard_value = column[String]("standard_value")
    //def inchi = column[String]("inchi")
    //def * = job_execution_id ~ job_data_raw_id ~ target_cwiki ~ cs_id ~ relation ~ standard_units ~ standard_value ~ activity_type ~ smiles ~ inchi <> (Activity, Activity.unapply _)
    def * = (job_execution_id, job_data_raw_id, activity_type.?, smiles) <> (es.imim.phi.collector.compounds.Activity.tupled, es.imim.phi.collector.compounds.Activity.unapply)
  }

  //val job_data_raw_cc = TableQuery[Job_data_raw_cc]

  class Job_data_filtered_vw(tag: Tag) extends Table[(Int, Int, String, String, String, String, String, String, String, String, String, String, String, String, String, String, Float, String, String, String)](tag, "job_data_filtered_vw") {
    def job_execution_id = column[Int]("job_execution_id")
    def job_data_raw_id = column[Int]("job_data_raw_id", O.PrimaryKey) // This is the primary key column
    //def target_cwiki = column[String]("target_cwiki")
    //def compound_cwiki = column[String]("compound_cwiki")
    //def activity_id = column[String]("activity_id")
    def assay_id = column[String]("assay_id")
    def target_id = column[String]("target_id")
    def molecule_id = column[String]("molecule_id")
    def cs_id = column[String]("cs_id")
    def relation = column[String]("relation")
    def standard_units = column[String]("standard_units")
    def standard_value = column[String]("standard_value")
    //def activity_value = column[String]("activity_value")
    def activity_type = column[String]("activity_type")
    def inchi = column[String]("inchi")
    def inchikey = column[String]("inchikey")
    def smiles = column[String]("smiles")
    def ro5violations = column[String]("ro5violations")
    def target_organism = column[String]("target_organism")
    //def assay_organism = column[String]("assay_organism")
    def pmid = column[String]("pmid")
    def full_mwt = column[Float]("full_mwt")
    def compoundpreflabel = column[String]("compound_pref_label")
    //def targetPrefLabel = column[String]("target_pref_label")
    def assayDescription = column[String]("assay_description")
    def targetTitle = column[String]("target_title")
    def * = (job_execution_id, job_data_raw_id, assay_id, target_id, molecule_id, cs_id,
      relation, standard_units, standard_value, activity_type,
      inchi, inchikey, smiles, ro5violations, target_organism, pmid, full_mwt, compoundpreflabel, assayDescription, targetTitle)
  }

  val job_data_filtered_vw = TableQuery[Job_data_filtered_vw]

  class Job_data_raw_ag_vw(tag: Tag) extends Table[(Int, String, String, String, String, String, String, Int, Double, String)](tag, "job_data_raw_vw_averaged") {
    def job_execution_id = column[Int]("job_execution_id")
    //def target_cwiki = column[String]("target_cwiki")
    def target_id = column[String]("target_id")
    def cs_id = column[String]("cs_id")
    def activity_type = column[String]("activity_type")
    def standard_value = column[String]("standard_units")
    def inchi = column[String]("inchi")
    def smiles = column[String]("smiles")
    def activities_count = column[Int]("activities_count")
    def activity_value_median = column[Double]("activity_value_median")
    def pmids = column[String]("pmids")
    def * = (job_execution_id, target_id, cs_id, activity_type, standard_value, inchi, smiles, activities_count, activity_value_median, pmids)
  }

  val job_data_raw_ag_vw = TableQuery[Job_data_raw_ag_vw]

  class Job_data_filtered_ag_vw(tag: Tag) extends Table[(Int, String, String, String, String, String, String, Int, Double, String)](tag, "job_data_filtered_vw_averaged") {
    def job_execution_id = column[Int]("job_execution_id")
    //def target_cwiki = column[String]("target_cwiki")
    def target_id = column[String]("target_id")
    def cs_id = column[String]("cs_id")
    def activity_type = column[String]("activity_type")
    def standard_value = column[String]("standard_units")
    def inchi = column[String]("inchi")
    def smiles = column[String]("smiles")
    def activities_count = column[Int]("activities_count")
    def activity_value_median = column[Double]("activity_value_median")
    def pmids = column[String]("pmids")
    def * = (job_execution_id, target_id, cs_id, activity_type, standard_value, inchi, smiles, activities_count, activity_value_median, pmids)
  }

  val job_data_filtered_ag_vw = TableQuery[Job_data_filtered_ag_vw]

  class Activities_series(tag: Tag) extends Table[(Option[Int], Option[String], Option[Int], Option[Int])](tag, "activities_series") {
    def activities_series_id = column[Int]("activities_series_id", O.PrimaryKey, O.AutoInc)
    def activities_series_description = column[String]("activities_series_description")
    def activities_count = column[Int]("activities_count")
    def compounds_count = column[Int]("compounds_count")
    def * = (activities_series_id.?, activities_series_description.?, activities_count.?, compounds_count.?)
    //    def forInsert = (activities_series_description.?, activities_count.?, compounds_count.?) returning activities_series_id
  }

  val activities_series = TableQuery[Activities_series]

  class Activities(tag: Tag) extends Table[(Option[Int], Option[Int], Option[String], Option[String], Option[Float], Option[String], Option[String], Option[String], Option[String], String, Option[String], Option[String])](tag, "activities") {
    def activities_id = column[Option[Int]]("activities_id", O.PrimaryKey, O.AutoInc)
    def activities_series_id = column[Option[Int]]("activities_series_id")
    def relation = column[Option[String]]("relation")
    def standard_units = column[Option[String]]("standard_units")
    def standard_value = column[Option[Float]]("standard_value")
    def activity_type = column[Option[String]]("activity_type")
    def inchi = column[Option[String]]("inchi")
    def inchikey = column[Option[String]]("inchikey")
    def smiles = column[Option[String]]("smiles")
    def data = column[String]("data")
    def sdf2d = column[Option[String]]("sdf2d")
    def sdf3d = column[Option[String]]("sdf3d")
    def * = (activities_id, activities_series_id, relation, standard_units, standard_value, activity_type, inchi, inchikey, smiles, data, sdf2d, sdf3d)
    def nokey = (activities_series_id, relation, standard_units, standard_value, activity_type, inchi, inchikey, smiles, data, sdf2d, sdf3d)
  }

  val activities = TableQuery[Activities]

  def InsertStep(job_execution_id: Int, filter_id: Int, activities_series_id: Int, job_step_id: Int) = {
    println("Inserting job step:")
    var resultset = doQuerySQLInsert("insert into job_execution_step " +
      " (job_execution_id,filter_id,job_step_id,activities_series_id)" +
      "   VALUES " +
      "  (" + job_execution_id + "," + filter_id + "," + job_step_id + "," + activities_series_id + ")")
  }

  def GetSDFforJobExecutionCSID(job_execution_id: Int, cs_id: String) = {
    for (msg <- database_eTOXOPS.job_data_raw_for_sdf if msg.job_execution_id === job_execution_id && msg.cs_id === cs_id) yield (msg.sdf2d)
  }
  def GetSDFforJobDataRawId(job_data_raw_id: Int) = {
    for (msg <- database_eTOXOPS.job_data_raw_for_sdf if msg.job_data_raw_id === job_data_raw_id) yield (msg.sdf2d)
  }
  def GetRAWDataJobExecutionForSDF(job_execution_id: Int) =
    for { rawdata <- job_data_raw_for_sdf if rawdata.job_execution_id === job_execution_id && rawdata.smiles.isNotNull }
      yield rawdata.*

  def GetRAWDataJobExecution(job_execution_id: Int, activityType: Option[String]) =
    activityType match {
      case None           => for { rawdata <- job_data_raw if rawdata.job_execution_id === job_execution_id } yield rawdata.*
      case Some(activity) => for { rawdata <- job_data_raw if rawdata.job_execution_id === job_execution_id && rawdata.activity_type === activityType } yield rawdata.*
    }

  def GetRAWDataJobExecutionId(job_execution_id: Int) =
    for { rawdata <- job_data_raw if rawdata.job_execution_id === job_execution_id }
      yield (rawdata.job_data_raw_id, rawdata.activity_type, rawdata.smiles)

  def GetFilteredDataJobExecution(job_execution_id: Int, activityType: Option[String]) =
    activityType match {
      case None           => for { filtereddata <- job_data_filtered_vw if filtereddata.job_execution_id === job_execution_id } yield filtereddata.*
      case Some(activity) => for { filtereddata <- job_data_filtered_vw if filtereddata.job_execution_id === job_execution_id && filtereddata.activity_type === activityType } yield filtereddata.*
    }

  def GetRAWDataAgJobExecution(job_execution_id: Int, activityType: Option[String]) =
    activityType match {
      case None           => for { rawdata <- job_data_raw_ag_vw if rawdata.job_execution_id === job_execution_id } yield rawdata.*
      case Some(activity) => for { rawdata <- job_data_raw_ag_vw if rawdata.job_execution_id === job_execution_id && rawdata.activity_type === activityType } yield rawdata.*
    }

  def GetFilteredDataAgJobExecution(job_execution_id: Int, activityType: Option[String]) =
    activityType match {
      case None           => for { filtereddata <- job_data_filtered_ag_vw if filtereddata.job_execution_id === job_execution_id } yield filtereddata.*
      case Some(activity) => for { filtereddata <- job_data_filtered_ag_vw if filtereddata.job_execution_id === job_execution_id && filtereddata.activity_type === activityType } yield filtereddata.*
    }

  def GetRAWDataJobExecutionSmiles(job_execution_id: Int) =
    for { rawdata <- job_data_raw if rawdata.job_execution_id === job_execution_id.bind }
      yield (rawdata.job_data_raw_id, rawdata.smiles)

  //  def GetRAWDataJobExecutionCC(job_execution_id : Int) =    for { rawdata <- job_data_raw_cc if rawdata.job_execution_id === job_execution_id.bind }      yield rawdata.*

  def GetCSIDForJobexecutionFiltered(job_execution_id: Int) =
    for { filteredata <- job_data_filtered_vw if filteredata.job_execution_id === job_execution_id.bind }
      yield filteredata.cs_id

  //update2015
  //  def GetJobInformation(job_id : Int) =
  //    for {
  //      j <- job if job.job_id === job_id.bind
  //      jf <- job_filtering
  //      je <- job_extraction
  //    } yield (j.job_id,j.job_description, jf.job_filtering_id, jf.job_filtering_description, je.job_extraction_id, je.job_extraction_description)

  def GetJobIdForJobDescription(job_description: String) =
    for {
      j <- job if j.job_description === job_description.bind
    } yield (j.job_id)

  def GetJobInformation2(job_id: Int) = {
    val rs = this.doQuerySQL("select job_id, job_description,job_filtering_id,job_filtering_id,job_series_id from job where job_id=" + job_id)
    rs.next()
    val i = rs.getInt("job_series_id")
    rs.close()
    i
  }

  def GetJobExecutionIdForJobDescription(job_description: String) = {
    database_eTOXOPS.db withDynSession {
      val jobid = this.GetJobIdForJobDescription(job_description).list.head
      println(jobid)
      val l = for (jexec <- GetJobExecutions if jexec._1 === jobid && jexec._4 === "OK")
        yield (jexec._3, jexec._7)
      println(l.selectStatement)
      val l2 = for ((jeid, timestamp) <- l.list)
        yield (jeid, timestamp.get.getTime)
      val jeids = for (p <- l2.sortBy(_._2).reverse)
        yield (p._1)
      println(jeids.head)
      jeids.head
    }
  }

  def GetJobExecutions =
    for {
      j <- job
      je <- job_execution if j.job_id === je.job_id
    } yield (j.job_id, j.job_description, je.job_execution_id, je.job_execution_status, je.job_execution_init_date, je.job_execution_finish_extraction_date, je.job_execution_finish_filtering_date)

  def GetFilteringProtocols =
    for {
      jfiltering <- job_filtering
      jff <- job_filtering_filter if jfiltering.job_filtering_id === jff.job_filtering_id
      filter <- filter if jff.filter_id === filter.filter_id
    } yield (jfiltering.job_filtering_id, jfiltering.job_filtering_description, jff.curation_order, filter.filter_id, filter.filter_description, filter.filter_class)

  def CreateJob(job_description: String, protein_uri: String, target_label: String, protocol_id: String) = {
    println("Inserting job:" + job_description + "/" + protein_uri + "/" + target_label)
    var resultset = doQuerySQLInsert("insert into job_extraction (job_extraction_description) VALUES ('" + target_label + "')")
    resultset.next()
    println("job extraction id " + resultset.getString("job_extraction_id"))
    var job_extraction_id = resultset.getString("job_extraction_id")
    var resultset2 = doQuerySQLInsert("insert into job (job_description,job_extraction_id,job_filtering_id) " +
      "VALUES ('" + job_description + "'," + resultset.getString("job_extraction_id") + "," + protocol_id + ")")
    resultset2.next()
    val job_id = resultset2.getString("job_id")
    var resultset3 = doQuerySQLInsert("insert into job_parameter_value (job_extraction_id,job_parameter_field,job_parameter_value) " +
      "VALUES (" + job_extraction_id + ",'target_cwiki','" + protein_uri + "');")
    job_id
  }

  //  def CreateJob_fromDataSeries(job_description: String, data_series_id: String, protocol_id: String) = {
  //    var resultset2 = doQuerySQLInsert("insert into job (job_description,job_extraction_id,job_filtering_id,job_series_id) VALUES " +
  //      "('" + job_description + "',1," + protocol_id + "," + data_series_id + ")")
  //    resultset2.next()
  //    val job_id = resultset2.getString("job_id")
  //    job_id
  //  }

  //  def CreateDataSeries_fromSDF(series_description: String, filename: String, fieldMapping: Map[String, String]) = {
  //    val activities = importData.getMolsFromFile(filename)
  //    val series_id = this.StoreMolecules(series_description, activities, fieldMapping)
  //    series_id
  //  }

  //  def CreateDataSeries_fromSDF_RAW(series_description : String, filename : String) = {
  //    val activities = importData.getMolsFromFile(filename, true)
  //    val series_id = this.StoreMoleculesRAW(series_description, activities)
  //    series_id
  //  }

  def GetActivitiesForDataSeries(series_id: String) = {
    val activities = database_eTOXOPS.db withDynSession {
      val mp = this.GetActivitiesForDataSeriesRAW(series_id.toInt).to[List]
      for (act <- mp)
        yield ({
        println(act._3)
        //println(act._4)
        //update2015
        //        val js = Json.parse(act._3)
        val js = Json.parse("")
        val actjs = js.asInstanceOf[JsObject]
        val actv = actjs.value
        val mp = for ((k, v) <- actv)
          yield ({
          k -> v.asInstanceOf[JsString].value
        })
        println(mp)
        //println("JS: "+actjs)
        println("------------------------")
      })
    }

    activities
  }

  def CreateProtocol(protocol_description: String, filters: List[String]) = {
    Logger.info("New protocol:  " + protocol_description)
    Logger.info("Filters: " + filters)
    var filterids = (for (filterdesc <- filters) yield (
      database_eTOXOPS.db withDynSession {
        var q = for {
          filter <- filter if filter.filter_description === filterdesc
        } yield filter.filter_id
        q.list
      })).flatten

    var qs = " insert into job_filtering  " +
      "		(job_filtering_description) " +
      " VALUES ('" + protocol_description + "')"
    var resultset = doQuerySQLInsert(qs)
    resultset.next()
    val job_filtering_id = resultset.getString("job_filtering_id")
    var order = 0
    for (filterid <- filterids) {
      order += 1
      var qs =
        "insert into job_filtering_filter  " +
          "		(job_filtering_id, filter_id, curation_order) " +
          " VALUES " +
          "       ('" + job_filtering_id + "','" + filterid + "','" + order + "')"
      val resultset2 = doQuerySQLInsert(qs)
    }

  }

  def GetFiltersForJobExecutionId(job_execution_id: Int) = {
    var q = Q.query[(Int), (Int, Int, Int, String, String, Int)](
      " SELECT t1.job_id,t1.job_filtering_id,t2.filter_id,t2.filter_description, t2.filter_class,t3.curation_order " +
        " FROM job_filtering_filter t3,job_filtering t4," +
        " job_execution t6, job t1, filter t2" +
        " WHERE (t1.job_filtering_id=t4.job_filtering_id) " +
        " AND (t3.filter_id=t2.filter_id)" +
        " AND (t3.job_filtering_id = t4.job_filtering_id ) " +
        " AND ((t1.job_id=t6.job_id) " +
        " and (t6.job_execution_id=?)) order by curation_order ASC ")
    Logger.info("GetFiltersForJobExecutionId query: " + " SELECT t1.job_id,t1.job_filtering_id,t2.filter_id,t2.filter_description, t2.filter_class,t3.curation_order " +
      " FROM job_filtering_filter t3,job_filtering t4," +
      " job_execution t6, job t1, filter t2" +
      " WHERE (t1.job_filtering_id=t4.job_filtering_id) " +
      " AND (t3.filter_id=t2.filter_id)" +
      " AND (t3.job_filtering_id = t4.job_filtering_id ) " +
      " AND ((t1.job_id=t6.job_id) " +
      " and (t6.job_execution_id=?)) order by curation_order ASC ")
    q(job_execution_id).list
  }

  def time[R](block: => R,message:String): R = {
    val t0 =java.lang.System.currentTimeMillis()
    val result = block // call-by-name
    val t1 =java.lang.System.currentTimeMillis()
    println("Profiling ++++++++++++++++++++ " + message + " duration " + (t1 - t0) + " ms")
    result
  }

  def GetStatisticsForJobExecutionOldId(job_execution_id: Int) = {
    //    var qAll = Q.query[(Int), (Int, Int)]("    select count(distinct job_data_raw_id), count(distinct smiles) " +
    //      "    		 from job_data_raw" +
    //      "    		 WHERE job_execution_id = ?")

    var qAllnew = Q.query[(Int), (Int, Int)](" SELECT  activities_raw, compounds_raw " +
      " FROM job_execution_vw_mater" +
      " WHERE job_execution_id = ?")

    var qq = time ({qAllnew(job_execution_id).list},"Acts and compounds")

     
    var res = List(Map("job_execution_id" -> job_execution_id.toString, "filter" -> "raw", "activities" -> qq(0)._1.toString, "compounds" -> qq(0)._2.toString))

    time({
    database_eTOXOPS.GetFiltersForJobExecutionId(job_execution_id).foreach { rec =>
      {
        var q = Q.query[(Int, Int), (Int, Int)]("select  count(distinct job_data_raw_id), count(distinct smiles) from" +
          "   		 (select job_data_raw_id, job_execution_id,activity_id, smiles,  bool_and(filter_passed) passed" +
          "    		 from job_data_statistics_base_vw" +
          "    		 WHERE curation_order <=  ? and job_execution_id = ?" +
          "    		 group by job_data_raw_id, job_execution_id,activity_id, smiles" +
          "    		 order by job_data_raw_id) t " +
          "	where passed=true")
        val l = q(rec._6, job_execution_id).list
        Logger.info("Old Filter: " + job_execution_id + " " + rec)
        res = res :+ Map("job_execution_id" -> job_execution_id.toString, "filter" -> rec._4.toString(), "activities" -> l(0)._1.toString, "compounds" -> l(0)._2.toString)
      }
    }
    }," Acts comps by filter")
    res
  }

  def GetStatisticsByTypeForJobExecutionIdJSON(job_execution_id: Int) = {

    var qAllnew = Q.query[(Int), (String, Int)](
      " select activity_type ,count(*) activities_count " +
        " from job_data_raw_vw where job_execution_id=? " +
        " group by activity_type" +
        " order by activity_type")

    var qq = qAllnew(job_execution_id).list

    qq
  }

  def GetStatisticsForJobExecutionIdJSON(job_execution_id: Int) = {
    var lmps = GetStatisticsForJobExecutionOldId(job_execution_id)
    var l2 = for (mp <- lmps) yield (
      {
        val l = for ((k, v) <- mp) yield (
          {
            if (k.equals("filter"))
              "\"" + k + "\":" + "\"" + v.toString() + "\""
            else
              "\"" + k + "\":" + v.toString()
          })

        "{" + l.mkString(",") + "}"
      })
    (lmps, "[" + l2.mkString(",") + "]")
  }

  def GetJobExecutionDataForHistogram(job_execution_id: Int) = {

    //    def qnolog = (min_value: Double, max_value: Double, buckets: Int, bucket_size: Double) => Q.query[(Int), (Int, Int, String)](
    //      "select count(*)," +
    //        " width_bucket(standard_value, " + min_value + "," + max_value + "," + buckets + ")," +
    //        "  cast(cast(((width_bucket(standard_value , " + min_value + ", " + max_value + ", " + buckets + "))*" + bucket_size + ") as numeric(10,2)) as varchar)" +
    //        " from	job_data_filtered_vw_norm  where job_execution_id = ? " +
    //        " group by" +
    //        "  width_bucket(standard_value, " + min_value + ", " + max_value + ", " + buckets + ") ," +
    //        " cast(cast(((width_bucket(standard_value," + min_value + ", " + max_value + ", " + buckets + "))*" + bucket_size + ") as numeric(10,2)) as varchar)" +
    //        " order by width_bucket(standard_value," + min_value + ", " + max_value + ", " + buckets + ")")
    def getqlog(min_value: String, max_value: String, buckets: String, bucket_size: String) = {
      " select " +
        " 	vals.count, " +
        " 	cast (buckets.bucket_order * 2 as int), " +
        " 	buckets.bucket " +
        " from " +
        " (select count(*)," +
        " width_bucket(log(standard_value) + 9, " + min_value + "," + max_value + "," + buckets + ")," +
        "  cast(cast(((width_bucket(log(standard_value) + 9, " + min_value + ", " + max_value + ", " + buckets + "))*" + bucket_size + ") as numeric(10,2)) as varchar) bucket" +
        " from	job_data_filtered_vw_norm  " +
        " where job_execution_id = ? " +
        " and standard_value>0 " +
        " group by" +
        "  width_bucket(log(standard_value) + 9, " + min_value + ", " + max_value + ", " + buckets + ") ," +
        " cast(cast(((width_bucket(log(standard_value) + 9," + min_value + ", " + max_value + ", " + buckets + "))*" + bucket_size + ") as numeric(10,2)) as varchar)" +
        " order by width_bucket(log(standard_value) + 9," + min_value + ", " + max_value + ", " + buckets + ")" +
        ") vals right join buckets on (vals.bucket=buckets.bucket)" +
        "order by buckets.bucket_order"
    }

    def qlog = (min_value: Double, max_value: Double, buckets: Int, bucket_size: Double) => Q.query[(Int), (Int, Int, String)](
      getqlog(min_value.toString(), max_value.toString(), buckets.toString(), bucket_size.toString))

    val num_bucks = 20
    val activities_type_for_histogram = List("AC50", "Kd", "Potency", "IC50", "Activity", "Ki", "EC50", "Potency")
    val pq = (qlog, 6.0, 18.0, num_bucks, 0.5)

    println(getqlog("6.0", "18.0", num_bucks.toString, "0.5"))

    Logger.info("Histogram data:")
    database_eTOXOPS.db withDynSession {

      val min_value = pq._2
      val max_value = pq._3
      val buckets = pq._4 + 1
      val bucket_size = pq._5

      val bucks = (Range(0, buckets).map(a => a * bucket_size)) zip (Range(1, buckets + 1).map(_ * bucket_size))
      val buckStr = bucks.map(a => a._1.toString() + "-" + a._2.toString())
      val mp = ((Range(1, buckets + 1) zip buckStr).toMap).withDefaultValue("")
      println(mp)
      val qq = pq._1(pq._2, pq._3, pq._4, pq._5)
      Logger.info("Histogram Q2:\n")
      var l = qq(job_execution_id).list

      println(mp.keys.toList.sorted)
      var res =
        for ((activities, bucket, descriptor) <- l)
          yield (Map(
          "job_execution_id" -> job_execution_id.toString(),
          "activities" -> activities.toString,
          "bucket" -> bucket.toString,
          "descriptor" -> mp(bucket).toString()))

      res
    }
  }

  def RefreshJobExecutionStatistics(job_execution_id: String) = {
    doQuerySQL("delete from job_execution_vw_mater where job_execution_id=" + job_execution_id)
    doQuerySQL("insert into job_execution_vw_mater select * from job_execution_vw where job_execution_id=" + job_execution_id +
      " and job_execution_id not in (select job_execution_id from job_execution_vw_mater where job_execution_id=" + job_execution_id + ")")

    //sqlConnection.doQuerySQL("delete from job_execution_vw_mater where job_execution_id=" + job_execution_id)
    //sqlConnection.doQuerySQL("insert into job_execution_vw_mater select * from job_execution_vw where job_execution_id=" + job_execution_id +
    //  " and job_execution_id not in (select job_execution_id from job_execution_vw_mater where job_execution_id=" + job_execution_id + ")")
  }

  def GetFilterClassForFilterDescription(filter_description: String) = {
    database_eTOXOPS.db withDynSession {
      val q = for (f <- database_eTOXOPS.filter.filter(_.filter_description === filter_description))
        yield f.filter_class
      val l = q.first
      println(l)
      Class.forName(l).newInstance.asInstanceOf[CompoundFilter]
    }
  }

  def GetFilteringProtocolForString(protocol: String) = {
    Logger.info("Filtering protocol for:" + protocol)
    val q = "select job_filtering_id,job_filtering_description from job_filtering where job_filtering_description like'%" + protocol + "%'"
    Logger.debug(q)
    db2JSON.getQueryJSONAll(q, "protocols")
  }

  def getFiltersForString(filter: String) = {
    Logger.info("Filters for:" + filter)
    val q = "select filter_id,filter_description,filter_class from filter where filter_description like'%" + filter + "%'"
    Logger.debug(q)
    db2JSON.getQueryJSONAll(q, "filters")
  }

  def doQuerySQL(query: String) = {
    Logger.debug("doQuerySQL: \n" + query)
    var statement = sqlConnection.createStatement()
    statement.execute(query)
    statement.getResultSet()
  }

  def doQuerySQLInsert(query: String) = {
    var statement = sqlConnection.createStatement()
    Logger.debug("doQuerySQLInsert: " + query)
    statement.execute(query, Statement.RETURN_GENERATED_KEYS)
    statement.getGeneratedKeys()
  }

  def doQuerySQL2Text(query: String, out: PrintStream) = {
    Logger.debug("SQL2Text: \n" + query)
    var statement = sqlConnection.createStatement()
    statement.execute(query)
    var rsmetadata = statement.getResultSet().getMetaData()
    var rs = statement.getResultSet()
    for (column <- Range(1, rsmetadata.getColumnCount() + 1)) {
      out.print(rsmetadata.getColumnLabel(column))
      out.print("\t")
    }
    out.println()
    while (rs.next()) {
      for (column <- Range(1, rsmetadata.getColumnCount() + 1)) {
        out.print(rs.getString(column))
        out.print("\t")
      }
      out.println()
    }
    statement.close
  }

  def doQuerySQL2TextSDF(rs: ResultSet, out: PrintStream) = {
    var rsmetadata = rs.getMetaData()
    var strSDF = ""
    for (column <- Range(1, rsmetadata.getColumnCount() + 1))
      strSDF = strSDF + ">  <" + rsmetadata.getColumnLabel(column) + ">\n" + rs.getString(column) + "\n\n"
    strSDF
  }

  val fieldMapLDA = scala.collection.immutable.HashMap(
    "compound_cwiki" -> "compound_cwiki",
    "target_organism" -> "target_organism",
    "pmid" -> "pmid",
    "activity_id" -> "activity_id",
    "assay_description" -> "assay_description",
    "standard_units" -> "standard_units",
    "relation" -> "relation",
    "activity_value" -> "activity_value",
    "target_title" -> "target_title",
    "inchi" -> "inchi",
    "smiles" -> "smiles",
    "cs_id" -> "cs_id",
    "target_id" -> "target_id",
    "job_execution_id" -> "job_execution_id",
    "molecule_id" -> "molecule_id",
    "inchikey" -> "inchikey",
    "target_pref_label" -> "target_pref_label",
    "target_cwiki" -> "target_cwiki",
    "standard_value" -> "standard_value",
    "assay_id" -> "assay_id",
    "full_mwt" -> "full_mwt",
    "activity_type" -> "activity_type",
    "assay_organism" -> "assay_organism",
    "ro5violations" -> "ro5violations",
    "compound_pref_label" -> "compound_pref_label")

  val fieldMapType = scala.collection.immutable.HashMap(
    "compound_cwiki" -> "STRING",
    "target_organism" -> "STRING",
    "pmid" -> "STRING",
    "activity_id" -> "STRING",
    "assay_description" -> "STRING",
    "standard_units" -> "STRING",
    "relation" -> "STRING",
    "activity_value" -> "FLOAT",
    "target_title" -> "STRING",
    "inchi" -> "STRING",
    "smiles" -> "STRING",
    "cs_id" -> "STRING",
    "target_id" -> "STRING",
    "job_execution_id" -> "INTEGER",
    "molecule_id" -> "STRING",
    "inchikey" -> "STRING",
    "target_pref_label" -> "STRING",
    "target_cwiki" -> "STRING",
    "standard_value" -> "FLOAT",
    "assay_id" -> "STRING",
    "full_mwt" -> "FLOAT",
    "activity_type" -> "STRING",
    "assay_organism" -> "STRING",
    "ro5violations" -> "FLOAT",
    "compound_pref_label" -> "STRING")

  def MoveLDAResults2SQL(ldaResults: Seq[Map[String, String]], jobExecutionId: String, c: Connection, destinationTable: String) = {

    val mapSQLType = Map(
      "INTEGER" -> java.sql.Types.INTEGER,
      "FLOAT" -> java.sql.Types.FLOAT,
      "STRING" -> java.sql.Types.VARCHAR)

    Logger.info("Open PHACTS LDA results " + ldaResults.size)

    var numRecords = 0

    var fields = fieldMapLDA.keys.toList
    var types = fields.map(f => mapSQLType(fieldMapType(f)))
    val fieldsString = fields.mkString(",")
    val posFields = List.range(1, fields.size + 1)
    val valuesmark = posFields.map(a => '?').mkString(",")
    var querySQL = "INSERT INTO " + destinationTable + " (" + fieldsString + ") VALUES \n (" + valuesmark + ")"

    for (row <- ldaResults) {
      Logger.debug("Row: " + row)
      Logger.debug("Row fields : " + row.keys)
      val row2 = row - "job_execution_id"
      var fieldsValuesMap = (("job_execution_id", jobExecutionId) :: row2.toList).toMap
      val fieldValues = fields.map(f => fieldsValuesMap(f))
      val values = fields.map(f => fieldsValuesMap(f))

      var insertStatement = c.prepareStatement(querySQL)
      val zl = (values, types, posFields).zipped
      for ((v, t, pos) <- zl.toList) {
        try {
          t match {
            case java.sql.Types.INTEGER => insertStatement.setInt(pos, Integer.parseInt(v))
            case java.sql.Types.VARCHAR => insertStatement.setString(pos, v)
            case java.sql.Types.FLOAT   => insertStatement.setFloat(pos, java.lang.Float.parseFloat(v))
          }
        } catch {
          case _: Throwable => insertStatement.setNull(pos, t)
        }
      }
      Logger.debug(insertStatement.toString())
      insertStatement.execute()
      insertStatement.close
      numRecords += 1
    }
    Logger.info("Inserted " + numRecords + " records")
  }
  // v1.3

  def CreateDataSeries(series_description: String) = {
    database_eTOXOPS.db withDynSession {
      //upgrade2015
      //val r = activities_series.forInsert insert (Some(series_description+" test "), None, None)
      //println(r)
      //activities_series.insert(None, Some(series_description + " test "), None, None)

      var resultset = doQuerySQLInsert("insert into activities_series (activities_series_description) VALUES ('" + series_description + "');")
      resultset.next()
      val r = resultset.getInt("activities_series_id")
      r
    }
  }

  def StoreMoleculesRAW(series_description: String, activities: List[scala.collection.immutable.Map[String, String]]): Int = {
    val activities_series_id = this.CreateDataSeries(series_description)
    println("activities_series_id:" + activities_series_id)
    var querySQL = "INSERT INTO activities (" +
      "activities_series_id," +
      "data," +
      "sdf2d) " +
      "VALUES " +
      "(?,?,?)"
    var insertStatement = sqlConnection.prepareStatement(querySQL)
    for (act <- activities) {
      //println(Json.toJson(act).toString)
      insertStatement.setInt(1, activities_series_id)
      insertStatement.setString(2, Json.toJson(act).toString)
      insertStatement.setString(3, act("sdf"))
      insertStatement.execute()
    }
    activities_series_id
  }

  type activityType = (Option[Int], Option[Int], String, Option[String], Option[String])

  def CompleteDataSeries(activities_series_id: Int, mapping: Map[String, (String, String)]) = {

    def CompleteDataSeriesFieldsFunc(activities_series_id: Int, f: activityType => Int) = {
      //println("Completing data series" + f)
      database_eTOXOPS.db withDynSession {
        this.GetActivitiesForDataSeriesRAW(activities_series_id) foreach {
          case a => f(a)
        }
      }
    }

    //    def UpdateFieldSmiles(activity: activityType) = {
    //      val smi = es.imim.phi.collector.compounds.CompoundUtil.getSmilesFromSDF(activity._4.get)
    //      val q = for { act <- database_eTOXOPS.activities if act.activities_id === activity._1 } yield act.smiles
    //      q.update(Some(smi))
    //    }
    //
    //    def UpdateFieldInchi(activity: activityType) = {
    //      val (inchi, inchikey) = es.imim.phi.collector.compounds.CompoundUtil.getInChIFromSMILES(activity._5.getOrElse(""))
    //      val q = for { act <- database_eTOXOPS.activities if act.activities_id === activity._1 } yield (act.inchi, act.inchikey)
    //      q.update(Some(inchi), Some(inchikey))
    //    }

    def UpdateActivityField(field: String)(activity: activityType) =
      {
        val act = Json.parse(activity._3)
        val activity_value = act \ field
        val actjs = activity_value.asInstanceOf[JsString]
        val q = for { act <- database_eTOXOPS.activities if act.activities_id === activity._1 } yield act.standard_value
        q.update(Some(java.lang.Float.parseFloat(actjs.value.toString())))
      }

    def UpdateUnitsField(field: String)(activity: activityType) =
      {
        val act = Json.parse(activity._3)
        val activity_value = act \ field
        val actjs = activity_value.asInstanceOf[JsString]
        val q = for { act <- database_eTOXOPS.activities if act.activities_id === activity._1 } yield act.standard_units
        q.update(Some(actjs.value.toString()))
      }

    def UpdateUnitsValue(value: String)(activity: activityType) =
      {
        val q = for { act <- database_eTOXOPS.activities if act.activities_id === activity._1 } yield act.standard_units
        q.update(Some(value))
      }

    def UpdateActivityTypeField(field: String)(activity: activityType) =
      {
        val act = Json.parse(activity._3)
        val activity_value = act \ field
        val actjs = activity_value.asInstanceOf[JsString]
        val q = for { act <- database_eTOXOPS.activities if act.activities_id === activity._1 } yield act.activity_type
        q.update(Some(actjs.value.toString()))
      }

    def UpdateActivityTypeValue(value: String)(activity: activityType) =
      {
        val q = for { act <- database_eTOXOPS.activities if act.activities_id === activity._1 } yield act.activity_type
        q.update(Some(value))
      }

    println("Completing data series")

    println("Updating smiles")
    //CompleteDataSeriesFieldsFunc(activities_series_id, UpdateFieldSmiles _)
    println("Updating Inchi")
    // CompleteDataSeriesFieldsFunc(activities_series_id, UpdateFieldInchi _)
    println("Updating activity")
    mapping("activity_field") match {
      case ("field", field) => CompleteDataSeriesFieldsFunc(activities_series_id, UpdateActivityField(field) _)
      case _                => Application.logger.error("Activity field not specified")
    }

    println("Updating activity units ")
    mapping("activity_units") match {
      case ("field", field) => CompleteDataSeriesFieldsFunc(activities_series_id, UpdateUnitsField(field) _)
      case ("value", value) => CompleteDataSeriesFieldsFunc(activities_series_id, UpdateUnitsValue(value) _)
      case _                => Application.logger.error("Activity units field or value not specified")
    }

    println("Updating activity type")
    mapping("activity_type") match {
      case ("field", field) => CompleteDataSeriesFieldsFunc(activities_series_id, UpdateActivityTypeField(field) _)
      case ("value", value) => CompleteDataSeriesFieldsFunc(activities_series_id, UpdateActivityTypeValue(value) _)
      case _                => Application.logger.error("Activity units field or value not specified")
    }
  }

  def UpdateStatisticsDataSeries(activities_series_id: Int) = {
    database_eTOXOPS.db withDynSession {
      val q = for { serie <- database_eTOXOPS.activities.filter(_.activities_series_id === activities_series_id) } yield serie.*
      val l = q.list.length
      println("Count: " + l)
      val q3 = for { serie <- database_eTOXOPS.activities.filter(_.activities_series_id === activities_series_id) } yield serie.smiles
      val qd = scala.slick.lifted.Query(q3.countDistinct).first
      val q2 = for { serie <- database_eTOXOPS.activities_series.filter(_.activities_series_id === activities_series_id) } yield (serie.activities_count, serie.compounds_count)
      q2.update(l, qd)
      println(qd)

    }

  }

  def GetActivitiesForDataSeriesRAW(activities_series_id: Int) = {
    for { act <- database_eTOXOPS.activities if act.activities_series_id === activities_series_id }
      yield (act.activities_id, act.activities_series_id, act.data, act.sdf2d, act.smiles)
  }

  def GetActivitiesForDataSeries(activities_series_id: Int) = {
    for { act <- database_eTOXOPS.activities if act.activities_series_id === activities_series_id }
      yield (act.*)
  }
  // fin v1.3
  def deleteDataSeries(data_series_id: Int) = {
    var deleteq = database_eTOXOPS.activities_series.where(_.activities_series_id === data_series_id)
    database_eTOXOPS.db withDynSession { deleteq.delete }

  }

  def deleteDataSeries(data_series_description: String) = {
    var deleteq = database_eTOXOPS.activities_series.where(_.activities_series_description === data_series_description)
    database_eTOXOPS.db withDynSession { deleteq.delete }

  }

  def jobDelete(job_id: Int) = {
    Logger.info("Action delete job")
    Logger.info("Job id: " + job_id)
    var deleteq = database_eTOXOPS.job.where(_.job_id === job_id)
    //println(deleteq.selectStatement)
    database_eTOXOPS.db withDynSession { deleteq.delete }
  }

  def jobExecutionDelete(job_execution_id: Int) = {
    Logger.info("Action delete job execution: " + job_execution_id)
    val deleteq = database_eTOXOPS.job_execution.where(_.job_execution_id === job_execution_id)
    //println(deleteq.selectStatement)
    Logger.debug(deleteq.deleteStatement)
    database_eTOXOPS.db withDynSession { deleteq.delete }
  }

  def jobExecutionsForJobDelete(job_id: Int) = {
    Logger.info("Action delete job executions for job_id: " + job_id)
    database_eTOXOPS.db withDynSession {
      for (je <- this.GetJobExecutions) {
        val jid = je._3
        val deleteq = database_eTOXOPS.job_execution.where(_.job_execution_id === jid)
        Logger.debug(deleteq.deleteStatement)
        deleteq.delete
      }
    }
  }

} 



