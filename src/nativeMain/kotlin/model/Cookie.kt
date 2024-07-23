package model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Cookie(
    @SerialName("cookies")
    val cookies: List<Cooky> = listOf()
) {
    @Serializable
    data class Cooky(
        @SerialName("domain")
        val domain: String = "",
        @SerialName("hostOnly")
        val hostOnly: Boolean = false,
        @SerialName("httpOnly")
        val httpOnly: Boolean = false,
        @SerialName("id")
        val id: Int = 0,
        @SerialName("name")
        val name: String = "",
        @SerialName("path")
        val path: String = "",
        @SerialName("sameSite")
        val sameSite: String = "",
        @SerialName("secure")
        val secure: Boolean = false,
        @SerialName("session")
        val session: Boolean = false,
        @SerialName("storeId")
        val storeId: String = "",
        @SerialName("value")
        val value: String = ""
    )
}