package ac.misohiyoko.navigatorCom

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

data class NamedLocation(val latitude:Double, val longitude:Double, val name:String ){
    companion object{
        public fun getBounds(locations : Iterable<NamedLocation>): LatLngBounds {
            val north = locations.reduce { acc, namedLocation ->
                if(acc.latitude < namedLocation.latitude){
                    namedLocation
                }
                else{
                    acc
                }
            }
            val east = locations.reduce { acc, namedLocation ->
                if(acc.longitude < namedLocation.longitude){
                    namedLocation
                }
                else{
                    acc
                }
            }
            val south = locations.reduce { acc, namedLocation ->
                if(acc.latitude > namedLocation.latitude){
                    namedLocation
                }
                else{
                    acc
                }
            }
            val west = locations.reduce { acc, namedLocation ->
                if(acc.longitude > namedLocation.longitude){
                    namedLocation
                }
                else{
                    acc
                }
            }
            val northeast = LatLng(north.latitude, east.longitude)
            val southwest = LatLng(south.latitude, west.longitude)

            return LatLngBounds(southwest, northeast)

        }
    }

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
    public fun getLatLng(): LatLng {
        return LatLng(latitude, longitude)
    }
}
