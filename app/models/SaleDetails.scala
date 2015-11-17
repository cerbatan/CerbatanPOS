package models

import models.db.SoldItem
import play.api.libs.json.JsValue

case class SaleDetails(items: Seq[SoldItem], details: JsValue)
