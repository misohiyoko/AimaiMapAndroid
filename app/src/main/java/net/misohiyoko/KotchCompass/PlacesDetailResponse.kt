package net.misohiyoko.KotchCompass


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlacesDetailResponse(
    @SerialName("html_attributions")
    val htmlAttributions: List<String> = listOf(),
    @SerialName("result")
    val result: Result = Result(),
    @SerialName("status")
    val status: String = ""
) {
    @Serializable
    data class Result(
        @SerialName("address_components")
        val addressComponents: List<AddressComponent> = listOf(),
        @SerialName("adr_address")
        val adrAddress: String = "",
        @SerialName("business_status")
        val businessStatus: String = "",
        @SerialName("formatted_address")
        val formattedAddress: String = "",
        @SerialName("formatted_phone_number")
        val formattedPhoneNumber: String = "",
        @SerialName("geometry")
        val geometry: Geometry = Geometry(),
        @SerialName("icon")
        val icon: String = "",
        @SerialName("icon_background_color")
        val iconBackgroundColor: String = "",
        @SerialName("icon_mask_base_uri")
        val iconMaskBaseUri: String = "",
        @SerialName("international_phone_number")
        val internationalPhoneNumber: String = "",
        @SerialName("name")
        val name: String = "",
        @SerialName("opening_hours")
        val openingHours: OpeningHours = OpeningHours(),
        @SerialName("photos")
        val photos: List<Photo> = listOf(),
        @SerialName("place_id")
        val placeId: String = "",
        @SerialName("plus_code")
        val plusCode: PlusCode = PlusCode(),
        @SerialName("rating")
        val rating: Double = 0.0,
        @SerialName("reference")
        val reference: String = "",
        @SerialName("reviews")
        val reviews: List<Review> = listOf(),
        @SerialName("types")
        val types: List<String> = listOf(),
        @SerialName("url")
        val url: String = "",
        @SerialName("user_ratings_total")
        val userRatingsTotal: Int = 0,
        @SerialName("utc_offset")
        val utcOffset: Int = 0,
        @SerialName("vicinity")
        val vicinity: String = "",
        @SerialName("website")
        val website: String = ""
    ) {
        @Serializable
        data class AddressComponent(
            @SerialName("long_name")
            val longName: String = "",
            @SerialName("short_name")
            val shortName: String = "",
            @SerialName("types")
            val types: List<String> = listOf()
        )

        @Serializable
        data class Geometry(
            @SerialName("location")
            val location: Location = Location(),
            @SerialName("viewport")
            val viewport: Viewport = Viewport()
        ) {
            @Serializable
            data class Location(
                @SerialName("lat")
                val lat: Double = 0.0,
                @SerialName("lng")
                val lng: Double = 0.0
            )

            @Serializable
            data class Viewport(
                @SerialName("northeast")
                val northeast: Northeast = Northeast(),
                @SerialName("southwest")
                val southwest: Southwest = Southwest()
            ) {
                @Serializable
                data class Northeast(
                    @SerialName("lat")
                    val lat: Double = 0.0,
                    @SerialName("lng")
                    val lng: Double = 0.0
                )

                @Serializable
                data class Southwest(
                    @SerialName("lat")
                    val lat: Double = 0.0,
                    @SerialName("lng")
                    val lng: Double = 0.0
                )
            }
        }

        @Serializable
        data class OpeningHours(
            @SerialName("open_now")
            val openNow: Boolean = false,
            @SerialName("periods")
            val periods: List<Period> = listOf(),
            @SerialName("weekday_text")
            val weekdayText: List<String> = listOf()
        ) {
            @Serializable
            data class Period(
                @SerialName("close")
                val close: Close = Close(),
                @SerialName("open")
                val `open`: Open = Open()
            ) {
                @Serializable
                data class Close(
                    @SerialName("day")
                    val day: Int = 0,
                    @SerialName("time")
                    val time: String = ""
                )

                @Serializable
                data class Open(
                    @SerialName("day")
                    val day: Int = 0,
                    @SerialName("time")
                    val time: String = ""
                )
            }
        }

        @Serializable
        data class Photo(
            @SerialName("height")
            val height: Int = 0,
            @SerialName("html_attributions")
            val htmlAttributions: List<String> = listOf(),
            @SerialName("photo_reference")
            val photoReference: String = "",
            @SerialName("width")
            val width: Int = 0
        )

        @Serializable
        data class PlusCode(
            @SerialName("compound_code")
            val compoundCode: String = "",
            @SerialName("global_code")
            val globalCode: String = ""
        )

        @Serializable
        data class Review(
            @SerialName("author_name")
            val authorName: String = "",
            @SerialName("author_url")
            val authorUrl: String = "",
            @SerialName("language")
            val language: String = "",
            @SerialName("profile_photo_url")
            val profilePhotoUrl: String = "",
            @SerialName("rating")
            val rating: Int = 0,
            @SerialName("relative_time_description")
            val relativeTimeDescription: String = "",
            @SerialName("text")
            val text: String = "",
            @SerialName("time")
            val time: Int = 0
        )
    }
}