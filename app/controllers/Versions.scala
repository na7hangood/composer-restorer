package controllers

import com.amazonaws.services.s3.model.S3Object
import play.api.mvc._
import play.api.libs.json.Json

import s3.S3

import scala.io.Source

object Versions extends Controller with PanDomainAuthActions {
  // List versions
  def index(contentId: String) = AuthAction {
    val s3 = new S3
    val draftVersions = s3.listDraftForId(contentId)

    // We use the last snapshot's headline.
    val snapshotPath = draftVersions.last
    val headline = (Json.parse(snapShotAsString(s3.getDraftSnapshot(snapshotPath))) \ "preview" \ "fields" \ "headline").asOpt[String]

    Ok(views.html.Versions.index(contentId, draftVersions, headline getOrElse ""))
  }

  // Show a specific version
  def show(contentId: String, isLive: Boolean, versionId: String) = AuthAction {
    val s3 = new S3
    val versionPath = versionId

    val snapshot = if (isLive) s3.getLiveSnapshot(versionPath) else s3.getDraftSnapshot(versionPath)

    Ok(snapShotAsString(snapshot))
  }

  def snapShotAsString(snapshot: S3Object) = {
    Source.fromInputStream(snapshot.getObjectContent(), "UTF-8").mkString
  }

}