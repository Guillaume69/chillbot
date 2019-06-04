package models

import java.util.UUID

import ai.x.play.json.Jsonx
import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import models.enums.Role
import play.api.libs.json.{Format, Json, OFormat, OWrites}
import slick.lifted.MappedTo

/**
 * The user object.
 *
 * @param userID The unique ID of the user.
 * @param loginInfo The linked login info.
 * @param name The name of the authenticated user.
 * @param email The email of the authenticated provider.
 * @param role The role of the authenticated user.
 */

case class User(userID: UserID,
                loginInfo: LoginInfo,
                name: String,
                email: String,
                role: Role = Role.God()) extends Identity {

  def isGod: Boolean = this.role == Role.God()
}

object User {
  implicit val writer: OWrites[User] = Json.writes[User]
}

case class SimpleUser(
                      userID: UserID,
                      firstName: String,
                      lastName: String,
                      fullName: String,
                      email: String,
                      avatarUrl: Option[String],
                      isCommercial: Boolean
                     )

object SimpleUser {
  implicit val writer: OWrites[SimpleUser] = Json.format[SimpleUser]
}

case class UserID(value: String) extends AnyVal with MappedTo[String]
object UserID {
  implicit val formatter: Format[UserID] = Jsonx.formatAuto[UserID]
}
