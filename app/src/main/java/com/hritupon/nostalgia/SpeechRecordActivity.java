package com.hritupon.nostalgia;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hritupon.nostalgia.commands.Command;
import com.hritupon.nostalgia.commands.CommandUtil;
import com.hritupon.nostalgia.models.Story;
import com.hritupon.nostalgia.services.DatabaseService;
import com.hritupon.nostalgia.services.impl.FirebaseService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.hritupon.nostalgia.util.RequestCodes.REQUEST_CODE_SPEECH_OUTPUT;

public class SpeechRecordActivity extends AppCompatActivity {

    private FloatingActionButton openMic;
    private Button saveButton;
    private FloatingActionButton storiesButton;
    private TextView showVoiceText;
    private Toolbar mToolbar;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference storiesDbRef;
    private DatabaseService firebaseService;
    private static final String STORIES = "Stories";
    static boolean databaseInitialized = false;
    private Boolean exit = false;
    private SpeechRecognizer mSpeechRecognizer;
    Intent recognizerIntent;
    private static final int REQUEST_MICROPHONE=119;
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
        mToolbar = (Toolbar)findViewById(R.id.speech_record_activity_toolbar);
        setSupportActionBar(mToolbar);
        openMic = (FloatingActionButton) findViewById(R.id.speakButton);
        showVoiceText = (TextView) findViewById(R.id.showVoiceOutput);
        saveButton = (Button)findViewById(R.id.saveButton);
        storiesButton = (FloatingActionButton)findViewById(R.id.myStoriesButton);
        mAuth = FirebaseAuth.getInstance();
        if(!databaseInitialized){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            databaseInitialized=true;
        }
        storiesDbRef = FirebaseDatabase.getInstance().getReference(STORIES);
        storiesDbRef.keepSynced(true);
        firebaseService = new FirebaseService(storiesDbRef);

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        recognizerIntent.putExtra("android.speech.extra.DICTATION_MODE", true);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,  this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "...Speak now...");

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizer.setRecognitionListener(new Listener());


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
                //btnToOpenMic(recognizerIntent);
                if (ContextCompat.checkSelfPermission(SpeechRecordActivity.this,
                        android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(SpeechRecordActivity.this,
                            new String[]{android.Manifest.permission.RECORD_AUDIO},
                            REQUEST_MICROPHONE);

                }
                mSpeechRecognizer.startListening(recognizerIntent);

            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
                startActivity(new Intent(SpeechRecordActivity.this, TabbedViewActivity.class));
            }
        });
        storiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SpeechRecordActivity.this, TabbedViewActivity.class));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.speech_record_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.speech_record_activity_menu_logout){
            mAuth.signOut();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
       super.onBackPressed();

    }

    @Override
    public void onDestroy() {
        if (mSpeechRecognizer != null)
        {
            mSpeechRecognizer.stopListening();
            mSpeechRecognizer.cancel();
            mSpeechRecognizer.destroy();
        }
        super.onDestroy();
    }


    private void btnToOpenMic(Intent recognizerIntent){
        try{
        startActivityForResult(recognizerIntent, REQUEST_CODE_SPEECH_OUTPUT);
        }
        catch (ActivityNotFoundException e){
        Toast.makeText(this, "Sorry.Try again later...",Toast.LENGTH_LONG).show();
        }
    }

    private void saveData(){
        String description = showVoiceText.getText().toString().trim();
        if(null != description && !description.isEmpty()){
            String userId = mAuth.getCurrentUser().getUid();
            long timeStamp = System.currentTimeMillis();
            Story story = new Story(description,timeStamp, userId);

            boolean isSaveSuccessful = firebaseService.save(story);
            if(isSaveSuccessful){
                Toast.makeText(SpeechRecordActivity.this, "Story saved. Add another story.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(SpeechRecordActivity.this, "Unable to save story. Please try again", Toast.LENGTH_SHORT).show();
            }
            //@Todo
            //cassandraService.save(story);

        }else{
            Toast.makeText(SpeechRecordActivity.this,"Sorry no story found.Try again.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_SPEECH_OUTPUT:{
                if(resultCode == RESULT_OK && null != data){
                    ArrayList<String> voiceInText  = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String recognizedSpeech = voiceInText.get(0);
                    if(null != recognizedSpeech && !recognizedSpeech.isEmpty()){
                        recognizedSpeech = recognizedSpeech.substring(0, 1).toUpperCase() + recognizedSpeech.substring(1);
                    }
                    showVoiceText.setText(recognizedSpeech);
                    //if it is a recognized command automatically save
                    Command command = CommandUtil.getCommand(SpeechRecordActivity.this,recognizedSpeech, mAuth, firebaseService);
                    if(command!=null){
                        command.execute();
                        startActivity(new Intent(SpeechRecordActivity.this, StoriesActivity.class));
                    }
                }
                break;
            }
        }
    }


    class Listener implements RecognitionListener {

        @Override
        public void onReadyForSpeech(Bundle params) {
        }
        @Override
        public void onBeginningOfSpeech() {

        }
        @Override
        public void onRmsChanged(float rmsdB) {
        }
        @Override
        public void onBufferReceived(byte[] buffer) {
        }
        @Override
        public void onEndOfSpeech() {
        }
        @Override
        public void onError(int error) {
            //Log.d(TAG, getErrorText(error));
        }

        @Override
        public void onResults(Bundle results) {
            receiveResults(results);
        }
        @Override
        public void onPartialResults(Bundle partialResults) {
            receiveResults(partialResults);
        }
        @Override
        public void onEvent(int eventType, Bundle params) {

        }
    };

    private void receiveResults(Bundle results)  {

        if ((results != null) && results.containsKey(SpeechRecognizer.RESULTS_RECOGNITION))  {
            List<String> heard = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String recognizedSpeech = heard.get(0);
            String prevRecognizedString = showVoiceText.getText().toString();
            //showVoiceText.setText(prevRecognizedString+" "+recognizedSpeech);
            showVoiceText.setText(recognizedSpeech);


        }

    }

}


