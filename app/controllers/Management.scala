package controllers

import controllers.Application._
import play.api.mvc._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import config.RestorerConfig

object Management extends Controller with PanDomainAuthActions {

  def healthCheck = Action {
    ServiceUnavailable("Deliberately broken")
  }

  def info = AuthAction {
    val info =
      s"""
      |Hostname: ${RestorerConfig.hostName}
      |Composer Domain: ${RestorerConfig.composerDomain}
      |Templates Bucket: ${RestorerConfig.templatesBucket}
      |Snapshots draft bucket: ${RestorerConfig.draftBucket}
      |Snapshots live bucket: ${RestorerConfig.liveBucket}
      |Credentials: ${RestorerConfig.creds}
      |Pandomain Credentials: ${RestorerConfig.pandomainCreds}
      """.stripMargin

    Ok(info)
  }
}
