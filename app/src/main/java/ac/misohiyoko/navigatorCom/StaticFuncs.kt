package ac.misohiyoko.navigatorCom

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun getNowDate(): String? {
    val df: DateFormat = SimpleDateFormat("yyyyMMddHH:mm:ss")
    val date = Date(System.currentTimeMillis())
    return df.format(date)
}