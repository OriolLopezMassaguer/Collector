

package es.imim.phi.collector.test

import scala.sys.process._ 

object testQED extends App {
  
  val res=Seq("/usr/bin/python","/opt/collector/scripts/compute_qed.py", "c1ccccc1").!
  println(res)
  println("eo")
  val res2=Seq("/usr/bin/python","/opt/collector/scripts/compute_qed.py", "c1ccccc1").!!
  println(res2)
}