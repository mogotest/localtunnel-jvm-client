name := "Mogotest SSH Tunnel Client"

version := "1.0.0"

scalaVersion := "2.9.1"

// Disable using the Scala version in output paths and artifacts.
crossPaths := false

resolvers += "JSch plugin repo" at "http://jsch.sf.net/maven2/"

libraryDependencies ++= Seq(
  "com.beust" % "jcommander" % "1.18",
  "com.jcraft" % "jsch" % "0.1.44",
  "org.scalaj" %% "scalaj-http" % "0.2.9"
)

seq(sbtassembly.Plugin.assemblySettings: _*)

seq(ProguardPlugin.proguardSettings :_*)

proguardOptions ++= Seq(
  keepMain("com.mogotest.localtunnel.Main"),
  "-keep class scala.tools.reflect.** { *; }",
  "-keep class java.io.Console { char[] readPassword(); }",
  "-keep class org.xml.sax.EntityResolver",
  "-keep class com.beust.jcommander.** { *; }",
  "-keep class com.jcraft.jsch.** { *; }",
  "-dontnote scala.Enumeration", // Proguard message:  "Note: scala.Enumeration accesses a field 'MODULE$' dynamically"
  "-dontnote org.apache.commons.logging.LogSource", // Proguard message: "Note: org.apache.commons.logging.LogSource: can't find dynamically referenced class org.apache.log4j.Logger"
  "-dontnote org.apache.commons.logging.impl.Log4JLogger" // Proguard message: "Note: org.apache.commons.logging.impl.Log4JLogger: can't find dynamically referenced class org.apache.log4j.Priority"
)