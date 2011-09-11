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

  def startTunnel(tunnelConfig: ServerResponse)
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

    println(String.format("  Host %s on port %s is now publicly accessible from http://%s/ ...",
      reflectedHost, reflectedPort.toString, tunnelConfig.host))

    sys.ShutdownHookThread { session.delPortForwardingR(tunnelConfig.throughPort) }
  }
}