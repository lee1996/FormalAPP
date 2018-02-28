package yihongzhang728.formalapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceView;

/**
 * Created by a87 on 2017/12/24.
 */

public class ScribbleView extends SurfaceView {
    public ScribbleView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.v("ScribbleView","the canvas is created");
    }
}
