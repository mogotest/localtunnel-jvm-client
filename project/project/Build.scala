import sbt._

object PluginDef extends Build {
  override def projects = Seq(root)
  lazy val root = Project("plugins", file(".")) dependsOn(proguard)
  lazy val proguard = uri("git://github.com/mogotest/xsbt-proguard-plugin.git")
}
