/**
 * Copyright 2011 Mogoterra, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mogotest.localtunnel

import collection.JavaConversions._
import java.net.{Authenticator, PasswordAuthentication}
import com.beust.jcommander.{ParameterException, JCommander, Parameter}


object Main
{
  object Args
  {
    @Parameter(names = Array("--tunnel_host"), description = "Connect to the named tunnel host (advanced debug mode)", hidden = true)
    var tunnelHost: String = "open.mogotunnel.com:80"

    @Parameter(names = Array("--api_host"), description = "Connect to the named API host for registration (advanced debug mode)", hidden = true)
    var apiHost: String = "mogotest.com"

    @Parameter(names = Array("-h", "--help"), description = "Show this help message.", hidden = true)
    var showHelp: Boolean = false

    @Parameter(names = Array("-v", "--version"), description = "Print out the version.", hidden = true)
    var showVersion: Boolean = false

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
      case e: ParameterException => { println(e.getMessage); parser.usage; sys.exit(-1) }
    }

    if (Args.showHelp)
    {
      parser.usage
      sys.exit(0)
    }

    if (Args.showVersion)
    {
      println("1.2.0")
      sys.exit(0)
    }

    if ((Args.args == null) || (Args.args.length != 3))
    {
      parser.usage
      sys.exit(-1)
    }

    val apiKey = Args.args(0)
    val testHost = Args.args(1)
    val reflectedConnection = Args.args(2)

    val reflectedHost = if (reflectedConnection.contains(':')) reflectedConnection.split(':').head else "127.0.0.1"
    val reflectedPort = if (reflectedConnection.contains(':')) reflectedConnection.split(':').last.toInt else reflectedConnection.toInt

    // Sync HTTPS and HTTP proxy settings if one isn't specified.
    if ((System.getProperty("http.proxyHost") == null) && (System.getProperty("https.proxyHost") != null))
    {
      System.setProperty("http.proxyHost", System.getProperty("https.proxyHost"))
    }

    if ((System.getProperty("http.proxyPort") == null) && (System.getProperty("https.proxyPort") != null))
    {
      System.setProperty("http.proxyPort", System.getProperty("https.proxyPort"))
    }

    val proxyUsername = System.getProperty("https.proxyUsername")
    val proxyPassword = System.getProperty("https.proxyPassword")

    // Set up proxy authentication if credentials were supplied.
    if ((proxyUsername != null) && (proxyPassword != null))
    {
      Authenticator.setDefault(new Authenticator
      {
        override def getPasswordAuthentication : PasswordAuthentication = new PasswordAuthentication(proxyUsername, proxyPassword.toCharArray)
      })
    }

    val tunnel = new Tunnel(Args.tunnelHost, reflectedHost, reflectedPort)
    val response = tunnel.registerTunnel

    val mogotest = new Mogotest(Args.apiHost, apiKey, testHost, response.host)
    mogotest.notifyMogotestOfTunnel

    sys.addShutdownHook { mogotest.teardownTunnelInMogotest }

    try
    {
      tunnel.startTunnel(response, mogotest)
    }
    catch
    {
      case e: Exception => { println(e.getMessage); parser.usage; sys.exit(-1) }
    }

    while (true)
      Thread.sleep(1000)
  }
}