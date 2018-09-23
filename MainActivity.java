package com.example.eliascan.citwebstage;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //The Variables
    private EditText txtFirstName, txtCityName;
    private ImageButton ibtMicrophoneName, ibtMicrophoneCity, ibtSpeaker;
    private final int REQ_INPUT_NAME = 100;
    private final int REQ_INPUT_CITY = 200;

    private TextToSpeech toSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init TextToSpeech
        toSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if (status == TextToSpeech.SUCCESS) {
                    int result = toSpeech.setLanguage(Locale.getDefault());
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(MainActivity.this, "This not supported", Toast.LENGTH_SHORT).show();
                    } else {
                        ibtSpeaker.isClickable();
                        toSpeech.setPitch(0.6f);
                        toSpeech.setSpeechRate(1.0f);
                        speak();
                    }
                }
            }
        });

        //Int Views
        txtFirstName = findViewById(R.id.etFirstName);
        txtCityName = findViewById(R.id.etCityName);
        ibtMicrophoneName = findViewById(R.id.iBMicrophoName);
        ibtMicrophoneCity = findViewById(R.id.iBMicrophoneCity);
        ibtSpeaker = findViewById(R.id.ibSpeaker);

        //OnClick Method for all ImageButtons
        ibtMicrophoneName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput(REQ_INPUT_NAME);
            }
        });

        ibtMicrophoneCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput(REQ_INPUT_CITY);
            }
        });

        ibtSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });
    }

    /*
        SHOW GOOGLE SPEECH INPUT DIALOG
     */
    private void promptSpeechInput(int req) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        try {
            startActivityForResult(intent, req);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show();
        }
    }

    /*
        RECEIVING SPEECH INPUT FROM THE EDIT TEXTS
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_INPUT_NAME) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                txtFirstName.setHint("");
                txtFirstName.setText(result.get(0));
            }
        }

        if (requestCode == REQ_INPUT_CITY){
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                txtCityName.setHint("");
                txtCityName.setText(result.get(0));
            }

        }
    }

    /*
        METHOD TO ACTIVATE THE SPEECH ACTIVITY
     */
    private void speak() {
        // conditional to check if the EditTexts are not empty to fire the content
        if (txtFirstName.getText().toString().trim().length() > 0 && txtCityName.getText().toString().trim().length() > 0) {
            String text = "Hi " +  txtFirstName.getText().toString() + " from " + txtCityName.getText().toString();

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                toSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                toSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    }

    /*
        ONDESTROY METHOD TO END SPEECH ACTIVITY AFTER CLICK BUTTON
     */

    @Override
    protected void onDestroy() {

        if (toSpeech != null) {
            toSpeech.stop();
            toSpeech.shutdown();
        }

        super.onDestroy();
    }
}
