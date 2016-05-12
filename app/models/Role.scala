package models

sealed trait Role

object Role {

  case object Owner extends Role

  case object Administrator extends Role

  case object Seller extends Role

  case object Guest extends Role

  def valueOf(value: String): Role = value match {
    case "Owner" => Owner
    case "Administrator" => Administrator
    case "Seller" => Seller
    case "Guest" => Guest
    case _ => throw new IllegalArgumentException()
  }
}
