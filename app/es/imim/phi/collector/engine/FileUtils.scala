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

package es.imim.phi.collector.engine
import java.util.GregorianCalendar
import java.text.SimpleDateFormat
import java.util.Properties
import java.io.FileInputStream
import scala.collection.mutable.HashMap
import scala.collection.immutable.HashSet

object FileUtils {
  def getFile(fileName: String) = {
    io.Source.fromFile(fileName).getLines().toSet
  }

  def readPropertiesFile(file: String) = {
    var defaultProps = new Properties()
    var in = new FileInputStream(file)
    defaultProps.load(in)
    in.close()
    defaultProps
  }

  def getNewFilename(prefix: String, suffix: String, path: String) = {
    val calendar = new GregorianCalendar()
    val sdf = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS")
    val datetime = sdf.format(calendar.getTime())
    path + "/"+prefix + "_" + datetime.toString() + suffix
  }
}