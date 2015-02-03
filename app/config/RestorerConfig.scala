package config

import _root_.aws.AwsInstanceTags
import play.api.Play.current
import play.api._

object RestorerConfig extends AwsInstanceTags {
  Logger.info("RestorerConfig")
//  lazy val stage: String = readTag("Stage") match {
//    case Some(value) => value
//    case None => "DEV" // default to dev stage
//  }
  lazy val stage: String = "CODE"

  Logger.info(s"stage = $stage")

  val liveBucket: String = "composer-snapshots-live-" + stage.toLowerCase()
  val draftBucket: String = "composer-snapshots-draft-" + stage.toLowerCase()
  val templatesBucket: String = "composer-templates-" + stage.toLowerCase()

//  val domain: String = stage match {
//    case "PROD" => "gutools.co.uk"
//    case "DEV" => "local.dev-gutools.co.uk"
//    case x => x.toLowerCase() + ".dev-gutools.co.uk"
//  }
  val domain: String = "code.dev-gutools.co.uk"
  Logger.info(s"domain = $domain")
  val composerDomain: String = "https://composer." + domain

  val hostName: String = "https://composer-restorer." + domain

  lazy val config = play.api.Play.configuration

  val accessKey: Option[String] = config.getString("AWS_ACCESS_KEY")
  val secretKey: Option[String] = config.getString("AWS_SECRET_KEY")
  val pandomainKey: Option[String] = config.getString("pandomain.aws.key")
  val pandomainSecret: Option[String] = config.getString("pandomain.aws.secret")
}
