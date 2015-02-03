package controllers

import com.amazonaws.services.s3.model.S3Object
import org.joda.time.DateTime
import play.api.mvc._
import play.api.libs.json.Json
import scala.collection.JavaConversions._

import s3.S3

import scala.io.Source

case class Snapshot(key: String) {
  lazy val savedAt: DateTime = new DateTime(key.split("/").last)
}

object Versions extends Controller with PanDomainAuthActions {
  // List versions
  def index(contentId: String) = AuthAction {
    val s3 = new S3
    val draftVersionKeys = s3.listDraftForId(contentId)

    val draftVersions = draftVersionKeys.map(Snapshot)

    if (draftVersions != Nil) {
      // We use the last snapshot's headline.
      val snapshot = draftVersions.lastOption
      val headline = snapshot.flatMap { ss =>
        (Json.parse(snapShotAsString(s3.getDraftSnapshot(ss.key))) \ "preview" \ "fields" \ "headline").asOpt[String]
      }

      Ok(views.html.Versions.index(contentId, draftVersions, headline getOrElse ""))
    } else {
      Ok(views.html.Versions.index(contentId, draftVersions, ""))
    }

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