package es.imim.phi.collector.test
import play.api.libs.json._
import es.imim.phi.collector.model.database_eTOXOPS
import es.imim.phi.collector.engine.ExtractionEngine

object testReadSDF extends App {

  val m = org.RDKit.RWMol.MolFromSmiles("")

  println(m)

}
