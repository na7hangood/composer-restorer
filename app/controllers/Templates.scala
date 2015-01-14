package controllers


import play.api.mvc._
import play.api.libs.json._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.LocalDateTime

import models.Template

/* Must allow cross origin requests for the templating service */
case class CORSable[A](origins: String*)(action: Action[A]) extends Action[A] {
  def apply(request: Request[A]): Future[Result] = {

    val headers = request.headers.get("Origin").map { origin =>
      if(origins.contains(origin)) {
        List("Access-Control-Allow-Origin" -> origin, "Access-Control-Allow-Credentials" -> "true")
      } else { Nil }
    }

    action(request).map(_.withHeaders(headers.getOrElse(Nil) :_*))
  }

  lazy val parser = action.parser
}


object CORSable {
  val CORS_ALLOW_ORIGIN = "Access-Control-Allow-Origin"
  val CORS_CREDENTIALS = "Access-Control-Allow-Credentials"
  val CORS_ALLOW_METHODS = "Access-Control-Allow-Methods"
  val CORS_ALLOW_HEADERS = "Access-Control-Allow-Headers"
}



object Templates extends Controller with PanDomainAuthActions {

  lazy val composer = config.getString("composer.domain").get

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

  def preflight(routes: String) = CORSable(composer) {
    Action { implicit req =>
      val requestedHeaders = req.headers("Access-Control-Request-Headers")

      NoContent.withHeaders(
        CORSable.CORS_ALLOW_METHODS -> "GET, DELETE, PUT",
        CORSable.CORS_ALLOW_HEADERS -> requestedHeaders)
    }
  }

}
