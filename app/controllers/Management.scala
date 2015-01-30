package controllers

import play.api.mvc._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import config.RestorerConfig

object Management extends Controller {

  def healthCheck = Action {
    Ok("Ok")
  }
}
