package controllers

import java.util.UUID

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.actions.{SecuredErrorHandler, SecuredRequest}
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.impl.providers._
import javax.inject.Inject
import models._
import models.enums._
import models.services.UserService
import utils.auth.DefaultEnv
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
 * The `Test` controller.
 *
 * @param components             The Play controller components.
 * @param silhouette             The Silhouette stack.
 * @param userService            The user service implementation.
 * @param authInfoRepository     The auth info repository implementation.
 * @param avatarService          The avatar service implementation.
 * @param passwordHasherRegistry The password hasher registry.
 * @param assets                 The Play assets finder.
 * @param ex                     The execution context.
 */
class TestController @Inject()(
                                 components: ControllerComponents,
                                 silhouette: Silhouette[DefaultEnv],
                                 userService: UserService,
                                 authInfoRepository: AuthInfoRepository,
                                 avatarService: AvatarService,
                                 passwordHasherRegistry: PasswordHasherRegistry,
                               )(
                                 implicit
                                 assets: AssetsFinder,
                                 ex: ExecutionContext
                               ) extends AbstractController(components) with I18nSupport {

  /**
   * Test page
   *
   * @return The result to display
   */
  def create_admin = Action.async { implicit request: Request[AnyContent] =>
    val loginInfo = LoginInfo(CredentialsProvider.ID, "guillaume.marchand69@gmail.com")
    userService.retrieve(loginInfo).flatMap {
      case Some(user) =>
        Future.successful(BadRequest)
      case None =>
        val authInfo = passwordHasherRegistry.current.hash("EqJX+iXZ[sk<{o6)bCH]9h7D")
        val userId = UserID(UUID.randomUUID().toString)
        val user = User(
          userID = userId,
          loginInfo = loginInfo,
          name = "Skynet",
          email = "guillaume.marchand69@gmail.com",
          role = Role.God()
        )
        for {
          user <- userService.save(user)
          authInfo <- authInfoRepository.add(loginInfo, authInfo)
        } yield {
          silhouette.env.eventBus.publish(SignUpEvent(user, request))
          Ok
        }
    }
  }
}
