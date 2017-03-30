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
import es.imim.phi.collector.engine.ExtractionEngine
import es.imim.phi.collector.engine.FileUtils

class CompoundFilterCorina extends CompoundFilter {
  val corinapath= ExtractionEngine.appBasePath+ "/external_tools/corina"
  
  val corinaProps = FileUtils.readPropertiesFile(corinapath + "/corina.properties")
  val inputpath= corinaProps.getProperty("inputPath")
  val outputPath= corinaProps.getProperty("outputPath")
  val corinaCommandline= corinaProps.getProperty("corinaCommandLine")

  def filterPass(compound: Compound): Boolean = { 
//    val inputFile = FileUtils.getNewFilename("input", ".sdf", this.inputPath)
//    val outputFile = FileUtils.getNewFilename("output", ".sdf", this.outputPath)
//    val traceFile = FileUtils.getNewFilename("trace", ".trc", this.outputPath)
//    compound.exportToSDF(inputFile.toString())
//    val cmd = corinaCommandline + " " + inputFile.toString() + " " + outputFile.toString() + " " + traceFile.toString()
//    val cmdProc = Runtime.getRuntime().exec(cmd);
//    val retValue = cmdProc.waitFor();
//    var retb = !grep(traceFile, "ERROR")
//    
//    var iFile = new File(inputFile)
//    iFile.delete()
//    var oFile = new File(outputFile)
//    oFile.delete() 
//    var sFile = new File(traceFile)
//    sFile.delete()

//    retb
	  true
  }

  def grep(file: String, pattern: String) = {
    val lines = io.Source.fromFile(file).getLines()
    val patternfound = lines.map(_.contains(pattern)).reduce(_ || _)
    patternfound
  }

}