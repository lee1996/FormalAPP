package yihongzhang728.formalapp;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.scribble.api.PenReader;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;

import butterknife.Bind;
import butterknife.ButterKnife;
import yihongzhang728.formalapp.R;

public class Scribble_Touch_Screen_Surface_View extends AppCompatActivity {//implements View.OnClickListener {
    /*
    @Bind(R.id.button_pen)
    Button buttonPen;
    @Bind(R.id.button_eraser)
    Button buttonEraser;
    @Bind(R.id.surfaceview)
    SurfaceView surfaceView;

    boolean scribbleMode = false;
    private PenReader penReader;

    PenReader.PenReaderCallback callback = new PenReader.PenReaderCallback() {
        final float baseWidth = 5;
        final float pressure = 1;
        final float size = 1;
        boolean begin = false;
        @Override
        public void onBeginRawData() {
            begin = true;
            enterScribbleMode();
        }

        @Override
        public void onEndRawData() {
        }

        @Override
        public void onRawTouchPointMoveReceived(TouchPoint touchPoint) {
            if (begin) {
                EpdController.moveTo(surfaceView, touchPoint.x, touchPoint.y, baseWidth);
            } else {
                EpdController.quadTo(surfaceView, touchPoint.x, touchPoint.y, UpdateMode.DU);
            }
            begin = false;
        }

        @Override
        public void onRawTouchPointListReceived(TouchPointList touchPointList) {
        }

        @Override
        public void onBeginErasing() {

        }

        @Override
        public void onEndErasing() {

        }

        @Override
        public void onEraseTouchPointMoveReceived(TouchPoint touchPoint) {

        }

        @Override
        public void onEraseTouchPointListReceived(TouchPointList touchPointList) {

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scribble__touch__screen__surface__view);

        ButterKnife.bind(this);
        buttonPen.setOnClickListener(this);
        buttonEraser.setOnClickListener(this);

        initSurfaceView();
    }

    @Override
    protected void onResume() {
        getPenReader().resume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        leaveScribbleMode();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        getPenReader().pause();
        super.onPause();
    }

    private void initSurfaceView() {
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                initPenReader();
                cleanSurfaceView();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                cleanSurfaceView();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });
    }

    public PenReader getPenReader() {
        if (penReader == null) {
            penReader = new PenReader(this, surfaceView);
        }
        return penReader;
    }

    private void initPenReader() {
        surfaceView.post(new Runnable() {
            @Override
            public void run() {
                penStart();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.equals(buttonPen)) {
            penStart();
            return;
        } else if (v.equals(buttonEraser)) {
            leaveScribbleMode();
            cleanSurfaceView();
            return;
        }
    }

    private void cleanSurfaceView() {
        if (surfaceView.getHolder() == null) {
            return;
        }
        Canvas canvas = surfaceView.getHolder().lockCanvas();
        if (canvas == null) {
            return;
        }
        canvas.drawColor(Color.WHITE);
        surfaceView.getHolder().unlockCanvasAndPost(canvas);
    }

    private void enterScribbleMode() {
        EpdController.enterScribbleMode(surfaceView);
        scribbleMode = true;
    }

    private void penStart() {
        getPenReader().setPenReaderCallback(callback);
        getPenReader().start();
        getPenReader().resume();
    }

    private void leaveScribbleMode() {
        scribbleMode = false;
        EpdController.leaveScribbleMode(surfaceView);
        closePenReader();
    }

    private void closePenReader() {
        if (penReader != null) {
            penReader.stop();
            penReader = null;
        }
    }
    */
}
