package controllers

import com.mohiva.play.silhouette.api.Authenticator.Implicits._
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.util.{Clock, Credentials}
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers._
import javax.inject.{Inject, Provider}
import models.User
import models.services.UserService
import utils.auth.DefaultEnv
import net.ceedubs.ficus.Ficus._
import play.api.Configuration
import play.api.i18n.{I18nSupport, Messages}
import play.api.libs.json.{JsError, Json, Reads}
import play.api.mvc._
import play.api.routing.Router

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

/**
 * The `Sign In` controller.
 *
 * @param components             The Play controller components.
 * @param silhouette             The Silhouette stack.
 * @param userService            The user service implementation.
 * @param credentialsProvider    The credentials provider.
 * @param configuration          The Play configuration.
 * @param clock                  The clock instance.
 * @param assets                 The Play assets finder.
 */
class SignInController @Inject()(
                                   components: ControllerComponents,
                                   silhouette: Silhouette[DefaultEnv],
                                   userService: UserService,
                                   credentialsProvider: CredentialsProvider,
                                   configuration: Configuration,
                                   clock: Clock,
                                   router: Provider[Router]
                                 )(
                                   implicit
                                   assets: AssetsFinder,
                                   ex: ExecutionContext
                                 ) extends AbstractController(components) with I18nSupport {

  case class SignInForm(email: String, password: String)

  /**
   * Handles the submitted form.
   *
   * @return The result to display.
   */
  def submit = silhouette.UnsecuredAction.async(parse.json) { implicit request =>
    implicit val signInFormReader: Reads[SignInForm] = Json.reads[SignInForm]

    request.body.validate[SignInForm].fold(
      errors => Future.successful(BadRequest(Json.obj("status" -> "KO", "message" -> JsError.toJson(errors)))),
      data => {
        val credentials = Credentials(data.email, data.password)
        credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
          userService.retrieve(loginInfo).flatMap {
            case Some(user) =>
              val c = configuration.underlying
              silhouette.env.authenticatorService.create(loginInfo).map { authenticator =>
                authenticator.copy(
                  expirationDateTime = clock.now + c.as[FiniteDuration]("silhouette.authenticator.rememberMe.authenticatorExpiry"),
                  idleTimeout = c.getAs[FiniteDuration]("silhouette.authenticator.rememberMe.authenticatorIdleTimeout")
                )
              }.flatMap { authenticator =>
                silhouette.env.eventBus.publish(LoginEvent(user, request))
                silhouette.env.authenticatorService.init(authenticator).map { token =>
                  Ok(Json.obj("token" -> token))
                }
              }
            case None => Future.failed(new IdentityNotFoundException("Couldn't find user"))
          }
        }.recover {
          case e: ProviderException =>
            BadRequest(Json.obj("status" -> "KO", "message" -> Messages("invalid.credentials")))
        }
      }
    )
  }

  def checkConnected: Action[AnyContent] = silhouette.UserAwareAction.async { implicit req =>

    req.identity match {
      case Some(user: User) =>
        Future(Ok(Json.toJson(user)))
      case _ =>
        Future(Ok(Json.toJson(Json.obj())))
    }
  }
}
