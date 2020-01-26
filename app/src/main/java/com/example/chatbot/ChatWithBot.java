package com.example.chatbot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chatbot.Model.Repository;
import com.example.chatbot.PruebasTTS.TextToSpeechActivity;
import com.example.chatbot.View.MessageAdapter;
import com.example.chatbot.apibot.ChatterBot;
import com.example.chatbot.apibot.ChatterBotFactory;
import com.example.chatbot.apibot.ChatterBotSession;
import com.example.chatbot.apibot.ChatterBotType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class ChatWithBot extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private static final String TAG ="si" ;
    private MessageAdapter adapter;
    private Repository repository;
    private ImageView ivSend;
    private ImageView mic;
    private EditText etMessage;
    private ArrayList<ChatSentence> messages=new ArrayList<>();
    private Messages message;
    private Messages messageBot;
    private  RecyclerView rvList;
    private TextToSpeech mTts;
    String refDate;
    private boolean inicio=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_with_bot);
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String day = new SimpleDateFormat("dd").format(date);    // always 2 digits
        String month = new SimpleDateFormat("MM").format(date);  // always 2 digits
        String year = new SimpleDateFormat("yyyy").format(date); // 4 digit year
        refDate=year+month+day;
        ivSend=findViewById(R.id.ivSend);
        ivSend.setVisibility(View.INVISIBLE);

        getTodayUserChat();
    }
    /*
    * users: example2@example.com-example2
    *        example3@example.com-example2
    *
    * */

    private void getTodayUserChat() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference refItem= database.getReference("user/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/"+refDate);

        refItem.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(inicio){
                    inicio=false;
                    Iterator<DataSnapshot> iterator=dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext()){
                        ChatSentence chatSentence = new ChatSentence();
                        Map<String,String> property=(Map<String,String>)iterator.next().getValue();
                        chatSentence.setTalker(property.get("talker"));
                        chatSentence.setSetenceEs(property.get("sentenceEs"));
                        chatSentence.setSentenceEn(property.get("sentenceEn"));
                        chatSentence.setTime(property.get("time"));
                        messages.add(chatSentence);
                    }
                    init();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void init() {
        repository=new Repository();
        adapter= new MessageAdapter(this );
        rvList = findViewById(R.id.rvList);
        final LinearLayoutManager mManager = new LinearLayoutManager(this);
        rvList.setLayoutManager(mManager);
        rvList.setAdapter(adapter);

        adapter.setUserMessagesList(messages);
        loadRecycler();
        mTts = new TextToSpeech(this,
                this  // TextToSpeech.OnInitListener
        );
        etMessage=findViewById(R.id.etMessage);
        mic=findViewById(R.id.mic);
        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(etMessage.getText().toString().trim().equalsIgnoreCase("")){
                    ivSend.setVisibility(View.INVISIBLE);
                    mic.setVisibility(View.VISIBLE);
                }else {
                    ivSend.setVisibility(View.VISIBLE);
                    mic.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mensaje=etMessage.getText().toString().trim();
                if(!mensaje.equalsIgnoreCase("")){
                    //Traduccion para el bot
                    new TraduceEsEn().execute(mensaje);
                    etMessage.setText("");
                }
            }
        });
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),"Say Something...",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

                if(intent.resolveActivity(getPackageManager())!=null) {
                    startActivityForResult(intent, 5);
                }
                else {
                    Toast.makeText(v.getContext(),"Your Device Doesn't Support Speech Intent", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public void onDestroy() {
        // Don't forget to shutdown!
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
        super.onDestroy();
    }
    // Implements TextToSpeech.OnInitListener.
    public void onInit(int status) {
        Locale spanish= new Locale("es","ES");
        // status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
        if (status == TextToSpeech.SUCCESS) {
            // Set preferred language to US english.
            // Note that a language may not be available, and the result will indicate this.
            int result = mTts.setLanguage(spanish);
            // Try this someday for some interesting results.
            // int result mTts.setLanguage(Locale.FRANCE);
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Lanuage data is missing or the language is not supported.
                Log.e(TAG, "Language is not available.");
            } else {
                // Check the documentation for other possible result codes.
                // For example, the language may be available for the locale,
                // but not for the specified country and variant.
                // The TTS engine has been successfully initialized.
                // Allow the user to press the button for the app to speak again.
                // Greet the user.
                //sayText();
            }
        } else {
            // Initialization failed.
            Log.e(TAG, "Could not initialize TextToSpeech.");
        }
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
        String msES,msEN;
        @Override
        protected String doInBackground(String... strings) {
            msES=strings[0];
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
            msEN=s;
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);
            int milisecond =calendar.get(Calendar.MILLISECOND);
            String time=hour+":"+minute+":"+second+":"+milisecond;
            ChatSentence chatSentence= new ChatSentence(msEN,msES,"user",time);
            saveMessageDB(chatSentence);
            messages.add(chatSentence);
            loadRecycler();
            try {
                new Chat().execute(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveMessageDB(ChatSentence item) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference refItem= database.getReference("user/"+FirebaseAuth.getInstance().getCurrentUser().getUid());
        refItem.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.v(TAG, "data changed: " + dataSnapshot.toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.v(TAG, "error: " + databaseError.toException());
            }
        });
        Map<String, Object> map = new HashMap<>();

        String key = refItem.child(refDate).push().getKey();
        map.put(refDate+"/" + key, item.toMap());
        refItem.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.v(TAG, "task succesfull");
                } else {
                    Log.v(TAG, task.getException().toString());
                }
            }
        });
    }

    //[{ "detectedLanguage": { "language": "es", "score": 1 },"translations": [{ "text": "Hello","to": "en" }],}],

    private class TraduceEnEs extends AsyncTask<String,Void, String> {
        String msES,msEN;
        @Override
        protected String doInBackground(String... strings) {
            msEN=strings[0];
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

            msES=s;
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);
            int milisecond =calendar.get(Calendar.MILLISECOND);
            String time=hour+":"+minute+":"+second+":"+milisecond;
            ChatSentence chatSentence= new ChatSentence(msEN,msES,"bot",time);
            saveMessageDB(chatSentence);
            messages.add(chatSentence);
            //Method for load the recycler with the new bot message
            putBotMessage();
            sayText(s);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==5) {
            if(resultCode==RESULT_OK && data!=null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String mensaje=result.get(0);
                //Traduccion para el bot
                new TraduceEsEn().execute(mensaje);
            }
        }
    }
    public void sayText(String text) {
        mTts.speak(text,
                TextToSpeech.QUEUE_FLUSH,  // Drop all pending entries in the playback queue.
                null);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_history) {
            startActivity(new Intent(this,HistorialActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
