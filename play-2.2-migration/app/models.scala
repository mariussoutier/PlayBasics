package models

import play.api.libs.json._

sealed trait Permission
case object AdminPermission extends Permission
case object UserPermission extends Permission

object Permission {
  def apply(num: Int): Permission = num match {
    case 0 => AdminPermission
    case _ => UserPermission
  }

  def unapply(p: Permission): Option[Int] = p match {
    case AdminPermission => Some(0)
    case UserPermission => Some(1)
  }
}

case class User(
  id: Option[Long] = None,
  name: String,
  permission: Permission = UserPermission,
  department: Department
)

object User {
  def getByToken(token: String): Option[User] =
    Some(User(name = "Horst-Kevin", department = Department("IT")))
}

case class Inventory(name: String, inventoryType: String, department: Department)

object Inventory {
  def findOne(): Option[Inventory] = Some(Inventory("Item 42", "Box", Department("IT")))
}

case class Department(name: String)


object json {

  implicit val permFormat = Json.format[Permission]
  implicit val depFormat = Json.format[Department]
  implicit val userFormat = Json.format[User]
  implicit val invFormat = Json.format[Inventory]

}
