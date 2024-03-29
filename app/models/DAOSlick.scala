package models

import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

/**
  * Trait that contains generic slick db handling code to be mixed in with DAOs
  */

trait DAOSlick extends HasDatabaseConfigProvider[JdbcProfile]