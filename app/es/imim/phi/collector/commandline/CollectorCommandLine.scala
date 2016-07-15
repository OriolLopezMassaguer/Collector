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

package es.imim.phi.collector.commandline

import play.api.libs.json.Json
import play.api.libs.json.JsArray
import play.api.libs.json.JsObject
import es.imim.phi.collector.ops.lda_api._
import es.imim.phi.collector.engine._
import es.imim.phi.collector.model.database_eTOXOPS
import es.imim.phi.collector.model.db2JSON
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import controllers.Application
//import es.imim.phi.collector.model.CompoundDataSeries
//import es.imim.phi.collector.model.CompoundDataSeriesDB

class ConfigImport {
  var activityfield: String = ""
  var activitytypeIsField: Boolean = true
  var activitytypefield: String = ""
  var activitytypevalue: String = ""
  var activityunitsIsField: Boolean = true
  var activityunitsfield: String = ""
  var activityunitsvalue: String = ""
}

object CollectorCommandLine {
  var api: OPSLDAScala = null

  private def process_matches(matchesString: String) = {
    def objToMap(obj: JsObject) = (for (f <- obj.fields) yield (f._1.toString().replace("\"", "") -> f._2.toString().replace("\"", ""))).toMap
    val mats = for (a <- Json.parse(matchesString).asInstanceOf[JsArray].value) yield (a.asInstanceOf[JsObject])
    for (mat <- mats) yield (objToMap(mat))
  }
  def getSDFForUnitprot(uniprot_accession: String, filename: String,activityType:Option[String]) = {
    val job_id = newJobByUniprotAccession("eTOX-job", uniprot_accession, "3")
    val job_execution_id = executeJob(job_id)
    exportExecutionDataComps(job_execution_id.toString, "sdf", filename, true,activityType)
  }

  def newJob(job_description: String, urltarget: String, target_label: String, protocol_id: String) = {
    val jobid = database_eTOXOPS.CreateJob(job_description, urltarget, target_label, protocol_id)
    println("Job created")
    println("	job description: " + job_description)
    println("	target uri: " + urltarget)
    println("	target_label: " + target_label)
    println("	protocol_id: " + protocol_id)
    println("	job_id: " + jobid)
    jobid
  }

  def newJobByUniprotAccession(job_description: String, uniprot_accession: String, protocol_id: String) = {
    val cwres = api.CW_Search_Protein(uniprot_accession).head
    newJob(job_description, cwres("uri"), cwres("prefLabel"), protocol_id)
  }

  def executeAllJobs = {
    database_eTOXOPS.db withDynSession {
      val jobids = (for (j <- database_eTOXOPS.job) yield (j.job_id)).list
      for (id <- jobids)
        this.executeJob(id.toString())
    }
  }

  def executeJob(job_id: String) = {
    val result = ExtractionEngine.executejob(job_id.toInt)
    val js = Json.parse(result)
    val success = (js \ "success").toString()
    success match {
      case "true" =>
        {
          println("job executed : " + job_id)
          println("job execution : " + js \ "jobexecutionid")
          println("Data extracted")
          this.listjobexecutions(job_id, (js \ "jobexecutionid").toString())
          (js \ "jobexecutionid").toString()
        }
      case "false" =>
        {
          println("job failed execution : " + job_id)
          "0"
        }
    }
  }

  def exportExecutionDataActs(job_execution_id: String, format: String, filename: String, filtered: Boolean,activityType:Option[String]) = {
    println("Exporting job execution")
    println("	job execution id: " + job_execution_id)
    var export = format match {
      case "sdf" => exportData.exportexecutiondata_filename(job_execution_id.toInt, filtered, false, "sdf", filename,activityType)
      case "csv" => exportData.exportexecutiondata_filename(job_execution_id.toInt, filtered, false, "csv", filename,activityType)
    }
  }

  def exportExecutionDataComps(job_execution_id: String, format: String, filename: String, filtered: Boolean,activityType:Option[String]) = {
    println("Exporting job execution")
    println("	job execution id: " + job_execution_id)
    var export = format match {
      case "sdf" => exportData.exportexecutiondata_filename(job_execution_id.toInt, filtered, true, "sdf", filename,activityType)
      case "csv" => exportData.exportexecutiondata_filename(job_execution_id.toInt, filtered, true, "csv", filename,activityType)
    }
    //println(export)
  }

  def listprotocols = {
    println("listprotocols")
    val protocols = db2JSON.getProtocolsAllJSON
    val fields = List("job_filtering_id", "job_filtering_description", "filter_description", "curation_order")
    printjson(protocols, fields, "\t")
  }

  def listfilters = {
    //println("listfilter")
    val filters = db2JSON.getFiltersAllJSON
    val fields = List("filter_id", "filter_description", "filter_class")
    printjson(filters, fields, "\t")
  }

  def listjobs = {
    println("listjobs")
    val jobs = db2JSON.getJobsAllJSON2
    val fields = List("job_id", "job_description", "job_filtering_description", "job_extraction_description")
    printjson(jobs, fields, "\t")
  }

  def listjobexecutions(job_id: String) = {
    val jobexecutions = db2JSON.getJobExecutionsJSON2(job_id.toInt)
    val fields = List("job_description", "job_filtering_description", "job_execution_id", "activities_filtered", "compounds_filtered")
    printjson(jobexecutions, fields, "\t")
  }

  def listjobexecutions(job_id: String, job_execution_id: String) = {
    val jobexecutions = db2JSON.getJobExecutionsJSON2(job_id.toInt, job_execution_id.toInt)
    val fields = List("job_description", "job_filtering_description", "job_execution_id", "activities_filtered", "compounds_filtered")
    printjson(jobexecutions, fields, "\t")
  }

  //  def filterdataseries(data_series_id : Int, protocol_id : Int) = {
  //    CompoundDataSeries.FilterDataSeriesProtocol(data_series_id, protocol_id)
  //  }
  //
  //  def deletedataseries(data_series_id : Int) = {
  //    CompoundDataSeries.deleteSeries(data_series_id)
  //  }

  def printjson(json: String, fields: List[String], separator: String) = {
    //val fields = List("job_description", "job_filtering_description", "job_execution_id", "activities_filtered", "compounds_filtered")
    println(fields.mkString(separator))
    val protocolslmap = process_matches(json)
    for (e <- protocolslmap) {
      println(fields.map(field => e(field)).mkString(separator))
    }
  }

  def deleteJob(job_id: Int) = {
    database_eTOXOPS.jobDelete(job_id)
  }

  def deleteJobExecution(job_id: Int) = {
    database_eTOXOPS.jobExecutionDelete(job_id)
  }

  def deleteJobExecutions(job_id: Int) = {
    database_eTOXOPS.jobExecutionsForJobDelete(job_id)
  }

  def main(args: Array[String]): Unit = {

    //var logger = LoggerFactory.getLogger((classOf[CollectorCommandLine2].getName()))

    //println("Load RDKit lib")
    //System.load("/opt/collector/lib/libGraphMolWrap.so")
    ExtractionEngine.initEngine(System.getenv("COLLECTOR_HOME"))

    api = new OPSLDAScala(
      ExtractionEngine.OPSAPIURL,
      ExtractionEngine.appKey,
      ExtractionEngine.appId,
      true,
      ExtractionEngine.dbURL,
      ExtractionEngine.dbUser,
      ExtractionEngine.dbPassword,
      false)
    val cfg = CommandLineParser.parser.parse(args, es.imim.phi.collector.commandline.ConfigCollector())
    //println("Configuration: " + cfg)
    //args.map(println)

    cfg match {
      case Some(cf) =>
        {
          cf.mode match {

            case "newjobuniprotid" => newJobByUniprotAccession(cf.jobdescription, cf.uniprotid, cf.protocolid.toString())
            case "executealljobs" => executeAllJobs
            case "executejob" => {
              database_eTOXOPS.db withDynSession {
                val l = database_eTOXOPS.GetJobIdForJobDescription(cf.jobdescription.toString()).list
                val jobid = if (l.isEmpty)
                  cf.jobid.toString()
                else
                  l.head.toString()

                println("Executing: " + jobid)

                if (jobid == "-1")
                  println("Job not found")
                else
                  executeJob(jobid)
              }

            }
            case "export" =>
              {

                val job_execution_id =
                  database_eTOXOPS.db withDynSession {
                    if (cf.jobexecutionid == -1)
                      database_eTOXOPS.GetJobExecutionIdForJobDescription(cf.jobdescription.toString())
                    else
                      cf.jobexecutionid
                  }
                println("Job execution id: " + job_execution_id)

                val actcomps = cf.datatoexport
                val sdfcsv = cf.exportformat
                val filename = cf.filename
                actcomps match {
                  case "activities" => exportExecutionDataActs(job_execution_id.toString, sdfcsv, filename, cf.filtered,None)
                  case "compounds" => exportExecutionDataComps(job_execution_id.toString, sdfcsv, filename, cf.filtered,None)
                }
              }

            case "numactivitiesjob" => {
              database_eTOXOPS.db withDynSession {
                val l = database_eTOXOPS.GetJobIdForJobDescription(cf.jobdescription.toString()).list
                val jobid = if (l.isEmpty)
                  cf.jobid.toString()
                else
                  l.head.toString()

                if (jobid == "-1")
                  println("Job not found")
                else {
                  val target_cwiki = ExtractionEngine.getJobParameterValues(jobid.toInt)("target_cwiki")
                  val numActivities = ExtractionEngine.opsAPI.GetPharmacologyByTargetLDA_count(target_cwiki)
                  println(cf.jobdescription.toString() + " Activities: " + numActivities)
                }
              }
            }
            case "listprotocols" => listprotocols
            case "listfilters" => listfilters
            case "listjobs" => listjobs
            case "listjobexecutions" => listjobexecutions(cf.jobid.toString)
            //case "filterdataseries"            => filterdataseries(cf.dataseriesid, cf.protocolid.toInt)
            //case "deletedataseries"            => deletedataseries(cf.dataseriesid)
            case "deletejob" => deleteJob(cf.jobid)
            case "deletejobexecution" => deleteJobExecution(cf.jobexecutionid)
            case "deletejobexecutionsforjobid" => deleteJobExecutions(cf.jobid)
            case "getsdffromuniprotid" => this.getSDFForUnitprot(cf.uniprotid, cf.filename,None)
            case _ => CommandLineParser.parser.showUsage
          }
        }
      case None => CommandLineParser.parser.showUsage
    }

  }

}

