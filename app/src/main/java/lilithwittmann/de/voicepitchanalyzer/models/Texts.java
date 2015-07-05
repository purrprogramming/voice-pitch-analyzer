package lilithwittmann.de.voicepitchanalyzer.models;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import lilithwittmann.de.voicepitchanalyzer.R;

/**
 * Created by lilith on 7/5/15.
 */
public class Texts {

    public Texts() {

    }

    public String getText(String lang, Integer id) {
        JsonElement jelement = new JsonParser().parse(getJsonData());
        JsonObject jobject = jelement.getAsJsonObject();
        JsonObject langs = jobject.getAsJsonObject("texts");
        JsonArray texts = langs.getAsJsonArray(lang);
        return texts.get(id - 1).getAsString();
    }

    public Integer countTexts(String lang) {
        JsonElement jelement = new JsonParser().parse(getJsonData());
        JsonObject jobject = jelement.getAsJsonObject();
        JsonObject langs = jobject.getAsJsonObject("texts");
        JsonArray texts = langs.getAsJsonArray(lang);
        return texts.getAsJsonArray().size();
    }

    private String jsonFile = "res/raw/texts.json";
    private String jsonData = null;

    String getJsonData() {
        if (this.jsonData != null) {
            return jsonData;
        } else {
            try {
                this.jsonData = this.readJsonNFile(this.jsonFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return this.jsonData;

        }
    }

    private String readJsonNFile(String jsonFile) throws IOException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(jsonFile);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            is.close();
        }

        String jsonString = writer.toString();

        return jsonString;
    }

}
