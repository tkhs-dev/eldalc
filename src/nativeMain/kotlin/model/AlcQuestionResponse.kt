package model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AlcQuestionResponse(
    @SerialName("description")
    val description: Description?,
    @SerialName("questions")
    val questions: List<Question?>?,
    @SerialName("rule")
    val rule: Rule?,
    @SerialName("technicalskill")
    val technicalskill: Technicalskill?
) {
    @Serializable
    data class Description(
        @SerialName("answer")
        val answer: String?,
        @SerialName("question")
        val question: String?
    )

    @Serializable
    data class Question(
        @SerialName("answer")
        val answer: String?,
        @SerialName("choices")
        val choices: List<Choice?>?,
        @SerialName("explanations")
        val explanations: Explanations?,
        @SerialName("question")
        val question: Question?,
        @SerialName("voice")
        val voice: Voice?
    ) {
        @Serializable
        data class Choice(
            @SerialName("symbol")
            val symbol: String?,
            @SerialName("text")
            val text: String?
        )

        @Serializable
        data class Explanations(
            @SerialName("choices")
            val choices: Choices?,
            @SerialName("explanation")
            val explanation: String?,
            @SerialName("question")
            val question: Question?,
            @SerialName("voice")
            val voice: Voice?
        ) {
            @Serializable
            data class Choices(
                @SerialName("A")
                val a: A?,
                @SerialName("B")
                val b: B?,
                @SerialName("C")
                val c: C?,
                @SerialName("D")
                val d: D?
            ) {
                @Serializable
                data class A(
                    @SerialName("en")
                    val en: String?
                )

                @Serializable
                data class B(
                    @SerialName("en")
                    val en: String?
                )

                @Serializable
                data class C(
                    @SerialName("en")
                    val en: String?
                )

                @Serializable
                data class D(
                    @SerialName("en")
                    val en: String?
                )
            }

            @Serializable
            data class Question(
                @SerialName("en")
                val en: String?,
                @SerialName("ja")
                val ja: String?
            )

            @Serializable
            data class Voice(
                @SerialName("normal")
                val normal: List<String?>?
            )
        }

        @Serializable
        data class Question(
            @SerialName("en")
            val en: String?
        )

        @Serializable
        data class Voice(
            @SerialName("normal")
            val normal: List<String?>?
        )
    }

    @Serializable
    data class Rule(
        @SerialName("paging")
        val paging: List<Paging?>?,
        @SerialName("timelimit")
        val timelimit: String?
    ) {
        @Serializable
        data class Paging(
            @SerialName("questionNumber")
            val questionNumber: String?
        )
    }

    @Serializable
    data class Technicalskill(
        @SerialName("point")
        val point: String?,
        @SerialName("type")
        val type: String?
    )
}