package controllers

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

  import play.api.Play.current
  lazy val config = play.api.Play.configuration

  override def validateUser(authedUser: AuthenticatedUser): Boolean = {
    (authedUser.user.email endsWith ("@guardian.co.uk")) && authedUser.multiFactor
  }

  override lazy val system: String = "composer-restorer"
  override def authCallbackUrl: String = RestorerConfig.hostName + "/oauthCallback"
  override lazy val domain: String = RestorerConfig.domain


  override lazy val awsCredentials =
    for (key <- RestorerConfig.accessKey;
      secret <- RestorerConfig.secretKey)
      yield { new BasicAWSCredentials(key, secret) }
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
}
