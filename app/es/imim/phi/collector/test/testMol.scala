/*   
     Collector is a tool for obtaining bioactivity data from the Open PHACTS platform.
     Copyright (C) 2013 UPF
     Contributed by Manuel Pastor(manuel.pastor@upf.edu) and Oriol L. Massaguer(olopez@imim.es). 
 
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

package es.imim.phi.collector.test

import org.openscience.cdk.smiles.SmilesParser
import org.openscience.cdk.DefaultChemObjectBuilder
import org.openscience.cdk.io.listener.PropertiesListener
import org.openscience.cdk.layout.StructureDiagramGenerator
import java.util.Properties
import java.io.StringWriter
import org.openscience.cdk.io.MDLV2000Writer

object testMol extends App {

  def smiles2Mol(smiles: String) = {

    val sp = new SmilesParser(DefaultChemObjectBuilder.getInstance())
    sp.setPreservingAromaticity(false)
    val mol = sp.parseSmiles(smiles)
    println(sp.isPreservingAromaticity())

    val sdg = new StructureDiagramGenerator()
    var customSettings = new Properties()
    customSettings.setProperty("WriteAromaticBondTypes", "true")
    var listener = new PropertiesListener(customSettings)
    
    sdg.setMolecule(mol)
    sdg.generateCoordinates()
    val molec = sdg.getMolecule()
    val sw = new StringWriter()
    val mw = new MDLV2000Writer(sw)
    mw.addChemObjectIOListener(listener)
    mw.customizeJob();
    mw.writeMolecule(molec)
    sw.toString()

  }

  println(smiles2Mol("O=S(=O)(c1ccccc1C)NC(=O)c2ccc(c(OC)c2)Cc4c3cc(ccc3n(c4)C)NC(=O)OC5CCCC5"))
}