Introduction
============

This utility allows for secure remote tunnel connections to the [Mogotest](http://mogotest.com/) service.  For more
information on how to use this tool, please see the [Mogotest documentation](http://docs.mogotest.com/display/HOWTO/Test+a+Site+on+a+Private+Network).

Building
========

This project will produced a fat JAR including all the necessary dependencies.  In order to build the project you must
have SBT >= 0.12.x installed.  Then you can check out the project and run:

    $ sbt proguard

In the `target/` directory you'll find both the skinny and fat JARs.  The fat one will be named
`mogotest-ssh-tunnel-client-VERSION.min.jar`.

While both JARs will work fine, the fat JAR is preferable in most cases because no classpath management is necessary.
I.e., all you need is this single, executable JAR in order to run the Mogotest tunnel tool.