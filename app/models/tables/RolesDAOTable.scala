package models.tables

import models.{DAOSlick, DBRole}
import models.enums.RoleId

trait RolesDAOTable extends DAOSlick {

  import profile.api._

  class RoleTable(tag: Tag) extends Table[DBRole](tag, "Role") {

    def id = column[RoleId]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def hierarchy = column[Long]("hierarchy")

    def * = (id, name, hierarchy) <> ((DBRole.apply _).tupled, DBRole.unapply)
  }

  lazy val slickRole = TableQuery[RoleTable]
}
