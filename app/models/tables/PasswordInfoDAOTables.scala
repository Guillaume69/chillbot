package models.tables

import models.DAOSlick

trait PasswordInfoDAOTables extends DAOSlick {

  import profile.api._

  case class DBPasswordInfo(id: Option[Long],
                            hasher: String,
                            password: String,
                            salt: Option[String],
                            loginInfoId: Long)

  class PasswordInfoDBTable(tag: Tag) extends Table[DBPasswordInfo](tag, "PasswordInfo") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def hasher = column[String]("hasher")
    def password = column[String]("password")
    def salt = column[Option[String]]("salt")
    def loginInfoId = column[Long]("loginInfoId")
    def * = (id.?, hasher, password, salt, loginInfoId) <> (DBPasswordInfo.tupled, DBPasswordInfo.unapply)
  }

  lazy val slickPasswordInfo = TableQuery[PasswordInfoDBTable]
}
