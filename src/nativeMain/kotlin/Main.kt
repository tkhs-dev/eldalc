import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.winhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.date.*
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cValue
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.*
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonTransformingSerializer
import me.archinamon.fileio.File
import me.archinamon.fileio.readText
import me.archinamon.fileio.writeText
import model.*
import platform.posix.nanosleep
import platform.posix.sleep
import platform.posix.timespec
import platform.windows.SHCreateDirectoryExA
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

const val APP_NAME = "E-Learning Destroyer for ALC"
const val VERSION = "1.3.0"

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
    var stateCache = fromCacheOrNull<StateCache>("state") ?: StateCache("", "", "", "")
    val stateCacheExist = stateCache.courseString.isNotBlank() && stateCache.courseId.isNotBlank()
    val subCourses = mutableSetOf<SubCourse>(
        SubCourse("リスニング","LI", "01"),
        SubCourse("文法","GR","06"),
        SubCourse("リーディング","RE","03"),
        SubCourse("テスト","JT","08"), //This course is not supported
    )
    if (stateCacheExist){
        subCourses.add(SubCourse("前回のユニット",stateCache.courseString,stateCache.courseId))
    }
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
    if(subCourse == null || subCourse !in 0.. subCourses.size){
        println("Error 2")
        return
    }
    val autoMode = subCourse == subCourses.size

    val unit = if (stateCacheExist && subCourse == subCourses.size-1){
        stateCache.unit
    }else{
        println("Enter a Unit number (ex: U001)")
        print(">")
        readlnOrNull()
    }

    if(autoMode && !unit.isNullOrBlank()){
        if(!unit.startsWith("JT0")){
            println("Error 3")
            return
        }
        if(unit.length != 4){
            println("Error 4")
            return
        }
        if(unit.last() !in '1'..'4'){
            println("Error 4")
            return
        }
        runBlocking {
            automatic(unit,client)
        }
        return
    }

    val courseString = subCourses.elementAt(subCourse).courseString
    val courseId = subCourses.elementAt(subCourse).courseId

    val steps = if(courseString == "JT"){
        val unitInfo : AlcJTUnitInfoResponse = getApiResource(client, getBaseUrl(courseString,unit,courseId)+"step_info.json")
        unitInfo.sections.flatMap { it.steps }.filter { isCapableType(it.type ?: "") }
    }else{
        val unitInfo : AlcUnitInfoResponse = getApiResource(client, getBaseUrl(courseString,unit,courseId)+"step_info.json")
        unitInfo.steps?.filter { isCapableType(it?.type ?: "") }
    }

    println("Select a step")
    if(steps==null){
        println("Error 3")
        return
    }

    stateCache = StateCache(
        courseString,
        courseId,
        unit ?: "",
        "")
    saveCache("state", stateCache)

    println("  (0)すべて表示")
    for ((index, s) in steps.withIndex()){
        println("  (${index+1})${s?.name}")
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
    val selectedSteps = if(stepI == 0){
        steps
    }else{
        steps.slice(stepI-1..<stepI)
    }

    for ((index,s) in selectedSteps.withIndex()){
        val url = getBaseUrl(courseString,unit,courseId)+"${steps[index]?.id}.json"
        when(s?.type){
            "14" -> {
                getApiResource<AlcQuestion14Response>(client, url)
            }
            "15" -> {
                getApiResource<AlcQuestion15Response>(client, url)
            }
            "16" -> {
                getApiResource<AlcQuestion16Response>(client, url)
            }
            "20" -> {
                getApiResource<AlcQuestion20Response>(client, url)
            }
            else -> {
                println("Error 6")
                return
            }
        }.printQuestion()
    }
    client.close()
    println("Press any key to exit")
    readln()
}

@OptIn(ExperimentalForeignApi::class)
suspend fun automatic(unit: String, client: HttpClient){
    data class QA(val No: Int, val QKNo: String, val CAnswer: String, val YAnswer: String)

    println("Automatic mode enabled!")
    println("Enter cookie value copied from EditThisCookie")
    print(">")
    val cookie = readlnOrNull()
    val cookieJson = """
        {
            "cookies": $cookie
        }
    """.trimIndent()
    val cookieObj = Json.decodeFromString<Cookie>(cookieJson)
    val sessionId = cookieObj.cookies.find { it.name == "ASP.NET_SessionId" }?.value
    val unitInfo : AlcJTUnitInfoResponse = getApiResource(client, getBaseUrl("JT",unit,"08")+"step_info.json")

    //header
    val headers = mapOf(
        "Cookie" to cookieObj.cookies.joinToString(";"){ "${it.name}=${it.value}" },
        "Accept" to "application/json, text/javascript, */*; q=0.01",
        "Accept-Encoding" to "gzip, deflate, br, zstd",
        "Accept-Language" to "ja-JP,ja;q=0.9,en-US;q=0.8,en;q=0.7",
        "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36",
        "X-Requested-With" to "XMLHttpRequest",
        "Content-Type" to "application/JSON",
        "sec-ch-ua" to "\"Not/A)Brand\";v=\"8\", \"Chromium\";v=\"126\", \"Google Chrome\";v=\"126\"",
        "sec-ch-ua-mobile" to "?0",
        "Sec-Fetch-platform" to "\"Windows\"",
        "Sec-Fetch-Dest" to "empty",
        "Sec-Fetch-Mode" to "cors",
        "Sec-Fetch-Site" to "same-origin",
    )

    println("Start JT Unit:$unit")

    //generate QA result data
    val result: MutableList<QA> = mutableListOf()
    for( step in unitInfo.sections.flatMap { it.steps }) {
        val qa = getApiResource<AlcQuestion20Response>(client, getBaseUrl("JT",unit,"08")+"${step.id}.json")
        val tmp = mutableListOf<List<AlcQuestion20Response.Question?>>()
        var count = 0
        qa.rule?.paging?.map {  it?.questionNumber?.toIntOrNull() ?: 0 }?.forEach {
            if(qa.questions != null){
                tmp.add(qa.questions.slice(count until count+it))
            }
            count += it
        }
        val random = Random(getTimeMillis())
        val questions = tmp.shuffled().flatten()
        for((index,question) in questions.withIndex()){
            if(question == null){
                continue
            }
            val isCorrect = random.nextInt(0,101) in 0..70
            val answer = if(isCorrect){
                question.answer
            }else{
                question.choices?.filter { it?.symbol != question.answer }?.random()?.symbol
            }
            result.add(QA(index, question.id!!, question.answer!!, answer!!))
        }
    }

    val sectionResults = result.groupBy { it.QKNo.split('_')[3] }.let{
        listOf(it["LI"]!!, it["GR"]!! + it["GS"]!!, it["RE"]!!)
    }
    val sleepSec = 6600 + Random.nextInt(-220, 220)
    println("Generated QA data:")
    result.forEach {
        println("  - ${it.No+1} ${it.QKNo} ${it.CAnswer} ${it.YAnswer} ${if(it.YAnswer == it.CAnswer)"O" else "X"}")
    }
    println("  - Listening: ${(sectionResults[0].filter { it.YAnswer == it.CAnswer }.size.toDouble()/sectionResults[0].size.toDouble())*100}%")
    println("  - Grammar: ${(sectionResults[1].filter { it.YAnswer == it.CAnswer }.size.toDouble()/sectionResults[1].size.toDouble())*100}%")
    println("  - Reading: ${(sectionResults[2].filter { it.YAnswer == it.CAnswer }.size.toDouble()/sectionResults[2].size.toDouble())*100}%")
    println("  - Total: ${(result.filter { it.YAnswer == it.CAnswer }.size.toDouble()/result.size.toDouble())*100}%")
    println()
    println("!!WARNING!! Soon after pressing any key, start history will be registered!")
    println("You can cancel by pressing Ctrl+C.")
    println("Press any key to start submitting data")
    readln()
    println("Result data will be submitted after $sleepSec seconds(${Clock.System.now().plus(sleepSec.seconds).toLocalDateTime(
        TimeZone.currentSystemDefault()).format(LocalDateTime.Formats.ISO)}).")
    println("!!WARNING!! DON'T close the application or shutdown/suspend your computer until the data is submitted.")

    //registerStartHistory
    val bodyJson = """
        {
        	"Qtype": "-",
        	"VId": "ALC",
        	"CId": "TLP",
        	"SId": "TLP_JT",
        	"UId": "TLP_JT_$unit",
        	"SessionId": "${sessionId!!}"
        }
    """.trimIndent()
    val registerStartHistory:StartHistoryResponse = client.post("https://nanext.alcnanext.jp/anetn/api/HistoryApi/registStartHistory"){
        headers.forEach { (k,v) ->
            header(k,v)
        }
        setBody(bodyJson)
    }.body()

    if(registerStartHistory.result != "0"){
        println("Failed to register StartHistory. Error code:${registerStartHistory.result}")
        return
    }

    nanosleep(cValue<timespec> {
        tv_sec = sleepSec.toLong()
    },null)
    println("Submitting data...")

    //acqSystemDateTime
    val acqSystemDateTime:AcqSystemDateTimeResponse = client.post("https://nanext.alcnanext.jp/anetn/api/HistoryApi/acqSystemDateTime"){
        headers.forEach { (k,v) ->
            header(k,v)
        }
        setBody("""
            {
                "SDate": "${registerStartHistory.sDate}",
            }
        """.trimIndent())
    }.body()

    //registerLearnHistory
    val registerRequest = RegisterHistoryRequest(
        fId = "19,19,19",
        lCD = "2",
        lInfo = RegisterHistoryRequest.LInfo(
            fID19 = RegisterHistoryRequest.LInfo.FID19(
                stepSection19 = sectionResults.mapIndexed { index, qa ->
                    RegisterHistoryRequest.LInfo.FID19.StepSection19(
                        sOrder = (index+1).toString(),
                        sFlag = "1",
                        arr19 = qa.mapIndexed { i, it ->
                            RegisterHistoryRequest.LInfo.FID19.StepSection19.Arr19(
                                qNo = (i+1).toString(),
                                qKNo = it.QKNo,
                                errata = if(it.YAnswer == it.CAnswer)"1" else "0",
                                explicationFlg = "1",
                                yans = it.YAnswer,
                                ans = it.CAnswer
                            )
                        }
                    )
                }
            ),
            forTest = RegisterHistoryRequest.LInfo.ForTest(
                rNo = result.filter { it.YAnswer == it.CAnswer }.size,
                qNo = result.size,
                score = "",
                eDate = acqSystemDateTime.sysDate,
                failingInfo = RegisterHistoryRequest.LInfo.ForTest.FailingInfo(
                    failingInfoFlg = "0",
                    unitArray = emptyList(),
                ),
                partInfo = RegisterHistoryRequest.LInfo.ForTest.PartInfo(
                    partInfoFlg = "1",
                    dArray = sectionResults.mapIndexed { index, qa ->
                        RegisterHistoryRequest.LInfo.ForTest.PartInfo.DArray(
                            sectionID = "Section${index+1}",
                            qCount = qa.size.toString(),
                            rCount = qa.filter { it.YAnswer == it.CAnswer }.size.toString(),
                            rPercent = (((qa.filter { it.YAnswer == it.CAnswer }.size.toDouble() / qa.size.toDouble() * 100)*10).roundToInt()/10.0).toString(),
                            ePoint = unitInfo.sections.map { it.rule.estimateInfo.estimates[0].scores.map { scoreList ->
                                val range = scoreList.correct.split(',').map { it.toIntOrNull() ?: 0 }
                                val score = scoreList.score.split(',').joinToString("～")
                                range[0]..range[1] to score
                            } }[index].find { it.first.contains(qa.filter { it.YAnswer == it.CAnswer }.size) }?.second ?: "0",
                            score = ""
                        )
                    }.toMutableList().let{
                        it.add(
                            RegisterHistoryRequest.LInfo.ForTest.PartInfo.DArray(
                                sectionID = "Total",
                                qCount = result.size.toString(),
                                rCount = result.filter { it.YAnswer == it.CAnswer }.size.toString(),
                                rPercent = (((result.filter { it.YAnswer == it.CAnswer }.size.toDouble() / result.size.toDouble() * 100)*10).roundToInt()/10.0).toString(),
                                ePoint = it.map { it.ePoint.split('～').map { it.toIntOrNull() ?: 0 }}.let{listOf((it.sumOf { it[0] }/3.0).roundToInt(),(it.sumOf { it[1] }/3.0).roundToInt())}.joinToString("～"),
                                score = ""
                            )

                        )
                        it
                    }
                ),
                adviceInfo = RegisterHistoryRequest.LInfo.ForTest.AdviceInfo(
                    adviceInfoFlg = "0",
                    aArray = emptyList()
                ),
            )
        ),
        sDate = registerStartHistory.sDate,
        skill = "0,0,0,0,0,0",
        vId = "ALC",
        cId = "TLP",
        sId = "TLP_JT",
        uId = "TLP_JT_$unit",
        sessionId = sessionId!!,
    )

    val registerResponse:String = client.post("https://nanext.alcnanext.jp/anetn/api/HistoryApi/registLearnHistory"){
        headers.forEach { (k,v) ->
            header(k,v)
        }
        setBody(Json.encodeToString(registerRequest))
    }.body()
    if(registerResponse != "0"){
        println("Failed to register LearnHistory. Error code:$registerResponse")
        return
    }else{
        println("Successfully submitted data!")
    }

    println("Press any key to exit")
    readln()
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
            Json {
                ignoreUnknownKeys = true
                explicitNulls = true
            }.decodeFromString(it)
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