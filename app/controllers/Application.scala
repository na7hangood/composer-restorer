package controllers

import play.api.mvc._

import scala.concurrent.Future
import s3.S3
import com.amazonaws.auth.policy.actions.S3Actions
import play.api.data._
import play.api.data.Forms._

// Pan domain
import com.gu.pandomainauth.action.AuthActions
import com.gu.pandomainauth.model.AuthenticatedUser

trait PanDomainAuthActions extends AuthActions {

  import play.api.Play.current
  lazy val config = play.api.Play.configuration

  override def validateUser(authedUser: AuthenticatedUser): Boolean = {
    (authedUser.user.email endsWith ("@guardian.co.uk")) && authedUser.multiFactor
  }

  override def authCallbackUrl: String = "https://" + config.getString("host").get + "/oauthCallback"

  override lazy val domain: String = config.getString("pandomain.domain").get
  override lazy val awsSecretAccessKey: String = config.getString("pandomain.aws.secret").get
  override lazy val awsKeyId: String = config.getString("pandomain.aws.key").get
  override lazy val system: String = "composer-restorer"
}

object Application extends Controller with PanDomainAuthActions {

  val urlForm = Form(
    "url" -> nonEmptyText
  )
  def index = AuthAction {
    Ok(views.html.Application.index())
  }

  def find = AuthAction { implicit request =>
    def extractContentId(url: String) = url.split("/").last

    urlForm.bindFromRequest.fold(
    {errorForm => Redirect(controllers.routes.Application.index)},
    {url => Redirect(controllers.routes.Versions.index(extractContentId(url)))}
    )
  }
}
