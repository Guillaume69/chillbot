package models.enums

import ai.x.play.json.Jsonx
import com.mohiva.play.silhouette.api.{Authenticator, Authorization}
import models.User
import play.api.i18n.{Messages, MessagesApi, MessagesProvider}
import play.api.libs.json._
import play.api.mvc.{PathBindable, Request}
import slick.lifted.MappedTo

import scala.concurrent.Future

case class WithRole[A <: Authenticator](role: Role, inclusive: Boolean = true) extends Authorization[User, A] {
  def isAuthorized[B](user: User, authenticator: A)(implicit request: Request[B]): Future[Boolean] = {
    Future.successful(user.role == role || (inclusive && role.hierarchy <= user.role.hierarchy))
  }
}


case class RoleId(value: Long) extends AnyVal with MappedTo[Long]

object RoleId {
  implicit val formatter: Format[RoleId] = Jsonx.formatAuto[RoleId]
}

sealed trait Role {
  def id: RoleId
  def name: String
  def hierarchy: Long
  def needCompany: Boolean

  def translate(implicit messagesProvider: MessagesProvider): Role
}

object Role {

  implicit val roleWrites: Writes[Role] {
    def writes(role: Role): JsObject
  } = new Writes[Role] {
    def writes(role: Role): JsObject = Json.obj(
      "id" -> role.id.value,
      "name" -> role.name,
      "hierarchy" -> role.hierarchy
    )
  }

  def apply(role: RoleId): Role = role.value match {
    case God.id.value             => God()
    case Unknown.id.value         => Unknown()
  }

  def apply(role: String): Role = role match {
    case God.name           => God()
    case Unknown.name       => Unknown()
  }

  def unapply(role: Role): Option[String] = Some(role.name)

  case class God(id: RoleId = RoleId(1), name: String = "role.admin", hierarchy: Long = 101, needCompany: Boolean = false, private val alreadyTranslated: Boolean = false) extends Role {
    override def translate(implicit messagesProvider: MessagesProvider): Role = {
      if (this.alreadyTranslated) this else this.copy(name = Messages(this.name), alreadyTranslated = true)
    }
  }

  object God {
    val id = RoleId(1)
    val name = "role.admin"
  }

  case class Unknown(id: RoleId = RoleId(0), name: String = "-", hierarchy: Long = 0, needCompany: Boolean = false, private val alreadyTranslated: Boolean = false) extends Role {
    override def translate(implicit messagesProvider: MessagesProvider): Role = this.copy(name = Messages(this.name), alreadyTranslated = true)
  }

  object Unknown {
    val id = RoleId(0)
    val name = "-"
    val hierarchy = 0
    val needCompany = false
  }
}