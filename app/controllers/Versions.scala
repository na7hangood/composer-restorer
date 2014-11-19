package controllers

import play.api.mvc._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Versions extends Controller with PanDomainAuthActions {
  def index(contentId: String) = AuthAction {
    Ok(views.html.Versions.index(contentId))
  }

  def show(contentId: String, versionId: String) = AuthAction {
    Ok(views.html.Versions.show(contentId, versionId))
  }

  def restore(contentId: String, versionId: String) = AuthAction {
    Ok("Restoring version ...")
  }
}