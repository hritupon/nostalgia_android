package com.hritupon.nostalgia;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.ImageView;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.hritupon.nostalgia.util.RequestCodes.REQUEST_CODE_CAPTURE_IMAGE;
import static com.hritupon.nostalgia.util.RequestCodes.REQUEST_CODE_CAPTURE_IMAGE_PERMISSION;
import static com.hritupon.nostalgia.util.RequestCodes.REQUEST_CODE_EXTERNAL_STORAGE;
import static com.hritupon.nostalgia.util.RequestCodes.REQUEST_CODE_IMAGE_SELECT;
import static com.hritupon.nostalgia.util.RequestCodes.REQUEST_CODE_SPEECH_OUTPUT;

public class SpeechRecordActivity extends AppCompatActivity {

    private Button openMic;
    private Button saveButton;
    private Button uploadImageButton;
    //private Button captureImageButton;
    private FloatingActionButton storiesButton;
    private TextView showVoiceText;
    private ImageView previewImage;
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
    private static final int REQUEST_MICROPHONE = 119;
    Uri imageCaptureUri=null;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
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
        initializeRefs();
        setupRecognizerIntent();
        //cassandraService = new CassandraService();
        setupEventListeners();
        //StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        //StrictMode.setVmPolicy(builder.build());
        requestCustomPermissions();
    }

    private void requestCustomPermissions() {
        verifyStoragePermissions(this);
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_CODE_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_CAPTURE_IMAGE_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCameraIntent();
            }
            else {
                Toast.makeText(SpeechRecordActivity.this, "You donot have permission for camera.", Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode == REQUEST_CODE_EXTERNAL_STORAGE){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted, yay! Do the
                // contacts-related task you need to do.

            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
            return;
        }
    }

    private void startCameraIntent() {
        /*File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        String mediaCapturePath = path + File.separator + "Camera" + File.separator + "wp-" + System.currentTimeMillis() + ".jpg";
        Intent captureImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        imageCaptureUri = Uri.fromFile(new File(mediaCapturePath));
        captureImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageCaptureUri);*/
        //startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_CODE_CAPTURE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SPEECH_OUTPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    handleSpeechOutput(data);
                }
                break;
            }
            case REQUEST_CODE_IMAGE_SELECT: {
                if (resultCode == RESULT_OK && null != data) {
                    handleGalleryImageSelection(data);
                }
                break;
            }
            case REQUEST_CODE_CAPTURE_IMAGE: {
                if (resultCode == RESULT_OK && null != data) {
                    handleImageCaptureOutput(data);
                }
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.speech_record_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.speech_record_activity_menu_logout) {
            mAuth.signOut();
        }
        return true;
    }

    @Override
    public void onDestroy() {
        if (mSpeechRecognizer != null) {
            mSpeechRecognizer.stopListening();
            mSpeechRecognizer.cancel();
            mSpeechRecognizer.destroy();
        }
        super.onDestroy();
    }

    private void initializeRefs() {
        mToolbar = (Toolbar) findViewById(R.id.speech_record_activity_toolbar);
        setSupportActionBar(mToolbar);
        openMic = (Button) findViewById(R.id.speakButton);
        showVoiceText = (TextView) findViewById(R.id.showVoiceOutput);
        uploadImageButton = (Button) findViewById(R.id.uploadImageButton);
        //captureImageButton = (Button) findViewById(R.id.captureImageButton);
        previewImage = (ImageView) findViewById(R.id.uploaded_image);
        saveButton = (Button) findViewById(R.id.saveButton);
        storiesButton = (FloatingActionButton) findViewById(R.id.myStoriesButton);
        mAuth = FirebaseAuth.getInstance();
        if (!databaseInitialized) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            databaseInitialized = true;
        }
        storiesDbRef = FirebaseDatabase.getInstance().getReference(STORIES);
        storiesDbRef.keepSynced(true);
        firebaseService = new FirebaseService(storiesDbRef);
    }

    private void setupEventListeners() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (null == firebaseAuth.getCurrentUser()) {
                    startActivity(new Intent(SpeechRecordActivity.this, MainActivity.class));
                }
            }
        };
        openMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        uploadImageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), REQUEST_CODE_IMAGE_SELECT);
            }
        });
        /*captureImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( ContextCompat.checkSelfPermission(SpeechRecordActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                                REQUEST_CODE_CAPTURE_IMAGE_PERMISSION);
                    }
                }else{
                    startCameraIntent();
                }
            }
        });*/
    }

    private void setupRecognizerIntent() {
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        recognizerIntent.putExtra("android.speech.extra.DICTATION_MODE", true);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "...Speak now...");
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizer.setRecognitionListener(new Listener());
    }
    private void saveData() {

        String imagePath = "";
        if (previewImage.getTag() != null) {
            imagePath = previewImage.getTag().toString();
        }
        String description = showVoiceText.getText().toString().trim();
        if (null != description && !description.isEmpty()) {
            String userId = mAuth.getCurrentUser().getUid();
            long timeStamp = System.currentTimeMillis();
            Story story = new Story(description, timeStamp, userId, imagePath);

            boolean isSaveSuccessful = firebaseService.save(story);
            if (isSaveSuccessful) {
                Toast.makeText(SpeechRecordActivity.this, "Story saved. Add another story.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SpeechRecordActivity.this, "Unable to save story. Please try again", Toast.LENGTH_SHORT).show();
            }
            //@Todo
            //cassandraService.save(story);

        } else {
            Toast.makeText(SpeechRecordActivity.this, "Sorry no story found.Try again.", Toast.LENGTH_SHORT).show();
        }
    }


    private void handleImageCaptureOutput(Intent data) {
        Uri uri = imageCaptureUri;
        String[] projection = {MediaStore.Images.Media.DATA};
        if(uri!=null){
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                previewImage.setImageBitmap(bitmap);
                previewImage.setTag(getPathFromUri(uri, projection));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleGalleryImageSelection(Intent data) {
        Uri uri = data.getData();
        String[] projection = {MediaStore.Images.Media.DATA};
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            previewImage.setImageBitmap(bitmap);
            previewImage.setTag(getPathFromUri(uri, projection));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getPathFromUri(Uri uri, String[] projection) {
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(projection[0]);
        String picturePath = cursor.getString(columnIndex); // returns null
        cursor.close();
        return picturePath;
    }

    private void handleSpeechOutput(Intent data) {
        ArrayList<String> voiceInText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        String recognizedSpeech = voiceInText.get(0);
        if (null != recognizedSpeech && !recognizedSpeech.isEmpty()) {
            recognizedSpeech = recognizedSpeech.substring(0, 1).toUpperCase() + recognizedSpeech.substring(1);
        }
        showVoiceText.setText(recognizedSpeech);
        Command command = CommandUtil.getCommand(SpeechRecordActivity.this, recognizedSpeech, mAuth, firebaseService);
        if (command != null) {
            command.execute();
            startActivity(new Intent(SpeechRecordActivity.this, StoriesActivity.class));
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
    }

    ;

    private void receiveResults(Bundle results) {

        if ((results != null) && results.containsKey(SpeechRecognizer.RESULTS_RECOGNITION)) {
            List<String> heard = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String recognizedSpeech = heard.get(0);
            String prevRecognizedString = showVoiceText.getText().toString();
            //showVoiceText.setText(prevRecognizedString+" "+recognizedSpeech);
            showVoiceText.setText(recognizedSpeech);


        }

    }
}


