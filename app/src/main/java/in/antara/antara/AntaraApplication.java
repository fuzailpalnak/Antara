package in.antara.antara;

import android.app.Application;
import android.graphics.Bitmap;

import java.util.concurrent.LinkedBlockingQueue;

import in.antara.antara.position.Position;

public class AntaraApplication extends Application {

    private LinkedBlockingQueue<Bitmap> picturesQ;

    private LinkedBlockingQueue<Position> positionsQ;

    @Override
    public void onCreate() {
        super.onCreate();
        picturesQ = new LinkedBlockingQueue<>();
        positionsQ = new LinkedBlockingQueue<>();
    }

    public LinkedBlockingQueue<Bitmap> getPicturesQ() {
        return picturesQ;
    }

    public LinkedBlockingQueue<Position> getPositionsQ() {
        return positionsQ;
    }
}

