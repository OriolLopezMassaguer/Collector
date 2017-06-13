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

//  def getProteinTest(q: String) = {
//    import uk.ac.ebi.kraken.interfaces.common.Value;
//    import uk.ac.ebi.kraken.interfaces.uniprot._
//    import uk.ac.ebi.kraken.interfaces.uniprot.citationsNew.Citation;
//    import uk.ac.ebi.kraken.interfaces.uniprot.comments.Comment;
//    import uk.ac.ebi.kraken.interfaces.uniprot.comments.CommentType;
//    import uk.ac.ebi.kraken.interfaces.uniprot.dbx.go.Go;
//    import uk.ac.ebi.kraken.interfaces.uniprot.description.Field;
//    import uk.ac.ebi.kraken.interfaces.uniprot.description.FieldType;
//    import uk.ac.ebi.kraken.interfaces.uniprot.description.Name;
//    import uk.ac.ebi.kraken.interfaces.uniprot.description.Section;
//    import uk.ac.ebi.kraken.interfaces.uniprot.features.Feature;
//    import uk.ac.ebi.kraken.interfaces.uniprot.features.FeatureLocation;
//    import uk.ac.ebi.kraken.interfaces.uniprot.features.FeatureType;
//
//    import uk.ac.ebi.uniprot.dataservice.client.Client;
//    import uk.ac.ebi.uniprot.dataservice.client.QueryResult;
//    import uk.ac.ebi.uniprot.dataservice.client.ServiceFactory;
//    import uk.ac.ebi.uniprot.dataservice.client.exception.ServiceException;
//    import uk.ac.ebi.uniprot.dataservice.client.uniprot.QuerySpec;
//    import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtField;
//    import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtQueryBuilder;
//    import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtService;
//    import uk.ac.ebi.uniprot.dataservice.query.Query;
//
//    val serviceFactoryInstance = Client.getServiceFactoryInstance()
//    val uniProtService = serviceFactoryInstance.getUniProtQueryService()
//    uniProtService.start()
//
//    def executeQuery(q: Query) = {
//      val limit = 10
//      var i = 0
//      val searchResult = uniProtService.getEntries(q, null)
//      var entries = Map[String, List[String]]()
//
//      while (searchResult.hasNext() && (i < limit)) {
//        i += 1
//        val entry = searchResult.next()
//        val accession = entry.getPrimaryUniProtAccession().getValue();
//
//        val values = List(
//          entry.getUniProtId().getValue,
//          entry.getProteinDescription.getRecommendedName.toString(),
//          entry.getProteinDescription.getRecommendedName.getFields.toString())
//
//        println(entry.getUniProtId().getValue)
//        for (o <- entry.getProteinDescription.getRecommendedName.getFields.toArray()) {
//          val e = o.asInstanceOf[Field]
//          println(e.getType)
//          println(e.getValue)
//        }
//
//        entries += (accession -> values)
//      }
//
//      entries
//
//    }
//
//    val query = UniProtQueryBuilder.proteinName(q)
//    val query2 = UniProtQueryBuilder.accession(q)
//
//    val j1 = Json.toJson(executeQuery(query))
//    val j2 = Json.toJson(executeQuery(query2))
//
//    val js = JsArray(List(JsObject(List(("query", JsString("q1")), ("res", j1))), JsObject(List(("query", JsString("q2")), ("res", j2)))))
//
//    js
//  }
  def getProtocolsForString(page: Int, start: Int, limit: Int, protocol_string: String) = Action {
    Logger.info("Action get Protocols for string: " + protocol_string)
    val protocols = database_eTOXOPS.GetFilteringProtocolForString(protocol_string: String)
    Logger.info("protocols obtained \n" + protocols)
    database_eTOXOPS.db withDynSession { Ok(protocols) }
  }

  def getProteinByText_list(protein_string: String) = {
    val url = "http://alpha.openphacts.org:8839/search?"
    val r = es.imim.phi.collector.engine.ExtractionEngine.opsAPI.makeCall(url, Map("query" -> protein_string))
    //println(r)

    import play.api.Play.current
    import play.api.libs.ws._
    import play.api.libs.ws.ning.NingAsyncHttpClientConfigBuilder
    import scala.concurrent.Future
    import scala.concurrent._
    import scala.concurrent.duration._

    val url2 = "http://alpha.openphacts.org:8839/search?"
    import scala.util.{ Success, Failure }
    val futureResult = WS.url(url2)
      .withQueryString(("query", protein_string))
      .withHeaders("accept" -> "application/json")
      .get().map(response => Json.parse(response.body))

    val r2 = Await.result(futureResult, 100 seconds)
    val hits = (r2 \ "hits" \ "hits").as[JsArray]
    val r3 = for (hit <- hits.value) yield {
      val hito = hit.as[JsObject]
      for ((field, value) <- hito.fieldSet) println(field + "#" + value)
      val id = (hito \ "_id").as[JsString].value
      val type_hit = (hito \ "_source" \ "@type").as[JsArray].value(0).as[JsString].value
      val label = (hito \ "_source" \ "label").as[JsArray].value(0).as[JsString].value
      val organism = (hito \ "_source" \ "organism").asOpt[JsArray]
      val org=organism match {
        case Some(org)=>org.value(0).as[JsString].value
        case None => ""
      }
      Map("id" -> id.value, "type" -> type_hit, "label" -> (label + " / "+ org))
    }
    val r4 = r3.filter(m => m("type") == "chembl:Target")
    r4.map(println)
    r4
  }

  def getProteinByText(page: Int, start: Int, limit: Int, protein_string: String) = {
  //def getProteinByText(protein_string: String) = {
    Action { request =>
      val l = this.getProteinByText_list(protein_string)
      val s = "{\"success\": true,\"total\": " + l.size + " , \"targets\":" + Json.toJson(l) + " }"
      Ok(s)
    }
  }

  def getFileResultFromFile(localfilename: String, filename: String) = {
    Logger.info("Result!")
    val file = new java.io.File(localfilename)
    val fileContent: Enumerator[Array[Byte]] = Enumerator.fromFile(file)
    Logger.info("Result!")
    Result(
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
      
      for (v <- params)
      {
        println(v)
      }
      database_eTOXOPS.CreateJob(params("job_description")(0), params("id")(0), params("target_label")(0), params("protocol_id")(0))
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
    Thread.sleep(3000)
    val filterparameters = parseJsonFilters(filter)
    Logger.info("Action get job execution info job execution id:" + filterparameters("job_execution_id"))

    val js = db2JSON.getJobDataDetailedJSON(filterparameters("job_execution_id").toInt, limit, start, filtered)
    Ok(js)
  }

  def jobdataexport(job_execution_id: Int, filtered: Boolean, agregated: Boolean, format: String, activityType: String) = Action {
    Logger.info("Action Download job execution info job execution id:" + job_execution_id)
    Logger.info(if (filtered) "Filtered" else "Not Filtered")
    Logger.info(if (agregated) "Compounds" else "Activities")
    Logger.info(format)
    Logger.info("Activity Type: '" + activityType + "'")
    val file = if (activityType == "All") {
      Logger.info("All case");
      exportData.exportexecutiondata(job_execution_id, filtered, agregated, format, None)
    } else {
      Logger.info("other case");
      exportData.exportexecutiondata(job_execution_id, filtered, agregated, format, Some(activityType))
    }
    getFileResultFromFile(file._1, file._2)
  }

  def jobExecAsync(job_id: Int) = Action {
    val res: Future[String] = scala.concurrent.Future { ExtractionEngine.executejob(job_id) }
    val futureResult: Future[Result] = res.map { resst => Ok("PI value computed: " + resst) }
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
    var deleteq = database_eTOXOPS.job.filter(_.job_id === job_id)
    Logger.debug(deleteq.deleteStatement)
    database_eTOXOPS.db withDynSession { deleteq.delete }
    Ok("{success: true}")
  }

  def jobExecutionDelete(job_execution_id: Int) = Action {
    Logger.info("Action delete job execution: " + job_execution_id)
    val deleteq = database_eTOXOPS.job_execution.filter(_.job_execution_id === job_execution_id)
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

      val js = database_eTOXOPS.GetStatisticsForJobExecutionIdJSON(filterparameters("job_execution_id").toInt)
      Ok(js)
      //Ok("")
    }
  }
  
  def getJobStatisticsHistogram(page: Int, start: Int, limit: Int, filter: String) = Action {
    Thread.sleep(2000)
    Logger.info("Action job execution histogram statistics")
    val filterparameters = parseJsonFilters(filter)
    val job_execution_id = filterparameters("job_execution_id")
    Logger.info("Job execution id:" + job_execution_id)
    database_eTOXOPS.db withDynSession {
      //val l = database_eTOXOPS.GetJobExecutionDataForHistogram(job_execution_id.toInt)
      val json = database_eTOXOPS.GetJobExecutionDataForHistogram_JSON(job_execution_id.toInt)
      //Logger.info("JSON Histogram Statistics:")
      //Logger.info("{success: true,total: " + l2.size + ", jobstatistics:" + Json.toJson(l) + " }")
      Ok(json)
    }
  }

  def getJobStatisticsType(page: Int, start: Int, limit: Int, filter: String) = Action {
    val filterparameters = parseJsonFilters(filter)

    Logger.info("Action job statistics type: ")
    Logger.info("job execution id: " + filterparameters("job_execution_id"))

    val jeid = filterparameters("job_execution_id")
    println("JSType")
    database_eTOXOPS.db withDynSession {
      //val l = database_eTOXOPS.GetStatisticsByTypeForJobExecutionIdJSON(filterparameters("job_execution_id").toInt)
      val l = database_eTOXOPS.GetStatisticsByTypeForJobExecutionIdJSON(page)
      //Ok("{success: true, total: " + l.size + ",jobstatistics:" + js + "}")
      println("L: " + l)
      val query =
        " select activity_type ,count(*) activities_count " +
          " from job_data_raw_vw where job_execution_id=" + jeid +
          " group by activity_type" +
          " order by activity_type"
      val js = db2JSON.getQueryJSONAll(query, "statsbytype")

      Ok("{success: true, total: " + l.size + "," + js + "}")
    }
  }


  def testCDK = Action {
    val comp = es.imim.phi.collector.compounds.CompoundUtil
    val sdf = comp.getSDFFromSMiles_CDK("CCCHC")
    Ok(sdf)
  }

  def testRDKit = Action {
    val comp = es.imim.phi.collector.compounds.CompoundUtil
    val sdf = comp.getSDFFromSMiles_RDKit("CCCC")
    Ok(sdf)
  }

  def getFilters(page: Int, start: Int, limit: Int, filter_string: String) = Action {
    Logger.info("Action get Filters")
    database_eTOXOPS.db withDynSession {
      Ok(database_eTOXOPS.getFiltersForString(filter_string: String))
    }
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

