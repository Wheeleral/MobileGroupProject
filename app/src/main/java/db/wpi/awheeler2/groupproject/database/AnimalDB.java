package db.wpi.awheeler2.groupproject.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import db.wpi.awheeler2.groupproject.Exception.DatabaseQueryException;
import db.wpi.awheeler2.groupproject.Exception.ExternalStorageException;

/**
 *  Functions to use for UI implementation:
 *  (1) void saveImagesFromAsset(String[] subdirectories)
 *      Saving images in the subdirectories to external storage and database with corresponding tags
 *      Input: a list of subdirectories in the assets folder - "cat", "dog"
 *      Output: void

 *  (2) long addToDB(String tagOfImage, String pathToImage)
 *      Adding a new image from user to database
 *      Input: a tag for the new image specifying what animal it is, a path to image in external storage
 *      Output: id - primary key value of the new row
 *
 *  (3) ArrayList<Bitmap> getAllImagesOfAnimal(String animal)
 *      Acquiring all the images with the given tag
 *      Input: a tag
 *      Output: an array list of bitmaps to display in ImageView for UI
 */

/* Options to optimize performance:
** (1) Run functions in the background thread
** (2) Store existing images in cache to optimize performance
** Assumption: File paths (of images) are saved in external storage as private files
*/
public class AnimalDB {
    private AnimalDbHelper helper;
    private SQLiteDatabase db;
    private Context context;
    ArrayList<Bitmap> imagesOfAnimal = new ArrayList<>();

    // Constructor
    public AnimalDB(Context context) {
        this.context = context;
        this.helper = new AnimalDbHelper(context);
    }

    // Ensures that image is saved if not already
    public String saveToStorage(String subdirectory, String fileName) throws ExternalStorageException, IOException {
        File image;

        // Checks if external storage exists
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            image = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);

            /*
            if (image.exists()) {
                image.delete();
            }
            */

            if (!image.exists()) {
                // Copy image
                InputStream from = context.getAssets().open(subdirectory + "/" + fileName);
                OutputStream to = new FileOutputStream(image.getAbsolutePath());

                // Copy data to private, external file
                byte[] data = new byte[from.available()];
                from.read(data);
                to.write(data);

                to.close();
                from.close();
            }

            // Return new file path in external storage
            return image.getAbsolutePath();

        } else {
            // Save to internal storage
            // BUT for now...

            throw new ExternalStorageException("External storage not available");
        }
    }

   /* Assumptions:
    * Default images are stored in Assets directory
    * Subdirectory should indicate the kind of animal - "cat", "dog", "rabbit"
    *
    * Note: Should call this once - load images in asset folder to DB and external storage
    */
    public void saveImagesFromAsset(String[] subdirectories) {
        String path = null;
        db = helper.getWritableDatabase();

        // Clear/create DB if exists
        helper.clearDb(db);

        if (subdirectories == null) {
            return;
        }

        for (String subdirectory: subdirectories) {
            // Loop through directory and process each image file
            try {
                String[] images = context.getAssets().list(subdirectory);

                if (images != null) {
                    for (String image : images) {
                        // Copy to internal/external storage
                        path = saveToStorage(subdirectory, image);

                        // Save to DB
                        if (addToDB(subdirectory, path) == -1) {
                            throw new DatabaseQueryException("Unable to add to database");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public long addToDB(String tagOfImage, String pathToImage) {
        // NOTE: tagOfImage may be the result of model
        db = helper.getWritableDatabase();

        // Create a new map of values
        ContentValues values = new ContentValues();
        values.put(AnimalContract.AnimalEntry.COLUMN_NAME_TAG, tagOfImage);
        values.put(AnimalContract.AnimalEntry.COLUMN_NAME_PATH, pathToImage);

        // Insert new row to DB
        return db.insert(AnimalContract.AnimalEntry.TABLE_NAME, null, values);
    }

    public ArrayList<Bitmap> getAllImagesOfAnimal(String animal) {
        InputStream image;
        String path;
        Bitmap bitmap;

        imagesOfAnimal.clear();

        db = helper.getReadableDatabase();

        // Projection/select which columns
        /*
        String[] projection = {BaseColumns._ID,
                AnimalContract.AnimalEntry.COLUMN_NAME_TAG,
                AnimalContract.AnimalEntry.COLUMN_NAME_PATH};
                */

        String[] projection = {AnimalContract.AnimalEntry.COLUMN_NAME_PATH};

        // Condition: where clause
        String selection = AnimalContract.AnimalEntry.COLUMN_NAME_TAG + " = ?";
        // Values of where clause
        String[] selectionArgs = {animal};

        Cursor cursor = db.query(
                AnimalContract.AnimalEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            path = cursor.getString(cursor.getColumnIndexOrThrow(AnimalContract.AnimalEntry.COLUMN_NAME_PATH));

            try {
                // Convert image to bitmap
                image = new FileInputStream(path);
                bitmap = BitmapFactory.decodeStream(image);
                imagesOfAnimal.add(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        cursor.close();

        return imagesOfAnimal;
    }

    public void close() {
        this.helper.close();
    }
}
