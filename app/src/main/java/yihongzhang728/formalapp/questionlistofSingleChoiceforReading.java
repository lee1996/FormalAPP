package yihongzhang728.formalapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class questionlistofSingleChoiceforReading extends AppCompatActivity {

    //用于储存从sectionlist传来的section的编号，方便使用
    private int a;
    //当前视图中的底部view的position（position从0开始）
    private int bottomviewposition = 0;
    //上一个视图中的底部view的position
    private int prevbottom = 0;
    //用与显示题目几到几
    private TextView pi;
    //一个用于储存每个视图的底部view的position的arraylist（该arraylist的第一个position是0）
    private ArrayList<Integer> positionindex = new ArrayList<>();
    //用于储存各个SingleChoice属性的arraylist
    private ArrayList<SingleChoice> data = new ArrayList<SingleChoice>(){};
    //用户当前的选项的选择
    private ArrayList<Integer> decision = new ArrayList<Integer>();
    //通过intent传入的刚才用户选项的选择
    private ArrayList<Integer> inputdecision = new ArrayList<Integer>();



    //自定义adapter
    private BaseAdapter singlechoiceAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public SingleChoice getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            //若当前页面在最初页，则隐去"上一页"按钮，若在最后页，则隐去"下一页"按钮
            if (position+1==data.size())
            {findViewById(R.id.down).setVisibility(View.INVISIBLE);}

            if (position == 0 )
            {findViewById(R.id.up).setVisibility(View.INVISIBLE);}

            //因为view的产生总是从上到下，所以每一次最下方的view所执行的代码会覆盖前面的
            //判断刚刚动作是向上翻还是向下翻
            if(position >= prevbottom)
            {   //向下翻
                bottomviewposition = position;
                pi.setText("题目"+((prevbottom+1)+"---"+(bottomviewposition+1)));
            }else {

                //向上翻
                for(int i = 0; i<positionindex.size() ; i++)
                {
                    //在positionindex数组中查找到当前的bottomview所对应在该arraylist中的位置
                    if (positionindex.get(i) == prevbottom)
                    {
                        bottomviewposition = positionindex.get(i -1);
                        pi.setText("题目"+(((positionindex.get(i -2))+1)+"---"+(bottomviewposition+1)));
                    }
                }


            }


            LinearLayout ll;


            //优化机制
            if(convertView!=null)
            {
                ll = (LinearLayout) convertView;
            }else {
                ll = (LinearLayout) LayoutInflater.from(questionlistofSingleChoiceforReading.this).inflate(R.layout.single_choice_sampleof_four,null);
            }



            //以下皆为对题干、选项的配置，同questionlistofSingleChoice中
            //区别在于当用户选择每一题的选项时，在decision这个数组中的对应位置记录（选A则记1 B2 C3 D4 初始为0）
            //并在inputdecision不为空时，读取其中用户之前的选择，填入到各个题目的选项中
            SingleChoice currentdata = getItem(position);

            TextView qb = (TextView) ll.findViewById(R.id.questionbody);
            RadioButton rb1 = (RadioButton) ll.findViewById(R.id.answerA);
            RadioButton rb2 = (RadioButton) ll.findViewById(R.id.answerB);
            RadioButton rb3 = (RadioButton) ll.findViewById(R.id.answerC);
            RadioButton rb4 = (RadioButton) ll.findViewById(R.id.answerD);
            final RadioGroup rg1 = (RadioGroup) ll.findViewById(R.id.answerGroup1);
            final RadioGroup rg2 = (RadioGroup) ll.findViewById(R.id.answerGroup2);
            LinearLayout linearLayoutofsinglechoice = (LinearLayout) ll.findViewById(R.id.linearlayout);

            qb.setText( (position+1) + ". " + currentdata.getQuestion());
            rb1.setText("A." + currentdata.getOptions().get(0));
            rb2.setText("B." + currentdata.getOptions().get(1));
            rb3.setText("C." + currentdata.getOptions().get(2));
            rb4.setText("D." + currentdata.getOptions().get(3));





            rb1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rg2.clearCheck();
                    decision.set(position,1);
                }
            });
            rb2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rg2.clearCheck();
                    decision.set(position,2);
                }
            });
            rb3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rg1.clearCheck();
                    decision.set(position,3);
                }
            });
            rb4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rg1.clearCheck();
                    decision.set(position,4);
                }
            });

            if(rb1.getText().length()<12 && rb2.getText().length()<12 && rb3.getText().length()<12 && rb4.getText().length()<12)
            {linearLayoutofsinglechoice.setOrientation(LinearLayout.HORIZONTAL);}

            if(rb1.getText().length()>27 || rb2.getText().length()>27 ||rb3.getText().length()>27 || rb4.getText().length()>27)
            {rg1.setOrientation(LinearLayout.VERTICAL);
                rg2.setOrientation(LinearLayout.VERTICAL);}

            if (inputdecision!=null) {
                if (inputdecision.get(position) == 1) {
                    rb1.setChecked(true);
                }
                if (inputdecision.get(position) == 2) {
                    rb2.setChecked(true);
                }
                if (inputdecision.get(position) == 3) {
                    rb3.setChecked(true);
                }
                if (inputdecision.get(position) == 4) {
                    rb4.setChecked(true);
                }
            }

            return ll;
        }
    };

    private ListView lv;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_questionlistof_reading_comprehension2, menu);
        return true;
    }



    //使actionbar右上图标拥有切换到文章界面功能
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id==R.id.action_arrowback){

           returnarticle();

        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_list);

        pi = (TextView) findViewById(R.id.pageindicater);


        findViewById(R.id.up).setVisibility(View.INVISIBLE);

        //手动给positionindex加上第一个数据，0
        positionindex.add(0);


        Test test = GeneralList.theTest;
        //获取从sectionlist传来的section的编号
        Intent i = getIntent();
        a = i.getExtras().getInt("position");
        data = test.getSections().get(a).getQuestions();

        //初始化decision和inputdecision两个数组，写上题目个数个0
        for (int q = 0; q<data.size() ; q++)
        {
            decision.add(0);
        }
        for (int q = 0; q<data.size() ; q++)
        {
            inputdecision.add(0);
        }

        lv= (ListView) findViewById(R.id.listviewofquestionlist);
        lv.setAdapter(singlechoiceAdapter);

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

    //获取从comprehension article界面返回的数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
       super.onActivityResult(requestCode,resultCode,data);


        try {
            Bundle b = data.getExtras();
            inputdecision = b.getIntegerArrayList("decisionlist");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    //向下翻页功能
    public void pagedown(){
        if(positionindex.get(positionindex.size()-1)<bottomviewposition)
            positionindex.add(bottomviewposition);


        prevbottom = bottomviewposition;
        lv.setSelection(bottomviewposition);

        findViewById(R.id.up).setVisibility(View.VISIBLE);
    }


    //向上翻页功能
    public void pageup(){
        if(positionindex.get(positionindex.size()-1)<bottomviewposition)
            positionindex.add(bottomviewposition);

        findViewById(R.id.down).setVisibility(View.VISIBLE);

        if (bottomviewposition != positionindex.get(1) ){


            for(int i = 0; i<positionindex.size() ; i++)
            {if (positionindex.get(i) == bottomviewposition)
            {
                prevbottom = bottomviewposition;
                lv.setSelection(positionindex.get(i -2));
            }

            }

        }
    }

    //使音量键拥有翻页功能，菜单键拥有切换到文章界面功能
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_VOLUME_DOWN:

                pagedown();

                return true;

            case KeyEvent.KEYCODE_VOLUME_UP:

                pageup();

                return true;

            case KeyEvent.KEYCODE_MENU:

                returnarticle();;

                return true;

        }

        return super.onKeyDown(keyCode, event);
    }


    //返回文章界面功能
    //同时传递decision，即用户当前的选项的选择
    //同时方便文章界面区分这个intent是来自哪里的
    public void returnarticle(){
        Bundle b = new Bundle();
        b.putIntegerArrayList("decisionlist",decision);
        Intent i = new Intent(questionlistofSingleChoiceforReading.this, questionlistReadingComprehension.class);
        i.putExtras(b);
        i.putExtra("distinguish","fromquestionlist");
        i.putExtra("position",String.valueOf(a));
        startActivityForResult(i,0);


    }
}
