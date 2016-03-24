package es.imim.phi.collector.compounds

class CompoundFilterList(fs: List[CompoundFilter]) extends CompoundFilter {
  val filters = fs

  override def toString = fs.map(_.toString()).mkString("/")
  
  def filterPass(compound: Compound): Boolean = {
    val passed = fs.map(filter => filter.filterPass(compound))
    passed.fold(true)((a, b) => a & b)
  }

}