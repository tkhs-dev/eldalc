package model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AlcJTUnitInfoResponse(
    @SerialName("cover")
    val cover: Cover = Cover(),
    @SerialName("isTest")
    val isTest: String = "",
    @SerialName("sections")
    val sections: List<Section> = listOf(),
    @SerialName("version")
    val version: String = ""
) {
    @Serializable
    data class Cover(
        @SerialName("free")
        val free: List<String> = listOf()
    )

    @Serializable
    data class Section(
        @SerialName("answersheet")
        val answersheet: Answersheet = Answersheet(),
        @SerialName("cover")
        val cover: Cover = Cover(),
        @SerialName("name")
        val name: String = "",
        @SerialName("rule")
        val rule: Rule = Rule(),
        @SerialName("steps")
        val steps: List<AlcUnitInfoResponse.Step> = listOf()
    ) {
        @Serializable
        data class Answersheet(
            @SerialName("id")
            val id: String = "",
            @SerialName("name")
            val name: String = ""
        )

        @Serializable
        data class Cover(
            @SerialName("operation")
            val operation: String = ""
        )

        @Serializable
        data class Rule(
            @SerialName("estimateInfo")
            val estimateInfo: EstimateInfo = EstimateInfo(),
            @SerialName("timelimit")
            val timelimit: String = "",
            @SerialName("timeupMessage")
            val timeupMessage: String = ""
        ) {
            @Serializable
            data class EstimateInfo(
                @SerialName("estimates")
                val estimates: List<Estimate> = listOf()
            ) {
                @Serializable
                data class Estimate(
                    @SerialName("category")
                    val category: String = "",
                    @SerialName("scores")
                    val scores: List<Score> = listOf()
                ) {
                    @Serializable
                    data class Score(
                        @SerialName("correct")
                        val correct: String = "",
                        @SerialName("score")
                        val score: String = ""
                    )
                }
            }
        }
    }
}