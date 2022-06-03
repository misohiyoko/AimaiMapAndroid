package ac.misohiyoko.navigatorCom

import android.location.Location

data class NamedLocation(val latitude:Double, val longitude:Double, val name:String ){

    constructor(location: Location, name: String) : this(
        latitude = location.latitude,
        longitude = location.longitude,
        name = name
    )
    public fun getLocation():Location{
        val nullStr : String? = null
        val location = Location(nullStr)
        location.latitude = latitude
        location.longitude = longitude
        location.accuracy = 1F

        return location
    }
}
