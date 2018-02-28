package yihongzhang728.formalapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VolumeList extends AppCompatActivity implements DownloadCallback{


    private String mTextbook_id;

    private ListView lv;
    private NetworkFragment mNetworkFragment;
    private boolean mDownloading = false;
    public static ArrayList<JSONObject> mVolumeList = new ArrayList<JSONObject>(){};

    private BaseAdapter volumeAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mVolumeList.size();
        }

        @Override
        public JSONObject getItem(int position) {
            return mVolumeList.get(position);
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
                ll = (LinearLayout) LayoutInflater.from(VolumeList.this).inflate(R.layout.volume_item,null);
            }

            final JSONObject currentdata = getItem(position);

            //当每一个view被点击时，启动相应的section界面
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(VolumeList.this, UnitList.class);
                    try {
                        i.putExtra("volume_id",currentdata.getJSONObject("_id").getString("$id"));
                        i.putExtra("volume_name",currentdata.getString("textbook_name"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    startActivity(i);

                }
            });



            //获取各view所对应的TestTitle
            TextView tv = (TextView) ll.findViewById(R.id.volumeItemTitle);
            try {
                tv.setText((position+1)+". "+currentdata.getString("textbook_name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return ll;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volume_list);

        Intent i = getIntent();
        mTextbook_id = i.getStringExtra("textbook_id");

        mNetworkFragment  = NetworkFragment.getInstance(getSupportFragmentManager());

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

        String function = "findVolumesByTextbook";
        String para = mTextbook_id;
        startDownload(function,para);
        Log.v("Para_id",mTextbook_id);

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
        mVolumeList = new ArrayList<JSONObject>();
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
            mVolumeList.add(item);
        }


        lv= (ListView) findViewById(R.id.listViewOfVolumeList);

        //主布局中的listview的adpater的设置
        lv.setAdapter(volumeAdapter);
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
}
