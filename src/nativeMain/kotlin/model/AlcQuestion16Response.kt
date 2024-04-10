package model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AlcQuestion16Response(
    @SerialName("description")
    val description: Description? = Description(),
    @SerialName("questions")
    val questions: List<Question?>? = listOf(),
    @SerialName("technicalskill")
    val technicalskill: Technicalskill? = Technicalskill()
) {
    @Serializable
    data class Description(
        @SerialName("question")
        val question: String? = ""
    )

    @Serializable
    data class Question(
        @SerialName("answer")
        val answer: String? = "",
        @SerialName("answers")
        val answers: List<String?>? = listOf(),
        @SerialName("explanations")
        val explanations: Explanations? = Explanations(),
        @SerialName("parts")
        val parts: List<String?>? = listOf(),
        @SerialName("question")
        val question: Question? = Question()
    ) {
        @Serializable
        data class Explanations(
            @SerialName("voice")
            val voice: Voice? = Voice()
        ) {
            @Serializable
            data class Voice(
                @SerialName("normal")
                val normal: String? = ""
            )
        }

        @Serializable
        data class Question(
            @SerialName("en")
            val en: String? = "",
            @SerialName("ja")
            val ja: String? = ""
        )
    }

    @Serializable
    data class Technicalskill(
        @SerialName("point")
        val point: String? = "",
        @SerialName("type")
        val type: String? = ""
    )
}