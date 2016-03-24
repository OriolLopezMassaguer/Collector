package es.imim.phi.collector.test
import es.imim.phi.collector.engine.ExtractionEngine
import es.imim.phi.collector.compounds.CompoundFilterIC50
import org.RDKit._

object testInChI extends App {
  System.load("/opt/collector/lib/libGraphMolWrap.so")
    val m1 = RWMol.MolFromSmiles("C[C@H](F)C(F)Cl")
  val evs = new ExtraInchiReturnValues()
  val inchi = RDKFuncs.MolToInchi(m1, evs)
  val key = RDKFuncs.InchiToInchiKey(inchi)
  println(m1)
println(evs)
println(inchi)
println(key)
  //  
  //  val factory = org.openscience.cdk.inchi.InChIGeneratorFactory.getInstance()
  //  val sp = new SmilesParser(DefaultChemObjectBuilder.getInstance())
  //  sp.setPreservingAromaticity(false)
  //  val mol = sp.parseSmiles("[H]C4(N)(CN(CC1CCC(CC1)C2CCNN2(C))CC4([H])(C(=O)N3CCCC3([H])(C#N)))")
  //  val gen = factory.getInChIGenerator(mol)
  //
  //  val ret = gen.getReturnStatus();
  //  if (ret == INCHI_RET.WARNING) {
  //    //InChI generated, but with warning message
  //    System.out.println("InChI warning: " + gen.getMessage());
  //  } else if (ret != INCHI_RET.OKAY) {
  //    // InChI generation failed
  //    println("InChI failed: " + ret.toString() + " [" + gen.getMessage() + "]");
  //  }
  //  //
  //  val inchi = gen.getInchi()
  //  println(inchi)
  //  val  auxinfo = gen.getAuxInfo()
  //  println(auxinfo)
}

