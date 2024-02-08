package io.github.allianaab2m.romechat.japanize

import com.google.common.io.CharStreams
import com.google.gson.Gson
import com.google.gson.JsonArray
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/*
* @author     ucchy
* @license    LGPLv3
* @copyright  Copyright ucchy 2020
*/
object IMEConverter {
    private const val API_URL = "https://www.google.com/transliterate?langpair=ja-Hira|ja&text="
    fun parseJson(json: String?): String {
        val result = StringBuilder()
        for (response in Gson().fromJson(json, JsonArray::class.java)) {
            result.append(response.asJsonArray[1].asJsonArray[0].asString)
        }
        return result.toString()
    }

    fun Convert(text: String): String {
        if (text.isEmpty()) {
            return ""
        }

        var urlconn: HttpURLConnection? = null
        var reader: BufferedReader? = null
        try {
            val baseurl = API_URL + URLEncoder.encode(text, StandardCharsets.UTF_8)
            val encode = "UTF-8"

            val url = URL(baseurl)

            urlconn = url.openConnection() as HttpURLConnection
            urlconn.requestMethod = "GET"
            urlconn.instanceFollowRedirects = false
            urlconn.connect()

            reader = BufferedReader(
                InputStreamReader(urlconn.inputStream, encode)
            )

            val json = CharStreams.toString(reader)

            return parseJson(json)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            urlconn?.disconnect()
            if (reader != null) {
                try {
                    reader.close()
                } catch (ignored: IOException) {
                }
            }
        }
        return ""
    }
    fun String.convIME(): String { return Convert(this) }
}
