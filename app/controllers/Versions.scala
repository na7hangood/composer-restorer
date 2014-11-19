package controllers

import play.api.mvc._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Versions extends Controller with PanDomainAuthActions {
  // List versions
  def index(contentId: String) = AuthAction {
    Ok(views.html.Versions.index(contentId))
  }

  // Show a specific version
  def show(contentId: String, versionId: String) = AuthAction {
    Ok(views.html.Versions.show(contentId, versionId))
  }

  // Restore a specific version
  def restore(contentId: String, versionId: String) = AuthAction {
    Ok("Restoring version ...")
  }
}