package model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import util.printBorder

@Serializable
data class AlcQuestion14Response(
    @SerialName("description")
    val description: Description? = Description(),
    @SerialName("questions")
    val questions: List<Question?>? = listOf(),
    @SerialName("technicalskill")
    val technicalskill: Technicalskill? = Technicalskill()
): IAlcQuestionResponse {
    @Serializable
    data class Description(
        @SerialName("question")
        val question: String? = ""
    )

    @Serializable
    data class Question(
        @SerialName("choices")
        val choices: List<Choice?>? = listOf(),
        @SerialName("explanations")
        val explanations: Explanations? = Explanations(),
        @SerialName("question")
        val question: Question? = Question()
    ) {
        @Serializable
        data class Choice(
            @SerialName("correct")
            val correct: String? = "",
            @SerialName("label")
            val label: String? = ""
        )

        @Serializable
        data class Explanations(
            @SerialName("target")
            val target: String? = ""
        )

        @Serializable
        data class Question(
            @SerialName("text")
            val text: String? = ""
        )
    }

    @Serializable
    data class Technicalskill(
        @SerialName("point")
        val point: String? = "",
        @SerialName("type")
        val type: String? = ""
    )

    override fun printQuestion() {
        for(q in this.questions.orEmpty()){
            if(q != null){
                for (c in q.choices.orEmpty()){
                    if(c != null){
                        println(" (${c.label})${c.correct}")
                    }
                }
            }
            printBorder()
        }
    }
}