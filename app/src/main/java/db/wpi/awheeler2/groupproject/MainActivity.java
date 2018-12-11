package db.wpi.awheeler2.groupproject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
//import org.tensorflow.lite.Interpreter;


import db.wpi.awheeler2.groupproject.cache.AnimalDBCache;
import db.wpi.awheeler2.groupproject.database.AnimalDbHelper;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String selectedBreed = ""; //Breed selected by spinner
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
    Boolean offDevice = false; // if off device inference is wanted

    int DIM_BATCH_SIZE = 1;
    int SIZE_X =224;
    int SIZE_Y = 224;//are the size of the input window and as discussed earlier should be 299
    int DIM_PIXEL_SIZE =3 ;// is how many channels there are per pixel, which in our case is 3
    int NUM_BYTES_PER_CHANNEL = 4;//is how many bytes each pixel is stored as in our bitmap. Since we'll be using a float based bitmap this is 4.
    ByteBuffer imgData;
    int IMAGE_MEAN = 128 ;
    int IMAGE_STD = 128;
    MappedByteBuffer tfliteModel ;
    Interpreter tflite;
    int NUM_CLASSES =10;
    private float[][] labelProbArray = new float[DIM_BATCH_SIZE][NUM_CLASSES];
    private String[] labels = new String[10];
    Bitmap imageTaken;
    AnimalDBCache db;
    String breedChosen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Spinner Setup
        SetUpSpinner();//Get spinner Populated
        Spinner spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        db = AnimalDBCache.getInstance(getApplicationContext());

        //Switch Setup
        Switch offDeviceSwitch = findViewById(R.id.offDevice);
        offDeviceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                offDevice = isChecked;
            }
        });


        try {
            tfliteModel = loadModelFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        tflite = new Interpreter(tfliteModel);

        imgData = ByteBuffer.allocateDirect(
                DIM_BATCH_SIZE
                        * SIZE_X
                        * SIZE_Y
                        * DIM_PIXEL_SIZE
                        * NUM_BYTES_PER_CHANNEL);
        imgData.order(ByteOrder.nativeOrder());
    }

    public void SetUpSpinner(){
        Spinner spinner = findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.animal_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    public void addToDB(View v) {
        db.addToDB(breedChosen.toLowerCase(),mCurrentPhotoPath);
    }

    public void changeActivity(View v) {//Switch to photo activity
        Intent intent = new Intent(this, photoActivity.class);
        String message = selectedBreed;
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // Get what is selected in the spinner
        selectedBreed = parent.getItemAtPosition(pos).toString().toLowerCase();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void onClickPicture(View view){
        dispatchTakePictureIntent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageView pictureView = findViewById(R.id.imagePreview);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Bundle extras = data.getExtras();
            int targetW = pictureView.getWidth();
            int targetH = pictureView.getHeight();
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;
            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;

            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            pictureView.setImageBitmap(bitmap);
            imageTaken = bitmap;
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {}
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "db.wpi.awheeler2.groupproject.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    //Deep inference code
    public void GetType(View view) throws FileNotFoundException {
        if (offDevice) { //run model off device
            switchPhoto();
        }
        else { //run on device
            switchPhoto();
        }
    }

    public void setAnimalTypeText(String type){ //set text field with type once inference is done
        TextView animalType = findViewById(R.id.typeText);
        animalType.setText("Type: " + type);
    }
    public void switchPhoto() throws FileNotFoundException {
        Context context=getApplicationContext();

        labels =  context.getResources().getStringArray(R.array.animal_array);
        //InputStream image_stream =null;
        //InputStream image_stream =null;
        //ImageView imageView = findViewById(R.id.photo);
        //Random randomImageGenerator = new Random();
        //int num = randomImageGenerator.nextInt(11)+1;
        //try {
         //   image_stream = getAssets().open(mCurrentPhotoPath);
        //} catch (Exception e ){}

        //ImageView img = findViewById(R.id.imagePreview);


        //Bitmap image = BitmapFactory.decodeStream(imageTaken);

        Bitmap resized  = imageTaken.createScaledBitmap(imageTaken, 224, 224, false); //filter?
        convertBitmapToByteBuffer(resized);
       // new InferenceAsync().execute("");

        long start = SystemClock.uptimeMillis();

        tflite.run(imgData, labelProbArray);


        long end = SystemClock.uptimeMillis();
        TextView guess = findViewById(R.id.typeText);
        int index = 0;
        float max = labelProbArray[0][0];
        for (int i = 0; i < 10; i++) {
            if (labelProbArray[0][i] > max) {
                max = labelProbArray[0][i];
                index = i;
            }
        }
        String bestguess = labels[index];
        guess.setText(bestguess + " : " + max);
        breedChosen = bestguess;

        //TextView time = findViewById(R.id.timeResult);
        //long totaltime = end - start;
        //time.setText(totaltime + "s");


    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = getAssets().openFd(getModelPath());
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
    private String getModelPath() {
        return "Model/optimized_graph.lite";
    }

    private void convertBitmapToByteBuffer(Bitmap bitmap) {
        int[] intValues = new int[SIZE_X * SIZE_Y];

        if (imgData == null) {
            return;
        }
        imgData.rewind();
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        // Convert the image to floating point.
        int pixel = 0;
        for (int i = 0; i < SIZE_X; ++i) {
            for (int j = 0; j < SIZE_Y; ++j) {
                final int val = intValues[pixel++];
                addPixelValue(val);
            }
        }
    }
    protected void addPixelValue(int pixelValue) {
        imgData.putFloat((((pixelValue >> 16) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
        imgData.putFloat((((pixelValue >> 8) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
        imgData.putFloat(((pixelValue & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
    }
    int num1;

    /*public void offDevice(View v){
        Random randomImageGenerator = new Random();
        num1 = randomImageGenerator.nextInt(11)+1;
        InputStream image_stream =null;
        ImageView imageView = findViewById(R.id.photo);

        try {
            image_stream = getAssets().open("imgs/"+num1+".jpeg");
        } catch (Exception e ){}
        Bitmap image = BitmapFactory.decodeStream(image_stream);

        imageView.setImageBitmap(image);
        //new RunInferenceAsync().execute("");
    }
    */

    private class InferenceAsync extends AsyncTask<String, Float, Long> {
        String time;
        long start;
        long end;
        protected void onPreExecute() {
        }

        protected Long doInBackground(String... img_files) {
            start = SystemClock.uptimeMillis();
            tflite.run(imgData, labelProbArray);
            end = SystemClock.uptimeMillis();
            return null;
        }

        protected void onPostExecute(Long result){
            TextView guess = findViewById(R.id.typeText);
            int index = 0;
            float max = labelProbArray[0][0];
            for (int i = 0; i < 1001; i++) {
                if (labelProbArray[0][i] > max) {
                    max = labelProbArray[0][i];
                    index = i;
                }
            }
            String bestguess = labels[index];
            guess.setText(bestguess + " : " + max);

           // TextView time = findViewById(R.id.timeResult);
           // long totaltime = end - start;
           // time.setText(totaltime + "s");
        }

    }

}
