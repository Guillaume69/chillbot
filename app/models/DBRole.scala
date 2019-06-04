package models

import models.enums.RoleId
import play.api.libs.json.{Json, OFormat}

case class DBRole(id: RoleId, name: String, hierarchy: Long)

object DBRole {
  implicit val formatter: OFormat[DBRole] = Json.format[DBRole]
}
