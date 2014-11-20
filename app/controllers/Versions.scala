package controllers

import play.api.mvc._
import play.api.libs.json.Json

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import s3.S3
import com.amazonaws.auth.policy.actions.S3Actions

import scala.io.Source

object Versions extends Controller with PanDomainAuthActions {
  // List versions
  def index(contentId: String) = AuthAction {
    val s3 = new S3

    val draftVersions = s3.listDraftForId(contentId)
    val liveVersions = s3.listLiveForId(contentId)

    Ok(views.html.Versions.index(contentId, draftVersions, liveVersions))
  }

  // Show a specific version
  def show(contentId: String, isLive: Boolean, versionId: String) = AuthAction {
    val s3 = new S3

    val versionObject = if (isLive) s3.getLiveSnapshot(versionId) else s3.getDraftSnapshot(versionId)

    val version = Source.fromInputStream(versionObject.getObjectContent()).mkString

    Ok(Json.parse(version))
  }

  // Restore a specific version
  def restore(contentId: String, isLive: Boolean, versionId: String) = AuthAction {
    Ok("Restoring version ...")
  }
}