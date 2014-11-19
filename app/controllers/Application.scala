package controllers

import play.api._
import play.api.mvc._
import scala.concurrent.Future

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

  def index = AuthAction {
    Ok(views.html.Application.index())
  }

  def find(url: String) = AuthAction {
    // Find content versions
    
    // Redirect to versions list for this piece of content
  }

  def lookupById(id:String) = AuthAction.async {request =>
    Future.successful(Ok(s"A message $id ${request.user}"))
  }
}
