package com.example.chatbot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.chatbot.Model.Repository;
import com.example.chatbot.View.MessageAdapter;
import com.example.chatbot.apibot.ChatterBot;
import com.example.chatbot.apibot.ChatterBotFactory;
import com.example.chatbot.apibot.ChatterBotSession;
import com.example.chatbot.apibot.ChatterBotType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class ChatWithBot extends AppCompatActivity {
    private MessageAdapter adapter;
    private Repository repository;
    private ImageView ivSend;
    private EditText etMessage;
    private ArrayList<Messages> messages= new ArrayList<>();
    private Messages message;
    private Messages messageBot;
    private  RecyclerView rvList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_with_bot);
        init();
    }

    private void init() {
        repository=new Repository();
        adapter= new MessageAdapter(this );
        rvList = findViewById(R.id.rvList);
        final LinearLayoutManager mManager = new LinearLayoutManager(this);
        rvList.setLayoutManager(mManager);
        rvList.setAdapter(adapter);

        etMessage=findViewById(R.id.etMessage);
        ivSend=findViewById(R.id.ivSend);
        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mensaje=etMessage.getText().toString().trim();
                if(!mensaje.equalsIgnoreCase("")){
                    //Creamos mensaje para recycler
                    message=new Messages();
                    message.setType("me");
                    etMessage.setText("");
                    message.setMessage(mensaje);
                    messages.add(message);
                    //Traduccion para el bot
                    new TraduceEsEn().execute(mensaje);
                    //cargamos recycler
                    loadRecycler();
                }
            }
        });
    }

    private void loadRecycler() {
        adapter.setUserMessagesList(messages);
        rvList.smoothScrollToPosition(adapter.getItemCount());
        rvList.invalidate();
    }

    private void chat(String s) throws Exception {

        ChatterBotFactory factory = new ChatterBotFactory();
        ChatterBot bot1 = factory.create(ChatterBotType.PANDORABOTS, "b0dafd24ee35a477");
        ChatterBotSession bot1session = bot1.createSession();
        s = bot1session.think(s);
        new TraduceEnEs().execute(s);


    }

    private void putBotMessage(){
        //recagar recycler en el thread principal con mensaje de bot traducido
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadRecycler();
            }
        });
    }



    private class TraduceEsEn extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                return repository.traduceMensajeEsEn(strings[0]);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONArray jObject = null;
            JSONObject jsonObject=null;
            //Json encode for get the text traduction
            try {

                jObject = new JSONArray(s);
                s=jObject.get(0).toString();
                jsonObject= new JSONObject(s);
                s=jsonObject.get("translations").toString();

                jObject= new JSONArray(s);
                s=jObject.get(0).toString();
                jsonObject= new JSONObject(s);
                s=jsonObject.get("text").toString();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Mandamos string en ingles al bot
            try {
                new Chat().execute(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //[{ "detectedLanguage": { "language": "es", "score": 1 },"translations": [{ "text": "Hello","to": "en" }],}],

    private class TraduceEnEs extends AsyncTask<String,Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                return repository.traduceMensajeEnEs(strings[0]);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONArray jObject = null;
            JSONObject jsonObject=null;
            //Json encode for get the text traduction
            try {

                jObject = new JSONArray(s);
                s=jObject.get(0).toString();
                jsonObject= new JSONObject(s);
                s=jsonObject.get("translations").toString();

                jObject= new JSONArray(s);
                s=jObject.get(0).toString();
                jsonObject= new JSONObject(s);
                s=jsonObject.get("text").toString();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Create bot message,traduced to es
            messageBot=new Messages();
            messageBot.setType("bot");
            messageBot.setMessage(s);
            messages.add(messageBot);
            //Method for load the recycler with the new bot message
            putBotMessage();
        }
    }

    private class Chat extends AsyncTask<String, Void, Void>{
        @Override
        protected Void doInBackground(String... strings) {
            try {
                chat(strings[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}
