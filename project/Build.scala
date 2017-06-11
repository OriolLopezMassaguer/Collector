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

import sbt._
import Keys._
import play.Play.autoImport._
import PlayKeys._

object ApplicationBuild extends Build {
  val appName = "Collector"
  val appVersion = "1.4.7"

  

  val appDependencies = Seq(
     "com.google.guava" % "guava" % "19.0",
     "org.openscience.cdk" % "cdk-bundle" % "1.5.13",
    "com.github.OriolLopezMassaguer" % "dataframe_2.11" % "1.2.1" classifier "assembly",
    "com.github.scopt" %% "scopt" % "3.3.0",
     "org.postgresql" % "postgresql" % "9.4.1208.jre7",
    "com.typesafe.slick" %% "slick" % "2.1.0",
    "org.scalaz" %% "scalaz-core" % "7.1.3",
    "ch.qos.logback" % "logback-classic" % "1.1.3",
    "uk.ac.ebi.uniprot" % "japi" % "1.0.13",
    ws
)

  //val main = play.Project(appName, appVersion, appDependencies)
  val main = Project(appName, file(".")).enablePlugins(play.PlayScala).settings(
    version := appVersion,
    scalaVersion := "2.11.8",
    maxErrors := 100,
    resolvers += "EBI" at "http://www.ebi.ac.uk/~maven/m2repo",
    ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) },
    libraryDependencies ++= appDependencies)

}
