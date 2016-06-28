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

package es.imim.phi.collector.ops.lda_api

//import collection.JavaConversions._
import java.net.URLEncoder

import java.net.URL
import java.net.HttpURLConnection
import scala.collection.mutable.LinkedList
import play.api.libs.json.JsArray
import play.api.libs.json.JsObject
import org.apache.commons.io.IOUtils
import java.io.StringWriter
import java.sql.Types._
import play.api.libs.json.Json
import play.api.libs.json.JsString
import play.api.libs.json.JsValue
import play.api.libs.json.JsUndefined
import play.api.libs.json.JsNumber
import play.Logger
import java.io.PrintStream
import scalaz.Memo
import java.sql.DriverManager
import scala.Array.canBuildFrom
import es.imim.phi.collector.compounds._
import es.imim.phi.collector.model._

object CHEMBLAPI {

  def GetCompoundInfo(id: String) = {
    println("GetCompoundInfo:" + id)
    val l = id.split("/")
    val ll = l.toList
    val id2 = ll.reverse.head
    val url = "https://www.ebi.ac.uk/chemblws/compounds/" + id2 + ".json"
    println(url)
    val r = es.imim.phi.collector.engine.ExtractionEngine.opsAPI.urlCall(url)
    //println("Response: " + r)
    if (r != "") {
      val json = Json.parse(r)
      val v = (json \ "compound" \ "smiles")
      v.asOpt[String].getOrElse("").toString
    } else
      ""
  }

  def test = {
    val smiles = this.GetCompoundInfo("http://rdf.ebi.ac.uk/resource/chembl/molecule/CHEMBL2074638")
    println(smiles)
  }
}

class OPSLDAScala(coreAPIURL: String, appKey: String, appId: String, threescale: Boolean, connURL: String, user: String, password: String, cachedapi: Boolean) {

  private def dbMemo: Memo[String, String] = {
    scalaz.Memo.memo[String, String](f =>
      k => {
        var conncachedb = DriverManager.getConnection(connURL, user, password)
        val q = "select * from ops_api_cached_calls where call='" + k + "'"
        val rs = conncachedb.createStatement().executeQuery(q)
        val v = if (rs.next())
          rs.getString(2)
        else {
          val r = try {
            val v2 = f(k)
            //println("post f")
            //println("R: " + v2)
            var insertStatement = conncachedb.prepareStatement("insert into ops_api_cached_calls (call,response,timestamp) values (?,?,?);")
            insertStatement.setString(1, k)
            insertStatement.setString(2, v2)
            val today = new java.util.Date()
            val ts = new java.sql.Timestamp(today.getTime())
            insertStatement.setTimestamp(3, ts)
            //println("To execute")
            insertStatement.execute()
            //println("Execution!")
            v2
          } catch {
            case _: Throwable => {
              val q = "select * from ops_api_cached_calls where call='" + k + "'"
              val rs = conncachedb.createStatement().executeQuery(q)
              rs.getString(2)
            }
          }
          r
        }
        conncachedb.close()
        v
      })
  }

  private def extraparams(threescale: Boolean) =
    if (threescale)
      Map("app_id" -> appId, "app_key" -> appKey, "_format" -> "json")
    else
      Map("_format" -> "json")

  private def buildURL(callPart: String, threescale: Boolean) =
    {
      if (!threescale) {
        val url = "http://api.openphacts.org" + callPart
        Logger.debug("buildURL: " + url)
        url

      } else {
        val url = coreAPIURL + callPart
        Logger.debug("buildURL: " + url)
        url
      }
    }

  private val memoizedurlcall: String => String = this.dbMemo(urlcall _)

  private def urlcall(call: String): String =
    {
      Logger.debug("urlcall Call :[ " + call + " ]")
      val url = new URL(call);
      val conn = url.openConnection().asInstanceOf[HttpURLConnection]
      conn.setInstanceFollowRedirects(true)
      conn.setDoInput(true)
      conn.setRequestMethod("GET")
      conn.connect()
      val responseCode = conn.getResponseCode()
      responseCode match {
        case 404 => {
          println("Empty response")
          ""
        }
        case 200 => {
          val is = conn.getInputStream()
          //val writer = new StringWriter()
          //IOUtils.copy(is, writer, "UTF-8")
          //var str = writer.toString()         
          //writer.close()
          val inr = new java.io.InputStreamReader(is)
          val str = com.google.common.io.CharStreams.toString(inr)

          is.close()

          conn.disconnect()
          str
        }
        case _ => {
          println("Unexpected return: " + conn.getResponseCode())
          conn.disconnect()
          ""
        }
      }
    }

  //  private def makeCall_out(urlCall: String, parameters: Map[String, String]) = {
  //    var params = for ((parameter, value) <- parameters) yield (URLEncoder.encode(parameter, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8"))
  //    var call = urlCall + params.mkString("&")
  //    println("URL: " + call)
  //    urlcall(call)
  //  }

  def urlCall(call: String) = {
    if (cachedapi)
      memoizedurlcall(call)
    else
      urlcall(call)
  }

  private def makeCall(urlCall: String, parameters: Map[String, String], threescale: Boolean): String = {
    var params = for ((parameter, value) <- parameters ++ extraparams(threescale)) yield (URLEncoder.encode(parameter, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8"))
    var call = urlCall + params.mkString("&")
    println("makeCall URL: " + call)
    Logger.info("makeCall URL: " + call)

    this.urlCall(call)
  }

  private def makeAPICall(urlpattern: String, threescale: Boolean, params: Map[String, String]): String = {
    val urlcall = buildURL(urlpattern, threescale)
    //println("URLcall: " + urlcall)
    Logger.info("URLcall: " + urlcall)
    Logger.info("Pattern: " + urlpattern)
    Logger.info("Params: " + params)
    makeCall(urlcall, params, threescale)
  }
  def GetPharmacologyByTargetLDA_count(targetURI: String) = {
    val response = makeAPICall("/target/pharmacology/count?", true, Map("uri" -> targetURI))
    val json = Json.parse(response)
    val results = json \ "result" \ "primaryTopic" \ "targetPharmacologyTotalResults"
    results.asInstanceOf[play.api.libs.json.JsNumber].value.toInt
  }

  def GetPharmacologyByTargetLDA_new(targetURI: String, jobExecutionId: String, c: java.sql.Connection, destinationTable: String) =
    {

      var excludedActivities = 0

      val (numPages, pageSize) = {
        val numActivities = GetPharmacologyByTargetLDA_count(targetURI)
        val pageSize = es.imim.phi.collector.engine.ExtractionEngine.bucketAPISize
        val numPages = (numActivities / pageSize) + 1
        println("Activities: " + numActivities)
        println("Pages: " + numPages)
        (numPages, pageSize)
      }

      val convert = (x: JsValue) => x.asOpt[String].getOrElse("").toString
      val convertInt = (x: JsValue) => x.asOpt[Int].getOrElse("").toString
      val convertDouble = (x: JsValue) => x.asOpt[Double].getOrElse("").toString

      def getMoleculeFields(forMolecule: JsValue) =
        {
          //println("ForMolecules:" + forMolecule)
          //println
          val csdata = (forMolecule \ "exactMatch") match {
            case arrayitems: JsArray => {
              val fields = arrayitems.value.filter(m => convert(m \ "inDataset").equalsIgnoreCase("http://ops.rsc.org"))
              val r: JsValue = if (fields.isEmpty) {
                //println
                //println("Excluded")
                //println(forMolecule)
                excludedActivities = excludedActivities + 1
                play.api.libs.json.JsUndefined("Undef")
              } else fields.head
              r
            }
            case obj: JsObject => obj
            case a => {
              println
              println("Excluded")
              //println(forMolecule)
              excludedActivities = excludedActivities + 1
              a
            }
          }
          //val molEM = (forMolecule \ "exactMatch").asInstanceOf[JsArray].value
          //val csdata = molEM.filter(m => convert(m \ "inDataset").equalsIgnoreCase("http://ops.rsc.org")).head
          //val cwdata = molEM.filter(m => convert(m \ "inDataset").equalsIgnoreCase("http://www.conceptwiki.org")).head
          //println("MW:" + csdata \ "molweight")
          //println
          val molid = convert(forMolecule \ "_about")

          def getSmiles = {
            val smiles = convert(csdata \ "smiles")
            if (smiles == "") CHEMBLAPI.GetCompoundInfo(molid) else smiles
          }

          Map(
            "inchi" -> convert(csdata \ "inchi"),
            "inchikey" -> convert(csdata \ "inchikey"),
            //"smiles" -> convert(csdata \ "smiles"),
            "smiles" -> getSmiles,
            "ro5violations" -> convertInt(csdata \ "ro5_violations"),
            "cs_id" -> convert(csdata \ "_about"),
            "compound_cwiki" -> "",
            "compound_pref_label" -> "",
            "compound_pref_label_en" -> "",
            "molecule_id" -> convert(forMolecule \ "_about"),
            "full_mwt" -> convertDouble(csdata \ "molweight"))
        }

      def getAssayFields(onAssay: JsValue) =
        {
          val target = (onAssay \ "hasTarget").asInstanceOf[JsObject]
          val targetCW = target \ "exactMatch"
          val fieldsmap =
            Map("assay_description" -> convert(onAssay \ "description"),
              "assay_organism" -> convert(onAssay \ "targetOrganismName"),
              "assay_id" -> convert(onAssay \ "_about"),
              "target_id" -> convert(target \ "_about"),
              "target_title" -> convert(target \ "title"),
              "target_organism" -> convert(target \ "targetOrganismName"),
              "target_pref_label" -> convert(targetCW \ "prefLabel"),
              "target_pref_label_en" -> convert(targetCW \ "prefLabel_en"),
              "target_cwiki" -> convert(targetCW \ "_about"))
          fieldsmap
        }

      def extractItems(array: JsArray) = {

        println("Items: " + array.value.size)
        (for (item <- array.value)
          yield ({
          //println("Item: " + item)
          //println
          val molFields = getMoleculeFields(item \ "hasMolecule")
          //println("Molfields:" + molFields)
          //println
          val assaytargetFields = getAssayFields(item \ "hasAssay")
          // println("Assay:" + assaytargetFields)
          //println
          val fieldsroot = Map(
            "activity_id" -> convert(item \ "_about"),
            "pmid" -> convert(item \ "pmid"),
            "relation" -> convert(item \ "activity_relation"),
            //"standard_units" -> convert(item \ "publishedUnits"),
            "standard_units" -> convert(item \ "activity_unit" \ "prefLabel"),
            "standard_value" -> convertDouble(item \ "activity_value"),
            "activity_type" -> convert(item \ "activity_type"),
            "activity_value" -> convertDouble(item \ "activity_value"),
            "targetURI" -> targetURI)
          val f2 = fieldsroot ++ molFields
          val f3 = for (t <- List(assaytargetFields))
            yield (f2 ++ t)
          f3
        })).flatten
      }

      println("GetPharmaByTarget: " + targetURI)

      for (page <- Range(1, numPages + 1)) yield ({
        val response = makeAPICall("/target/pharmacology/pages?", true, Map("uri" -> targetURI, "_pageSize" -> pageSize.toString, "_page" -> page.toString))
        if (response == "") {
          Logger.debug("Activities obtained: 0")
          List()
        } else {
          val json = Json.parse(response)
          val items = json \ "result" \ "items"
          val is = items match {
            case arrayitems: JsArray => extractItems(arrayitems)
            case _ => List()
          }
          Logger.debug("Activities obtained: " + is.size)
          println("Excluded activities: " + excludedActivities)

          //println()
          //val is2 = for (row <- is) yield row
          val is2 = for (row <- is if (row("smiles") != "")) yield row
          println("Page: " + page)
          println("Size: " + is2.size)

          database_eTOXOPS.MoveLDAResults2SQL(is2, jobExecutionId, database_eTOXOPS.sqlConnection, "job_data_raw")
        }
      })
      //r.flatten
    }

  def CW_Search_Protein_RAW(q: String): String = makeAPICall("/search/byTag?", true, Map("uuid" -> "eeaec894-d856-4106-9fa1-662b1dc6c6f1", "q" -> q))

  def CW_Search_Protein(q: String): Seq[Map[String, String]] = CW_process_matches(CW_Search_Protein_RAW(q))

  def CW_process_matches(matchesString: String): LinkedList[Map[String, String]] = {

    def getValues(j: JsValue) = {
      j match {
        case j: JsArray => j
        case _ => new JsArray(List(j))
      }
    }
    var matchesList = LinkedList[Map[String, String]]()
    var js = Json.parse(matchesString)
    val vals = getValues(js \ "result" \ "primaryTopic" \ "result")
    val r = js \ "result" \ "primaryTopic" \ "result"
    if (r.isInstanceOf[JsUndefined])
      LinkedList()
    else {
      var mats = for (a <- vals.value) yield (a.asInstanceOf[JsObject])
      for (mat <- mats) {
        var currentMatch = Map[String, String]()
        var uri = mat \ "_about"
        val altlabels = getValues(mat \ "altLabel")
        val preflabels = getValues(mat \ "prefLabel")
        val urls = (mat \ "exactMatch" \\ "url").map(_.asInstanceOf[JsString].value.toString())
        val l1 = preflabels(0).as[JsString].value

        currentMatch += ("uri" -> uri.as[String])
        currentMatch += ("altLabel" -> altlabels(0).toString())
        currentMatch += ("prefLabel" -> l1)
        currentMatch += ("urls" -> urls.mkString(","))
        matchesList = matchesList :+ currentMatch
      }
      matchesList
    }
  }
}

