package conscript

import sbt._
import Keys._

object Harness extends Plugin {
  val conscriptBase = SettingKey[File]("conscript-base")
  val conscriptOutput = SettingKey[File]("conscript-output")
  val conscriptBoot = SettingKey[File]("conscript-boot")
  val csWrite = TaskKey[Unit]("cs-write",
      "Write test launchconfig files to conscript-output")
  val csRun = InputKey[Unit]("cs-run",
      "Run a named launchconfig, with parameters")
  val conscriptSettings: Seq[Def.Setting[_]] = Seq(
    resolvers += Classpaths.typesafeReleases,
    libraryDependencies += {
      "org.scala-sbt" % "launcher-interface" % "1.0.0" % "provided"},
    conscriptBase := ((sourceDirectory in Compile).value / "conscript"),
    conscriptOutput := (target.value / "conscript"),
    conscriptBoot := (conscriptOutput.value / "boot"),
    csWrite := csWriteTask.value,
    csRun := csRunTask.evaluated,
    csRun := csRun.dependsOn(csWrite, publishLocal),
    (aggregate in csRun) := false
  )
  private def configs(path: File) = (path ** "launchconfig").get
  private def configName(path: File) =
    new File(path.getParent).getName
  def csWriteTask =
    Def.task[Unit] {
      val base = conscriptBase.value
      val output = conscriptOutput.value
      val boot = conscriptBoot.value
      IO.delete(output)
      IO.copyDirectory(base, output)
      configs(output).map { path =>
        IO.append(path, 
          """
          |[boot]
          |  directory: %s
          |""".stripMargin.format(boot)
        )
      }
    }
  def csRunTask = Def.inputTask {
    import sbt.Process._
    val args = Def.spaceDelimited().parsed
    val config = args.headOption.map { name =>
      configs(conscriptOutput.value).find {
        p => configName(p) == name
      }.getOrElse { sys.error("No launchconfig found for " + name) }
    }.getOrElse { sys.error("Usage: cs-run <appname> [args ...]") }
    "sbt @%s %s".format(config,
                        args.toList.tail.mkString(" ")
    ) ! match {
      case 0 => ()
      case n => sys.error("Launched app error code: " + n)
    }
  }
}
