package controllers

import javax.inject.{Inject, Singleton}
import models.DAOs.ChillDAO
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ChillController @Inject()(
                                 cc: ControllerComponents,
                                 chillDAO: ChillDAO
                               )(implicit exec: ExecutionContext) extends AbstractController(cc) {

  def getChill(slackRef: String): Action[AnyContent] = Action.async { implicit r =>
    chillDAO.getChill(slackRef).map { chill =>
      Ok(Json.toJson(Json.obj("chill" -> chill)))
    }
  }

  def addChillax(slackRef: String): Action[AnyContent] = Action.async { implicit r =>
    chillDAO.addChillax(slackRef).map { _ =>
      NoContent
    }
  }

  def addChill(slackRef: String, positive: Long, negative: Long): Action[AnyContent] = Action.async { implicit r =>
    chillDAO.addChill(slackRef, positive, negative).map { _ =>
      NoContent
    }
  }

  def chillBro: Action[AnyContent] = Action.async(implicit r => Future.successful(Ok("Chill bro!")))
}
