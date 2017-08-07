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

import scala.slick.driver.PostgresDriver.simple._
import scala.collection.mutable.ArrayBuffer
import java.sql.ResultSet
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession
import play.Logger

object db2JSON {

  var db = database_eTOXOPS.db
  var sqlConnection = database_eTOXOPS.sqlConnection

  def getQueryJSONPaging(query: String, jsonlabel: String, limit: String, offset: String) = {
    //Logger.info("getQueryJSONPaging: \n" + query)
    getQueryJSONBase(query, jsonlabel, " limit " + limit, " offset " + offset)
  }

  def getQueryJSONAll(query: String, jsonlabel: String) = {
    getQueryJSONBase(query, jsonlabel, "", "")

  }

  def getString(field: String, r: ResultSet, i: Int) = {

    if (field == "standard_value") {
      //println(field, i)
      //println(r.getFloat(i))
      r.getFloat(i).toString
    } else
      r.getString(i)
  }

  private def getQueryJSONBase(query: String, jsonlabel: String, limit: String, offset: String) = {
    //db withDynSession {      
      var resultSet = database_eTOXOPS.doQuerySQL(query + " " + limit + " " + offset)
      Logger.debug(query + " " + limit + " " + offset)
      var metadata = resultSet.getMetaData()

      var i = 1
      var js = ""
      var lines = ArrayBuffer[String]()

      while (resultSet.next) {
        var jsline = ""
        for (i <- 1 to metadata.getColumnCount()) {
          if (i == metadata.getColumnCount())
            jsline = jsline + "\"" + metadata.getColumnLabel(i) + "\":\"" + getString(metadata.getColumnLabel(i), resultSet, i) + "\""
          else
            jsline = jsline + "\"" + metadata.getColumnLabel(i) + "\":\"" + getString(metadata.getColumnLabel(i), resultSet, i) + "\","
        }
        jsline = "\n {" + jsline + "}"
        lines += jsline
      }

      var json: java.lang.StringBuffer = new StringBuffer();

      for (i <- 0 to lines.size - 1) {
        if (i == lines.size - 1)
          json.append(lines(i))
        else
          json.append(lines(i) + ",")
      }
      var statement = sqlConnection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)
      statement.execute(query)
      var resultSetCount = statement.getResultSet()
      resultSetCount.last()
      val v=resultSetCount.getRow().toString()
      resultSetCount.close()
      "{success: true,total: " + v + " , " + jsonlabel + ":[" + json + "] }"
    //}
  }

  private def getQueryJSONBase2(query: String, jsonlabel: String, limit: String, offset: String) = {
    //db withDynSession {
      var resultSet = database_eTOXOPS.doQuerySQL(query + " " + limit + " " + offset)
      var metadata = resultSet.getMetaData()

      var i = 1
      var js = ""
      var lines = ArrayBuffer[String]()

      while (resultSet.next) {
        var jsline = ""
        for (i <- 1 to metadata.getColumnCount()) {
          if (i == metadata.getColumnCount())
            jsline = jsline + "\"" + metadata.getColumnLabel(i) + "\":\"" + getString(metadata.getColumnLabel(i), resultSet, i) + "\""
          else
            jsline = jsline + "\"" + metadata.getColumnLabel(i) + "\":\"" + getString(metadata.getColumnLabel(i), resultSet, i) + "\","
        }
        jsline = "\n {" + jsline + "}"
        lines += jsline
      }

      var json: java.lang.StringBuffer = new StringBuffer();

      for (i <- 0 to lines.size - 1) {
        if (i == lines.size - 1)
          json.append(lines(i))
        else
          json.append(lines(i) + ",")
      }
      resultSet.close()
      "[" + json + "]"
    //}
  }

  def getJobsAllJSON = {
    db withDynSession {
      getQueryJSONAll(
        "SELECT t1.job_filtering_id,t1.job_description,t1.job_id,t2.job_filtering_id,t2.job_filtering_description," +
          "t3.job_extraction_id,t3.job_extraction_description " +
          "FROM job_extraction t3,job t1,job_filtering t2 " +
          "where t1.job_filtering_id = t2.job_filtering_id and t1.job_extraction_id = t3.job_extraction_id" +
          " order by job_description", "jobs")
    }
  }

  def getJobsAllJSON2 = {
    db withDynSession {
      val q = "SELECT " +
        //"t1.job_filtering_id," +
        "t1.job_description," +
        "t1.job_id," +
        "t2.job_filtering_id," +
        "t2.job_filtering_description," +
        "t3.job_extraction_id," +
        "t3.job_extraction_description " +
        "FROM job_extraction t3,job t1,job_filtering t2 " +
        "where t1.job_filtering_id = t2.job_filtering_id and t1.job_extraction_id = t3.job_extraction_id" +
        " order by job_id"
      val r = getQueryJSONBase2(q, "", "", "")
      r
    }
  }

  def getDataSeries = {
    db withDynSession {
      val q = "SELECT activities_series_id, activities_series_description, activities_count, compounds_count " +
        "  FROM activities_series order by activities_series_id;"
      val r = getQueryJSONBase2(q, "", "", "")
      r
    }
  }

  def getJobExecutionsJSON = {
    db withDynSession {
      getQueryJSONAll("SELECT * FROM job_execution_vw_mater order by status_date", "jobsexecutions")
    }
  }

  def getJobExecutionsJSON(job_id: Int) = {
    db withDynSession {
      getQueryJSONAll("SELECT * FROM job_execution_vw_mater where job_id=" + job_id + " order by status_date", "jobsexecutions")
    }
  }

  def getJobExecutionsJSON2(job_id: Int, job_execution_id: Int) = {
    db withDynSession {
      val q = "SELECT * FROM job_execution_vw_mater where job_execution_id=" + job_execution_id + " AND job_id= " + job_id + " order by status_date"
      val r = getQueryJSONBase2(q, "", "", "")
      r
    }
  }

  def getJobExecutionsJSON2(job_id: Int) = {
    db withDynSession {
      val q = "SELECT * FROM job_execution_vw_mater where job_id=" + job_id + " order by status_date"
      val r = getQueryJSONBase2(q, "", "", "")
      r
    }
  }

  def getJobDataDetailedJSON(job_execution_id: Int, limit: Int, offset: Int, filtered: Boolean) = {
    db withDynSession {
      filtered match {
        case false =>
          getQueryJSONPaging("SELECT * from job_data_raw_vw where job_execution_id=" + job_execution_id + " order by job_data_raw_id",
            "jobdatadetailed", limit.toString(), offset.toString())
        case true =>
          getQueryJSONPaging("SELECT * from job_data_filtered_vw_mater where job_execution_id=" + job_execution_id + " order by job_data_raw_id",
            "jobdatadetailed", limit.toString(), offset.toString())
      }

    }
  }

  def getProtocolsAllJSON = {
    db withDynSession {
      val q = database_eTOXOPS.GetFilteringProtocols.sortBy(_._1).selectStatement
      //println(q)
      val r = getQueryJSONBase2(q, "", "", "")
      r
    }
  }

  def getFiltersAllJSON = {
    db withDynSession {
      val q1 = for (f <- database_eTOXOPS.filter) yield f.*
      val q = q1.sortBy(_._1).selectStatement
      val r = getQueryJSONBase2(q, "", "", "")
      r
    }
  }

}
