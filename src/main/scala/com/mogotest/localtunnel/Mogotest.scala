package com.mogotest.localtunnel

import scalaj.http.Http

case class Mogotest(apiHost: String, apiKey: String, testHost: String, tunnelUrl: String)
{
  def notifyMogotestOfTunnel()
  {
    val responseCode = Http.post(String.format("https://%s/api/v1/ssh_tunnels", apiHost)).
        params("user_credentials" -> apiKey, "hostname" -> testHost,"tunnel_url" -> tunnelUrl).responseCode

    if (responseCode != 201)
    {
      println("  [Error] Unable to register tunnel location with Mogotest API.  Perhaps your credentials are bad.")
      sys.exit(-1)
    }
  }

  def teardown_tunnel_in_mogotest()
  {
    val responseCode = Http.post(String.format("https://%s/api/v1/ssh_tunnels/destroy", apiHost)).
        params("user_credentials" -> apiKey, "hostname" -> testHost).responseCode

    if (responseCode != 200)
    {
      println("  [Error] Unable to delete tunnel location from Mogotest API.  Perhaps your credentials are bad.")
      sys.exit(-1)
    }
  }
}