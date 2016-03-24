package es.imim.phi.collector.compounds

class Nofilter extends CompoundFilter {
  override def toString = "NoFilter"
  def filterPass(compound: Compound): Boolean = true

}