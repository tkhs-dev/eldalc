import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.winhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import model.*

const val APP_NAME = "E-Learning Destroyer for ALC"
const val VERSION = "1.0.0"

@kotlinx.serialization.ExperimentalSerializationApi
fun main() {
    println("Welcome to $APP_NAME version:$VERSION!!")
    val client = HttpClient(WinHttp){
        install(ContentNegotiation){
            json(
                Json {
                    ignoreUnknownKeys = true
                    explicitNulls = true
                }
            )
        }
    }
    println("Enter a Unit number (ex: U001)")
    print(">")
    val unit = readlnOrNull()
    val unitInfo : AlcUnitInfoResponse = runBlocking{
        client.get("https://nanext.alcnanext.jp/anetn/course/materials/ALC/TLP/TLP_GR/$unit/06/step_info.json").body()
    }
    println("Select a step")
    val steps = unitInfo.steps?.filter { isCapableType(it?.type ?: "") }
    if(steps==null){
        println("Error 1")
        return
    }
    for ((index, s) in steps.withIndex()){
        println("  ($index)${s?.name}")
    }
    print(">")
    val step = readlnOrNull()
    if (step.isNullOrBlank()){
        println("Error 2")
        return
    }
    val stepI = step.toIntOrNull()
    if(stepI == null){
        println("Error 3")
        return
    }

    when(steps[stepI]?.type){
        "14" -> {
            val stepAns: AlcQuestion14Response = runBlocking{
                client.get("https://nanext.alcnanext.jp/anetn/course/materials/ALC/TLP/TLP_GR/$unit/06/${steps[stepI]?.id}.json").body()
            }
            for(q in stepAns.questions.orEmpty()){
                if(q != null){
                    for (c in q.choices.orEmpty()){
                        if(c != null){
                            println(" (${c.label})${c.correct}")
                        }
                    }
                }
                println("------------------------------------------------")
            }
        }
        "15" -> {
            val stepAns: AlcQuestion15Response = runBlocking{
                client.get("https://nanext.alcnanext.jp/anetn/course/materials/ALC/TLP/TLP_GR/$unit/06/${steps[stepI]?.id}.json").body()
            }
            for(q in stepAns.questions.orEmpty()){
                if(q != null){
                    println("Q:${q.question?.en?.let{removeHtmlTags(it)} ?: "No question"}")
                    for (c in q.choices.orEmpty()){
                        if(c != null){
                            println(" (${c.symbol})${c.text}")
                        }
                    }
                    println("A:(${q.answer?.choice})->${q.answer?.correct ?: ""}")
                }
                println("------------------------------------------------")
            }
        }
        "16" -> {
            val stepAns: AlcQuestion16Response = runBlocking{
                client.get("https://nanext.alcnanext.jp/anetn/course/materials/ALC/TLP/TLP_GR/$unit/06/${steps[stepI]?.id}.json").body()
            }
            for(q in stepAns.questions.orEmpty()){
                if(q != null){
                    println("Q:${q.question?.en?.let{removeHtmlTags(it)} ?: "No question"}")
                    println("A:(${q.answer})")
                }
                println("------------------------------------------------")
            }
        }
        "20" -> {
            val stepAns: AlcQuestion20Response = runBlocking{
                client.get("https://nanext.alcnanext.jp/anetn/course/materials/ALC/TLP/TLP_GR/$unit/06/${steps[stepI]?.id}.json").body()
            }
            for(q in stepAns.questions.orEmpty()){
                if(q != null){
                    println("Q:${q.question?.en?.let{removeHtmlTags(it)} ?: "No question"}")
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
        else -> {
            println("Error 4")
            return
        }
    }
    client.close()
    println("Press any key to exit")
    readln()
}

fun removeHtmlTags(input: String): String {
    return input.replace(Regex("<(\"[^\"]*\"|'[^']*'|[^'\">])*>"),"")
}

fun isCapableType(input: String): Boolean {
    val capable = setOf("14","15","16","20")
    return capable.contains(input)
}