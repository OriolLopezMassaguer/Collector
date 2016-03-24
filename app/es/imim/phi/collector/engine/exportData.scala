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

package es.imim.phi.collector.engine
import es.imim.phi.collector.compounds._
import java.io.PrintStream
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession
import play.Logger
import es.imim.phi.collector.model.database_eTOXOPS
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.libs.json.JsArray

object exportData {

  var sqlConnection = database_eTOXOPS.sqlConnection

  def queryExport(job_execution_id: Int, filtered: Boolean, agregated: Boolean) = {
    database_eTOXOPS.db withDynSession {
      (filtered, agregated) match {
        case (false, false) => database_eTOXOPS.GetRAWDataJobExecution(job_execution_id).selectStatement
        case (true, false) => database_eTOXOPS.GetFilteredDataJobExecution(job_execution_id).selectStatement
        case (false, true) => database_eTOXOPS.GetRAWDataAgJobExecution(job_execution_id).selectStatement
        case (true, true) => database_eTOXOPS.GetFilteredDataAgJobExecution(job_execution_id).selectStatement
      }
    }
  }
  def exportexecutiondata_filename(job_execution_id: Int, filtered: Boolean, agregated: Boolean, format: String, filename: String) = {
    format match {
      case "sdf" => exportexecutiondata2SDF_fileName(job_execution_id, filtered, agregated, filename)
      case "csv" => exportexecutiondata2CSV_fileName(job_execution_id, filtered, agregated, filename)
    }
  }

  def exportexecutiondata(job_execution_id: Int, filtered: Boolean, agregated: Boolean, format: String) = {
    format match {
      case "sdf" => exportexecutiondata2SDF(job_execution_id, filtered, agregated)
      case "csv" => exportexecutiondata2CSV(job_execution_id, filtered, agregated)
    }
  }

  def fileNamePrefix(job_execution_id: Int, filtered: Boolean, agregated: Boolean) = {
    var filteredStr = if (filtered) "filtered" else "raw"
    var agregatedStr = if (agregated) "compounds" else "activities"
    var fileNamePrefix = "Job_" + job_execution_id + "_" + agregatedStr + "_" + filteredStr
    fileNamePrefix
  }

  def exportexecutiondata2SDF_fileName(job_execution_id: Int, filtered: Boolean, agregated: Boolean, fileName: String) = {
    var query = queryExport(job_execution_id, filtered, agregated)
    var out = new PrintStream(fileName)
    var resultSet = database_eTOXOPS.doQuerySQL(query)
    while (resultSet.next) {
      val st = if (agregated) {
        val r = database_eTOXOPS.db withDynSession {
          val l = database_eTOXOPS.GetSDFforJobExecutionCSID(job_execution_id, resultSet.getString("cs_id"))
          l.list
        }
        val h = r.head
        h
      } else {
        val jobdatarawid = resultSet.getString("job_data_raw_id")
        val s = database_eTOXOPS.db withDynSession {
          val l = database_eTOXOPS.GetSDFforJobDataRawId(jobdatarawid.toInt)
          l.list
        }
        s.head
      }
      if (st.isDefined) {
        var sdf = st.get
        var strSDF = database_eTOXOPS.doQuerySQL2TextSDF(resultSet, null)
        out.println(sdf.replaceAll("\\$\\$\\$\\$", "") + strSDF + "$$$$")
      }
    }
    out.close()
    var s = fileName.toList.reverse.takeWhile(c => (c != '/'))
    //println(s.reverse.toList.mkString)
    (fileName, s.reverse.toList.mkString)

  }

//  def GenerateSDF(data:JsValue):String ={
//    println("Generating SDF...")
//    val dataArray = data.asInstanceOf[JsArray]
//    println(dataArray)
//    ""    
//  }
      
//  def exportdataseries2SDF(series_id: String, filename: String) = {
//    var out = new PrintStream(filename)  
//    println("to export " + filename)
//    database_eTOXOPS.db withSession {
//      val mp= database_eTOXOPS.GetActivitiesForDataSeriesRAW(series_id.toInt).to[List]
//      for (act <- mp)
//        yield({
//          val js=Json.parse(act._3)
//          val sd= (js \ "sdf").asInstanceOf[JsString]
//          out.println(sd.value)
//          GenerateSDF(js)
//        })
//    }   	
//    out.close()
//  }
  
  def exportexecutiondata2SDF(job_execution_id: Int, filtered: Boolean, agregated: Boolean) = {
    val filename = FileUtils.getNewFilename(fileNamePrefix(job_execution_id, filtered, agregated), ".sdf", ExtractionEngine.exportDataDir)
    exportexecutiondata2SDF_fileName(job_execution_id, filtered, agregated, filename)
  }

  def exportexecutiondata2CSV_fileName(job_execution_id: Int, filtered: Boolean, agregated: Boolean, fileName: String) = {
    var query = queryExport(job_execution_id, filtered, agregated: Boolean)
    Logger.debug("Query to export:\n" + query)
    var out = new PrintStream(fileName)
    database_eTOXOPS.doQuerySQL2Text(query, out)
    out.close
    var s = fileName.toList.reverse.takeWhile(c => (c != '/'))
    (fileName, s.reverse.toList.mkString)
  }

  def exportexecutiondata2CSV(job_execution_id: Int, filtered: Boolean, agregated: Boolean) = {
    val filename = FileUtils.getNewFilename(fileNamePrefix(job_execution_id, filtered, agregated), ".csv", ExtractionEngine.exportDataDir)
    exportexecutiondata2CSV_fileName(job_execution_id, filtered, agregated, filename)
  }

}
