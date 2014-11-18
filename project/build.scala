import play.PlayScala
import sbt._
import Keys._




object myBuild extends Build {

    lazy val mainProject = Project(
        id = "composer-restorer",
        base = file("."),
        settings = Defaults.coreDefaultSettings ++ Seq(
        )
    ).enablePlugins(PlayScala)
}
