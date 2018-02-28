package yihongzhang728.formalapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class questionListOfShortAnswer extends AppCompatActivity implements DownloadCallback{

    private String mVolume_id;

    private ListView lv;
    private NetworkFragment mNetworkFragment;
    private boolean mDownloading = false;
    public static ArrayList<JSONObject> mShortAnswerList = new ArrayList<JSONObject>(){};

    //private EventBus eventBus = new EventBus();
    //private TouchHelper touchHelper;
    private static final String TAG = questionListOfShortAnswer.class.getSimpleName();

    private BaseAdapter shortAnswerAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mShortAnswerList.size();
        }

        @Override
        public JSONObject getItem(int position) {
            return mShortAnswerList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        //自定义它的view，每次生成view时该方法都会执行，顺序从上往下
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            LinearLayout ll;

            //一个优化机制：当因翻页而不再显示的view被系统回收时，将其重新赋给下一个linearlayout再次利用，避免生成过多的view
            if(convertView!=null)
            {
                ll = (LinearLayout) convertView;
            }else {
                ll = (LinearLayout) LayoutInflater.from(questionListOfShortAnswer.this).inflate(R.layout.short_answer_item,null);
            }

            final JSONObject currentdata = getItem(position);

            //当每一个view被点击时，启动相应的section界面
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(questionListOfShortAnswer.this, ShortAnswer.class);
                    try {
                        i.putExtra("question_id",currentdata.getJSONObject("_id").getString("$id"));


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    startActivity(i);

                }
            });





            //获取各view所对应的TestTitle
            TextView tv = (TextView) ll.findViewById(R.id.shortAnswerItemTitle);
            try {
                tv.setText(Html.fromHtml(currentdata.getString("question_title").replace("\\/","/")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return ll;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_list_of_short_answer);

        Intent i = getIntent();
        mVolume_id = i.getStringExtra("volume_id");

        mNetworkFragment  = NetworkFragment.getInstance(getSupportFragmentManager());


        //eventBus.register(this);
        //touchHelper = new TouchHelper(eventBus);
    }

    @Override
    public void onStart() {
        super.onStart();

        //Setup the Action Bar
        //ActionBar bar = getActionBar();
        //bar.setDisplayHomeAsUpEnabled(false);

        downloadSubjects();

    }





    private void downloadSubjects(){

        String function = "findQuestionsByVolume";
        String para = mVolume_id + "|short_answer";
        startDownload(function,para);


    }

    private void startDownload(String function, String para){


        if (!mDownloading && mNetworkFragment != null){

            //Execute teh Async Download
            mNetworkFragment.startDownload(function, para);

            mDownloading = true;
        }
    }


    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo;
    }

    @Override
    public void updateFromDownload(String function,String result) throws JSONException {
        // Update your UI here based on result of download.

        Log.v("RESULT",result);

        setDataFromString(result);



    }

    private void setDataFromString(String data_string) throws JSONException {
        mShortAnswerList = new ArrayList<JSONObject>();
        data_string = data_string.substring(1,data_string.length()-1);
        data_string = data_string.replace("\\\"","\"");
        data_string = data_string.replace("\\u","u");
        data_string = data_string.replace("\\\\r\\\\n","\\n");
        data_string = data_string.replace("\\\\n","     ");
        data_string = data_string.replace("%","%25");
        data_string = data_string.replace("'","\'");
        data_string = data_string.replace("&"," ");
        data_string = data_string.replace("nbsp"," ");
        //data_string = data_string.replace("amp"," ");

        JSONArray jsonArray = new JSONArray(data_string);
        for (int i = 0;i <jsonArray.length();i++) {

            JSONObject item = jsonArray.getJSONObject(i);
            mShortAnswerList.add(item);
        }


        lv= (ListView) findViewById(R.id.listViewOfShortAnswerList);






        //主布局中的listview的adpater的设置
        lv.setAdapter(shortAnswerAdapter);
    }
    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {
        switch(progressCode) {
            // You can add UI behavior for progress updates here.
            case DownloadCallback.Progress.ERROR:

                break;
            case DownloadCallback.Progress.CONNECT_SUCCESS:

                break;
            case DownloadCallback.Progress.GET_INPUT_STREAM_SUCCESS:

                break;
            case DownloadCallback.Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:

                break;
            case DownloadCallback.Progress.PROCESS_INPUT_STREAM_SUCCESS:

                break;
        }
    }

    @Override
    public void finishDownloading() {
        mDownloading = false;
        if (mNetworkFragment != null) {
            mNetworkFragment.cancelDownload();
        }
    }

/*
    @Override
    protected void onResume() {
        touchHelper.resumeRawDrawing();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        EpdController.leaveScribbleMode(lv);
        touchHelper.stopRawDrawing();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        touchHelper.pauseRawDrawing();
        super.onPause();
    }

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
        Log.d(TAG, "onRawTouchPointMoveReceivedEvent");
        Log.v("Point","X is " +   e.getTouchPoint().getX() +" and Y is "+ e.getTouchPoint().getY());
    }

    @Subscribe
    public void onRawTouchPointListReceivedEvent(RawTouchPointListReceivedEvent e) {
        Log.d(TAG, "onRawTouchPointListReceivedEvent");

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
    }
   */
}
