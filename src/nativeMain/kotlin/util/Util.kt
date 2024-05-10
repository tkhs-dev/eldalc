package util

fun removeHtmlTags(input: String): String {
    return input.replace(Regex("<(\"[^\"]*\"|'[^']*'|[^'\">])*>"),"").replace("&nbsp;"," ")
}

fun printBorder(){
    println("------------------------------------------------")
}