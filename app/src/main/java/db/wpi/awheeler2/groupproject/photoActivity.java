package db.wpi.awheeler2.groupproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import db.wpi.awheeler2.groupproject.database.AnimalDB;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class photoActivity extends AppCompatActivity {
    ArrayList<Bitmap> imagesOfAnimal;
    AnimalDB db;
    String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        //need to set the db here, but don't know how to with the context
        db = new AnimalDB(this);

        Intent intent = getIntent();
        category = intent.getExtras().getString(EXTRA_MESSAGE);

        //test category
        TextView text = findViewById(R.id.testCategory);
        text.setText(category);
    }

    public void changeActivityMain(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //probably need to run this in an async task
    public void populateScroll(){  //this will need to take in the category
        LinearLayout layout = findViewById(R.id.photoLayout);
        //grab the images in correct category
        imagesOfAnimal = db.getAllImagesOfAnimal(category);  //need to get category somehow
        for(int i = 0; i < imagesOfAnimal.size(); i++) {
            ImageView image = new ImageView(this);
            image.setLayoutParams(new android.view.ViewGroup.LayoutParams(80,60));
            image.setMaxHeight(20);
            image.setMaxWidth(20);

            // Adds the view to the layout
            layout.addView(image);

            // need to set the imageView to the correct image from database (call the async task)
            setPic(image, imagesOfAnimal.get(i));
        }
    }

    private void setPic(ImageView image, Bitmap bitmap) { // I feel like we should just be passing the filepath instead of the entire bitmap
        // Get the dimensions of the View
        int targetW = image.getWidth();
        int targetH = image.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        //BitmapFactory.decodeFile(path, bmOptions);  //hopefully putting this back in
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        //Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);  //hopefully putting this back in
        image.setImageBitmap(bitmap);
    }

    /*
    //copied this from project 4, but will probably look different
    private class ImageAsync extends AsyncTask<String, Float, Long> {
        String category;


        protected void onPreExecute() {

        }

        protected Long doInBackground(String... img_files) {
            //
            return null;
        }

        protected void onPostExecute(Long result){
        }
    }
    */
}
