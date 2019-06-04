package models.daos.tables

import com.mohiva.play.silhouette.api.LoginInfo
import models._
import models.enums.RoleId

trait UserDAOTables extends DAOSlick {

  import profile.api._

  class UserDBTable(tag: Tag) extends Table[DBUser](tag, "User") {
    def id = column[UserID]("id", O.PrimaryKey)
    def name = column[String]("name")
    def email = column[String]("email")
    def roleId = column[RoleId]("roleId")
    def * = (id, name, email, roleId) <> ((DBUser.apply _).tupled, DBUser.unapply)

  }

  case class DBLoginInfo(id: Option[Long],
                         providerID: String,
                         providerKey: String)

  class LoginInfoDBTable(tag: Tag) extends Table[DBLoginInfo](tag, "LoginInfo") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def providerID = column[String]("providerID")
    def providerKey = column[String]("providerKey")
    def * = (id.?, providerID, providerKey) <> (DBLoginInfo.tupled, DBLoginInfo.unapply)
  }

  case class DBUserLoginInfo(id: Option[Long],
                             userID: UserID,
                             loginInfoId: Long)

  class UserLoginInfoDBTable(tag: Tag) extends Table[DBUserLoginInfo](tag, "UserLoginInfo") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def userID = column[UserID]("userID")
    def loginInfoId = column[Long]("loginInfoId")
    def * = (id.?, userID, loginInfoId) <> (DBUserLoginInfo.tupled, DBUserLoginInfo.unapply)
  }

  lazy val slickUser = TableQuery[UserDBTable]
  lazy val slickLoginInfo = TableQuery[LoginInfoDBTable]
  lazy val slickUserLoginInfo = TableQuery[UserLoginInfoDBTable]

  def loginInfoQuery(loginInfo: LoginInfo): Query[LoginInfoDBTable, DBLoginInfo, Seq] =
    slickLoginInfo.filter(dbLoginInfo => dbLoginInfo.providerID === loginInfo.providerID && dbLoginInfo.providerKey === loginInfo.providerKey)
}
