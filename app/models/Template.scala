package models

import play.api.libs.json._
import org.joda.time.format.ISODateTimeFormat
import scala.collection.JavaConverters._
import scala.io.Source

import aws.s3._

/* Template is just strings. We repreent the contentRaw as an enormous string
 * so we don't have to copy any models over or anything
 * */
case class Template(title: String, dateCreated: String, contents: String)
case class TemplateSummary(title: String, dateCreated: String)

object Template {
  import play.api.Play.current

  lazy val config = play.api.Play.configuration
  lazy val bucket: String = config.getString("templates.bucket").get

  lazy val s3 = new S3()

  implicit val templateWrites: Writes[Template] = new Writes[Template] {

    def writes(template: Template): JsValue = {
      Json.toJson(Map(
        "title" -> template.title,
        "dateCreated" -> template.dateCreated,
        "contents" -> template.contents
      ))
    }
  }

  def save(template: Template) = {
    // for now defer to S3 but it might go to mongo

    s3.saveItem(
      bucket,
      template.title + "_" + template.dateCreated,
      Json.stringify(Json.toJson(template)))
  }
  def retrieveAll() = {
    val req = s3.objectRequest(bucket)
    val objects = s3.getObjects(req).getObjectSummaries.asScala.toList
    val keys = objects.map(x => x.getKey())
    val results = keys.map(s3.getObject(_, bucket))
    results.map({ x =>
      val json = Json.parse(Source.fromInputStream(x.getObjectContent(), "UTF-8").mkString)
      TemplateSummary(
        (json \ "title").as[String],
        (json \ "dateCreated").as[String])
    })
  }

  def retrieve(id: String) = {
    val result = s3.getObject(id, bucket)
    val json = Json.parse(Source.fromInputStream(result.getObjectContent(), "UTF-8").mkString)
    Template(
        (json \ "title").as[String],
        (json \ "dateCreated").as[String],
        (json \ "contents").as[String])
  }
}


object TemplateSummary {
  implicit val templateSummaryWrites: Writes[TemplateSummary] = new Writes[TemplateSummary] {
    def writes(template: TemplateSummary): JsValue = {
      Json.toJson(Map(
        "title" -> template.title,
        "dateCreated" -> template.dateCreated
      ))
    }
  }
}
