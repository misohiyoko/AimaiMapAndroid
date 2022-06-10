package net.misohiyoko.navigatorCom


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeocodingResponse(
    @SerialName("results")
    val results: List<Result>,
    @SerialName("status")
    val status: String
) : ISerializableDummy{
    @Serializable
    data class Result(
        @SerialName("address_components")
        val addressComponents: List<AddressComponent>,
        @SerialName("formatted_address")
        val formattedAddress: String,
        @SerialName("geometry")
        val geometry: Geometry,
        @SerialName("place_id")
        val placeId: String,
        @SerialName("plus_code")
        val plusCode: PlusCode,
        @SerialName("types")
        val types: List<String>
    ) {
        @Serializable
        data class AddressComponent(
            @SerialName("long_name")
            val longName: String,
            @SerialName("short_name")
            val shortName: String,
            @SerialName("types")
            val types: List<String>
        )

        @Serializable
        data class Geometry(
            @SerialName("location")
            val location: Location,
            @SerialName("location_type")
            val locationType: String,
            @SerialName("viewport")
            val viewport: Viewport
        ) {
            @Serializable
            data class Location(
                @SerialName("lat")
                val lat: Double,
                @SerialName("lng")
                val lng: Double
            )

            @Serializable
            data class Viewport(
                @SerialName("northeast")
                val northeast: Northeast,
                @SerialName("southwest")
                val southwest: Southwest
            ) {
                @Serializable
                data class Northeast(
                    @SerialName("lat")
                    val lat: Double,
                    @SerialName("lng")
                    val lng: Double
                )

                @Serializable
                data class Southwest(
                    @SerialName("lat")
                    val lat: Double,
                    @SerialName("lng")
                    val lng: Double
                )
            }
        }

        @Serializable
        data class PlusCode(
            @SerialName("compound_code")
            val compoundCode: String,
            @SerialName("global_code")
            val globalCode: String
        )
    }
}