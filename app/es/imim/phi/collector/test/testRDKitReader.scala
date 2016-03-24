package es.imim.phi.collector.test

import org.RDKit.SDWriter
import org.RDKit.SDMolSupplier
import org.RDKit.RWMol
import es.imim.phi.collector.engine.ExtractionEngine
import es.imim.phi.collector.engine.FileUtils
import java.nio.file.Files
import java.nio.file.FileSystems

object testRDKitReader extends App {

  System.load("/opt/collector/lib/libGraphMolWrap.so")

  def getSDFFromSMiles_RDKit(smiles: String) = {
    val s = System.load("/opt/collector/lib/libGraphMolWrap.so")
    println("Load: " + s)
    val filename = FileUtils.getNewFilename("sdf_smile", ".sdf", System.getenv("COLLECTOR_HOME") + "/temp")
    val sw = new org.RDKit.SDWriter(filename)
    val m = org.RDKit.RWMol.MolFromSmiles(smiles)
    m.compute2DCoords()
    sw.write(m)
    sw.flush()
    sw.close()
    val f2 = Files.readAllBytes(FileSystems.getDefault().getPath(filename))
    new String(f2)
  }

  def getProps_RDKitMol(m: org.RDKit.ROMol) =
    {
      val proplist = m.getPropList()
      val siz = proplist.size() - 1
      var mp = scala.collection.Map[String, String]()
      //println(proplist.size())
      for (i <- Range(0, siz.toInt)) {
        var prop = proplist.get(i)
        if (!prop.startsWith("_")) {
          mp = mp + (prop -> m.getProp(prop))
          //println(prop)
          //println(i + "#" + prop + " / " + m.getProp(prop))
        } else
          println("Discarted: " + prop)

      }
      mp
    }

  //ExtractionEngine.initEngine("/opt/collector")

  //new org.RDKit.SDWriter("/opt/etox_reports/etoxreports_rdkit.sdf")

  //getSDFFromSMiles_RDKit("COC2CCC(CCN(C)CCCC(C#N)(C(C)C)C1CCC(OC)C(C1)OC)CC2(OC)")

  val molsup = new org.RDKit.SDMolSupplier("/media/sf_oriol/eTOX/BD reports/all_2013_3_retry_pc135.sdf")
  var m: org.RDKit.ROMol = null

  m = molsup.next()


}