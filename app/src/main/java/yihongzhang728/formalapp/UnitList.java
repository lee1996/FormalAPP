package yihongzhang728.formalapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

public class UnitList extends AppCompatActivity implements DownloadCallback{

    private String mVolume_id;

    private ListView lv;
    private NetworkFragment mNetworkFragment;
    private boolean mDownloading = false;
    public static ArrayList<JSONObject> mUnitList = new ArrayList<JSONObject>(){};

    private BaseAdapter unitAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mUnitList.size();
        }

        @Override
        public JSONObject getItem(int position) {
            return mUnitList.get(position);
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
                ll = (LinearLayout) LayoutInflater.from(UnitList.this).inflate(R.layout.unit_item,null);
            }

            final JSONObject currentdata = getItem(position);

            //当每一个view被点击时，启动相应的section界面
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(UnitList.this, Unit.class);
                    try {
                        i.putExtra("unit_id",currentdata.getJSONObject("_id").getString("$id"));
                        i.putExtra("unit_name",currentdata.getString("unit_name"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    startActivity(i);

                }
            });



            //获取各view所对应的TestTitle
            TextView tv = (TextView) ll.findViewById(R.id.unitItemTitle);
            try {
                tv.setText(currentdata.getString("unit_name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return ll;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_list);

        Intent i = getIntent();
        mVolume_id = i.getStringExtra("volume_id");

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.textbook_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.textbook_concepts:
                showConcepts();
                return true;
            case R.id.textbook_single_choices:

                showSingleChoices();

                return true;

            case R.id.textbook_short_answers:

                showShortAnswers();

                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSingleChoices(){
        Intent i  = new Intent(UnitList.this,questionlistofSingleChoice.class);
        i.putExtra("volume_id",mVolume_id);
        i.putExtra("page_mark","textbook");
        startActivity(i);
    }

    private void showShortAnswers(){
        Intent i  = new Intent(UnitList.this,questionListOfShortAnswer.class);
        i.putExtra("volume_id",mVolume_id);
        i.putExtra("page_mark","textbook");
        startActivity(i);
    }

    private void showConcepts(){
        Intent i = new Intent(UnitList.this,ConceptList.class);
        i.putExtra("volume_id",mVolume_id);

        startActivity(i);

    }

    private void downloadSubjects(){

        String function = "findUnitsByVolume";
        String para = mVolume_id;
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
        mUnitList = new ArrayList<JSONObject>();
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
            mUnitList.add(item);
        }


        lv= (ListView) findViewById(R.id.listViewOfUnitList);

        //主布局中的listview的adpater的设置
        lv.setAdapter(unitAdapter);
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
