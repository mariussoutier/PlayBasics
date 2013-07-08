package models

//import org.joda.time.DateTime
import java.util.Date

case class User(
  id: Option[Long],
  email: String,
  password: String,
  name: String,
  dateOfBirth: Option[Date],
  createdAt: Date = new Date
)

object User {

  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  implicit val UserFromJson: Reads[User] = (
    (__ \ "id").readNullable[Long] ~
    (__ \ "email").read(Reads.email) ~
    (__ \ "password").read[String] ~
    (__ \ "name").read[String] ~
    (__ \ "dateOfBirth").readNullable[Date] ~
    (__ \ "createdAt").read(new Date)
  )(User.apply _)

  implicit val UserToJson: Writes[User] = (
    (__ \ "id").writeNullable[Long] ~
    (__ \ "email").write[String] ~
    (__ \ "password").writeNullable[String] ~ // don't write the password
    (__ \ "name").write[String] ~
    (__ \ "dateOfBirth").writeNullable[Date] ~
    (__ \ "createdAt").write[Date]
  )((user: User) => (
    user.id,
    user.email,
    None,
    user.name,
    user.dateOfBirth,
    user.createdAt
  ))


  import anorm._
  import anorm.~
  import anorm.SqlParser._
  import play.api.db.DB

  implicit val app: play.api.Application = play.api.Play.current

  val parser = {
    get[Option[Long]]("id") ~
    get[String]("email") ~
    get[String]("password") ~
    get[String]("name") ~
    get[Option[Date]]("dateOfBirth") ~
    get[Date]("createdAt")
  } map {
    case id ~ email ~ password ~ name ~ dateOfBirth ~ createdAt =>
      User(id, email, password, name, dateOfBirth, createdAt)
  }

  val multiParser: ResultSetParser[Seq[User]] = parser *

  def findOneById(id: Long): Option[User] = DB.withConnection { implicit connection =>
    SQL("SELECT * FROM usertable WHERE id = {id}").on('id -> id).as(parser.singleOpt)
  }

  def findByEmailAndPassword(email: String, password: String): Option[User] = DB.withConnection {
    implicit connection =>
      SQL("SELECT * FROM usertable WHERE email = {email} AND password = {password}").on(
        'email -> email, 'password -> password
      ).as(parser.singleOpt)
  }

}
