package db.wpi.awheeler2.groupproject;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

import db.wpi.awheeler2.groupproject.database.AnimalDB;

public class TestActivity extends AppCompatActivity {
    Button getCats;
    Button getDogs;
    ImageView result;
    AnimalDB db;
    int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        getCats = (Button)findViewById(R.id.getCatButton);
        getDogs = (Button)findViewById(R.id.getDogButton);
        result = (ImageView)findViewById(R.id.animalView);

        getCats.setOnClickListener(getCatListener);
        getDogs.setOnClickListener(getDogListener);

        db = DB.getInstance(getApplicationContext());

        db.saveImagesFromAsset(new String[]{"cat", "dog"});

        counter = 0;
    }

    private View.OnClickListener getCatListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ArrayList<Bitmap> cats = db.getAllImagesOfAnimal("cat");

            if (cats.size() == 4) {
                System.out.println("*****CORRECT number of cats!*****");
                if ((counter % 2) == 0) {
                    result.setImageBitmap(cats.get(0));
                } else if (((counter % 3) == 0)){
                    result.setImageBitmap(cats.get(1));
                } else if (((counter % 4) == 0)) {
                    result.setImageBitmap(cats.get(2));
                } else {
                    result.setImageBitmap(cats.get(3));
                }

            } else {
                System.out.println("*****Incorrect number of cats! : " + cats.size() + " *****");
            }

            counter++;
        }
    };

    private View.OnClickListener getDogListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ArrayList<Bitmap> dogs = db.getAllImagesOfAnimal("dog");

            if (dogs.size() == 2) {
                System.out.println("*****CORRECT number of dogs!*****");

                if ((counter % 2) == 0) {
                    result.setImageBitmap(dogs.get(0));
                } else {
                    result.setImageBitmap(dogs.get(1));
                }
            } else {
                System.out.println("*****Incorrect number of dogs! : " + dogs.size() + " *****");
            }

            counter++;
        }
    };
}
