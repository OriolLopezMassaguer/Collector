//package es.imim.phi.collector.model
//
////import scala.slick.jdbc.{ GetResult, StaticQuery => Q }
////import scala.slick.lifted._
////import scala.slick.lifted.Column
////import scala.slick.lifted.TypeMapper
////import scala.slick.session.Database
////import scala.slick.driver.PostgresDriver.simple._
////import scala.slick.driver.PostgresDriver._
////import scala.slick.driver.ExtendedProfile
////import scala.slick.driver.ExtendedDriver
////import scala.slick.session.Session
////import scala.slick.driver._
////import scala.slick.lifted.Query
////import scala.slick.jdbc._
////import scala.slick.lifted.TypeMapperDelegate
////import scala.slick.session.Database
////import scala.slick.lifted.Column
////import scala.slick.lifted.TypeMapper
////import scala.slick.session.Database
////import scala.slick.driver.PostgresDriver.simple._
////import scala.slick.driver.ExtendedProfile
////import scala.slick.driver.ExtendedDriver
////import scala.slick.session.Session
////import Database.threadLocalSession
//
//import es.imim.phi.collector.ops.lda_api._
//import es.imim.phi.collector.compounds._
//import es.imim.phi.collector.engine.ExtractionEngine
//import java.io.PrintStream
//import play.api.libs.json.Json
//
//import slick.driver.PostgresDriver.simple._
//import slick.jdbc.JdbcBackend.Database.dynamicSession
//import slick.jdbc.{ StaticQuery => Q }
//import slick.jdbc.StaticQuery.interpolation
//import slick.jdbc.GetResult
//import slick.backend._
//
//object CompoundDataSeries {
//
//  val opsAPI = new OPSLDAScala(ExtractionEngine.OPSAPIURL, ExtractionEngine.appKey, ExtractionEngine.appId, true, ExtractionEngine.dbURL, ExtractionEngine.dbUser, ExtractionEngine.dbPassword, ExtractionEngine.cachedapi)
//
//  val opsFieldsMap = scala.collection.immutable.HashMap(
//    "fops" -> "fdataseries")
//
//  def newSeriesFromSDF(filename : String, seriesName : String, overwrite : Boolean, mappingSDF : Map[String, (String, String)],importFields:Boolean) : Int = {
//    if (overwrite) CompoundDataSeries.deleteSeries(seriesName)
//    val seriesid = CompoundDataSeriesDB.CreateDataSeries_fromSDF_RAW(seriesName, filename,importFields)  
//    CompoundDataSeriesDB.CompleteDataSeries_SDF(seriesid, mappingSDF = mappingSDF)
//    println("Data Series created")
//    println("	series description: "+seriesName)
//    println("	sdf filename: "+filename)
//    println("	series id: "+seriesid)
//    println("   fields imported: " +importFields)
//    seriesid
//  }
//
//  def newSeriesFromCSV(filename : String, seriesName : String) : Int = ???
//
//  def newSeriesFromOPS(target_cwiki : String, seriesName : String, overwrite : Boolean, mappingFieldsOPS : Map[String, String]) : Int = {
//    if (overwrite) CompoundDataSeries.deleteSeries(seriesName)
//    val ldaresults = opsAPI.GetPharmacologyByTargetLDA(target_cwiki)
//    val seriesid = CompoundDataSeriesDB.CreateDataSeries_fromOPS_RAW(seriesName, ldaresults)
//    CompoundDataSeriesDB.CompleteDataSeries_OPS(seriesid, mappingFieldsOPS = mappingFieldsOPS, computeSDF = true)
//    seriesid
//  }
//
//  def exportexecutiondata2SDF_fileName(seriesId : Int, fileName : String,exportFields:Boolean) = {
//    CompoundDataSeriesDB.sqlConnection.setAutoCommit(false)
//    val st = CompoundDataSeriesDB.sqlConnection.createStatement()
//    st.setFetchSize(1)
//    val resultSet = st.executeQuery("SELECT activities_id,smiles,data,sdf2d FROM activities where activities_series_id="+seriesId)
//    val sdw = CompoundUtil.openSDFFile(fileName)
//    
//    println("File opened")
//
//    while ((resultSet.next)) {
//      //println(i, resultSet.getString(1), resultSet.getString(2))
//      //CompoundUtil.writeSDFMol_RDKit(sdw, resultSet.getString(4), resultSet.getString(3))
//      //val sdf2d= resultSet.getString(4)
//      //val data= resultSet.getString(3)
//      CompoundUtil.writeSDFMol_RDKit_sdf2d(sdw, resultSet.getString(4))
//      if (exportFields) CompoundUtil.writeSDFMol_RDKit_data(sdw, resultSet.getString(3))
//      CompoundUtil.writeSDFMol_RDKit_endmol(sdw)
//    }
//    CompoundUtil.close_SDF(sdw)
//
//  }
//
//  def getSeriesSDF(seriesId : Int) = ???
//  def getSeriesJSON(seriesId : Int) = ???
//  def getSeriesCSV(seriesId : Int) = ???
//
//  def getSeriesName(seriesId : Int) = ???
//  
//  def getSeriesId(seriesName: String) = ???
//
//  def deleteSeries(seriesId : Int) = CompoundDataSeriesDB.deleteDataSeries(seriesId)
//  def deleteSeries(seriesName : String) = CompoundDataSeriesDB.deleteDataSeries(seriesName)
//
//  //def filterSeries(seriesId : Int, newSeriesName : String, filter : String) = ???
//
//  def FilterDataSeriesProtocol(data_series_id : Int, protocol : Int) = {
//    database_eTOXOPS.db withDynSession {
//      val q = for (f <- database_eTOXOPS.GetFilteringProtocols.sortBy(f => f._3.asc) if f._1 === protocol) yield f._6
//      val l = q.to[List]
//      //update2015
//	//val filters = l.map(Class.forName(_).newInstance.asInstanceOf[CompoundFilter])
//      val filters = List(Class.forName("").newInstance.asInstanceOf[CompoundFilter])
//      val filterAll = new CompoundFilterList(filters)
//      FilterDataSeries(data_series_id, filterAll)
//    }
//  }
//
//  def FilterDataSeries(data_series_id : Int, filter : CompoundFilter) = {
//    database_eTOXOPS.db withDynSession {
//      val new_data_series_id = database_eTOXOPS.CreateDataSeries("Filter "+filter.toString()+" for data series "+data_series_id)
//      database_eTOXOPS.GetActivitiesForDataSeries(data_series_id).foreach {
//        case (activities_id, activities_series_id, relation, standard_units, standard_value, activity_type, inchi, inchikey, smiles, data, sdf2d, sdf3d) =>
//          {
//            val activity = new es.imim.phi.collector.compounds.Activity(0, 0, activity_type, smiles.get)
//            val compound = new es.imim.phi.collector.compounds.Compound(activity)
//            //if (filter.filterPass(compound))
//		//database_eTOXOPS.activities.nokey.insert(Some(new_data_series_id), relation, standard_units, standard_value, activity_type, inchi, inchikey, smiles, data, sdf2d, sdf3d)
//          }
//      }
//      database_eTOXOPS.UpdateStatisticsDataSeries(new_data_series_id)
//      new_data_series_id
//    }
//  }
//
//  def exportSeriesSDF(seriesName : String, filename : String) = ???
//
//  def exportSeriesCSV(seriesName : String, filename : String) = ???
//
//  /// Helper methods
//
//  private def ComputeImages(seriesId : Int, method : String) = ???
//  private def ComputeSDF(seriesId : Int, mehtod : String) = ???
//
//}
