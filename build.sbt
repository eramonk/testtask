import ReleaseTransformations._

lazy val akkaHttpVersion = "10.0.7"
lazy val akkaVersion    = "2.5.2"
lazy val elastic4sVersion = "5.4.0"

enablePlugins(BuildInfoPlugin)

enablePlugins(GitBranchPrompt)

enablePlugins(DockerPlugin)

//enablePlugins(ScalaJSPlugin, WorkbenchPlugin)

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "eramonk",
      scalaVersion    := "2.12.2"
    )),
    name := "todo",
    libraryDependencies ++= Seq(
      "com.sksamuel.elastic4s" %% "elastic4s-core"           % elastic4sVersion,
      "com.sksamuel.elastic4s" %% "elastic4s-circe"          % elastic4sVersion,
      "com.sksamuel.elastic4s" %% "elastic4s-tcp"            % elastic4sVersion,
      "com.sksamuel.elastic4s" %% "elastic4s-streams"        % elastic4sVersion,
      "com.sksamuel.elastic4s" %% "elastic4s-http"           % elastic4sVersion,
      "net.debasishg"          %% "redisclient"              % "3.4",
      "jp.co.bizreach"         %% "elastic-scala-httpclient" % "2.0.6",
      "com.typesafe.akka"      %% "akka-http"                % akkaHttpVersion,
      "com.typesafe.akka"      %% "akka-http-xml"            % akkaHttpVersion,
      "com.typesafe.akka"      %% "akka-http-spray-json"     % akkaHttpVersion,
      "com.typesafe.akka"      %% "akka-stream"              % akkaVersion,
      "com.typesafe.akka"      %% "akka-http-testkit"        % akkaHttpVersion    % Test,
      "info.mukel"             %% "telegrambot4s"            % "3.0.9",
      "org.scalatest"          %% "scalatest"                % "3.0.1"            % Test
//      "org.scala-js"           %%% "scalajs-dom"             % "0.9.1"
    ),
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "org.ra.build"
  )

//dockerAutoPackageJavaApplication(fromImage = "java:8", exposedPorts = Seq(9050))

dockerfile in docker := {
  val jarFile = sbt.Keys.`package`.in(Compile, packageBin).value
  val classpath = (managedClasspath in Compile).value
  val mainclass = mainClass.in(Compile, packageBin).value.getOrElse(sys.error("Expected exactly one main class"))
  val jarTarget = s"/app/${jarFile.getName}"
  val classpathString = classpath.files.map("/app/" + _.getName).mkString(":") + ":" + jarTarget

  new Dockerfile {
    from("java:8")
    add(classpath.files, "/app/")
    add(jarFile, jarTarget)

    run("apt", "update")
    run("apt", "install", "-y", "vim")
    run("apt", "install", "-y", "net-tools")
    run("apt", "install", "-y", "nmap")
    run("wget", "https://packages.chef.io/files/stable/chefdk/2.1.11/debian/8/chefdk_2.1.11-1_amd64.deb", "-P", "/opt")
    run("dpkg", "-i", "/opt/chefdk_2.1.11-1_amd64.deb")

//    //ssh
//    run("apt", "install", "-y", "openssh-server")
//    run("mkdir", "/var/run/sshd")
//    run("echo", "'root:monk'", "|", "chpasswd")
//    run("sed", "-i", "s/PermitRootLogin prohibit-password/PermitRootLogin yes/", "/etc/ssh/sshd_config")
//    run("sed", "s@session/s*required/s*pam_loginuid.so@session optional pam_loginuid.so@g", "-i", "/etc/pam.d/sshd")
//    env("NOTVISIBLE", "in users profile")
//    run("echo", "export VISIBLE=now", ">>", "/etc/profile")
//    expose(22)
//    cmd("chef-client", "--local-mode", "/home/webserver.rb")

//    entryPoint("java", "-cp", classpathString, mainclass)
    entryPoint("./home/init.sh")
  }
}

git.useGitDescribe := true

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,              // : ReleaseStep
  inquireVersions,                        // : ReleaseStep
  runTest,                                // : ReleaseStep
  setReleaseVersion,                      // : ReleaseStep
  commitReleaseVersion,                   // : ReleaseStep, performs the initial git checks
  tagRelease,                             // : ReleaseStep
  releaseStepCommand("dockerBuildAndPush"),
  setNextVersion,                         // : ReleaseStep
  commitNextVersion,                      // : ReleaseStep
  pushChanges                             // : ReleaseStep, also checks that an upstream branch is properly configured
)

packageOptions in (Compile, packageBin) +=  {
  import java.util.jar.{Manifest}
  import java.util.jar.Attributes.Name
  val manifest = new Manifest
  val mainAttributes = manifest.getMainAttributes()
  mainAttributes.put(new Name("Git-Version"), git.gitDescribedVersion.value.getOrElse("Unknown-git-version"))
  mainAttributes.put(new Name("Git-Uncommitted-Changes"), git.gitUncommittedChanges.value.toString)
  Package.JarManifest( manifest )
}

buildInfoKeys ++= Seq[BuildInfoKey](
  "applicationOwner" -> organization.value,
  BuildInfoKey.action("buildTime") { System.currentTimeMillis },
  BuildInfoKey.action("gitVersion") { git.gitDescribedVersion.value.getOrElse("Unknown-git-version") },
  BuildInfoKey.action("releasedVersion") { git.gitUncommittedChanges.value.toString }
)

buildInfoOptions += BuildInfoOption.ToJson

imageNames in docker := Seq(
  ImageName(s"${organization.value}/${name.value}:latest"),
  {
    val baseVersion = version.value
    val actualVersion = if (baseVersion.endsWith("-SNAPSHOT")) baseVersion else "v" + baseVersion
    ImageName(
      namespace = Some(organization.value),
      repository = name.value,
      tag = Some(actualVersion)
    )
  }
)
