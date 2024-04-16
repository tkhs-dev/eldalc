import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.winhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.archinamon.fileio.File
import me.archinamon.fileio.readText
import me.archinamon.fileio.writeText
import model.*
import platform.windows.SHCreateDirectoryExA
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

const val APP_NAME = "E-Learning Destroyer for ALC"
const val VERSION = "1.1.0"

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
    val subCourses = setOf<SubCourse>(
        SubCourse("リスニング","LI", "01"),
        SubCourse("文法","GR","06"),
        SubCourse("リーディング","RE","03"),
        //SubCourse("テスト","JT","08"), //This course is not supported
    )
    println("Select a sub-course")
    for ((index, s) in subCourses.withIndex()){
        println("  ($index)${s.name}")
    }
    print(">")
    val subCourseI = readlnOrNull()
    if (subCourseI.isNullOrBlank()){
        println("Error 1")
        return
    }
    val subCourse = subCourseI.toIntOrNull()
    if(subCourse == null){
        println("Error 2")
        return
    }
    val courseString = subCourses.elementAt(subCourse).courseString
    val courseId = subCourses.elementAt(subCourse).courseId

    println("Enter a Unit number (ex: U001)")
    print(">")
    val unit = readlnOrNull()
    val unitInfo : AlcUnitInfoResponse = getApiResource(client, getBaseUrl(courseString,unit,courseId)+"step_info.json")
    println("Select a step")
    val steps = unitInfo.steps?.filter { isCapableType(it?.type ?: "") }
    if(steps==null){
        println("Error 3")
        return
    }
    for ((index, s) in steps.withIndex()){
        println("  ($index)${s?.name}")
    }
    print(">")
    val step = readlnOrNull()
    if (step.isNullOrBlank()){
        println("Error 4")
        return
    }
    val stepI = step.toIntOrNull()
    if(stepI == null){
        println("Error 5")
        return
    }

    when(steps[stepI]?.type){
        "14" -> {
            val stepAns: AlcQuestion14Response = getApiResource(client, getBaseUrl(courseString,unit,courseId)+"${steps[stepI]?.id}.json")
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
            val stepAns: AlcQuestion15Response = getApiResource(client, getBaseUrl(courseString,unit,courseId)+"${steps[stepI]?.id}.json")
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
            val stepAns: AlcQuestion16Response = getApiResource(client, getBaseUrl(courseString,unit,courseId)+"${steps[stepI]?.id}.json")
            for(q in stepAns.questions.orEmpty()){
                if(q != null){
                    println("Q:${q.question?.en?.let{removeHtmlTags(it)} ?: "No question"}")
                    println("A:(${q.answer})")
                }
                println("------------------------------------------------")
            }
        }
        "20" -> {
            val stepAns: AlcQuestion20Response = getApiResource(client, getBaseUrl(courseString,unit,courseId)+"${steps[stepI]?.id}.json")
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
            println("Error 6")
            return
        }
    }
    client.close()
    println("Press any key to exit")
    readln()
}

fun removeHtmlTags(input: String): String {
    return input.replace(Regex("<(\"[^\"]*\"|'[^']*'|[^'\">])*>"),"").replace("&nbsp;"," ")
}

fun isCapableType(input: String): Boolean {
    val capable = setOf("14","15","16","20")
    return capable.contains(input)
}

fun getBaseUrl(courseString: String, unit: String?, courseId: String): String {
    return "https://nanext.alcnanext.jp/anetn/course/materials/ALC/TLP/TLP_$courseString/$unit/$courseId/"
}

@OptIn(ExperimentalEncodingApi::class)
inline fun <reified T> getApiResource(client: HttpClient, url: String): T {
    val cache = fromCacheOrNull<T>(Base64.encode(url.encodeToByteArray()))
    if(cache != null){
        return cache
    }
    val resp = runBlocking<T>{
        client.get(url).body()
    }
    saveCache(Base64.encode(url.encodeToByteArray()), resp)
    return resp
}

inline fun <reified T> fromCacheOrNull(key: String):T?{
    val file = File("cache/$key")
    if(file.exists()){
        return file.readText().let{
            Json.decodeFromString(it)
        }
    }
    return null
}

@OptIn(ExperimentalForeignApi::class)
inline fun <reified T> saveCache(key: String, value: T){
    val dir = File("cache")
    if(!dir.exists()){
        SHCreateDirectoryExA(null, dir.getAbsolutePath(), null)
    }
    val file = File("cache/$key")
    file.createNewFile()
    file.writeText(Json.encodeToString(value))
}
data class SubCourse(val name:String, val courseString:String, val courseId:String )