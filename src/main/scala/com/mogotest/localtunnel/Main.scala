package com.mogotest.localtunnel

import java.io.File;
import collection.JavaConversions._

import ch.ethz.ssh2.Connection
import com.beust.jcommander.{ParameterException, JCommander, Parameter}


object Main
{
  object Args
  {
    @Parameter(names = Array("-k", "--key"), description = "Upload a public key for authentication", required = true)
    var privateSshKey: File = null

    @Parameter(names = Array("--tunnel_host"), description = "Connect to the named tunnel host (advanced debug mode)", hidden = true)
    var tunnelHost: String = "open.mogotunnel.com:8888"

    @Parameter(names = Array("--api_host"), description = "Connect to the named API host for registration (advanced debug mode)", hidden = true)
    var apiHost: String = "mogotest.com"

    @Parameter(names = Array("-h", "--help"), description = "Show this help message.")
    var showHelp: Boolean = false

    @Parameter
    var args: java.util.List[String] = null
  }

  def main(args: Array[String])
  {
    // Scala can't disambiguate method arity when varargs are present.  So, we have to use reflection to find the correct
    // constructor from Java directly.
    val unambiguousConstructor = classOf[JCommander].getConstructors.filter(_.getParameterTypes.length == 1).head
    val parser = unambiguousConstructor.newInstance(Args).asInstanceOf[JCommander]
    parser.setProgramName("java -jar mogotest.jar <mogotest_api_key> <hostname_to_test> <local_port>")

    try
    {
      parser.parse(args.toArray: _*)
    }
    catch
    {
      case e: ParameterException => { println(e.getMessage); println(parser.usage); sys.exit(-1) }
    }

    if (Args.showHelp)
    {
      println(parser.usage)
      sys.exit(0)
    }

    if ((Args.args == null) || (Args.args.length != 3))
    {
      println(parser.usage)
      sys.exit(-1)
    }

    val apiKey = Args.args(0)
    val testHost = Args.args(1)
    val reflectedConnection = Args.args(2)

    val reflectedHost = if (reflectedConnection.contains(':')) reflectedConnection.split(':').head else "127.0.0.1"
    val reflectedPort = if (reflectedConnection.contains(':')) reflectedConnection.split(':').last.toInt else reflectedConnection.toInt

    val conn = new Connection("open.mogotunnel.com")
    conn.connect

    val isAuthenticated = conn.authenticateWithPublicKey("localtunnel", Args.privateSshKey, null)

    if (isAuthenticated)
    {
      conn.requestRemotePortForwarding("127.0.0.1", 30003, reflectedHost, reflectedPort)
    }
    else
    {
      println("Invalid SSH key supplied.")
      println(parser.usage)
      sys.exit(-1)
    }

    Thread.sleep(60000)
  }
}