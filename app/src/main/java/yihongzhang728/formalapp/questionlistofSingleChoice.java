package yihongzhang728.formalapp;


import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class questionlistofSingleChoice extends AppCompatActivity implements DownloadCallback{

    private String mVolume_id;

    private String mPage_mark;
    private NetworkFragment mNetworkFragment;
    private boolean mDownloading = false;

    //当前视图中的底部view的position（position从0开始）
    private int bottomviewposition = 0;
    //上一个视图中的底部view的position
    private int prevbottom = 0;
    //一个用于储存每个视图的底部view的position的arraylist（该arraylist的第一个position是0）
    private ArrayList<Integer> positionindex = new ArrayList<>();
    //用与显示题目几到几
    private TextView pi;
    //用于储存各个Test属性的arraylist
    private ArrayList<JSONObject> data = new ArrayList<JSONObject>(){};
    //听力的音乐播放器
    private MediaPlayer mp;
    //播放按钮的图标
    private ImageView ivplay;
    //用单双数来判断这次点击应该触发播放还是暂停
    private int CountofPlay = 2;
    //播放器的时长显示
    private TextView TimeofAudio;
    private ListView lv;
    //播放器进度条
    private SeekBar sb;
    private android.os.Handler handler = new android.os.Handler();

    //自定义adapter
    private BaseAdapter singlechoiceAdapter;

    {
        singlechoiceAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return data.size();
            }

            @Override
            public JSONObject getItem(int position) {
                return data.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                //若当前页面在最初页，则隐去"上一页"按钮，若在最后页，则隐去"下一页"按钮
                if (position + 1 == data.size()) {
                    findViewById(R.id.down).setVisibility(View.INVISIBLE);
                }

                if (position == 0) {
                    findViewById(R.id.up).setVisibility(View.INVISIBLE);
                }
                //因为view的产生总是从上到下，所以每一次最下方的view所执行的代码会覆盖前面的
                //判断刚刚动作是向上翻还是向下翻
                if (position >= prevbottom) {
                    //向下翻
                    bottomviewposition = position;
                    pi.setText("题目" + ((prevbottom + 1) + "---" + (bottomviewposition + 1)));


                } else {

                    //向上翻
                    for (int i = 0; i < positionindex.size(); i++) {
                        //在positionindex数组中查找到当前的bottomview所对应在该arraylist中的位置
                        if (positionindex.get(i) == prevbottom) {
                            bottomviewposition = positionindex.get(i - 1);
                            pi.setText("题目" + (((positionindex.get(i - 2)) + 1) + "---" + (bottomviewposition + 1)));
                        }
                    }


                }

                LinearLayout ll = null;


                //判断这套questionlist中的题目是3个选项还是4个
                JSONObject demoQues = data.get(0);
                String[] options = {"a","b","c","d","e"};
                int optionCount = 0;
                for (int k=0;k < options.length;k++){
                    String option = "question_option_"+options[k];
                    try {
                        String optionContent = demoQues.getString(option);
                        if (optionContent.equals("")){
                            break;
                        }else{
                            optionCount ++;
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                Log.v("Option_count", ("Option count is " + optionCount));

                if (optionCount == 4)

                {
                    //4个选项
                    //优化机制
                    if (convertView != null) {
                        ll = (LinearLayout) convertView;
                    } else {

                        ll = (LinearLayout) LayoutInflater.from(questionlistofSingleChoice.this).inflate(R.layout.single_choice_sampleof_four, null);
                    }


                    JSONObject currentdata = getItem(position);


                    TextView qb = (TextView) ll.findViewById(R.id.questionbody);
                    RadioButton rb1 = (RadioButton) ll.findViewById(R.id.answerA);
                    RadioButton rb2 = (RadioButton) ll.findViewById(R.id.answerB);
                    RadioButton rb3 = (RadioButton) ll.findViewById(R.id.answerC);
                    RadioButton rb4 = (RadioButton) ll.findViewById(R.id.answerD);

                    //RadioGroup1中存放A B选项，RadioGroup2中存放C D选项
                    final RadioGroup rg1 = (RadioGroup) ll.findViewById(R.id.answerGroup1);
                    final RadioGroup rg2 = (RadioGroup) ll.findViewById(R.id.answerGroup2);
                    LinearLayout linearLayoutofsinglechoice = (LinearLayout) ll.findViewById(R.id.linearlayout);

                    //以下分别是题干、四个选项的赋值
                    try {
                        String title = (position + 1) + ". " + currentdata.getString("question_title").replace("\\/","/");
                        Log.v("The content is", title);
                        qb.setText(Html.fromHtml(title));
                        rb1.setText("A. " + currentdata.getString("question_option_a"));
                        rb2.setText("B. " + currentdata.getString("question_option_b"));
                        rb3.setText("C. " + currentdata.getString("question_option_c"));
                        rb4.setText("D. " + currentdata.getString("question_option_d"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                    //同一个RadioGroup中只有一个选项可被选中，之所以用2个RadioGroup是因为1个无法实现2*2的选项排列
                    //当A或B选中时，RadioGroup2，也就是C D被取消选定
                    //当C或D选中时，RadioGroup1，也就是A B被取消选定
                    rb1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rg2.clearCheck();
                        }
                    });
                    rb2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rg2.clearCheck();
                        }
                    });
                    rb3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rg1.clearCheck();
                        }
                    });
                    rb4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rg1.clearCheck();
                        }
                    });


                    //以下为手动通过各个选项的长短来判断选项的布局应该是1*4 2*2 4*1
                    if (rb1.getText().length() < 12 && rb2.getText().length() < 12 && rb3.getText().length() < 12 && rb4.getText().length() < 12) {
                        linearLayoutofsinglechoice.setOrientation(LinearLayout.HORIZONTAL);
                    }

                    if (rb1.getText().length() > 27 || rb2.getText().length() > 27 || rb3.getText().length() > 27 || rb4.getText().length() > 27) {
                        rg1.setOrientation(LinearLayout.VERTICAL);
                        rg2.setOrientation(LinearLayout.VERTICAL);
                    }


                }


                if (optionCount == 3)

                {

                    //3个选项时，其中的代码机理与4个选项时基本一致

                    if (convertView != null) {
                        ll = (LinearLayout) convertView;
                    } else {

                        ll = (LinearLayout) LayoutInflater.from(questionlistofSingleChoice.this).inflate(R.layout.single_choice_sampleof_three, null);
                    }


                    JSONObject currentdata = getItem(position);

                    TextView qb = (TextView) ll.findViewById(R.id.questionbody);
                    RadioButton rb1 = (RadioButton) ll.findViewById(R.id.answerA);
                    RadioButton rb2 = (RadioButton) ll.findViewById(R.id.answerB);
                    RadioButton rb3 = (RadioButton) ll.findViewById(R.id.answerC);
                    RadioGroup rg = (RadioGroup) ll.findViewById(R.id.answerGroup);

                    try {
                        qb.setText((position + 1) + ". " + currentdata.getString("question_title"));
                        rb1.setText("A." + currentdata.getString("question_option_a"));
                        rb2.setText("B." + currentdata.getString("question_option_b"));
                        rb3.setText("C." + currentdata.getString("question_option_c"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                    if (rb1.getText().length() < 16 && rb2.getText().length() < 16 && rb3.getText().length() < 16) {
                        rg.setOrientation(LinearLayout.HORIZONTAL);
                    } else {
                        rg.setOrientation(LinearLayout.VERTICAL);
                    }


                }


                return ll;
            }
        };
    }


    //向下翻页
    public void pagedown(){
        //此方法为向下翻页
        //先判断此时的bottomview是否是当前所到达的最底端，是则加入到positionindex中并放在最后，否则不加
        if(positionindex.get(positionindex.size()-1)<bottomviewposition)
            positionindex.add(bottomviewposition);


        //让prev = 现在的bottomview
        prevbottom = bottomviewposition;
        //跳转到当前最后一个view的顶端
        lv.setSelection(bottomviewposition);

        findViewById(R.id.up).setVisibility(View.VISIBLE);
    }
    //向上翻页
    public void pageup(){
        //此方法为向上翻页
        //先判断此时的bottomview是否是当前所到达的最底端，是则加入到positionindex中并放在最后，否则不加
        if(positionindex.get(positionindex.size()-1)<bottomviewposition)
            positionindex.add(bottomviewposition);


            //在positionindex数组中查找到当前的bottomview所对应在该arraylist中的位置
            for(int i = 0; i<positionindex.size() ; i++)
            {if (positionindex.get(i) == bottomviewposition)
            {
                //让prev = 现在的bottomview
                prevbottom = bottomviewposition;
                //跳转到所找到的view在positionindex中的前两个位置的view的顶端
                lv.setSelection(positionindex.get(i -2));}
            }

        findViewById(R.id.down).setVisibility(View.VISIBLE);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_list);

        Intent i = getIntent();
        mVolume_id = i.getStringExtra("volume_id");
        mPage_mark = i.getStringExtra("page_mark");
        mNetworkFragment  = NetworkFragment.getInstance(getSupportFragmentManager());

        if (mPage_mark.equals("audio")){
            //设定音频播放器播放的文件
            mp= MediaPlayer.create(this, R.raw.my_music);
            sb = (SeekBar) findViewById(R.id.seekBar);
            ivplay = (ImageView) findViewById(R.id.play);
            TimeofAudio = (TextView) findViewById(R.id.TimeofAudio);


            //点击音乐播放键时
            ivplay.setOnClickListener(playlis);

            //设置音乐进度条监听器
            sb.setOnSeekBarChangeListener(sbLis);

            //音乐总时长
            int duration = mp.getDuration();
            sb.setMax(duration);

        }

        findViewById(R.id.up).setVisibility(View.INVISIBLE);

        pi = (TextView) findViewById(R.id.pageindicater);
        //手动给positionindex加上第一个数据，0
        positionindex.add(0);



        /*
        Test test = GeneralList.theTest;
        //获取从sectionlist传来的section的编号
        Intent i = getIntent();
        int a = i.getExtras().getInt("position");


        data =test.getSections().get(a).getQuestions();

        */


        lv= (ListView) findViewById(R.id.listviewofquestionlist);
        //lv.setAdapter(singlechoiceAdapter);

        //使listview无法用手滑动，只能通过翻页
        lv.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        return true;
                    default:
                        break;
                }
                return true;
            }

        });

        //使屏幕上的图标拥有翻页功能
        findViewById(R.id.down).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               pagedown();
            }
        });

        findViewById(R.id.up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               pageup();
            }
        });

    }

    //使音量键拥有翻页功能
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_VOLUME_DOWN:

                pagedown();

                return true;

            case KeyEvent.KEYCODE_VOLUME_UP:

                pageup();


                return true;

        }

        return super.onKeyDown(keyCode, event);
    }



    private SeekBar.OnSeekBarChangeListener sbLis=new SeekBar.OnSeekBarChangeListener(){

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
            mp.seekTo(sb.getProgress());
            //SeekBar确定位置后，跳到指定位置
        }

    };


    //点击播放键时
    //先判断该播放还是暂停
    private View.OnClickListener playlis = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            if (CountofPlay%2==0)
            {// TODO Auto-generated method stub
                //调用handler播放
                handler.post(start);
                ivplay.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);
                CountofPlay++;}else
            {
                mp.pause();
                ivplay.setImageResource(R.drawable.ic_play_circle_outline_black_24dp);
                CountofPlay++;
            }
        }

    };
    Runnable start=new Runnable(){

        @Override
        public void run() {
            // TODO Auto-generated method stub
            mp.start();
            //用一个handler更新SeekBar
            handler.post(updatesb);
        }

    };
    //使seekbar每隔一秒更新进度以及最右侧的时间显示
    Runnable updatesb =new Runnable(){

        @Override
        public void run() {
            // TODO Auto-generated method stub
            sb.setProgress(mp.getCurrentPosition());
            handler.postDelayed(updatesb, 1000);

            TimeofAudio.setText(transferMilliToTime(mp.getCurrentPosition())+"/"+transferMilliToTime(mp.getDuration()));

        }

    };
    //让毫秒转换成"分钟：秒钟"的方法
    private String transferMilliToTime(int millis){
        DateFormat format = new SimpleDateFormat("mm:ss");
        String result = format.format(new Date(millis));
        return result;
    }



    @Override
    public void onStart() {
        super.onStart();

        Log.v("RESULT","The activity is started");
        //Setup the Action Bar
        //ActionBar bar = getActionBar();
        //bar.setDisplayHomeAsUpEnabled(false);

        downloadSubjects();

    }

    private void downloadSubjects(){
        Log.v("RESULT","Going to Download");
        String function = "findQuestionsByVolume";
        String para = mVolume_id + "|single_choice";
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
        data = new ArrayList<JSONObject>();
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
            data.add(item);
        }




        //主布局中的listview的adpater的设置
        lv.setAdapter(singlechoiceAdapter);
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
