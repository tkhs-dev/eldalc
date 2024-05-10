package model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import util.printBorder
import util.removeHtmlTags

@Serializable
data class AlcQuestion15Response(
    @SerialName("description")
    val description: Description? = Description(),
    @SerialName("questions")
    val questions: List<Question?>? = listOf(),
    @SerialName("technicalskill")
    val technicalskill: Technicalskill? = Technicalskill()
): IAlcQuestionResponse{
    @Serializable
    data class Description(
        @SerialName("question")
        val question: String? = ""
    )

    @Serializable
    data class Question(
        @SerialName("answer")
        val answer: Answer? = Answer(),
        @SerialName("choices")
        val choices: List<Choice?>? = listOf(),
        @SerialName("explanations")
        val explanations: Explanations? = Explanations(),
        @SerialName("question")
        val question: Question? = Question()
    ) {
        @Serializable
        data class Answer(
            @SerialName("choice")
            val choice: String? = "",
            @SerialName("correct")
            val correct: String? = ""
        )

        @Serializable
        data class Choice(
            @SerialName("symbol")
            val symbol: String? = "",
            @SerialName("text")
            val text: String? = ""
        )

        @Serializable
        data class Explanations(
            @SerialName("choices")
            val choices: Choices? = Choices(),
            @SerialName("correctanswer")
            val correctanswer: String? = "",
            @SerialName("question")
            val question: Question? = Question(),
            @SerialName("translation")
            val translation: String? = "",
            @SerialName("voice")
            val voice: String? = ""
        ) {
            @Serializable
            data class Choices(
                @SerialName("A")
                val a: A? = A(),
                @SerialName("B")
                val b: B? = B(),
                @SerialName("C")
                val c: C? = C(),
                @SerialName("D")
                val d: D? = D()
            ) {
                @Serializable
                data class A(
                    @SerialName("en")
                    val en: String? = ""
                )

                @Serializable
                data class B(
                    @SerialName("en")
                    val en: String? = ""
                )

                @Serializable
                data class C(
                    @SerialName("en")
                    val en: String? = ""
                )

                @Serializable
                data class D(
                    @SerialName("en")
                    val en: String? = ""
                )
            }

            @Serializable
            data class Question(
                @SerialName("en")
                val en: String? = ""
            )
        }

        @Serializable
        data class Question(
            @SerialName("en")
            val en: String? = ""
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
                println("Q:${q.question?.en?.let{removeHtmlTags(it)} ?: "No question"}")
                for (c in q.choices.orEmpty()){
                    if(c != null){
                        println(" (${c.symbol})${c.text}")
                    }
                }
                println("A:(${q.answer?.choice})->${q.answer?.correct ?: ""}")
            }
            printBorder()
        }
    }
}