package models.DAOs

import javax.inject.Inject
import models.tables._
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

class ChillDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ex: ExecutionContext) extends ChillaxTable with ChillScoreTable {

  import profile.api._

  def getChill(slackRef: String): Future[Long] = {
    val q = chillaxTable
      .filter(_.slackRef === slackRef)
      .join(chillScoreTable)
      .on(_.chillScoreId === _.id)
      .map(x => x._2.positive - x._2.negative)
      .result.headOption.map(_.getOrElse(0l))

    db.run(q)
  }

  def addChill(slackRef: String, positive: Long, negative: Long): Future[Int] = {
    val q = chillaxTable.filter(_.slackRef === slackRef)
      .join(chillScoreTable)
      .on(_.chillScoreId === _.id)
      .map(_._2).result.headOption

      db.run(q).flatMap { chillScoreOpt =>
        chillScoreOpt.map { chillScore =>
          db.run(chillScoreTable.filter(_.id === chillScore.id).map(x => (x.positive, x.negative)).update(chillScore.positive + positive, chillScore.negative + negative))
        }.getOrElse(Future.successful(1))
      }
  }

  def addChillax(slackRef: String): Future[Int] = {
    val q = ((chillScoreTable returning chillScoreTable.map(_.id)) += ChillScoreDBRow(id = ChillScoreId(0l), positive = 0l, negative = 0l)).flatMap { chillScoreId =>
      chillaxTable.insertOrUpdate(ChillaxTableDBRow(id = ChillaxId(0l), slackRef = slackRef, chillScoreId = chillScoreId))
    }

    db.run(q)
  }
  
}
