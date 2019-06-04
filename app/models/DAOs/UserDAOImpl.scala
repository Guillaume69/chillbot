package models.daos

import java.util.UUID

import javax.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import models.daos.tables.UserDAOTables
import models._
import models.enums.Role
import models.tables.{PasswordInfoDAOTables, RolesDAOTable}
import play.api.db.slick.DatabaseConfigProvider
import slick.lifted.QueryBase

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

/**
 * Give access to the user object using Slick
 */
class UserDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ex: ExecutionContext)
  extends UserDAO with UserDAOTables with RolesDAOTable with PasswordInfoDAOTables {

  import profile.api._

  /**
   * Finds a user by its login info.
   *
   * @param loginInfo The login info of the user to find.
   * @return The found user or None if no user for the given login info could be found.
   */
  def find(loginInfo: LoginInfo) = {
    val userQuery = loginInfoQuery(loginInfo)
      .join(slickUserLoginInfo).on(_.id === _.loginInfoId)
      .join(slickUser).on(_._2.userID === _.id)

    db.run(userQuery.result.headOption).map { resOpt =>
      resOpt.map { res =>
        val (_, user) = res

        User(
          user.userID,
          loginInfo,
          user.name,
          user.email,
          Role(user.roleId)
        )
      }
    }
  }

  /**
   * Finds a user by its user ID.
   *
   * @param userID The ID of the user to find.
   * @return The found user or None if no user for the given ID could be found.
   */
  def find(userID: UserID): Future[Option[User]] = {
    val userQuery = slickUser.filter(_.id === userID)
      .join(slickUserLoginInfo).on(_.id === _.userID)
      .join(slickLoginInfo).on(_._2.loginInfoId === _.id)

    db.run(userQuery.result.headOption).map { resOpt =>
      resOpt.map { res =>
        val ((user, _), loginInfo) = res
        User(
          user.userID,
          LoginInfo(loginInfo.providerID, loginInfo.providerKey),
          user.name,
          user.email,
          Role(user.roleId)
        )
      }
    }
  }
  
  def findAll(): Future[Seq[User]] = {
    val userQuery = slickUser
      .join(slickUserLoginInfo).on(_.id === _.userID)
      .join(slickLoginInfo).on(_._2.loginInfoId === _.id)

    db.run(userQuery.result).map { resOpt =>
      resOpt.map { res =>
        val ((user, _), loginInfo) = res
        User(
          user.userID,
          LoginInfo(loginInfo.providerID, loginInfo.providerKey),
          user.name,
          user.email,
          Role(user.roleId)
        )
      }
    }
  }

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  def save(user: User): Future[User] = {
    val dbUser = DBUser(
      userID = user.userID,
      name = user.name,
      email = user.email,
      roleId = user.role.id
    )
    val dbLoginInfo = DBLoginInfo(None, user.loginInfo.providerID, user.loginInfo.providerKey)
    // We don't have the LoginInfo id so we try to get it first.
    // If there is no LoginInfo yet for this user we retrieve the id on insertion.
    val loginInfoAction = {
      val retrieveLoginInfo = slickLoginInfo.filter(
        info => info.providerID === user.loginInfo.providerID &&
          info.providerKey === user.loginInfo.providerKey).result.headOption
      val insertLoginInfo = slickLoginInfo.returning(slickLoginInfo.map(_.id)).
        into((info, id) => info.copy(id = Some(id))) += dbLoginInfo
      for {
        loginInfoOption <- retrieveLoginInfo
        loginInfo <- loginInfoOption.map(DBIO.successful).getOrElse(insertLoginInfo)
      } yield loginInfo
    }
    // combine database actions to be run sequentially
    val actions = (for {
      _ <- slickUser.insertOrUpdate(dbUser)
      loginInfo <- loginInfoAction
      _ <- slickUserLoginInfo += DBUserLoginInfo(None, dbUser.userID, loginInfo.id.get)
    } yield ()).transactionally
    // run actions and return user afterwards
    db.run(actions).map(_ => user)
  }

  def deleteUser(userId: UserID): Future[Int] = {
    db.run(slickUser.filter(_.id === userId).delete)
  }

  def saveNoAuthUser(dbUser: DBUser): Future[UserID] = {
    db.run(slickUser += dbUser).map(_ => dbUser.userID)
  }

  def updateLoginInfo(userId: UserID, login: String): Future[Int] = {
    db.run(slickUserLoginInfo.filter(_.userID === userId).result.headOption).flatMap { userLoginInfoOpt =>
      userLoginInfoOpt.map { userLoginInfo =>
        db.run(slickLoginInfo.filter(_.id === userLoginInfo.loginInfoId).map(_.providerKey).update(login))
      }.getOrElse(Future.successful(0))
    }
  }
}
