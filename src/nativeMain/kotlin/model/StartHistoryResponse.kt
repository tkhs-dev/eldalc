package model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StartHistoryResponse(
    @SerialName("Estep")
    val estep: String? = null,
    @SerialName("Result")
    val result: String = "",
    @SerialName("SDate")
    val sDate: String = ""
)