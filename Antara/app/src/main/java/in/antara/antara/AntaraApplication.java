package in.antara.antara;

import android.app.Application;
import android.graphics.Bitmap;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by udar on 8/29/2017.
 */
public class AntaraApplication extends Application {

    private LinkedBlockingQueue<Bitmap> picturesQ;

    @Override
    public void onCreate() {
        super.onCreate();
        picturesQ = new LinkedBlockingQueue<>();
    }

    public LinkedBlockingQueue<Bitmap> getPicturesQ() {
        return picturesQ;
    }
}

