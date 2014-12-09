package controllers

import play.api.mvc._
import play.api.libs.json.Json

import s3.S3

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
    val versionPath = versionId

    val versionObject = if (isLive) s3.getLiveSnapshot(versionPath) else s3.getDraftSnapshot(versionPath)
    val version = Source.fromInputStream(versionObject.getObjectContent(), "UTF-8").mkString

    Ok(version)
  }

}