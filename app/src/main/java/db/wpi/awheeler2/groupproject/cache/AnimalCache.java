package db.wpi.awheeler2.groupproject.cache;

import android.graphics.Bitmap;
import android.util.LruCache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AnimalCache extends LruCache<String, ArrayList<String>> {

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    public AnimalCache(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(String key, ArrayList<String> keys) {
        // The cache size will be measured in kilobytes rather than
        // number of items.
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(byteArrayOutputStream);
            out.writeObject(keys);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteArrayOutputStream.toByteArray().length / 1024;
    }
}
