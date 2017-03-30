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

case class Activity(
  job_execution_id: Int,
  job_data_raw_id: Int,
//  target_cwiki: String,
//  cs_id: String,
//  relation: String,
//  standard_units: String,
//  standard_value: String,
  activity_type: Option[String],
  smiles: String
//  inchi: String
  ) {
  
//  def isIC50:Boolean= this.isActivityType("IC50")  
//  
//  def isActivityType(activityType:String)= 
//  {
//    val at=this.activity_type.getOrElse("")    
//    at eq activityType   
//  }
}