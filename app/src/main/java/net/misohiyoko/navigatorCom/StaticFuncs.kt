package net.misohiyoko.navigatorCom

import android.location.Location
import com.opencsv.CSVWriter
import java.io.StringWriter

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
