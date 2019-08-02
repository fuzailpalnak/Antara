package in.antara.antara.position;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

import in.antara.antara.AntaraApplication;
import in.antara.antara.R;
import in.antara.antara.objects.Circle;
import in.antara.antara.objects.Line;
import in.antara.antara.objects.Rectangle;
import in.antara.antara.objects.SlantedText;

/**
 * Created by udar on 8/25/2017.
 */
public class PositionView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String LOG_TAG = PositionView.class.getSimpleName();

    private Bitmap newsIcon;
    private Paint paint = new Paint();
    private PositionArea positionArea;
    private int widthPixels;
    private int heightPixels;
    private LinkedBlockingQueue<Position> positionsQ;

    public PositionView(Context context) {
        super(context);
        init();
    }

    public PositionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PositionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        getHolder().addCallback(this);
        setNewsIcon();
        setViewSize();
        positionArea = new PositionArea(widthPixels, heightPixels);

        positionsQ =
                ((AntaraApplication) getContext().getApplicationContext()).getPositionsQ();
    }

    private void setViewSize() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        widthPixels = displayMetrics.widthPixels;
        heightPixels = displayMetrics.heightPixels;
        Log.d(LOG_TAG, "width pixel: " + widthPixels + ", height pixels: " + heightPixels);
    }

    public void draw(Canvas canvas) {
        Log.d(LOG_TAG, "Draw");
        super.draw(canvas);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(LOG_TAG, "Surface Created");
        startTimer();
    }

    private void draw() {
        Canvas canvas = getHolder().lockCanvas(null);
        renderStaticObjects(canvas);
        renderMovingObjects(canvas);
        getHolder().unlockCanvasAndPost(canvas);
    }

    private void renderStaticObjects(Canvas canvas) {
        // Log.d(LOG_TAG, "Render");
        if (canvas == null) {
            return;
        }

        canvas.drawColor(Color.WHITE);

        int newsIconX = widthPixels - 100;
        int newsIconY = heightPixels - 200;
//        Log.d(LOG_TAG, "News icon X: " + newsIconX + ", Y: " + newsIconY);
        canvas.drawBitmap(newsIcon, newsIconX, newsIconY, paint);

        Rectangle room = positionArea.getRoom();
        Log.d(LOG_TAG, "Room " + room);
        canvas.save();
        canvas.rotate(room.getTilt(), room.getCenter().getX(), room.getCenter().getY());
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(room.left(), room.top(), room.right(), room.bottom(), paint);

        canvas.restore();
    }

    private void renderMovingObjects(Canvas canvas) {
        if (canvas == null) {
            return;
        }
        // Render moving objects
        Line distanceLine = positionArea.getDistanceLine();
        canvas.drawLine(distanceLine.getStart().getX(), distanceLine.getStart().getY(),
                distanceLine.getEnd().getX(), distanceLine.getEnd().getY(), paint);

        Circle position = positionArea.getPosition();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        canvas.drawCircle(position.getCenter().getX(),
                position.getCenter().getY(), position.getRadius(), paint);

        SlantedText distText = positionArea.getDistText();
        canvas.save();
        canvas.drawText(distText.getText(), distText.getStartPt().getX(),
                distText.getStartPt().getY(), paint);
        canvas.restore();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(LOG_TAG, "Surface Changed");
    }

    private void setNewsIcon() {
        newsIcon = BitmapFactory.decodeResource(getResources(),
                R.mipmap.news);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(LOG_TAG, "Surface Destroyed");
        stopTimer();
    }

    private Timer timer = null;
    private TimerTask task = null;

    public void startTimer() {
        timer = new Timer();
        task = new TimerTask() {

            @Override
            public void run() {
                // Log.d(LOG_TAG, "Available positions in Q " + positionsQ.size());
                if (!positionsQ.isEmpty()) {
                    Position position = positionsQ.remove();
                    Log.d(LOG_TAG, "New position : " + position.toString());
                    positionArea.setPosition(position.getAngle(), position.getLength());
                }
                draw();
            }
        };
        timer.schedule(task, 1000, 1000);
    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

}
