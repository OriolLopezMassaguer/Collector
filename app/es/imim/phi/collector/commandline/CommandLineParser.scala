package es.imim.phi.collector.commandline

import scopt._

case class ConfigCollector(
  mode: String = "",
  jobid: Int = -1,
  jobexecutionid: Int = -1,
  datatoexport: String = "",
  exportformat: String = "",
  filename: String = "",
  uniprotid: String = "",
  uuid: String = "",
  jobdescription: String = "",
  protocolid: String = "",
  seriesdescription: String = "",
  dataseriesid: Int = -1,
  activityfield: String = "",
  activitytypefield: String = "",
  activitytypevalue: String = "",
  activityunitsfield: String = "",
  activityunitsvalue: String = "",
  importfields: Boolean = false,
  exportfields: Boolean = false,
  overwrite: Boolean = false,
  raw: Boolean = true,
  filtered: Boolean = true)

object CommandLineParser {

  val parser = new scopt.OptionParser[ConfigCollector]("Collector") {
    head("Collector", es.imim.phi.collector.engine.ExtractionEngine.version)

    cmd("newjobuniprotid") action { (_, c) =>
      c.copy(mode = "newjobuniprotid")
    } text ("newjobuniprotid is a command.") children {
      opt[String]("uniprotid") action { (x, c) =>
        c.copy(uniprotid = x)
      } text ("uniprotid is string property.\n")
      opt[String]("jobdescription") action { (x, c) =>
        c.copy(jobdescription = x)
      } text ("jobdescription is String property.\n")
      opt[String]("protocolid") action { (x, c) =>
        c.copy(protocolid = x)
      } text ("protocolid is Int property.\n")
    }

    cmd("numactivitiesjob") action { (_, c) =>
      c.copy(mode = "numactivitiesjob")
    } text ("numactivitiesjob is a command.") children {
      opt[Int]("jobid") action { (x, c) =>
        c.copy(jobid = x)
      } text ("jobid is a integer property.\n")
      opt[String]("jobdescription") action { (x, c) =>
        c.copy(jobdescription = x)
      } text ("jobdescription is String property.\n")
    }

    cmd("executejob") action { (_, c) =>
      c.copy(mode = "executejob")
    } text ("executejob is a command.") children {
      opt[Int]("jobid") action { (x, c) =>
        c.copy(jobid = x)
      } text ("jobid is a integer property.\n")
      opt[String]("jobdescription") action { (x, c) =>
        c.copy(jobdescription = x)
      } text ("jobdescription is String property.\n")
    }

    cmd("listfilters") action { (_, c) =>
      c.copy(mode = "listfilters")
    } text ("listfilters is a command.\n")

    cmd("listprotocols") action { (_, c) =>
      c.copy(mode = "listprotocols")
    } text ("listprotocols is a command.\n")

    cmd("executealljobs") action { (_, c) =>
      c.copy(mode = "executealljobs")
    } text ("executealljobs is a command.\n")

    cmd("listjobs") action { (_, c) =>
      c.copy(mode = "listjobs")
    } text ("listjobs is a command.\n")

    cmd("listjobexecutions") action { (_, c) =>
      c.copy(mode = "listjobexecutions")
    } text ("listjobexecutions is a command.") children {
      opt[Int]("jobid") action { (x, c) =>
        c.copy(jobid = x)
      } text ("jobid is a integer property\n")
    }

    cmd("export") action { (_, c) =>
      c.copy(mode = "export")
    } text ("export data to file") children {
      opt[Int]("jobexecutionid") action { (x, c) =>
        c.copy(jobexecutionid = x)
      } text ("jobexecutionid is a integer property")
      opt[String]("datatoexport") action { (x, c) =>
        c.copy(datatoexport = x)
      } text ("datatoexport must be \"activities\" or \"compounds\"")
      opt[String]("exportformat") action { (x, c) =>
        c.copy(exportformat = x)
      } text ("exportformat must be \"sdf\" or \"csv\"\n")
      opt[String]("filename") action { (x, c) =>
        c.copy(filename = x)
      } text ("filename is a string")
      opt[Unit]("raw") action { (_, c) =>
        c.copy(filtered = false)
      } text ("raw is a flag")
      opt[String]("jobdescription") action { (x, c) =>
        c.copy(jobdescription = x)
      } text ("jobdescription is String property.\n")
    }

    cmd("deletejob") action { (_, c) =>
      c.copy(mode = "deletejob")
    } text ("deletejob is a command.") children {
      opt[Int]("jobid") action { (x, c) =>
        c.copy(jobid = x)
      } text ("jobid is a integer property.\n")
    }

    cmd("deletejobexecution") action { (_, c) =>
      c.copy(mode = "deletejobexecution")
    } text ("deletejobexecution is a command.") children {
      opt[Int]("jobexecutionid") action { (x, c) =>
        c.copy(jobexecutionid = x)
      } text ("jobexecutionid is a integer property.\n")
    }

    cmd("deletejobexecutionsforjobid") action { (_, c) =>
      c.copy(mode = "deletejobexecutionsforjobid")
    } text ("deletejobexecutionsforjobid is a command.") children {
      opt[Int]("jobid") action { (x, c) =>
        c.copy(jobid = x)
      } text ("jobid is a integer property.\n")
    }

  }

}