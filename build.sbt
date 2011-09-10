name := "Mogotest SSH Tunnel Client"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.9.1"

libraryDependencies += "com.beust" % "jcommander" % "1.18" % "compile"

seq(sbtassembly.Plugin.assemblySettings: _*)
