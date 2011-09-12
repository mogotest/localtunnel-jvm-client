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

import scalaj.http.Http
import com.jcraft.jsch.{JSch, KeyPair}
import java.io.ByteArrayOutputStream

class Tunnel(tunnelHost: String, reflectedHost: String, reflectedPort: Int)
{
  val jsch = new JSch()
  val keyPair = KeyPair.genKeyPair(jsch, KeyPair.RSA, 2048)

  def registerTunnel(): ServerResponse =
  {
    val publicKey = new ByteArrayOutputStream()
    keyPair.writePublicKey(publicKey, "localtunnel")

    val res = new ServerResponse(Http.post(String.format("http://%s/", tunnelHost)).params("key" -> publicKey.toString).asString)

    if (res.errorMessage != null)
    {
      println(String.format("  [Error] %s", res.errorMessage))
      sys.exit(-1)
    }

    res
  }

  def startTunnel(tunnelConfig: ServerResponse, mogotest: Mogotest)
  {
    val privateKey = new ByteArrayOutputStream()
    keyPair.writePrivateKey(privateKey)
    jsch.addIdentity(tunnelConfig.user, privateKey.toByteArray, keyPair.getPublicKeyBlob, null)

    val session = jsch.getSession(tunnelConfig.user, tunnelConfig.sshHost)
    session.setConfig("StrictHostKeyChecking", "no")

    session.connect

    if (!session.isConnected)
    {
      println("Invalid SSH key supplied.")
      sys.exit(-1)
    }

    session.setPortForwardingR(tunnelConfig.throughPort, reflectedHost, reflectedPort)

    if (!tunnelConfig.banner.isEmpty)
      println("  " + tunnelConfig.banner)

    println(String.format("   You're good to go. Any tests you run against '%s' on Mogotest will now access the site running on http://%s:%s/.",
      mogotest.testHost, reflectedHost, reflectedPort.toString))

    sys.ShutdownHookThread { session.delPortForwardingR(tunnelConfig.throughPort) }
  }
}