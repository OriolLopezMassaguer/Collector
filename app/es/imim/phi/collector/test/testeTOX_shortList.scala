package es.imim.phi.collector.test

import es.imim.phi.collector.engine.ExtractionEngine
import es.imim.phi.collector.ops.lda_api.OPSLDAScala
import java.io.PrintWriter
import java.io.File

object testeTOXshortList extends App {

  ExtractionEngine.initEngine("/opt/collector")

  //val opsAPI = new OPSLDAScala13("https://beta.openphacts.org", ExtractionEngine.appKey, ExtractionEngine.appId, true, ExtractionEngine.dbURL, ExtractionEngine.dbUser, ExtractionEngine.dbPassword, ExtractionEngine.cachedapi)
  
//  val opsAPI = new OPSLDAScala("https://api.openphacts.org", ExtractionEngine.appKey, ExtractionEngine.appId, true, ExtractionEngine.dbURL, ExtractionEngine.dbUser, ExtractionEngine.dbPassword, ExtractionEngine.cachedapi)
//
//  
//  val pharm=opsAPI.GetPharmacologyByTargetLDA("http://www.conceptwiki.org/concept/00059958-a045-4581-9dc5-e5a08bb0c291")
//  
//  for(p <- pharm)    
//    p.map(x=>println(x))

  
}