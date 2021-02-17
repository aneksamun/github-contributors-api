val Http4sVersion = "0.21.18"
val CirceVersion = "0.13.0"
val PureConfigVersion = "0.14.0"
val PureConfigRefinedVersion = "0.9.20"
val EnumeratumVersion = "1.6.1"
val ScalaCacheVersion = "0.28.0"
val WiremockVersion = "2.27.2"
val ScalaCheckVersion = "1.15.2"
val ScalaTestScalaCheckVersion = "3.1.0.0-RC2"
val ScalaTestCatsEffectVersion = "0.5.1"
val ScalaTestVersion = "3.2.3"
val LogbackVersion = "1.2.3"

lazy val root = (project in file("."))
  .settings(
    organization := "co.uk.redpixel",
    name := "github-contributors-api",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.4",
    libraryDependencies ++= Seq(
      "org.http4s"             %% "http4s-blaze-server"           % Http4sVersion,
      "org.http4s"             %% "http4s-blaze-client"           % Http4sVersion,
      "org.http4s"             %% "http4s-circe"                  % Http4sVersion,
      "org.http4s"             %% "http4s-dsl"                    % Http4sVersion,
      "io.circe"               %% "circe-generic"                 % CirceVersion,
      "com.github.pureconfig"  %% "pureconfig"                    % PureConfigVersion,
      "eu.timepit"             %% "refined-pureconfig"            % PureConfigRefinedVersion,
      "com.beachape"           %% "enumeratum"                    % EnumeratumVersion,
      "com.github.cb372"       %% "scalacache-cats-effect"        % ScalaCacheVersion,
      "com.github.cb372"       %% "scalacache-caffeine"           % ScalaCacheVersion,
      "org.scalatest"          %% "scalatest"                     % ScalaTestVersion           % Test,
      "org.scalacheck"         %% "scalacheck"                    % ScalaCheckVersion          % Test,
      "org.scalatestplus"      %% "scalatestplus-scalacheck"      % ScalaTestScalaCheckVersion % Test,
      "com.codecommit"         %% "cats-effect-testing-scalatest" % ScalaTestCatsEffectVersion % Test,
      "com.github.tomakehurst" %  "wiremock-jre8"                 % WiremockVersion            % Test,
      "ch.qos.logback"         %  "logback-classic"               % LogbackVersion
    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.10.3"),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1"),
  )

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-language:implicitConversions",
  "-feature",
  "-Xfatal-warnings",
)
