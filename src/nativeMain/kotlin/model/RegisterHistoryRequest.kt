package model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterHistoryRequest(
    @SerialName("CId")
    val cId: String = "",
    @SerialName("FId")
    val fId: String = "",
    @SerialName("LCD")
    val lCD: String = "",
    @SerialName("LInfo")
    val lInfo: LInfo = LInfo(),
    @SerialName("SDate")
    val sDate: String = "",
    @SerialName("SId")
    val sId: String = "",
    @SerialName("SessionId")
    val sessionId: String = "",
    @SerialName("Skill")
    val skill: String = "",
    @SerialName("UId")
    val uId: String = "",
    @SerialName("VId")
    val vId: String = ""
) {
    @Serializable
    data class LInfo(
        @SerialName("FID19")
        val fID19: FID19 = FID19(),
        @SerialName("ForTest")
        val forTest: ForTest = ForTest()
    ) {
        @Serializable
        data class FID19(
            @SerialName("StepSection19")
            val stepSection19: List<StepSection19> = emptyList()
        ) {
            @Serializable
            data class StepSection19(
                @SerialName("Arr19")
                val arr19: List<Arr19> = emptyList(),
                @SerialName("SFlag")
                val sFlag: String = "",
                @SerialName("SOrder")
                val sOrder: String = ""
            ) {
                @Serializable
                data class Arr19(
                    @SerialName("Ans")
                    val ans: String = "",
                    @SerialName("Errata")
                    val errata: String = "",
                    @SerialName("ExplicationFlg")
                    val explicationFlg: String = "",
                    @SerialName("QKNo")
                    val qKNo: String = "",
                    @SerialName("QNo")
                    val qNo: String = "",
                    @SerialName("Yans")
                    val yans: String = ""
                )
            }
        }

        @Serializable
        data class ForTest(
            @SerialName("AdviceInfo")
            val adviceInfo: AdviceInfo = AdviceInfo(),
            @SerialName("EDate")
            val eDate: String = "",
            @SerialName("FailingInfo")
            val failingInfo: FailingInfo = FailingInfo(),
            @SerialName("PartInfo")
            val partInfo: PartInfo = PartInfo(),
            @SerialName("QNo")
            val qNo: Int = 0,
            @SerialName("Qtype")
            val qtype: String = "",
            @SerialName("RNo")
            val rNo: Int = 0,
            @SerialName("Score")
            val score: String = ""
        ) {
            @Serializable
            data class AdviceInfo(
                @SerialName("AArray")
                val aArray: List<String> = emptyList(),
                @SerialName("AdviceInfoFlg")
                val adviceInfoFlg: String = ""
            )

            @Serializable
            data class FailingInfo(
                @SerialName("FailingInfoFlg")
                val failingInfoFlg: String = "",
                @SerialName("UnitArray")
                val unitArray: List<String> = emptyList()
            )

            @Serializable
            data class PartInfo(
                @SerialName("DArray")
                val dArray: List<DArray> = listOf(),
                @SerialName("PartInfoFlg")
                val partInfoFlg: String = ""
            ) {
                @Serializable
                data class DArray(
                    @SerialName("EPoint")
                    val ePoint: String = "",
                    @SerialName("QCount")
                    val qCount: String = "",
                    @SerialName("RCount")
                    val rCount: String = "",
                    @SerialName("RPercent")
                    val rPercent: String = "",
                    @SerialName("Score")
                    val score: String = "",
                    @SerialName("SectionID")
                    val sectionID: String = ""
                )
            }
        }
    }
}