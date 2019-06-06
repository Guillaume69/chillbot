package models.tables

import com.github.tototoshi.slick.MySQLJodaSupport._
import models.DAOSlick
import org.joda.time.DateTime
import play.api.libs.json.{Json, OWrites}
import slick.lifted.MappedTo

case class ChillScoreId(value: Long) extends AnyVal with MappedTo[Long]

case class ChillScoreDBRow(id: ChillScoreId, positive: Long, negative: Long, created: DateTime = DateTime.now(), updated: DateTime = DateTime.now())

case class ChillScoreData(positive: Long, negative: Long)

object ChillScoreData {
  implicit val writer: OWrites[ChillScoreData] = Json.writes[ChillScoreData]
}

trait ChillScoreTable extends DAOSlick {

  import profile.api._

  class ChillScoreDBTable(tag: Tag) extends Table[ChillScoreDBRow](tag, "ChillScore") {

    def id = column[ChillScoreId]("id", O.PrimaryKey, O.AutoInc)
    def positive = column[Long]("positive")
    def negative = column[Long]("negative")
    def created = column[DateTime]("created")
    def updated = column[DateTime]("updated")
    def * = (id, positive, negative, created, updated) <> (ChillScoreDBRow.tupled, ChillScoreDBRow.unapply)
  }

  lazy val chillScoreTable = TableQuery[ChillScoreDBTable]
}
