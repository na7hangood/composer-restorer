package controllers

import play.api.mvc._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import s3.S3
import com.amazonaws.auth.policy.actions.S3Actions

object Versions extends Controller with PanDomainAuthActions {
  // List versions
  def index(contentId: String) = AuthAction {
    val s3 = new S3

    val draftVersions = s3.listDraftForId(contentId)
    val liveVersions = s3.listLiveForId(contentId)

    Ok(views.html.Versions.index(contentId, draftVersions, liveVersions))
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