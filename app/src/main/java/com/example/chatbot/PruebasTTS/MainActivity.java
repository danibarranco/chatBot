package com.example.chatbot.PruebasTTS;

import android.os.AsyncTask;
import android.os.Bundle;

import com.example.chatbot.R;
import com.example.chatbot.apibot.ChatterBot;
import com.example.chatbot.apibot.ChatterBotFactory;
import com.example.chatbot.apibot.ChatterBotSession;
import com.example.chatbot.apibot.ChatterBotType;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private volatile boolean chat = false;
    private static final String TAG = "TextToSpeechDemo";
    private TextToSpeech mTts;
    private Button mAgainButton;
    private EditText etText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                chat = !chat;
                if(chat){
                    new Chat().execute();
                }

            }
        });
        // Initialize text-to-speech. This is an asynchronous operation.
        // The OnInitListener (second argument) is called after initialization completes.
        mTts = new TextToSpeech(this,
                this  // TextToSpeech.OnInitListener
        );
        // The button is disabled in the layout.
        // It will be enabled upon initialization of the TTS engine.
        mAgainButton =findViewById(R.id.again_button);
        mAgainButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                chat = !chat;
                if(chat){
                    new Chat().execute();
                }
            }
        });
        etText=findViewById(R.id.etText);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void chat(){
        try{
            ChatterBotFactory factory = new ChatterBotFactory();
            ChatterBot bot1 = factory.create(ChatterBotType.PANDORABOTS, "b0dafd24ee35a477");
            ChatterBotSession bot1session = bot1.createSession();
            ChatterBot bot2 = factory.create(ChatterBotType.PANDORABOTS, "b0dafd24ee35a477");
            ChatterBotSession bot2session = bot2.createSession();
            String s = "Hello";
            String r = "I say: ";
            while (chat) {
                sayText(s);
                s = bot2session.think(s);
                sayText(s);
                s = bot1session.think( r + s);
            }
        }catch(Exception e){

        }
    }

    private class Chat extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            chat();
            String s = getTextFromUrl("https://www.google.com/search?q=prueba+de+lo+que+sea&oq=prueba+de+lo+que+sea&aqs=chrome..69i57j0l7.11925j0j7&sourceid=chrome&ie=UTF-8");
            Log.v("xyz",s);
            return null;
        }
    }

    //Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36
    //https://www.bing.com/ttranslatev3
    //POST
    //&fromLang=es&text=soy%20programador&to=en
    //fromLang=es
    //text=soy programador
    //to=en

    public static String getTextFromUrl(String src) {
        StringBuffer out = new StringBuffer();
        try {
            URL url = new URL(src);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = in.readLine()) != null) {
                out.append(line + "\n");
            }
            in.close();
        } catch (IOException e) {
        }
        return out.toString();
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
                mAgainButton.setEnabled(true);
                // Greet the user.
                //sayText();
            }
        } else {
            // Initialization failed.
            Log.e(TAG, "Could not initialize TextToSpeech.");
        }
    }

    private void sayText(String text) throws InterruptedException {
        mTts.speak(text,
                TextToSpeech.QUEUE_FLUSH,  // Drop all pending entries in the playback queue.
                null);
        while (mTts.isSpeaking()){

        }
    }
}
