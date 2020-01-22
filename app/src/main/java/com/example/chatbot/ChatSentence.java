package com.example.chatbot;

import java.util.HashMap;
import java.util.Map;

public class ChatSentence {
    private String sentenceEn, setenceEs,talker,time;

    public ChatSentence() {
    }

    public ChatSentence(String sentenceEn, String setenceEs, String talker, String time) {
        this.sentenceEn = sentenceEn;
        this.setenceEs = setenceEs;
        this.talker = talker;
        this.time = time;
    }

    public String getSentenceEn() {
        return sentenceEn;
    }

    public void setSentenceEn(String sentenceEn) {
        this.sentenceEn = sentenceEn;
    }

    public String getSetenceEs() {
        return setenceEs;
    }

    public void setSetenceEs(String setenceEs) {
        this.setenceEs = setenceEs;
    }

    public String getTalker() {
        return talker;
    }

    public void setTalker(String talker) {
        this.talker = talker;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "ChatSentence{" +
                "sentenceEn='" + sentenceEn + '\'' +
                ", setenceEs='" + setenceEs + '\'' +
                ", talker='" + talker + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("sentenceEn", sentenceEn);
        result.put("sentenceEs", setenceEs);
        result.put("talker", talker);
        result.put("time", time);
        return result;
    }

    public void setPropertyValue(String key, String value) {

    }
}
