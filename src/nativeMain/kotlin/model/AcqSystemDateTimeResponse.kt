package model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AcqSystemDateTimeResponse(
    @SerialName("Result")
    val result: String = "",
    @SerialName("SysDate")
    val sysDate: String = "",
    @SerialName("TTime")
    val tTime: String = ""
)