package in.antara.antara.compass;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

/**
 * Created by udar on 8/30/2017.
 */

public class DirectionListener implements SensorEventListener {

    private static final String LOG_TAG = DirectionListener.class.getSimpleName();

    private float directionDegree;

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Log.d(LOG_TAG, "onSensorChanged");
        directionDegree = Math.round(event.values[0]);
        // Log.d(LOG_TAG, "Direction degree: " + directionDegree);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(LOG_TAG, "onAccuracyChanged");
    }

    public float getDirectionDegree() {
        return this.directionDegree;
    }
}
