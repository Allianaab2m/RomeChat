package io.github.allianaab2m.romechat.japanize;

import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
public class IMEConverter {
    private static final String API_URL = "https://www.google.com/transliterate?langpair=ja-Hira|ja&text=";
    public static String parseJson(String json) {
        StringBuilder result = new StringBuilder();
        for ( JsonElement response : new Gson().fromJson(json, JsonArray.class) ) {
            result.append(response.getAsJsonArray().get(1).getAsJsonArray().get(0).getAsString());
        }
        return result.toString();
    }

    public static String Convert(String text) {
        if (text.isEmpty()) {
            return "";
        }

        HttpURLConnection urlconn = null;
        BufferedReader reader = null;
        try {
            String baseurl = API_URL + URLEncoder.encode(text, StandardCharsets.UTF_8);
            String encode = "UTF-8";

            URL url = new URL(baseurl);

            urlconn = (HttpURLConnection)url.openConnection();
            urlconn.setRequestMethod("GET");
            urlconn.setInstanceFollowRedirects(false);
            urlconn.connect();

            reader = new BufferedReader(
                    new InputStreamReader(urlconn.getInputStream(), encode)
            );

            String json = CharStreams.toString(reader);

            return parseJson(json);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlconn != null) {
                urlconn.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
        }
        return "";
    }
}
