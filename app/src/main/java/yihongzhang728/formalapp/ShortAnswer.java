package yihongzhang728.formalapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.Touch;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.liulishuo.filedownloader.i.IFileDownloadIPCCallback;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.scribble.api.TouchHelper;
import com.onyx.android.sdk.scribble.api.event.BeginRawDataEvent;
import com.onyx.android.sdk.scribble.api.event.BeginRawErasingEvent;
import com.onyx.android.sdk.scribble.api.event.DrawingTouchEvent;
import com.onyx.android.sdk.scribble.api.event.EndRawDataEvent;
import com.onyx.android.sdk.scribble.api.event.ErasingTouchEvent;
import com.onyx.android.sdk.scribble.api.event.RawErasePointListReceivedEvent;
import com.onyx.android.sdk.scribble.api.event.RawErasePointMoveReceivedEvent;
import com.onyx.android.sdk.scribble.api.event.RawTouchPointListReceivedEvent;
import com.onyx.android.sdk.scribble.api.event.RawTouchPointMoveReceivedEvent;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import yihongzhang728.formalapp.device.DeviceConfig;

public class ShortAnswer extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = ShortAnswer.class.getSimpleName();
    //private static final String[] SCRIBBLE_COLUMNS = ;

    @Bind(R.id.button_pen)
    ImageView buttonPen;

    @Bind(R.id.button_eraser)
    ImageView buttonEraser;

    @Bind(R.id.surfaceview)
    SurfaceView surfaceView;



    @Bind(R.id.left)
    ImageView buttonLeft;

    @Bind(R.id.right)
    ImageView buttonRight;


    private EventBus eventBus = new EventBus();
    private TouchHelper touchHelper;


    private int page = 1;
    private int stroke;
    private page scribbleList = new page();
    private ArrayList<page> pages = new ArrayList<page>();
    private ArrayList<ArrayList<scribbleObject>> oldPages = new ArrayList<>();
    private String parent_id;
    private HashMap<Integer,Integer> strokeArray = new HashMap<>();
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private ScribbleDatabase db;
    //private ScribbleDao db;


    /*
    //是否在书写模式
    boolean scribbleMode = false;
    //笔
    private PenReader penReader;
    private Matrix viewMatrix;
    //用来存放作文纸
    private ArrayList<Canvas> List = new ArrayList<>();
    //当前作文纸页数
    private int page = 1;

    //用来存放画作
    private TouchPointList pList = new TouchPointList();
    private ArrayList<String> sList = new ArrayList();
    */




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_short_answer);

        //sharedPref = this.getSharedPreferences(
                //"answer1", Context.MODE_PRIVATE);
        //editor = sharedPref.edit();

        Intent i = getIntent();
        parent_id = i.getStringExtra("question_id");

        db = new ScribbleDatabase(this);

        ButterKnife.bind(this);

        buttonPen.setOnClickListener(this);
        buttonEraser.setOnClickListener(this);
        buttonLeft.setOnClickListener(this);
        buttonRight.setOnClickListener(this);





        //初始化

        getPages();
        initSurfaceView();


        Log.v("CREATE", "THE Activity is being created.");



    }

    @Override
    protected void onResume() {
        touchHelper.resumeRawDrawing();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        EpdController.leaveScribbleMode(surfaceView);
        touchHelper.closeRawDrawing();
        Log.v("DESTROY","FINISHED");
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        touchHelper.pauseRawDrawing();
        super.onPause();
    }

    @Override
    protected void onStart() {

        super.onStart();


    }
    private void initSurfaceView() {

        eventBus.register(this);
        touchHelper = new TouchHelper(eventBus);
        surfaceView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                List<Rect> exclude = new ArrayList<>();
                exclude.add(touchHelper.getRelativeRect(surfaceView, buttonEraser));
                exclude.add(touchHelper.getRelativeRect(surfaceView, buttonPen));
                Rect test = new Rect(100,100,400,600);
                exclude.add(test);


                Rect limit = new Rect();
                surfaceView.getLocalVisibleRect(limit);
                //cleanSurfaceView();
                touchHelper.setup(surfaceView)
                        .setStrokeWidth(2.0f)
                        .setUseRawInput(true)
                        .setLimitRect(limit, exclude)
                        .createRawDrawing();
            }
        });


        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                pages.add(scribbleList);
                drawScribble(scribbleList.getStrokes(),1);
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });


    }




    @Override
    public void onBackPressed() {

        //EpdController.leaveScribbleMode(surfaceView);

        //touchHelper.stopRawDrawing();
        //drawScribble(scribbleList.getStrokes(),page);
        //eventBus.unregister(this);
        //savePage();
        //super.onBackPressed();
        this.finish();

        Display display = getWindow().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        Log.v("DISPLAY","Width "+outMetrics.widthPixels);
        Log.v("DISPLAY","Height "+outMetrics.heightPixels);

    }
    public void getPages(){

        SQLiteDatabase sqlDB = db.getReadableDatabase();

        Cursor cursor = sqlDB.query(
                db.TABLE_NAME,
                new String[]{"X1","Y1","X2","Y2","Page","Stroke"},
                "ParentID = ? and UserID = ? and Type = ?",
                new String[]{parent_id,"default_user","answer"},
                null,null,"Page, Stroke asc"
        );
        Log.v("Count",cursor.getCount()+"");
        //scribbleList = new page();
        if (cursor.getCount() > 0) {
            int formerPageIndex = 0;
            int formerStrokeIndex = 0;
            float[] e = new float[cursor.getCount()*4];
            while (cursor.moveToNext()) {

                int pageIndex = cursor.getInt(cursor.getColumnIndex("Page"));
                int strokeIndex = cursor.getInt(cursor.getColumnIndex("Stroke"));

                if (pageIndex != formerPageIndex){
                    strokeArray.put(formerPageIndex,formerStrokeIndex);

                }
                if (cursor.isLast()){
                    strokeArray.put(pageIndex,strokeIndex);
                }
                //Log.v("Point","Page is "+pageIndex+ " Stroke is "+strokeArray.get(pageIndex));

                ArrayList<scribbleObject> parentPage;
                if (pageIndex>oldPages.size()){
                    parentPage = new ArrayList<scribbleObject>();
                    oldPages.add(parentPage);
                }else{
                    parentPage = oldPages.get(pageIndex-1);
                }




                float X1 = cursor.getFloat(cursor.getColumnIndex("X1"));
                float Y1 = cursor.getFloat(cursor.getColumnIndex("Y1"));
                float X2 = cursor.getFloat(cursor.getColumnIndex("X2"));
                float Y2 = cursor.getFloat(cursor.getColumnIndex("Y2"));

                scribbleObject point = new scribbleObject(pageIndex,strokeIndex,X1,Y1,X2,Y2);
                parentPage.add(point);
                oldPages.set(pageIndex-1,parentPage);

                formerPageIndex = pageIndex;
                formerStrokeIndex = strokeIndex;


            }



        }else{
            strokeArray.put(1,0);
        }
        cursor.close();
        db.close();

        for (Integer key : strokeArray.keySet()) {
            Log.v("Stats","Page is "+ key + " and Stroke count is "+strokeArray.get(key));

        }


    }

    public void savePage(){

        SQLiteDatabase sqlDB = db.getWritableDatabase();
        sqlDB.beginTransaction();

        String insertSQL = "insert into " + ScribbleDatabase.TABLE_NAME + "(" + "ParentID"
                + "," + "UserID" + "," + "Type" + ","
                + "Page" + "," + "Stroke" + "," + "X1"
                + "," + "X2" + ", " + "Y1" + "," + "Y2"
                + ") values " + "(?,?,?,?,?,?,?,?,?)";

        SQLiteStatement stat = sqlDB.compileStatement(insertSQL);

        for(int i = 0;i<pages.size();i++){
            ArrayList<float[]> list = pages.get(i).getStrokes();
            for(int j = 0;j<list.size();j++){
                float[] stroke = list.get(j);
                for (int k =0; k<stroke.length/4;k++){
                    stat.bindString(1,parent_id);
                    stat.bindString(2,"default_user");
                    stat.bindString(3,"answer");
                    stat.bindLong(4,i+1);
                    stat.bindLong(5,j+1+strokeArray.get(i+1));
                    Log.v("Stroke Add", j+1+strokeArray.get(i+1)+"");
                    stat.bindDouble(6,stroke[k*4+0]);
                    stat.bindDouble(7,stroke[k*4+1]);
                    stat.bindDouble(8,stroke[k*4+2]);
                    stat.bindDouble(9,stroke[k*4+3]);
                    stat.executeInsert();
                }
            }

        }



        sqlDB.setTransactionSuccessful();
        sqlDB.endTransaction();
        sqlDB.close();


        /*
        Gson gson = new Gson();
        String dataString = gson.toJson(scribbleList);
        String label = parent_id+"|"+"default_user"+"|"+index;



        editor.putString(label,dataString);
        editor.apply();
        */
    }

    @Override
    public void onClick(View v) {
        //若点击笔，启动书写
        if (v.equals(buttonPen)) {
            touchHelper.resumeRawDrawing();
            return;
        } else if (v.equals(buttonEraser)) {
            EpdController.leaveScribbleMode(surfaceView);
            touchHelper.pauseRawDrawing();
            cleanSurfaceView();
            return;
        } else if (v.equals(buttonLeft)){
            //若点击向左，回到上一页作文纸
            if(page>1){

                //保存当前页面的内容
                pages.set(page-1,scribbleList);
                //savePage(page);



                //读取pages里面的上一页
                scribbleList = pages.get(page-2);


                Log.v("Action","Previous Page");
                EpdController.leaveScribbleMode(surfaceView);
                touchHelper.pauseRawDrawing();
                cleanSurfaceView();
                //getPage(page-1);

                //把东西画上去
                drawScribble(scribbleList.getStrokes(),page-1);
                //Log.v("Length",scribbleList.getStrokes().size()+"");


                page --;
                //touchHelper.resumeRawDrawing();
            }

            return;
        }else if (v.equals(buttonRight)){
            //若点击向右，出现下一页作文纸
            //保存当前页面的内容
            pages.set(page-1,scribbleList);
            //savePage(page);
            EpdController.leaveScribbleMode(surfaceView);
            touchHelper.pauseRawDrawing();
            cleanSurfaceView();
            if (page == pages.size()){
                //新添加一个页面
                scribbleList = new page();
                pages.add(scribbleList);
                //getPage(page+1);
                //Log.v("Action","Next New Page");
            }else{
                //读取pages里面的下一页
                scribbleList = pages.get(page);
                //getPage(page+1);
                //Log.v("Action","Next Old Page");
            }



            //把东西画上去
            drawScribble(scribbleList.getStrokes(),page+1);
            //Log.v("Length",scribbleList.getStrokes().size()+"");


            page ++;
            //touchHelper.resumeRawDrawing();
            return;
        }

    }

    //清空界面
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


    private void drawPage(float[] e){
        EpdController.leaveScribbleMode(surfaceView);
        touchHelper.pauseRawDrawing();
        if (surfaceView.getHolder() == null) {

            return;
        }
        Canvas canvas = surfaceView.getHolder().lockCanvas();
        if (canvas == null) {

            return;
        }

        canvas.drawColor(Color.WHITE);
        Paint paint = new Paint();
        //paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(2.0f);
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);

        //canvas.drawLines(e,paint);
        canvas.drawPoints(e,paint);
        surfaceView.getHolder().unlockCanvasAndPost(canvas);

    }


    private void drawScribble(ArrayList<float[]> list,int pageIndex){
        EpdController.leaveScribbleMode(surfaceView);
        touchHelper.pauseRawDrawing();
        if (surfaceView.getHolder() == null) {

            return;
        }
        Canvas canvas = surfaceView.getHolder().lockCanvas();
        if (canvas == null) {

            return;
        }
        canvas.drawColor(Color.WHITE);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.0f);
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        //Path path = new Path();
        //path.reset();
        for(int i =0;i<list.size();i++){

            /*
            //there is sth wrong with the TouchPointList data
            TouchPointList pList = list.get(i);
            float[] pts = new float[pList.size()*4];
            for(int j =0;j<pList.size()-1;j++){

                TouchPoint point = pList.get(j);
                TouchPoint point2 = pList.get(j+1);
                pts[(j*4+0)]=point.getX();
                pts[(j*4+1)]=point.getY();
                pts[(j*4+2)]=point2.getX();
                pts[(j*4+3)]=point2.getY();

                //canvas.drawLine(point.getX(),point.getY(),point2.getX(),point2.getY(), paint);
                //Log.v("Draw", "Point X "+point.getX() + "  Point Y" + point.getY());
            }

            */


            canvas.drawLines(list.get(i),paint);
            /*
            float[] stroke = list.get(i);

            for (int j=0;j<(stroke.length/8);j++) {

                if ((j * 8) <= stroke.length) {
                //if (j == 0){

                    //画曲线
                    float startX = stroke[j*8+0];
                    float startY = stroke[j*8+1];
                    float passX = stroke[j*8+2];
                    float passY = stroke[j*8+3];
                    float endX = stroke[j*8+6];
                    float endY = stroke[j*8+7];
                    float midX = (startX+endX)/2;
                    float midY = (startY+endY)/2;
                    float controlX = passX*2-midX;
                    float controlY = passY*2-midY;

                    path.moveTo(startX,startY);
                    path.quadTo(controlX,controlY,endX,endY);




                }

                else {
                    //画曲线
                    //float startX = stroke[j*8+0];
                    //float startY = stroke[j*8+1];
                    float passX = stroke[j*8+2];
                    float passY = stroke[j*8+3];

                    path.lineTo(passX,passY);

                }

            }

            canvas.drawPath(path,paint);
            */
        }

        //画老的
        if ((pageIndex-1)<oldPages.size()){
            ArrayList<scribbleObject> oldList = oldPages.get(pageIndex-1);
            for(int l = 0;l <oldList.size();l++){
                scribbleObject ob = oldList.get(l);
                canvas.drawLine(ob.X1,ob.Y1,ob.X2,ob.Y2,paint);
            }
        }



        surfaceView.getHolder().unlockCanvasAndPost(canvas);
        touchHelper.resumeRawDrawing();
    }

    private void removeScribble(ArrayList<float[]> list){
        EpdController.leaveScribbleMode(surfaceView);
        touchHelper.pauseRawDrawing();
        if (surfaceView.getHolder() == null) {

            return;
        }
        Canvas canvas = surfaceView.getHolder().lockCanvas();
        if (canvas == null) {

            return;
        }

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.0f);
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        //Path path = new Path();
        //path.reset();
        for(int i =0;i<list.size();i++){




            canvas.drawLines(list.get(i),paint);

        }

        surfaceView.getHolder().unlockCanvasAndPost(canvas);
    }

    // below are callback events sent from TouchHelper

    @Subscribe
    public void onErasingTouchEvent(ErasingTouchEvent e) {
        Log.d(TAG, "onErasingTouchEvent");
    }

    @Subscribe
    public void onDrawingTouchEvent(DrawingTouchEvent e) {
        Log.d(TAG, "onDrawingTouchEvent");
    }

    @Subscribe
    public void onBeginRawDataEvent(BeginRawDataEvent e) {
        Log.d(TAG, "onBeginRawDataEvent");
    }

    @Subscribe
    public void onEndRawDataEvent(EndRawDataEvent e) {
        Log.d(TAG, "onEndRawDataEvent");
    }

    @Subscribe
    public void onRawTouchPointMoveReceivedEvent(RawTouchPointMoveReceivedEvent e) {

    }

    @Subscribe
    public void onRawTouchPointListReceivedEvent(RawTouchPointListReceivedEvent e) {


        TouchPointList list = e.getTouchPointList();
        Log.v("LENGTH","Before "+list.size());
        scribbleList.addStroke(ToFloatList(list));




    }

    public float[] ToFloatList(TouchPointList pList){

        ArrayList<TouchPoint> simple = new ArrayList<>();
        int after = 0;
        int after1 = 0;
        int odd = 1;
        for(int j =0;j<pList.size()-1;j++){

            TouchPoint point = pList.get(j);
            TouchPoint point2 = pList.get(j+1);

            float X1 = Math.round(point.getX());
            float Y1 = Math.round(point.getY());
            float X2 = Math.round(point2.getX());
            float Y2 = Math.round(point2.getY());

            if (X1 != X2 || Y1 != Y2){
                after ++;
                if ( (pList.size()>20 && after%3 == 0) || pList.size()<20 ) {
                    after1 ++;
                    //simple.add(point);
                    simple.add(point2);
                    //odd =2;
                }
            }
            if (j==(pList.size()-2)|| j == 0){
                simple.add(point2);
            }


        }

        float[] pts2 = new float[(simple.size()-1)*4];
        for(int i = 0;i< (simple.size()-1);i++){
            pts2[i*4+0] = Math.round(simple.get(i).getX());
            pts2[i*4+1] = Math.round(simple.get(i).getY());
            pts2[i*4+2] = Math.round(simple.get(i+1).getX());
            pts2[i*4+3] = Math.round(simple.get(i+1).getY());
        }


        Log.v("LENGTH","AFTER is "+ after1);
        return pts2;
    }



    @Subscribe
    public void onRawErasingStartEvent(BeginRawErasingEvent e) {
        Log.d(TAG, "onRawErasingStartEvent");
    }

    @Subscribe
    public void onRawErasingFinishEvent(RawErasePointListReceivedEvent e) {
        Log.d(TAG, "onRawErasingFinishEvent");
    }

    @Subscribe
    public void onRawErasePointMoveReceivedEvent(RawErasePointMoveReceivedEvent e) {
        Log.d(TAG, "onRawErasePointMoveReceivedEvent");

    }

    @Subscribe
    public void onRawErasePointListReceivedEvent(RawErasePointListReceivedEvent e) {
        Log.d(TAG, "onRawErasePointListReceivedEvent");

        TouchPointList list = e.getTouchPointList();
        Log.v("LENGTH","Before "+list.size());
        float[] erasePoints = ToFloatList(list);

        eraseScribbles(erasePoints);

        //先得到轨迹

    }





    public void eraseScribbles(float[] erase){

        //第一步，先擦除内存里面的Scribbles
        //遍历当前页面的所有笔画
        ArrayList<float[]> strokes = scribbleList.getStrokes();
        ArrayList<float[]> strokesToRemove = new ArrayList<>();
        for(int i=0;i<strokes.size();i++){
            Log.v("COMPARE",i+"");
            float[] test = strokes.get(i);

            int adjacencyCount = 0;

            //把test的每一个点拿出来比对
            for(int j=0;j<test.length/4;j++){
                float testX = test[j*4+0];
                float testY = test[j*4+1];

                for(int k=0;k<erase.length/4;k++){

                    float eraseX = erase[k*4+0];
                    float eraseY = erase[k*4+1];
                    float Xdist = testX-eraseX;
                    float Ydist = testY-eraseY;

                    float distance = Xdist*Xdist+Ydist*Ydist;
                    if (distance<100){
                        adjacencyCount++;
                    }

                }
            }
            if (adjacencyCount > 3){

                strokesToRemove.add(test);

                scribbleList.removeStroke(i);
                i--;
            }

        }




        //遍历原有老的笔画

        if ((page-1)<oldPages.size()){
            ArrayList<scribbleObject> oldList = oldPages.get(page-1);
            ArrayList<Integer> oldErase = new ArrayList<>();

            Map<Integer,Integer> oldStat = new HashMap<>();
            for(int l = 0;l <oldList.size();l++){
                scribbleObject ob = oldList.get(l);

                //和橡皮的点进行比对
                for(int m=0;m<erase.length/4;m++){

                    float eraseX = erase[m*4+0];
                    float eraseY = erase[m*4+1];
                    float Xdist = ob.X1-eraseX;
                    float Ydist = ob.Y1-eraseY;

                    float distance = Xdist*Xdist+Ydist*Ydist;
                    if (distance<100){
                        Integer count = oldStat.get(ob.stroke);
                        if (count == null){
                            count = 0;
                        }

                        count++;
                        oldStat.put(ob.stroke,count);

                    }

                }

            }

            for (Integer key : oldStat.keySet()) {
                if (oldStat.get(key)>3){
                    oldErase.add(key);
                }
            }

            for(int n = 0;n <oldList.size();n++){

                scribbleObject ob = oldList.get(n);
                if (oldErase.indexOf(ob.stroke) != -1){
                    oldList.remove(n);
                    n--;
                }

            }

            //把oldErase里的从数据库删除
            SQLiteDatabase sqlDB = db.getReadableDatabase();
            sqlDB.beginTransaction();
            for(int q=0;q<oldErase.size();q++){
                sqlDB.delete(db.TABLE_NAME,"ParentID = ? and UserID = ? and Type = ? and Page = ? and Stroke = ?",
                        new String[]{parent_id,"default_user","answer",page+"",oldErase.get(q)+""});

            }
            sqlDB.setTransactionSuccessful();
            sqlDB.endTransaction();


        }



        //removeScribble(strokesToRemove);
        drawScribble(scribbleList.getStrokes(),page);

    }

    public void redrawPage(){
        pages.set(page-1,scribbleList);
        //savePage(page);
        EpdController.leaveScribbleMode(surfaceView);
        touchHelper.pauseRawDrawing();
        cleanSurfaceView();

        //把东西画上去
        drawScribble(scribbleList.getStrokes(),page);
        //Log.v("Length",scribbleList.getStrokes().size()+"");

        touchHelper.resumeRawDrawing();
    }

    private int compare(float[]erase,float[]test){

        int adjacencyCount = 0;

        //把test的每一个点拿出来比对
        for(int j=0;j<test.length/4;j++){
            float testX = test[j*4+0];
            float testY = test[j*4+1];

            for(int k=0;k<erase.length/4;k++){

                float eraseX = erase[k*4+0];
                float eraseY = erase[k*4+1];
                float Xdist = testX-eraseX;
                float Ydist = testY-eraseY;

                float distance = Xdist*Xdist+Ydist*Ydist;
                if (distance<400){
                    adjacencyCount++;
                }

            }
        }
        Log.v("ERASER","The count is "+adjacencyCount);
        if (adjacencyCount>1){
            return 1;
        }else{
            return 2;
        }




    }


}
