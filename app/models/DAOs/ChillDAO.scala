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

  def addChill(slackRef: String, positive: Long, negative: Long): Future[Unit] = {
    val actions = (for {
      chillScoreOpt <- chillaxTable.filter(_.slackRef === slackRef).join(chillScoreTable).on(_.chillScoreId === _.id).map(_._2).result.headOption
      addChillAndGetScore = ((chillScoreTable returning chillScoreTable.map(_.id)) += ChillScoreDBRow(id = ChillScoreId(0l), positive = 0l, negative = 0l)).flatMap { chillScoreId =>
        chillaxTable.insertOrUpdate(ChillaxTableDBRow(id = ChillaxId(0l), slackRef = slackRef, chillScoreId = chillScoreId)).map(_ => ChillScoreDBRow(id = chillScoreId, positive = 0l, negative = 0l))
      }
      chillScore <- chillScoreOpt.map(DBIO.successful).getOrElse(addChillAndGetScore)
      _ <- chillScoreTable.filter(_.id === chillScore.id).map(x => (x.positive, x.negative)).update(chillScore.positive + positive, chillScore.negative + negative)
    } yield()).transactionally

    db.run(actions)
  }

  def getChills: Future[Seq[ChillaxData]] = {
    val q = chillaxTable
      .join(chillScoreTable)
      .on(_.chillScoreId === _.id)
      .map(x => (x._1.slackRef, (x._2.positive, x._2.negative) <> ((ChillScoreData.apply _).tupled, ChillScoreData.unapply)) <> ((ChillaxData.apply _).tupled, ChillaxData.unapply))
      .result

    db.run(q)
  }
}
