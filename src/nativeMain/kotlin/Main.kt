import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.winhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import model.AlcQuestionResponse
import model.AlcUnitInfoResponse

const val APP_NAME = "E-Learning Destroyer for ALC"
const val VERSION = "1.0.0"

fun main() {
    println("Welcome to $APP_NAME version:$VERSION!!")
    val client = HttpClient(WinHttp){
        install(ContentNegotiation){
            json()
        }
    }
    println("Enter a Unit number (ex: U001)")
    print(">")
    val unit = readlnOrNull()
    val unitInfo : AlcUnitInfoResponse = runBlocking{
        client.get("https://nanext.alcnanext.jp/anetn/course/materials/ALC/TLP/TLP_GR/$unit/06/step_info.json").body()
    }
    println("Select a step")
    val steps = unitInfo.steps
    if(steps==null){
        println("Error")
        return
    }
    for ((index, s) in unitInfo.steps.withIndex()){
        println("  ($index)${s?.name}")
    }
    print(">")
    val step = readlnOrNull()
    if (step.isNullOrBlank()){
        println("Error")
        return
    }
    val stepI = step.toIntOrNull()
    if(stepI == null){
        println("Error")
        return
    }
    val stepAns:AlcQuestionResponse = runBlocking{
        client.get("https://nanext.alcnanext.jp/anetn/course/materials/ALC/TLP/TLP_GR/$unit/06/${steps[stepI]?.id}.json").body()
    }
    for(q in stepAns.questions.orEmpty()){
        if(q != null){
            println("Q:${q.question?.en?.replace(Regex("<(\"[^\"]*\"|'[^']*'|[^'\">])*>"),"")}")
            for (c in q.choices.orEmpty()){
                if(c != null){
                    println(" (${c.symbol})${c.text}")
                }
            }
            println("A:(${q.answer})")
        }
        println("------------------------------------------------")
    }
}