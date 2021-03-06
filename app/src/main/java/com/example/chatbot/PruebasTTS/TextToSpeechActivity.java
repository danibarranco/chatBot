package com.example.chatbot.PruebasTTS;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.chatbot.R;

import java.util.Locale;
import java.util.Random;

public class TextToSpeechActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    private static final String TAG = "TextToSpeechDemo";
    private TextToSpeech mTts;
    private Button mAgainButton;
    private EditText etText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_to_speech);

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
                sayText();
            }
        });
        etText=findViewById(R.id.etText);
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
    private static final Random RANDOM = new Random();
    private static final String[] HELLOS = {
            "Hello",
            "Salutations",
            "Greetings",
            "Howdy",
            "What's crack-a-lackin?",
            "That explains the stench!"
    };
    private void sayText() {
        // Select a random hello.
        // int helloLength = HELLOS.length;
        //String hello = HELLOS[RANDOM.nextInt(helloLength)];
        mTts.speak(etText.getText().toString(),
                TextToSpeech.QUEUE_FLUSH,  // Drop all pending entries in the playback queue.
                null);
    }
    public void sayText(String text) {
        mTts.speak(text,
                TextToSpeech.QUEUE_FLUSH,  // Drop all pending entries in the playback queue.
                null);
    }
}
