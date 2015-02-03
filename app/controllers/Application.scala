package controllers

import play.api.Logger
import play.api.mvc._

import scala.concurrent.Future

import play.api.data._
import play.api.data.Forms._

// Pan domain
import com.gu.pandomainauth.action.AuthActions
import com.gu.pandomainauth.model.AuthenticatedUser
import helpers.CORSable
import config.RestorerConfig
import com.amazonaws.auth.BasicAWSCredentials

trait PanDomainAuthActions extends AuthActions {
  override def validateUser(authedUser: AuthenticatedUser): Boolean = {
    (authedUser.user.email endsWith ("@guardian.co.uk")) && authedUser.multiFactor
  }

  override lazy val system: String = "composer-restorer"
  override def authCallbackUrl: String = RestorerConfig.hostName + "/oauthCallback"
  override lazy val domain: String = RestorerConfig.domain

  Logger.info(s"panda domain = $domain")
  Logger.info(s"panda key = ${RestorerConfig.pandomainKey}")
  Logger.info(s"panda domain = ${RestorerConfig.pandomainSecret}")

  override lazy val awsCredentials =
    for (key <- RestorerConfig.pandomainKey;
      secret <- RestorerConfig.pandomainSecret)
      yield { Logger.info("using basic credentials from config"); new BasicAWSCredentials(key, secret) }

  if(awsCredentials.isEmpty) Logger.info("falling back to default provider chain for panda")
}


object Application extends Controller with PanDomainAuthActions {

  lazy val composer = RestorerConfig.composerDomain

  val urlForm = Form(
    "url" -> nonEmptyText
  )

  def index = AuthAction {
    Ok(views.html.Application.index())
  }

  def find = AuthAction { implicit request =>
    def extractContentId(url: String) = url
      .split('#').head // remove any hash fragment, e.g. referring to live blog posts
      .split("/").last.trim // get the id

    urlForm.bindFromRequest.fold(
      {errorForm => Redirect(controllers.routes.Application.index)},
      {url => Redirect(controllers.routes.Versions.index(extractContentId(url)))}
    )
  }

  def preflight(routes: String) = CORSable(composer) {
    Action { implicit req =>
      val requestedHeaders = req.headers("Access-Control-Request-Headers")

      NoContent.withHeaders(
        CORSable.CORS_ALLOW_METHODS -> "GET, DELETE, PUT",
        CORSable.CORS_ALLOW_HEADERS -> requestedHeaders)
    }
  }

  def info = AuthAction {
    val info = Seq(
      "Hostname: " + RestorerConfig.hostName,
      "Composer Domain: " + RestorerConfig.composerDomain,
      "Templates Bucket: " + RestorerConfig.templatesBucket,
      "Snapshots draft bucket: " + RestorerConfig.draftBucket,
      "Snapshots live bucket: " + RestorerConfig.liveBucket
    ).mkString("\n")

    Ok(info)
  }

}
