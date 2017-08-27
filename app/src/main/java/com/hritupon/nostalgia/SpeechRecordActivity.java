package com.hritupon.nostalgia;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.hritupon.nostalgia.Services.DatabaseService;
import com.hritupon.nostalgia.Services.impl.FirebaseService;
import com.hritupon.nostalgia.models.Story;

import java.util.ArrayList;
import java.util.Locale;

import static com.hritupon.nostalgia.util.RequestCodes.REQUEST_CODE_SPEECH_OUTPUT;

public class SpeechRecordActivity extends AppCompatActivity {

    private Button openMic;
    private Button logoutButton;
    private Button saveButton;
    private Button storiesButton;
    private TextView showVoiceText;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseDatabase firebaseDatabase;
    private DatabaseService firebaseService;

    //private DatabaseService cassandraService;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_record);

        openMic = (Button) findViewById(R.id.speakButton);
        showVoiceText = (TextView) findViewById(R.id.showVoiceOutput);
        saveButton = (Button)findViewById(R.id.saveButton);
        storiesButton = (Button)findViewById(R.id.myStoriesButton);
        logoutButton = (Button) findViewById(R.id.logoutButton);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseService = new FirebaseService(firebaseDatabase);
        //cassandraService = new CassandraService();



        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(null == firebaseAuth.getCurrentUser()){
                    startActivity(new Intent(SpeechRecordActivity.this, MainActivity.class));
                }
            }
        };

        openMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnToOpenMic();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
                startActivity(new Intent(SpeechRecordActivity.this, StoriesActivity.class));
            }
        });
        storiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SpeechRecordActivity.this, StoriesActivity.class));
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
            }
        });


    }

    private void btnToOpenMic(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi...Speak now...");

        try{
            startActivityForResult(intent, REQUEST_CODE_SPEECH_OUTPUT);
        }
        catch (ActivityNotFoundException e){
            Toast.makeText(this, "Sorry.Try again later...",Toast.LENGTH_LONG).show();
        }
    }

    private void saveData(){
        String description = showVoiceText.getText().toString().trim();
        if(null != description && !description.isEmpty()){
            String userId = mAuth.getCurrentUser().getUid();
            String timeStamp = System.currentTimeMillis()+"";
            Story story = new Story(description,timeStamp, userId);

            boolean isSaveSuccessful = firebaseService.save(story);
            if(isSaveSuccessful){
                Toast.makeText(SpeechRecordActivity.this, "Story saved. Add another story.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(SpeechRecordActivity.this, "Unable to save story. Please try again", Toast.LENGTH_SHORT).show();
            }
            //@Todo
            //cassandraService.save(story);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_SPEECH_OUTPUT:{
                if(resultCode == RESULT_OK && null != data){
                    ArrayList<String> voiceInText  = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    showVoiceText.setText(voiceInText.get(0));
                }
                break;
            }
        }
    }

}