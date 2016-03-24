///*   
//     Collector is a tool for obtaining bioactivity data from the Open PHACTS platform.
//     Copyright (C) 2013 UPF
//     Contributed by Manuel Pastor(manuel.pastor@upf.edu) and Oriol L. Massaguer(olopez@imim.es). 
// 
//    This file is part of Collector.
//
//    Collector is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Collector is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with Collector.  If not, see <http://www.gnu.org/licenses/>.
//   
//*/
//
//package es.imim.phi.collector.model
//
//import play.api.Logger
//import es.imim.phi.collector.engine._
//import java.sql.Statement
//import java.io.PrintStream
//import java.sql.ResultSet
//import java.sql.Connection
//import collection.JavaConversions._
//import scala.runtime.ZippedTraversable3.zippedTraversable3ToTraversable
//import play.api.libs.json.Json
//import play.api.libs.json._
//import controllers.Application
//import es.imim.phi.collector.compounds.CompoundFilter
//import es.imim.phi.collector.compounds.CompoundUtil
//import slick.driver.PostgresDriver.simple._
//import slick.jdbc.JdbcBackend.Database.dynamicSession
//import slick.jdbc.{ StaticQuery => Q }
//import slick.jdbc.StaticQuery.interpolation
//import slick.jdbc.GetResult
//import slick.backend._
//
//
//case class Activity_Series(
//  activities_series_id: Int,
//  activities_series_description: Int,
//  activities_count: Option[Int],
//  compounds_count: Option[Int])
//
//case class Activity(
//  activities_id: Int,
//  activities_series_id: Int,
//  relation: Option[String],
//  standard_units: Option[String],
//  standard_value: Option[Float],
//  activity_type: Option[String],
//  inchi: Option[String],
//  inchikey: Option[String],
//  smiles: Option[String],
//  data: Option[String],
//  sdf2d: Option[String],
//  sdf3d: Option[String],
//  img2d: Option[String])
//
//object CompoundDataSeriesDB {
//  var db: Database = null
//  var sqlConnection: java.sql.Connection = null
//
//  class Activities_series(tag:Tag) extends Table[(Option[Int], Option[String], Option[Int], Option[Int])](tag,"activities_series") {
//    def activities_series_id = column[Int]("activities_series_id", O.PrimaryKey, O.AutoInc)
//    def activities_series_description = column[String]("activities_series_description")
//    def activities_count = column[Int]("activities_count")
//    def compounds_count = column[Int]("compounds_count")
//    def * = (activities_series_id.? ,activities_series_description.? ,activities_count.? ,compounds_count.?)
////update2015
////    def forInsert = (activities_series_description.?,activities_count.? ,compounds_count.?) returning activities_series_id
//  }
//
//  val activities_series = TableQuery[Activities_series]
//
//  class Activities(tag:Tag) extends Table[es.imim.phi.collector.model.Activity](tag, "activities") {
//    def activities_id = column[Int]("activities_id", O.PrimaryKey, O.AutoInc)
//    def activities_series_id = column[Int]("activities_series_id")
//    def relation = column[String]("relation")
//    def standard_units = column[String]("standard_units")
//    def standard_value = column[Float]("standard_value")
//    def activity_type = column[String]("activity_type")
//    def inchi = column[String]("inchi")
//    def inchikey = column[String]("inchikey")
//    def smiles = column[Option[String]]("smiles")
//    def data = column[String]("data")
//    def sdf2d = column[String]("sdf2d")
//    def sdf3d = column[String]("sdf3d")
//    def img2d = column[String]("img_base64")
//    def * = (activities_id ,activities_series_id , relation.? , standard_units.?, standard_value.? , activity_type.? , inchi.? , inchikey.? , smiles , data.? , sdf2d.? , sdf3d.? , img2d.?) <> (es.imim.phi.collector.model.Activity.tupled, es.imim.phi.collector.model.Activity.unapply)
//    def nokey = (activities_series_id ,relation ,standard_units , standard_value , activity_type , inchi , inchikey ,smiles , data , sdf2d , sdf3d , img2d)
//  }
//
//  val activities = TableQuery[Activities]
//
//  def CreateDataSeries_fromSDF_RAW(series_description: String, filename: String, importFields: Boolean) = {
//    val activities_series_id = this.CreateDataSeries(series_description)
//    val activities = es.imim.phi.collector.engine.importData.getMolsFromFileAndInsert_RDKit(activities_series_id, filename, importFields)
//    activities_series_id
//  }
//
//  def GetDataSeriesIdFromDescription(series_description: String) = {
//    this.db withDynSession {
//      val q = for { act <- this.activities_series if act.activities_series_description === series_description } yield act.activities_series_id
//      val l = q.to[List]
//      println(l)
//      //update2015
//      //l.head
//      "" 
//    }
//  }
//
//  def CreateDataSeries_fromOPS_RAW(seriesName: String, ldaresults: Seq[Map[String, String]]) = this.StoreMoleculesRAW(seriesName, ldaresults)
//
//
//  def GetActivitiesForDataSeries(series_id: Int) = {
//    val rs = CompoundDataSeriesDB.GetActivitiesForDataSeriesRS(series_id)
//    val strea = new Iterator[(String, String, String)] {
//      def hasNext = rs.next()
//      def next() = (rs.getString(1), rs.getString(2), rs.getString(3))
//    }.toStream
//  }
//
//  def CreateDataSeries(series_description: String) = this.db withDynSession {
//	//update2015
//	//activities_series.forInsert insert (Some(series_description), None, None)
//	0
//  }
//
//  def StoreMoleculesRAW(series_description: String, activities: Seq[Map[String, String]]): Int = {
//    val activities_series_id = this.CreateDataSeries(series_description)
//    //println("activities_series_id:" + activities_series_id)
//    var querySQL = "INSERT INTO activities (" +
//      "activities_series_id," +
//      "data," +
//      "sdf2d) " +
//      "VALUES " +
//      "(?,?,?)"
//    var insertStatement = sqlConnection.prepareStatement(querySQL)
//    for (act <- activities) {
//      insertStatement.setInt(1, activities_series_id)
//      insertStatement.setString(2, Json.toJson(act).toString)
//      insertStatement.setString(3, act("sdf"))
//      insertStatement.execute()
//    }
//    activities_series_id
//  }
//
//  def StoreMoleculeRAW(activities_series_id: Int, activity: Map[String, String]): Int = {
//    //println("Activity: "+activity.take(10))
//    //println("ActivityJS: "+Json.toJson(activity).toString.take(100))
//    var querySQL = "INSERT INTO activities (" +
//      "activities_series_id," +
//      "data," +
//      "sdf2d) " +
//      "VALUES " +
//      "(?,?,?)"
//    var insertStatement = sqlConnection.prepareStatement(querySQL)
//    insertStatement.setInt(1, activities_series_id)
//    insertStatement.setString(2, Json.toJson(activity - "sdf").toString)
//    insertStatement.setString(3, activity("sdf"))
//    insertStatement.execute()
//    activities_series_id
//  }
//
//  //type activityType = (Option[Int], Option[Int], String, Option[String], Option[String])
//
//  def CompleteDataSeries_OPS(activities_series_id: Int, mappingFieldsOPS: Map[String, String] = Map(), computeImages: Boolean = true, computeChemFields: Boolean = true, computeActFields: Boolean = true, computeSDF: Boolean = false) = {
//    //TODO   
//    println("TODO: CompleteDataSeries_OPS")
//  }
//
//  def CompleteDataSeries_SDF(activities_series_id: Int, mappingSDF: Map[String, (String, String)] = Map(), computeImages: Boolean = true, computeChemFields: Boolean = true, computeActFields: Boolean = true, computeSDF: Boolean = true) = {
//
//    def CompleteDataSeriesFieldsFunc(f: Activity => Int) = {
//      this.db withDynSession {
//        var i = 0
//
//        val st = CompoundDataSeriesDB.sqlConnection.createStatement()
//        st.setFetchSize(1)
//        val rs = st.executeQuery("SELECT activities_id FROM activities where activities_series_id=" + activities_series_id)
//        while (rs.next()) {
//          val activities_id = rs.getString(1).toInt
//          println("activities_id: " + activities_id)
//          val q = CompoundDataSeriesDB.GetActivitiesForDataSeriesRowRAW(activities_id)
//          q.foreach {
//            case a => {
//              i = i + 1
//              println("Func to " + i + " activity")
//              f(a)
//            }
//          }
//        }
//      }
//    }
//    def computeIMG(method: String = "RDKit") = {
//
//      def UpdateActivityImage(CDK: Boolean)(activity: Activity) =
//        {
//          var i = 0
//          try {
//            i = i + 1
//            //            println("Computing img: "+i)
//            //            println("CDK?:"+CDK)
//            //            println("Smiles: "+activity.smiles)
//            //            println("SDF2D: "+activity.sdf2d.get)
//            val cmpimg =
//              if (CDK)
//                activity.smiles match {
//                  case Some(smiles) =>
//                    if (smiles == "")
//                      CompoundUtil.noStructureIMG
//                    else
//                      CompoundUtil.getIMGFromSMiles(activity.smiles, CDK)
//                  case None => CompoundUtil.noStructureIMG
//                }
//              else
//                activity.sdf2d match {
//                  case Some(sdf2d) =>
//                    {
//                      val m = org.RDKit.RWMol.MolFromMolBlock(sdf2d)
//                      if ((sdf2d == "") || (m.getAtoms().size() == 0))
//                        CompoundUtil.noStructureIMGFixed
//                      else {
//                        CompoundUtil.getIMGFromSDF_RDKit(sdf2d)
//                      }
//                    }
//                  case None => CompoundUtil.noStructureIMGFixed
//                }
//            //println("Img computed: "+cmpimg.take(100))
//            val q = for { act <- this.activities if act.activities_id === activity.activities_id } yield act.img2d
//            q.update(cmpimg.toString)
//          } catch {
//            case e : Throwable => {
//              println(e)
//              Logger.info("Error computing image for: " + activity.smiles)
//              Logger.info("Error computing image for: " + activity.sdf2d.take(100))
//              val q = for { act <- this.activities if act.activities_id === activity.activities_id } yield act.img2d
//              //this.activities.map(p => p).elements()
//              q.update(CompoundUtil.noStructureIMGFixed)
//              0
//            }
//          }
//        }
//
//      method match {
//        case "CDK" => CompleteDataSeriesFieldsFunc(UpdateActivityImage(true) _)
//        case "RDKit" => CompleteDataSeriesFieldsFunc(UpdateActivityImage(false) _)
//      }
//    }
//
//    def computeChemistryFields = {
//
//      def UpdateFieldSmiles(activity: Activity) = {        
//        val sdf2d = activity.sdf2d
//        val smi = es.imim.phi.collector.compounds.CompoundUtil.getSmilesFromSDF(activity.sdf2d.get)
//        val q = for { act <- this.activities if act.activities_id === activity.activities_id } yield act.smiles
//        println("Smiles: " + smi)
//        if (smi != "")
//          q.update(Some(smi))
//        else
//          q.update(None)
//      }
//
//      def UpdateFieldInchi(activity: Activity) = {
//        val (inchi, inchikey) = es.imim.phi.collector.compounds.CompoundUtil.getInChIFromSMILES(activity.smiles.getOrElse(""))
//        val q = for { act <- this.activities if act.activities_id === activity.activities_id } yield (act.inchi , act.inchikey)
//        q.update(inchi, inchikey)
//      }
//
//      println("Updating smiles")
//      CompleteDataSeriesFieldsFunc(UpdateFieldSmiles _)
//      println("End smiles computation")
//      //println("Updating Inchi")
//      //CompleteDataSeriesFieldsFunc(UpdateFieldInchi _)
//    }
//
//    def computeActivityFields = {
//
//      def UpdateActivityField(field: String)(activity: Activity) =
//        {
//          val act = Json.parse(activity.data.get)
//          val activity_value = act \ field
//          val actjs = activity_value.asInstanceOf[JsString]
//          val q = for { act <- this.activities if act.activities_id === activity.activities_id } yield act.standard_value
//          q.update(java.lang.Float.parseFloat(actjs.value.toString()))
//        }
//
//      def UpdateUnitsField(field: String)(activity: Activity) =
//        {
//          val act = Json.parse(activity.data.get)
//          val activity_value = act \ field
//          val actjs = activity_value.asInstanceOf[JsString]
//          val q = for { act <- this.activities if act.activities_id === activity.activities_id } yield act.standard_units
//          q.update(actjs.value.toString())
//        }
//
//      def UpdateUnitsValue(value: String)(activity: Activity) =
//        {
//          val q = for { act <- this.activities if act.activities_id === activity.activities_id } yield act.standard_units
//          q.update(value)
//        }
//
//      def UpdateActivityTypeField(field: String)(activity: Activity) =
//        {
//          val actUpdateStatisticsDataSeries = Json.parse(activity.data.get)
//          val activity_value = actUpdateStatisticsDataSeries \ field
//          val actjs = activity_value.asInstanceOf[JsString]
//          val q = for { act <- this.activities if act.activities_id === activity.activities_id } yield act.activity_type
//          q.update(actjs.value.toString())
//        }
//
//      def UpdateActivityTypeValue(value: String)(activity: Activity) =
//        {
//          val q = for { act <- this.activities if act.activities_id === activity.activities_id } yield act.activity_type
//          q.update(value)
//        }
//
//      if (mappingSDF.size != 0) {
//        println("Updating activity")
//        mappingSDF("activity_field") match {
//          case ("field", field) => CompleteDataSeriesFieldsFunc(UpdateActivityField(field) _)
//          case _ => Application.logger.error("Activity field not specified")
//        }
//
//        println("Updating activity units ")
//        mappingSDF("activity_units") match {
//          case ("field", field) => CompleteDataSeriesFieldsFunc(UpdateUnitsField(field) _)
//          case ("value", value) => CompleteDataSeriesFieldsFunc(UpdateUnitsValue(value) _)
//          case _ => Application.logger.error("Activity units field or value not specified")
//        }
//
//        println("Updating activity type")
//        mappingSDF("activity_type") match {
//          case ("field", field) => CompleteDataSeriesFieldsFunc(UpdateActivityTypeField(field) _)
//          case ("value", value) => CompleteDataSeriesFieldsFunc(UpdateActivityTypeValue(value) _)
//          case _ => Application.logger.error("Activity units field or value not specified")
//        }
//      }
//
//    }
//
//    sqlConnection.setAutoCommit(false)
//
//    println("Completing data series")
//    if (computeChemFields) computeChemistryFields
//
//    println("Activity fields")
//    if (computeActFields) computeActivityFields
//
//    this.UpdateStatisticsDataSeries(activities_series_id)
//
//    println("SDF Cmpt")
//    if (computeSDF)
//      CompleteDataSeriesFieldsFunc { (activity: Activity) =>
//        {
//          //val sdf2d1 = (Json.parse(activity.data.get) \ "sdf").asInstanceOf[JsString]
//          //val sdf2d = sdf2d1.value.toString()
//          //println("SDF2D: " + activity.sdf2d)
//          val sdf2d = activity.sdf2d
//          println("Computing: " + sdf2d.take(100))
//          val q = for { act <- this.activities if act.activities_id === activity.activities_id } yield act.sdf2d
//          q.update(sdf2d.get)
//        }
//      }
//
//    println("Img Cmpt")
//    if (computeImages) computeIMG("RDKit")
//    println("End complete")
//  }
//
//  def UpdateStatisticsDataSeries(activities_series_id: Int) = {
//    println("Updating statistics!")
//    this.db withDynSession {
//      //val q = for { serie <- this.activities.filter(_.activities_series_id === activities_series_id) } yield serie.*
//      //val l = q.list.length
//      //println("Count: "+l)
//      //val q3 = for { serie <- this.activities.filter(_.activities_series_id === activities_series_id) } yield serie.smiles
//      //val qd = scala.slick.lifted.Query(q3.countDistinct).first
//      val qs = "select count(*) from activities where activities_series_id=" + activities_series_id.toString
//      val rs = this.doQuerySQL(qs)
//      rs.next()
//      println("Num activities:" + rs.getString(1))
//      val numacts = rs.getInt(1)
//
//      val qs2 = "select count(*) from activities where activities_series_id=" + activities_series_id.toString + " and smiles is not null"
//      val rs2 = this.doQuerySQL(qs2)
//      println(qs2)
//      rs2.next()
//      val numcomps = rs2.getInt(1)
//      println("Num compounds:" + numcomps)
//      val qu = for { serie <- this.activities_series.filter(_.activities_series_id === activities_series_id) } yield (serie.activities_count, serie.compounds_count)
//      qu.update(numacts, numcomps)
//    }
//
//  }
//
//  //  def GetActivitiesForDataSeriesRAW(activities_series_id: Int) = {
//  //    for { act <- this.activities if act.activities_series_id === activities_series_id }
//  //      yield (act.activities_id, act.activities_series_id, act.data, act.sdf2d, act.smiles)
//  //  }
//
//  def GetActivitiesForDataSeriesRAW(activities_series_id: Int) = for { act <- this.activities if act.activities_series_id === activities_series_id } yield (act)
//
//  def GetActivitiesForDataSeriesRowRAW(activities_id: Int) = for { act <- this.activities if act.activities_id === activities_id } yield (act)
//
//  def GetActivitiesForDataSeriesRS(activities_series_id: Int): java.sql.ResultSet = {
//    val st = CompoundDataSeriesDB.sqlConnection.createStatement()
//    st.setFetchSize(1)
//    //println("SELECT activities_id,sdf2d,img_base64,data FROM activities where activities_series_id="+activities_series_id)
//    st.executeQuery("SELECT activities_id,sdf2d,img_base64,data FROM activities where activities_series_id=" + activities_series_id)
//    st.getResultSet()
//  }
//
//  def GetActivitiesForDataSeriesFields(activities_series_id: Int) = {
//    val rs1 = CompoundDataSeriesDB.GetActivitiesForDataSeriesRS(activities_series_id)
//    rs1.next
//    val mp = getFromROW(rs1)
//    mp.keys
//  }
//
//  def GetActivitiesForDataSeriesIterator(activities_series_id: Int) = {
//    val rs = CompoundDataSeriesDB.GetActivitiesForDataSeriesRS(activities_series_id)
//    println("resultset obtained")
//    val strea = new Iterator[Map[String, String]] {
//      def hasNext = rs.next()
//      def next() = getFromROW(rs)
//    }
//    println("stream created")
//    strea
//  }
//
//  private def getFromROW(rs: java.sql.ResultSet) = {
//    val data = rs.getString("data")
//    //println("Data:\n"+data)
//    val data_js = Json.parse(data)
//    val jso = data_js.asInstanceOf[play.api.libs.json.JsObject]
//    val fs = jso.fieldSet
//    val mapdata = for { (f, v) <- fs.take(100) } yield ({
//      val vs = v.asInstanceOf[play.api.libs.json.JsString]
//      (f, vs.value)
//    })
//    val mp1 = Map("activities_id" -> rs.getString("activities_id"), "sdf2d" -> rs.getString("sdf2d"), "img_base64" -> rs.getString("img_base64"))
//    println("Activities id :" + mp1("activities_id"))
//    //println(mapdata.take(10))
//    mp1 ++ mapdata.toMap
//  }
//
//  def GetAllDataSeries = for { act <- this.activities_series } yield (act)
//
//  def deleteDataSeries(data_series_id: Int) = {
//    var deleteq = this.activities_series.where(_.activities_series_id === data_series_id)
//    database_eTOXOPS.db withDynSession { deleteq.delete }
//  }
//
//  def deleteDataSeries(data_series_description: String) = {
//    var deleteq = this.activities_series.where(_.activities_series_description === data_series_description)
//    database_eTOXOPS.db withDynSession { deleteq.delete }
//
//  }
//
//  def doQuerySQL(query: String) = {
//    Logger.debug("doQuerySQL: \n" + query)
//    var statement = sqlConnection.createStatement()
//    statement.execute(query)
//    statement.getResultSet()
//  }
//  def doQuerySQLInsert(query: String) = {
//    var statement = sqlConnection.createStatement()
//    Logger.debug("doQuerySQLInsert: " + query)
//    statement.execute(query, Statement.RETURN_GENERATED_KEYS)
//    statement.getGeneratedKeys()
//  }
//
//
//
//}
//
//
