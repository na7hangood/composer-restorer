name := "composer-restorer"

version := "0.0.1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  "com.gu" %% "pan-domain-auth-play" % "0.1.8",
  ws
)
