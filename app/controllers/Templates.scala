package controllers


import play.api.mvc._
import play.api.libs.json._

import org.joda.time.format.ISODateTimeFormat
import org.joda.time.LocalDateTime
import helpers.CORSable

import models.Template
import config.RestorerConfig

object Templates extends Controller with PanDomainAuthActions {

  lazy val composer = RestorerConfig.composerDomain

  def index = CORSable(composer) {
    Action {
      val res = Json.toJson(Template.retrieveAll())
      Ok(res)
    }
  }

  def getTemplate(key: String) = CORSable(composer) {
    Action {
      val res = Json.toJson(Template.retrieve(key))
      Ok(res)
    }
  }

  def saveTemplate = CORSable(composer) {
    Action(parse.json) { request =>

      val template = Template(
        (request.body \ "title").as[String],
        ISODateTimeFormat.dateTime.print(LocalDateTime.now),
        (request.body \ "contents").as[String]
      )

      Template.save(template)

      Ok(Json.toJson("{data : 'saved template'}"))
    }
  }
}
