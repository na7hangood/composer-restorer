package config
import play.api.Play.current
import aws.AwsInstanceTags
import play.api._

object RestorerConfig extends AwsInstanceTags {

  lazy val stage: String = readTag("Stage") match {
    case Some(value) => value
    case None => "DEV" // default to dev stage
  }

  val liveBucket: String = "composer-snapshots-live-" + stage.toLowerCase()
  val draftBucket: String = "composer-snapshots-draft-" + stage.toLowerCase()
  val templatesBucket: String = "composer-templates-" + stage.toLowerCase()

  val domain: String = stage match {
    case "PROD" => ".gutools.co.uk"
    case x => x + ".dev-gutools.co.uk"
  }

  val composerDomain: String = "https://composer" + stage.toLowerCase() +domain

  val hostName: String = "https://composer-restorer" + stage.toLowerCase() + domain

  lazy val config = play.api.Play.configuration

  val accessKey: Option[String] = config.getString("AWS_ACCESS_KEY")
  val secretKey: Option[String] = config.getString("AWS_SECRET_KEY")
  val pandomainKey: Option[String] = config.getString("pandomain.aws.key")
  val pandomainSecret: Option[String] = config.getString("pandomain.aws.secret")

}
