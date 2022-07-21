package net.misohiyoko.KotchCompass

import android.location.Location
import com.opencsv.CSVWriter
import java.io.StringWriter
import kotlin.math.abs

class GPSDataDump(val destination: String){
    var locationList = mutableListOf<Location>()
    fun writeStringCSV():String{
        val stringWriter = StringWriter()
        val csvWriter = CSVWriter(stringWriter)

        locationList.forEach {
            val textToBeWrite = arrayOf<String>(it.time.toString(),it.latitude.toString(),it.longitude.toString(),it.accuracy.toString(),it.bearing.toString(),it.speed.toString())
            csvWriter.writeNext(textToBeWrite)
        }
        return stringWriter.toString()
    }

}


public fun angularDistance(alpha: Float, beta: Float): Float {
    val phi = (beta - alpha) % 360
    return if (phi < 0) 360 + phi else phi
}

public fun angularDistanceAbs(alpha: Float,beta: Float): Float{
    val phi = abs(beta - alpha) % 360
    return if (phi > 180) 360 - phi else phi
}

public fun deltaAngleTo(location: Location, destination: Location): Float {
    val headingToDestination = location.bearingTo(destination)
    return angularDistance( location.bearing,headingToDestination)
}

public fun deltaAngleToAbs(location: Location, destination: Location):Float{
    val headingToDestination = location.bearingTo(destination)
    return angularDistanceAbs( location.bearing,headingToDestination)
}

public fun changeAlphabetHalfToFull(str: String?): String? {
    var result: String? = null
    if (str != null) {
        val sb = StringBuilder(str)
        for (i in sb.indices) {
            val c = sb[i].code
            if (c in 0x41..0x5A || c in 0x61..0x7A) {
                sb.setCharAt(i, (c + 0xFEE0).toChar())
            }
        }
        result = sb.toString()
    }
    return result
}

public fun getAngularRange(list : List<Float>) : Float{
    val sorted = list.sortedDescending()
    ///if the list too small return big
    return if(sorted.count() > 1) angularDistanceAbs(sorted.last(), sorted[0] ) else Float.NaN
}
