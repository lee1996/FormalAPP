package yihongzhang728.formalapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

import static yihongzhang728.formalapp.GeneralList.theTest;

public class questionlistReadingComprehension extends AppCompatActivity {

    //显示文章
    private TextView tv;
    //用于储存从sectionlist传来的section的编号，方便使用
    private int a;
    //从sectionlist或singlechoiceforReading或singlechocieforQuiz传来的intent数据
    private Intent i;
    //向singlechoiceforReading发送的intent数据
    private Intent k;
    //向singlechocieforQuiz发送的intent数据
    private Intent kq;
    //用来区别传来的intent的来源是sectionlist，singlechoiceforReading还是singlechocieforQuiz
    private String recognize = null;
    private ScrollView sv;
    //当前文字所处的高度
    private int currentheight = 0;
    //文章的总高度
    private int maxheight;
    //显示当前页数
    private TextView pi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionlist_reading_comprehension);
        getActionBar();

        findViewById(R.id.up).setVisibility(View.INVISIBLE);


        //显示当前页数
        pi = (TextView) findViewById(R.id.pageindicater);
        pi.setText("页数："+1);

        //获取intent及其中内容
        i = this.getIntent();
        recognize = i.getStringExtra("distinguish");
        a =  Integer.valueOf(i.getStringExtra("position"));
        tv = (TextView) findViewById(R.id.article);
        tv.setText(theTest.getSections().get(a).getPassage());

        //k和kq所对应启动的activity不同，但其中内容（当前section的编号）相同
        k = new Intent(questionlistReadingComprehension.this,questionlistofSingleChoiceforReading.class);

        k.putExtras(i.getExtras());

        k.putExtra("position",a);

        kq = new Intent(questionlistReadingComprehension.this,questionlistofSingleChoiceforQuiz.class);

        kq.putExtras(i.getExtras());

        kq.putExtra("position",a);



        sv= (ScrollView) findViewById(R.id.scroller);

        //使listview无法用手滑动，只能通过翻页
        sv.setOnTouchListener(new View.OnTouchListener() {

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



        //此处用viewtreeObserver因为在onCreate时用于显示article的textview还没有创建，无法获取其高度，需要在其创建完毕后再获取
        ViewTreeObserver vto = tv.getViewTreeObserver();
         vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
             @Override
             public void onGlobalLayout() {

                maxheight = tv.getHeight();
                 if (maxheight<=970){findViewById(R.id.down).setVisibility(View.INVISIBLE);}
             }
         });

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_questionlistof_reading_comprehension, menu);
        return true;
    }

    //向下翻页
    public void pagedown(){
        if(currentheight+970<=maxheight)
    {
        //970是预测的在Boox中翻页恰当的距离
        sv.scrollTo(0,970+currentheight);
        currentheight =currentheight +970;
        pi.setText("页数："+(currentheight/970+1));
        if (currentheight+970>=maxheight){findViewById(R.id.down).setVisibility(View.INVISIBLE);}
    }

        findViewById(R.id.up).setVisibility(View.VISIBLE);
    }

    //向上翻页
    public void pageup(){

        if (currentheight != 0){sv.scrollTo(0,currentheight-970);
            currentheight =currentheight -970;
            pi.setText("页数："+(currentheight/970+1));}
            if (currentheight==0){findViewById(R.id.up).setVisibility(View.INVISIBLE);}

        findViewById(R.id.down).setVisibility(View.VISIBLE);
    }

    //使音量键拥有翻页功能，菜单键拥有切换到做题界面功能
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

                   writeanswer();

                return true;


        }

        return super.onKeyDown(keyCode, event);
    }



    //使actionbar右上图标拥有切换到做题界面功能
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id==R.id.action_create){

            writeanswer();
        }


        return super.onOptionsItemSelected(item);
    }

    //切换到做题界面的功能
    //此处先判断intent的来源，若来自sectionlist，则开启对应的activity即可，若来自questionlist或者questionlistofquiz，则需返回相应的存放着用户选项的intent
    //这个功能是用来保存用户刚刚选项的选择
    public void writeanswer(){
        switch (recognize)
        {
            case "fromsection":

                if((theTest.getSections().get(a).getQuestions().get(0).getQuestion().length())>1)
                {startActivity(k);}else {startActivity(kq);}

                break;

            case "fromquestionlist":

                questionlistReadingComprehension.this.setResult(RESULT_OK, k);
                finish();
                break;
            case "fromquestionlistofquiz":

                questionlistReadingComprehension.this.setResult(RESULT_OK, kq);
                finish();
                break;


        }

    }

}
