package es.imim.phi.collector.test

import es.imim.phi.collector.engine.ExtractionEngine
import es.imim.phi.collector.ops.lda_api.OPSLDAScala
import java.io.PrintWriter
import java.io.File

object testOPSAPI extends App {

  def run() = {
    System.load("/opt/collector/lib/libGraphMolWrap.so")

    ExtractionEngine.initEngine("/opt/collector")
    println("Cached: " + ExtractionEngine.cachedapi)
    val opsAPI = new OPSLDAScala(
      ExtractionEngine.OPSAPIURL,
      ExtractionEngine.appKey,
      ExtractionEngine.appId,
      true,
      ExtractionEngine.dbURL, ExtractionEngine.dbUser,
      ExtractionEngine.dbPassword,
      ExtractionEngine.cachedapi)
    val target1 = "http://www.conceptwiki.org/concept/9363a402-9bbe-4a3c-b2ca-01060c59fb8a"
    val targetTest = "http://www.conceptwiki.org/concept/00059958-a045-4581-9dc5-e5a08bb0c291"
    val hERG = "http://www.conceptwiki.org/concept/d2a9dbd1-78fb-4f2f-adba-9be028a15bd1"

    //val r = opsAPI.GetPharmacologyByTargetLDA(hERG)
    //for ((row, i) <- r.zipWithIndex) {
    //println(i)
    //      println(row("smiles"))
    //}
    //r
    //opsAPI.GetPharmacologyByTargetLDA_count(hERG)
    ExtractionEngine.executejob(1)
    es.imim.phi.collector.model.database_eTOXOPS.jobExecutionDelete(64)
  }

  //  def mapTextToURLS(originalRow : String, text : String, separator : Char) = {
  //
  //    def filter(urls : String, pattern : String => Boolean) = urls.split(',').filter(pattern).mkString(",")
  //
  //    def patterncs(url : String) = url.contains("chemspider")
  //
  //    def patternchembl(url : String) = url.contains("chembl")
  //
  //    println("Mapping: "+text)
  //    val r = opsAPI.CW_Search_Compound(text)
  //    val outlines = for (info <- r) yield (originalRow + separator + info("altLabel") + separator + filter(info("urls"), patterncs) + separator + filter(info("urls"), patternchembl))
  //    outlines.mkString("\n")+"\n"
  //  }
  //
  //  def mapSMILESToURLS(originalRow : String, smiles : String, separator : Char) = {
  //    def filter(urls : String, pattern : String => Boolean) = urls.split(',').filter(pattern).mkString(",")
  //    val r = try {
  //      opsAPI.GetCompoundbySMILES(smiles, false)
  //    } catch {
  //      case _ : Throwable => List("Not found")
  //    }
  //    val r2 = try {
  //      opsAPI.GetCompoundbySMILES(smiles, true)
  //    } catch {
  //      case _ : Throwable => List("Not found")
  //    }
  //    println("Result: "+r)
  //    println("Result: "+r.size)
  //    val rr = originalRow.split(separator)
  //    val rr2 = rr.mkString(separator.toString())
  //    val zped = r.zipAll(r2, "", "")
  //    val outlines = for ((u1, u2) <- zped)
  //      yield (rr2 + separator + u1 + separator + u2)
  //    outlines.mkString("\n")+"\n"
  //  }
  //
  //  def readCSV_textSearch(inputFilename : String, outputFilename : String, separator : Char, positionField : Int) = {
  //    val writer = new PrintWriter(new File(outputFilename))
  //    val lines = scala.io.Source.fromFile(inputFilename, "utf-8").getLines
  //    for (line <- lines) {
  //      val row = line.split(separator)
  //      writer.write(mapTextToURLS(line, row(positionField), separator))
  //    }
  //    writer.close()
  //  }
  //
  //  def readCSV_SMILESSearch(inputFilename : String, outputFilename : String, separator : Char, positionField : Int) = {
  //    val writer = new PrintWriter(new File(outputFilename))
  //    val lines = scala.io.Source.fromFile(inputFilename, "utf-8").getLines
  //    var i = 0
  //    val result = for (line <- lines) yield ({
  //      i += 1
  //      val row = line.split(separator)
  //      println(i+"-SMILES: "+row(positionField))
  //      mapSMILESToURLS(line, row(positionField), separator)
  //    })
  //    for (l <- result.toSeq)
  //      writer.write(l)
  //    writer.close()
  //  }
  //
  //  //readCSV_textSearch("/media/sf_oriol/eTOX/searchdataOPSforDIPL/caco2-dummy.csv", "/media/sf_oriol/eTOX/searchdataOPSforDIPL/caco2-dummy_plusID.csv", '\t', 0)
  //  readCSV_SMILESSearch("/media/sf_oriol/eTOX/searchdataOPSforDIPL/Kruhlak_2008_phospholipidosis_data_md_plusIDfromSmiles.csv", "/media/sf_oriol/eTOX/searchdataOPSforDIPL/Kruhlak_2008_phospholipidosis_data_md_plusIDfromSmiles2.csv", ';', 1)

  //val cmps = opsAPI.GetCompoundbySMILES("CNC(=O)c1cc(ccn1)Oc2ccc(cc2)NC(=O)Nc3ccc(c(c3)C(F)(F)F)Cl", false)
  //println("CMPS: "+cmps)

  //  val listcmps = List("CC(=O)NC1=C(I)C=C(I)C(C([O-])=O)=C1I", "NC1=NC=NC2=C1N=CN2")
  //  for (smiles <- listcmps) {
  //    //println(opsAPI.GetCompoundbySMILES(smiles, true))
  //    println(mapSMILESToURLS("Row#", smiles, '|'))
  //  }

}