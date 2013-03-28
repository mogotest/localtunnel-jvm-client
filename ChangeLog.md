# 1.2.0 - 2013-03-28

  * Added support for authenticating with a proxy server.
  * Simplified proxy server configuration by synchronizing HTTPS and HTTP proxy info if the latter is not configured.
  * Added "-v" and "--version" flags to print out the version of the tool.
  * Updated Scala 2.9.2 to 2.10.1 to pick up some bug fixes and performance improvements.
  * Updated JCommander from 1.27 to 1.30 to get some bug fixes.
  * Updated JSch from 0.1.48 to 0.1.49 to get some bug fixes and performance improvements.
  * Updated scalaj-http from 0.3.2 to 0.3.6 to get something that will work with Scala 2.10.1.

# 1.1.0 - 2012-10-12

  * Changed the default HTTP tunnel server port from 8888 to 80 to better work out of the box with restrictive networks.
  * Updated Scala 2.9.1 to Scala 2.9.2 to pick up some bug fixes.
  * Updated to SBT 0.12.0 for building the app and updated corresponding SBT plugins.
  * Updated JCommander from 1.18 to 1.27 to get some bug fixes.
  * Updated scalaj-http from 0.2.9 to 0.3.2 to get some bug fixes and Scala 2.9.2 compatibility.
  * Updated JSch from 0.1.44 to 0.1.48 to get some performance enhancements and bug fixes in SSH session handling.

# 1.0.1 - 2012-02-01

  * Increased HTTP timeout values since the defaults were aggressively low.

# 1.0.0 - 2011-09-11

  * Initial release