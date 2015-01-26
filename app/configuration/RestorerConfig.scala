package configuration

import play.api.Play.current
import aws.AwsInstanceTags

object RestorerConfig extends AwsInstanceTags {

  lazy val conf = readTag("Stage") match {
    case Some("PROD")    => new ProdConfig
    case Some("CODE")    => new CodeConfig
    case Some("QA")      => new QaConfig
    case Some("RELEASE") => new ReleaseConfig
    case Some("TEST")    => new TestConfig
    case _               => new DevConfig
  }

def apply() = {
conf
}
}

sealed trait Config {
  def stage: String

  def draftBucket: String
  def liveBucket: String

  def accessKey: String
  def secretKey: String

  def pandomainDomain: String
}

class DevConfig extends Config {
  val playConfig = play.api.Play.configuration

  override val stage = "DEV"

  override val draftBucket = playConfig.getString("aws.s3.draftbucket").get
  override val liveBucket = playConfig.getString("aws.s3.livebucket").get

  override val accessKey = playConfig.getString("AWS_ACCESS_KEY").get
  override val secretKey = playConfig.getString("AWS_SECRET_KEY").get

  override val pandomainDomain = "local.dev-gutools.co.uk"
}

class CodeConfig extends Config {
  override val stage = "CODE"

  override val draftBucket = ""
  override val liveBucket = ""

  override val accessKey = ""
  override val secretKey = ""

  override val pandomainDomain = "code.dev-gutools.co.uk"
}

class QaConfig extends Config {
  override val stage = "QA"

  override val draftBucket = ""
  override val liveBucket = ""

  override val accessKey = ""
  override val secretKey = ""

  override val pandomainDomain = "qa.dev-gutools.co.uk"
}

class TestConfig extends Config {
  override val stage = "TEST"

  override val draftBucket = ""
  override val liveBucket = ""

  override val accessKey = ""
  override val secretKey = ""

  override val pandomainDomain = "test.dev-gutools.co.uk" // correct???

}

class ReleaseConfig extends Config {
  override val stage = "RELEASE"

  override val draftBucket = ""
  override val liveBucket = ""

  override val accessKey = ""
  override val secretKey = ""

  override val pandomainDomain = "release.dev-gutools.co.uk"
}

class ProdConfig extends Config {
  override val stage = "PROD"

  override val draftBucket = ""
  override val liveBucket = ""

  override val accessKey = ""
  override val secretKey = ""

  override val pandomainDomain = "dev-gutools.co.uk"
}
