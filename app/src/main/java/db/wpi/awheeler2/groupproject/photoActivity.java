package db.wpi.awheeler2.groupproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import db.wpi.awheeler2.groupproject.cache.AnimalDBCache;
import db.wpi.awheeler2.groupproject.database.AnimalDB;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class photoActivity extends AppCompatActivity {
    ArrayList<Bitmap> imagesOfAnimal;
    //AnimalDB db;
    AnimalDBCache db;
    String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        // TODO: We need to be able to pass this database object between activities - currently cache is reloading all data every time the activity is created
        //need to set the db here, but don't know how to with the context
        //db = new AnimalDB(this);
        db = AnimalDBCache.getInstance(getApplicationContext());

        //for testing purposes
        //db.saveImagesFromAsset(new String[]{"cat", "dog"});

        Intent intent = getIntent();
        category = intent.getExtras().getString(EXTRA_MESSAGE);

        //test category
        TextView text = findViewById(R.id.testCategory);
        text.setText(category.toUpperCase());

        //populate the scrollview
        populateScroll();
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
            System.out.print("entered for loop");
            ImageView image = new ImageView(this);
            image.setLayoutParams(new android.view.ViewGroup.LayoutParams(500,500));
            image.setMaxHeight(500);
            image.setMaxWidth(500);

            // Adds the view to the layout
            layout.addView(image);

            setPic(image, imagesOfAnimal.get(i));
        }
    }

    private void setPic(ImageView image, Bitmap bitmap) {
        image.setImageBitmap(bitmap);
    }
}
