package model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AlcUnitInfoResponse(
    @SerialName("cover")
    val cover: Cover?,
    @SerialName("steps")
    val steps: List<Step?>?,
    @SerialName("version")
    val version: String?
) {
    @Serializable
    data class Cover(
        @SerialName("free")
        val free: List<String?>?
    )

    @Serializable
    data class Step(
        @SerialName("id")
        val id: String?,
        @SerialName("name")
        val name: String?,
        @SerialName("type")
        val type: String?
    )
}