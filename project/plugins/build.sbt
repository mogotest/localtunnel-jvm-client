resolvers += "Proguard plugin repo" at "http://siasia.github.com/maven2"

libraryDependencies <+= sbtVersion("com.github.siasia" %% "xsbt-proguard-plugin" % _)

libraryDependencies <+= (sbtVersion) { sv => "com.eed3si9n" %% "sbt-assembly" % ("sbt" + sv + "_0.6") }
