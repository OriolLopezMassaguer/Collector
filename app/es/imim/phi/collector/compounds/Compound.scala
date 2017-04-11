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

package es.imim.phi.collector.compounds

import es.imim.phi.collector.engine.ExtractionEngine
import java.sql.DriverManager

import es.imim.phi.collector.engine.FileUtils
import scala.sys.process._
import org.openscience.cdk.smiles.SmilesParser
import org.openscience.cdk.DefaultChemObjectBuilder
import org.openscience.cdk.qsar.descriptors.molecular.RuleOfFiveDescriptor
import org.openscience.cdk.inchi.InChIGenerator
import org.openscience.cdk.inchi.InChIGeneratorFactory
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.listener.PropertiesListener;
import org.openscience.cdk.smiles.InvPair;

//import org.openscience.cdk.interfaces.IMolecule
import org.openscience.cdk.layout.StructureDiagramGenerator
import org.openscience.cdk.graph.ConnectivityChecker
import org.openscience.cdk.io.SDFWriter
import org.openscience.cdk.io.listener.PropertiesListener
import org.openscience.cdk.io.MDLV3000Reader
import org.openscience.cdk.ChemFile
import org.openscience.cdk.smiles.SmilesGenerator
import org.openscience.cdk.io.MDLV2000Reader
import org.openscience.cdk.interfaces.IChemFile
//import org.openscience.cdk.io.iterator.IteratingMDLReader
import org.openscience.cdk.renderer.generators.BasicSceneGenerator
import org.openscience.cdk.renderer.generators.BasicBondGenerator
import org.openscience.cdk.renderer.generators.BasicAtomGenerator
import org.openscience.cdk.renderer.AtomContainerRenderer
import org.openscience.cdk.renderer.font.AWTFontManager
import org.openscience.cdk.renderer.RendererModel
import org.openscience.cdk.renderer.visitor.AWTDrawVisitor
import collection.JavaConversions._
import play.api.Logger

import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.util.GregorianCalendar
import java.text.SimpleDateFormat
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.File
import java.io.FileInputStream
import java.io.FileReader
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.CharBuffer
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.Charset
import java.nio.charset.CharsetDecoder
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException
import java.io.StringWriter
import org.apache.commons.io.IOUtils
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Properties

import java.io.ByteArrayInputStream

import net.sf.jniinchi.INCHI_RET
import javax.imageio.ImageIO
import sun.misc.BASE64Encoder
import sun.misc.BASE64Decoder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.awt.Rectangle

import scala.collection.mutable.LinkedList

import java.awt.Graphics2D
import java.awt.Color

import java.awt.image.RenderedImage
//import org.RDKit.SDWriter
import java.nio.file.FileSystems
import play.api.libs.json.Json
import java.io.PrintStream
import scalaz.Memo

object CDKutils {
  var smilesParser = new SmilesParser(DefaultChemObjectBuilder.getInstance())
  val ro5Descriptor = new RuleOfFiveDescriptor()
}

object CompoundUtil {

  lazy val noStructureIMGFixed = noStructureIMG
  def noStructureIMG = {
    val f = new File(resourceDir + "/no_structure.png")
    val bi = ImageIO.read(f)
    val bos = new ByteArrayOutputStream();
    ImageIO.write(bi, "png", bos);
    val imageBytes = bos.toByteArray()
    val encoder = new BASE64Encoder()
    val imageString = encoder.encode(imageBytes)
    //println("No structure: " + imageString)
    bos.close()
    imageString
  }

  //def tmpDir = System.getenv("COLLECTOR_HOME") + "/temp"
  lazy val tmpDir = System.getenv("COLLECTOR_HOME") + "/logs"
  def resourceDir = System.getenv("COLLECTOR_HOME") + "/resources"
  def pythonIMGScript = System.getenv("COLLECTOR_HOME") + "/scripts/generate_img.py"
  def pythonIMGScriptSDF = System.getenv("COLLECTOR_HOME") + "/scripts/generate_img_sdf.py"

  def getIMGFromSMiles(smile: Option[String], cdk: Boolean): String = {
    smile match {
      case Some(sm) => if (cdk)
        getIMGFromSMiles_CDK(sm)
      else
        getIMGFromSMiles_RDKit(sm)

      case None => noStructureIMG
    }
  }

  def getIMGFromSDF_RDKit(sdf2d: String) = {
    val filenamesdf = getSDFFile_FromSDF_RDKit(sdf2d)
    val filenameimg = FileUtils.getNewFilename("compound_img_rdkit", ".png", tmpDir)
    println("SDF Filename:" + filenamesdf)
    println("IMG filename:" + filenameimg)
    val seq = Seq("/usr/bin/python", pythonIMGScriptSDF, filenamesdf, filenameimg)
    val s = seq.!
    val f = new File(filenameimg)
    val bi = ImageIO.read(f)
    val bos = new ByteArrayOutputStream();
    ImageIO.write(bi, "png", bos);
    val imageBytes = bos.toByteArray()
    val encoder = new BASE64Encoder()
    val imageString = encoder.encode(imageBytes)
    Logger.debug("Image result: " + imageString)
    bos.close()
    //Seq("rm", filename).!
    imageString
  }

  def getIMGFromSMiles_RDKit(smiles: String): String = {
    val filename = FileUtils.getNewFilename("compound_img", ".png", tmpDir)
    val s = Seq("/usr/bin/python", pythonIMGScript, smiles, filename).!
    val f = new File(filename)
    val bi = ImageIO.read(f)
    val bos = new ByteArrayOutputStream();
    ImageIO.write(bi, "png", bos);
    val imageBytes = bos.toByteArray()
    val encoder = new BASE64Encoder()
    val imageString = encoder.encode(imageBytes)
    Logger.debug("Image result: " + imageString)
    bos.close()
    //Seq("rm", filename).!
    imageString
  }

  def getIMGFromSMiles_CDK(smiles: String): String = {
    ""
    //    println("Smiles img:"+smiles)
    //    val WIDTH = 200
    //    val HEIGHT = 250
    //    val drawArea = new Rectangle(WIDTH, HEIGHT);
    //    val image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB)
    //    var moleculeI1 = CDKutils.smilesParser.parseSmiles(smiles)
    //    val fbt = new org.openscience.cdk.smiles.FixBondOrdersTool()
    //    val moleculeI = fbt.kekuliseAromaticRings(moleculeI1)
    //    val mols = for (m <- ConnectivityChecker.partitionIntoMolecules(moleculeI).molecules()) yield m
    //    val m2 = mols.toArray
    //    val m3 = for (m <- m2) yield (m, m.atoms.size)
    //    val m4 = m3.sortBy(f => f._2)
    //    var molecule = m4.reverse.head._1.asInstanceOf[IMolecule]
    //    val sdg = new StructureDiagramGenerator()
    //    sdg.setMolecule(molecule)
    //    sdg.generateCoordinates()
    //    molecule = sdg.getMolecule()
    //    val ge1 = new BasicSceneGenerator()
    //    val ge2 = new BasicBondGenerator()
    //    val ge3 = new BasicAtomGenerator()
    //    val generators = LinkedList(ge1, ge2, ge3)
    //    val renderer = new AtomContainerRenderer(generators, new AWTFontManager())
    //    renderer.setup(molecule, drawArea)
    //    var mdl = renderer.getRenderer2DModel().asInstanceOf[RendererModel]
    //    val czf = classOf[org.openscience.cdk.renderer.generators.BasicSceneGenerator.ZoomFactor]
    //
    //    val g2 = image.getGraphics().asInstanceOf[Graphics2D]
    //    g2.setColor(Color.WHITE)
    //    g2.fillRect(0, 0, WIDTH, HEIGHT)
    //    //renderer.paint(molecule, new AWTDrawVisitor(g2))
    //    renderer.paint(molecule, new AWTDrawVisitor(g2), drawArea, true)
    //    //ImageIO.write(image.asInstanceOf[RenderedImage], "PNG", new File("/opt/CTR2.png"))
    //    val ri = image.asInstanceOf[RenderedImage]
    //    //
    //    val bos = new ByteArrayOutputStream()
    //    javax.imageio.ImageIO.write(ri, "png", bos)
    //    val imageBytes = bos.toByteArray()
    //    val encoder = new BASE64Encoder()
    //    val imageString = encoder.encode(imageBytes)
    //    //println(imageString)
    //    bos.close()
    //    imageString
  }

  private def dbMemo: Memo[String, String] = {
    scalaz.Memo.memo[String, String](f =>
      k => {

        var conncachedb = DriverManager.getConnection(ExtractionEngine.dbURL, ExtractionEngine.dbUser, ExtractionEngine.dbPassword)
        val q = "select * from ops_api_cached_calls where call='" + k + "'"
        val st = conncachedb.createStatement()
        val rs = st.executeQuery(q)
        val v = if (rs.next()) {
          val str = rs.getString(2)
          st.close()
          rs.close()
          str
        } else {
          val r = try {
            val v2 = f(k)
            if (v2 != "") {
              var insertStatement = conncachedb.prepareStatement("insert into ops_api_cached_calls (call,response,timestamp) values (?,?,?);")
              insertStatement.setString(1, k)
              insertStatement.setString(2, v2)
              val today = new java.util.Date()
              val ts = new java.sql.Timestamp(today.getTime())
              insertStatement.setTimestamp(3, ts)
              insertStatement.execute()
              insertStatement.close()
              st.close()
              rs.close()
            }
            v2
          } catch {
            case _: Throwable => {
              val q = "select * from ops_api_cached_calls where call='" + k + "'"
              val st2 = conncachedb.createStatement()
              val rs = st2.executeQuery(q)
              val s = rs.getString(2)
              rs.close()
              st.close()
              st2.close()
              s
            }
          }
          r
        }
        rs.close()
        conncachedb.close()
        v
      })
  }

  val getSDFFromSMiles_CDK_memoed = (smi: String) => this.dbMemo(getSDFFromSMiles_CDK _)

  def getSDFFromSmiles(smile: Option[String], cdk: Boolean): String = {
    smile match {
      case Some(sm) =>
        if (cdk)
          getSDFFromSMiles_CDK(sm)
        else //getSDFFromSMiles_RDKit(sm){
        {
          println("Computing SDF RDKit ...")
          models.chemistry.CompoundUtil.getSDFFromSMILES(sm)
        }

    }
  }

  def getMapFromDataString(data: String) = {
    val data_js = Json.parse(data)
    val jso = data_js.asInstanceOf[play.api.libs.json.JsObject]
    val fs = jso.fieldSet
    val fields_values = for { (f, v) <- fs } yield ({
      val vs = v.asInstanceOf[play.api.libs.json.JsString]
      f -> vs.value
    })
    fields_values
  }

  def openSDFFile(filename: String) = new PrintStream(filename)

  def writeSDFMol_RDKit_sdf2d(out: PrintStream, sdf2d: String) = {
    out.println(sdf2d)
  }

  def writeSDFMol_RDKit_data(out: PrintStream, data: String) = {
    for ((f, v) <- getMapFromDataString(data))
      out.println(">  <" + f + ">\n" + v + "\n\n")
  }

  def writeSDFMol_RDKit_endmol(out: PrintStream) = {
    out.println("$$$$")
  }

  //  def writeSDFMol_RDKit(out:PrintStream, sdf2d : String, data : String)={
  //    out.println(sdf2d) 
  //    for ((f,v) <-  getMapFromDataString(data))
  //      out.println(">  <"+f+">\n"+v+"\n\n")    
  //    out.println("$$$$")
  //  }

  def close_SDF(out: PrintStream) = out.close

  def openSDFFile_FromSDF_RDKit(filename: String) = {
    new org.RDKit.SDWriter(filename)
  }

  def writeSDFMol_RDKit(fw: org.RDKit.SDWriter, sdf2d: String, data: String): Unit = {
    val m = org.RDKit.RWMol.MolFromMolBlock(sdf2d)
    println("SDF2D: " + sdf2d)
    val data_js = Json.parse(data)
    val jso = data_js.asInstanceOf[play.api.libs.json.JsObject]
    val fs = jso.fieldSet
    for { (f, v) <- fs } {
      val vs = v.asInstanceOf[play.api.libs.json.JsString]
      m.setProp(f, vs.value)
    }
    writeSDFMol_RDKit(fw, m)
  }

  private def writeSDFMol_RDKit(fw: org.RDKit.SDWriter, m: org.RDKit.RWMol): Unit = {
    m.compute2DCoords()
    fw.write(m)
    fw.flush()
  }

  def closeSDF_RDKit(fw: org.RDKit.SDWriter) = fw.close()

  def getSDFFile_FromSDF_RDKit(sdf2d: String) = {
    println("SDFFromFile:" + sdf2d)
    val m = org.RDKit.RWMol.MolFromMolBlock(sdf2d)
    println("Atoms: " + m.getAtoms().size())
    val filename = FileUtils.getNewFilename("sdf_smile", ".sdf", tmpDir)
    println("Filename:  " + filename)
    val sw = new org.RDKit.SDWriter(filename)
    m.compute2DCoords()
    sw.write(m)
    sw.flush()
    sw.close()
    filename
  }

  def getSDFFromSMiles_RDKit(smiles: String) = {
    val sdf = try {
      val m = org.RDKit.RWMol.MolFromSmiles(smiles)
      val filename = FileUtils.getNewFilename("sdf_smile", ".sdf", tmpDir)
      val sw = new org.RDKit.SDWriter(filename)
      m.compute2DCoords()
      sw.write(m)
      sw.flush()
      sw.close()
      val fileContent = Files.readAllBytes(FileSystems.getDefault().getPath(filename))
      val file = new File(filename)
      file.delete()
      new String(fileContent)
    } catch {
      case e: Throwable => {
        org.RDKit.RWMol.MolFromSmiles("")
        ""
      }
    }
    sdf
  }

  def getSDFFromSMiles_CDK(smi: String) = {
    import org.openscience.cdk.silent.SilentChemObjectBuilder;
    val sp = CDKutils.smilesParser
    //val sp = new org.openscience.cdk.smiles.SmilesParser(SilentChemObjectBuilder.getInstance())

    var smiles = if (smi.contains(".")) {
      val smis = smi.split('.').sortBy(w => w.size).reverse
      smis.head
    } else {
      smi
    }
    val mol = sp.parseSmiles(smiles)

    val writer = new StringWriter()
    val molSet = new AtomContainerSet()
    val molecule = mol
    //molecule.addAtom(new Atom("C"))
    //molecule.setProperty("foo", "bar")
    molSet.addAtomContainer(molecule)

    val sdfWriter = new SDFWriter(writer)
    val sdfWriterProps = new Properties()
    sdfWriterProps.put("writeProperties", "false")
    sdfWriter.addChemObjectIOListener(new PropertiesListener(sdfWriterProps))
    sdfWriter.customizeJob()
    sdfWriter.write(molSet)
    sdfWriter.close()
    writer.toString()

    //    def molToSDF(mol: IMolecule) = {
    //      var customSettings = new Properties()
    //      var listener = new PropertiesListener(customSettings)
    //      val sdg = new StructureDiagramGenerator()
    //      sdg.setMolecule(mol)
    //      sdg.generateCoordinates()
    //      val molec = sdg.getMolecule()
    //      val sw = new StringWriter()
    //      val mw = new SDFWriter(sw)
    //      mw.addChemObjectIOListener(listener)
    //      mw.customizeJob()
    //      mw.write(molec)
    //      mw.close()
    //      sw.toString()
    //    }
    //    val sp = new SmilesParser(DefaultChemObjectBuilder.getInstance())

    //    val mol = sp.parseSmiles(smiles)
    //    molToSDF(mol)

  }

  //  private def molToSmiles(mol: IMolecule) = {
  //    val smgen = new SmilesGenerator()
  //    smgen.createSMILES(mol)
  //  }

  def grep(file: String, pattern: String) = {
    val lines = io.Source.fromFile(file).getLines()
    val patternfound = lines.map(_.contains(pattern)).reduce(_ || _)
    patternfound
  }

  def getSmilesFromSDF = getSmilesFromSDF_CDK _ ///Ojo revisar

  def getSmilesFromSDF_CDK(sdf: String) = {
    //    val ins = new ByteArrayInputStream(sdf.getBytes())
    //    val reader = new IteratingMDLReader(ins, DefaultChemObjectBuilder.getInstance())
    //    val l = reader.to[List]
    //    val smgen = new SmilesGenerator()
    //    smgen.createSMILES(l.head.asInstanceOf[IMolecule])
    ""
  }

  def getInChIFromSMILES(smiles: String) = {
    //    val factory = org.openscience.cdk.inchi.InChIGeneratorFactory.getInstance()
    //    val sp = new SmilesParser(DefaultChemObjectBuilder.getInstance())
    //    sp.setPreservingAromaticity(false)
    //    val mol = sp.parseSmiles(smiles)
    //    val gen = factory.getInChIGenerator(mol)
    //
    //    val ret = gen.getReturnStatus();
    //    if (ret == INCHI_RET.WARNING) {
    //      //System.out.println("InChI warning: " + gen.getMessage());
    //    } else if (ret != INCHI_RET.OKAY) {
    //      // InChI generation failed
    //      println("InChI failed: " + ret.toString() + " [" + gen.getMessage() + "]");
    //    }
    //    //
    //    val inchi = gen.getInchi()
    //    val inchikey = gen.getInchiKey()
    //    (inchi, inchikey)
    ("", "")
  }

}

object CompoundsFilters {

  val validAtoms = Set("H", "C", "N", "O", "S", "P", "Cl", "I", "Br", "F")

  val filters = Map("NoFiltering" -> ((c: CompoundSimple) => true),
    "LipinskiRo5" -> ((c: CompoundSimple) => c.passesRuleOf5),
    "ValidateAtoms" -> ((c: CompoundSimple) => c.containsValidAtoms(validAtoms)),
    "Activity" -> ((c: CompoundSimple) => c.filterByActivityType("Activity")),
    "IC50" -> ((c: CompoundSimple) => c.filterByActivityType("IC50")),
    "Inhibition" -> ((c: CompoundSimple) => c.filterByActivityType("Inhibition")),
    "Ki" -> ((c: CompoundSimple) => c.filterByActivityType("Ki")))

}

class CompoundSimple(activityType: Option[String], smiles: String) {
  val moleculeCDK = CDKutils.smilesParser.parseSmiles(smiles)

  def passesRuleOf5 = {
    var numviolations = 0
    numviolations = try {
      val ro5Descriptor = new RuleOfFiveDescriptor()
      val calc = ro5Descriptor.calculate(moleculeCDK)
      Integer.valueOf(calc.getValue().toString())
    } catch {
      case _: Throwable => 1
    }
    numviolations < 2
  }
  def filterByActivityType(activityTypeFilter: String): Boolean = activityType.getOrElse("") equals activityTypeFilter

  def containsValidAtoms(validAtoms: Set[String]) =
    {
      val listatoms = moleculeCDK.atoms().toList
      Logger.debug(listatoms.map(atom => atom.getSymbol()).mkString)
      val b = listatoms.map(atom => validAtoms.contains(atom.getSymbol())).fold(true)(_ && _)
      Logger.debug(b.toString())
      b
    }
}

class Compound(activity: es.imim.phi.collector.compounds.Activity) {
  val activ = activity
  val strSmiles = activ.smiles

  val moleculeCDK = CDKutils.smilesParser.parseSmiles(this.strSmiles)

  def containsValidAtoms(validAtoms: Set[String]) =
    {
      //      moleculeCA.getAtomArray().map(atom => validAtoms.contains(atom.getSymbol())).fold(true)(_ && _)
      val listatoms = moleculeCDK.atoms().toList
      Logger.debug(listatoms.map(atom => atom.getSymbol()).mkString)
      val b = listatoms.map(atom => validAtoms.contains(atom.getSymbol())).fold(true)(_ && _)
      Logger.debug(b.toString())
      b
    }
  def passesRuleOf5 = {

    var numviolations = 0
    numviolations = try {
      val calc = CDKutils.ro5Descriptor.calculate(moleculeCDK)
      Integer.valueOf(calc.getValue().toString())
    } catch {
      case _: Throwable => 1
    }
    numviolations < 2
  }

  def filterByActivityType(activityType: String): Boolean = activ.activity_type.getOrElse("") equals activityType

}
