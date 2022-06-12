package net.misohiyoko.navigatorCom

import android.location.Location
import androidx.compose.runtime.Composable
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

data class NamedLocation(val latitude:Double, val longitude:Double, val id:String, val address:String ){
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

    constructor(location: Location, name: String, address: String) : this(
        latitude = location.latitude,
        longitude = location.longitude,
        id = name,
        address = address
    )
    constructor(marker: Marker) : this (
            latitude = marker.position.latitude,
        longitude = marker.position.longitude,
        id = marker.title ?: "",
        address = ""
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

    @Composable
    public fun SetMarker(markerOnClick:(NamedLocation)->Unit){
        Marker(
            state = MarkerState(position = this.getLatLng()),
            title = this.id,
            onClick = {
                ///markerをクリックして目的地に指定
                markerOnClick(this)
                return@Marker true
            }
        )
    }


}
