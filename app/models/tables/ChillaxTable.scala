package models.tables

import com.github.tototoshi.slick.MySQLJodaSupport._
import models.DAOSlick
import org.joda.time.DateTime
import play.api.libs.json.{Json, OWrites}
import slick.lifted.MappedTo

case class ChillaxId(value: Long) extends AnyVal with MappedTo[Long]

case class ChillaxTableDBRow(id: ChillaxId, slackRef: String, created: DateTime = DateTime.now(), updated: DateTime = DateTime.now(), chillScoreId: ChillScoreId)

case class ChillaxData(slackRef: String, score: ChillScoreData)

object ChillaxData {
  implicit val writer: OWrites[ChillaxData] = Json.writes[ChillaxData]
}

trait ChillaxTable extends DAOSlick {

  import profile.api._

  class ChillaxDBTable(tag: Tag) extends Table[ChillaxTableDBRow](tag, "Chillax") {

    def id = column[ChillaxId]("id", O.PrimaryKey, O.AutoInc)
    def slackRef = column[String]("slackRef")
    def created = column[DateTime]("created")
    def updated = column[DateTime]("updated")
    def chillScoreId = column[ChillScoreId]("chillScoreId")
    def * = (id, slackRef, created, updated, chillScoreId) <> (ChillaxTableDBRow.tupled, ChillaxTableDBRow.unapply)
  }

  lazy val chillaxTable = TableQuery[ChillaxDBTable]
}
