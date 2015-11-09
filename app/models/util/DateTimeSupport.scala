package models.util

import java.sql.Timestamp
import java.time.{ZoneId, LocalDateTime, Instant}
import org.virtuslab.unicorn.LongUnicornPlay.driver.api._

object DateTimeSupport {
     implicit val localDateTimeType = MappedColumnType.base[LocalDateTime, Timestamp](
           dt => if (dt == null) null else new Timestamp(dt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()),
           ts => if (ts == null) null else  ts.toLocalDateTime
     )
 }
