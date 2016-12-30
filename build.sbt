sbtPlugin := true

name := "conscript-plugin"

organization := "org.foundweekends"

description := "conscript-plugin is an sbt plugin for making conscript apps"

version := "0.3.5"

scalacOptions ++= Seq("-deprecation", "-language:_")

homepage := Some(url("https://github.com/foundweekends/conscript-plugin"))

publishTo :=
  Some("releases" at
       "https://oss.sonatype.org/service/local/staging/deploy/maven2")

licenses := Seq("LGPL v3" -> url("http://www.gnu.org/licenses/lgpl.txt"))

pomExtra := (
  <scm>
    <url>git@github.com:foundweekends/conscript-plugin.git</url>
    <connection>scm:git:git@github.com:foundweekends/conscript-plugin.git</connection>
  </scm>
  <developers>
    <developer>
      <id>n8han</id>
      <name>Nathan Hamblen</name>
      <url>http://github.com/n8han</url>
    </developer>
  </developers>)
