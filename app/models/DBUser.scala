package models

import models.enums.RoleId
import play.api.libs.json.{Json, OFormat}

case class DBUser(userID: UserID,
                  name: String,
                  email: String,
                  roleId: RoleId
                 )

object DBUser {
  implicit val formatter: OFormat[DBUser] = Json.format[DBUser]
}
