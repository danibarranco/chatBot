package com.example.chatbot.Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class Repository {

    private String urlTraductor="https://www.bing.com/ttranslatev3";

    public Repository() {
    }

    public String traduceMensajeEsEn(String message) throws UnsupportedEncodingException {
        HashMap<String, String> httpBodyParams;
        httpBodyParams = new HashMap<>();
        httpBodyParams.put("fromLang", "es");
        httpBodyParams.put("text", message);
        httpBodyParams.put("to", "en");

        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : httpBodyParams.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        String parameters = result.toString();
        return postHttp(urlTraductor,parameters);
    }

    public String traduceMensajeEnEs(String message) throws UnsupportedEncodingException {
        HashMap<String, String> httpBodyParams;
        httpBodyParams = new HashMap<>();
        httpBodyParams.put("fromLang", "en");
        httpBodyParams.put("text", message);
        httpBodyParams.put("to", "es");

        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : httpBodyParams.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        String parameters = result.toString();
        return postHttp(urlTraductor,parameters);
    }

    public String postHttp(String src, String body) {
        StringBuffer buffer = new StringBuffer();
        try {
            URL url = new URL(src);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.connect();
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            out.write(body);
            out.flush();
            out.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                buffer.append(line + "\n");
            }
            in.close();
        } catch (IOException e) {
        }
        return buffer.toString();
    }
}
