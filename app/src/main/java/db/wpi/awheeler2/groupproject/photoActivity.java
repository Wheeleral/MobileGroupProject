package db.wpi.awheeler2.groupproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class photoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
    }

    public void changeActivityMain(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //probably need to run this in an async task
    public void populateScroll(){  //this will need to take in the category
        LinearLayout layout = (LinearLayout)findViewById(R.id.photoLayout);
        for(int i = 0; i < 3; i++)  //eventually we need to hook this up to the database
        {
            ImageView image = new ImageView(this);
            image.setLayoutParams(new android.view.ViewGroup.LayoutParams(80,60));
            image.setMaxHeight(20);
            image.setMaxWidth(20);

            // Adds the view to the layout
            layout.addView(image);

            // need to set the imageView to the correct image from database
        }
    }

    //copied this from project 4, but will probably look different
    private class ImageAsync extends AsyncTask<String, Float, Long> {

        protected void onPreExecute() {
        }

        protected Long doInBackground(String... img_files) {
            return null;
        }

        protected void onPostExecute(Long result){
        }
    }
}
