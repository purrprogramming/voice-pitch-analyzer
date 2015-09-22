package de.lilithwittmann.voicepitchanalyzer.models;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lilith on 7/5/15.
 */
public class Texts {

    public Texts() {

    }

    public String getText(String lang, Integer id) {
        JsonElement jelement = new JsonParser().parse(getJsonData(lang));
        JsonObject jobject = jelement.getAsJsonObject();
        JsonObject langs = jobject.getAsJsonObject("texts");
        JsonArray texts = langs.getAsJsonArray(lang);
        return texts.get(id - 1).getAsString();
    }

    public Integer countTexts(String lang) {
        JsonElement jelement = new JsonParser().parse(getJsonData(lang));
        JsonObject jobject = jelement.getAsJsonObject();
        JsonObject langs = jobject.getAsJsonObject("texts");
        JsonArray texts = langs.getAsJsonArray(lang);
        return texts.getAsJsonArray().size();
    }

    private String jsonFolder = "res/raw/";
    private String jsonData = null;

    private List<String> supportedLanguages = new ArrayList<String>() {{add("de"); add("en"); add("it"); add("pt");}};

    String getJsonData(String country) {
        if (this.jsonData != null) {
            return jsonData;
        } else {
            try {
                this.jsonData = this.readJsonNFile(this.jsonFolder+country+".json");
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

    public boolean supportsLocale(String language) {
        if(this.supportedLanguages.contains(language))
        {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
