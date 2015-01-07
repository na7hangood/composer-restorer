package models

import play.api.libs.json._
import org.joda.time.format.ISODateTimeFormat

import s3._

/* Template just has a name and contains a JSON object - which is the ContentRaw*/
case class Template(title: String, dateCreated: String, contents: JsValue)


object Template {

  def save(template: Template) = {
    // for now defer to S3 but it might go to mongo
    val s3 = new S3()

    s3.saveItem("composer-templates",
      template.title + template.dateCreated,

      Json.stringify(Json.toJson(Map(
        "title" -> template.title,
        "dateCreated" -> template.dateCreated,
        "contents" -> Json.stringify(template.contents)
      )))
    )
  }
}
