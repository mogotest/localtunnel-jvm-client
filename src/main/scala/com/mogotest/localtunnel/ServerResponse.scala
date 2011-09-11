package com.mogotest.localtunnel

import scala.util.parsing.json._

class ServerResponse(json: String)
{
  private val decoded = JSON.parseFull(json).get.asInstanceOf[Map[String, Any]]

  val host = decoded.getOrElse("host", null).asInstanceOf[String]
  val throughPort = decoded.getOrElse("through_port", -1).asInstanceOf[Double].toInt
  val banner = decoded.getOrElse("banner", null).asInstanceOf[String]
  val user = decoded.getOrElse("user", null).asInstanceOf[String]
  val errorMessage = decoded.getOrElse("error", null).asInstanceOf[String]

  val sshHost = if (host == null) null else host.split(':').head
}