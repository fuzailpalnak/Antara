package in.antara.antara.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by udar on 8/29/2017.
 */
public class PictureTakenCallback implements Camera.PictureCallback {

    private LinkedBlockingQueue<Bitmap> picturesQ;

    public PictureTakenCallback(LinkedBlockingQueue<Bitmap> pictures) {
        this.picturesQ = pictures;
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
        picturesQ.add(bmp);
    }
}