package yihongzhang728.formalapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import static yihongzhang728.formalapp.GeneralList.theTest;

//显示Section的界面
public class sectionlist extends AppCompatActivity {


    //用于储存各个Section属性的arraylist
    private ArrayList<Section> data = new ArrayList<Section>(){

    };

    private ListView lv;



    //自定义一个adapter
    private BaseAdapter sectionadapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Section getItem(int position) {
            return data.get(position);
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
                ll = (LinearLayout) LayoutInflater.from(sectionlist.this).inflate(R.layout.section_sample,null);
            }



            //当每一个view被点击时，启动相应的question或reading界面，同时传入该section的编号
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //通过判断该section用有无文章，判断该section是选择部分还是阅读部分
                    if (theTest.getSections().get(position).getPassage().length()<1)
                    {
                        //若为选择部分
                        Intent i = new Intent(sectionlist.this, questionlistofSingleChoice.class);
                        //用intent传递section的编号
                        i.putExtra("position", position);
                        startActivity(i);
                    }else
                    {
                        //若为阅读部分
                        Intent i = new Intent(sectionlist.this, questionlistReadingComprehension.class);
                        Bundle b = new Bundle();
                        ArrayList<Integer> array = new ArrayList<Integer>();
                        for (int t = 0; t < data.get(position).getQuestions().size(); t++)
                        //创建一个由题目数个0组成的数组
                        {array.add(0);}
                        //将该数组放在bundle中
                        b.putIntegerArrayList("decisionlist",array);
                        //将包含数组的bundle放在intent中
                        i.putExtras(b);
                        i.putExtra("distinguish","fromsection");
                        //用intent传递section的编号
                        i.putExtra("position", String.valueOf(position));
                        startActivity(i);}
                }
            });

            Section currentdata = getItem(position);

            TextView tv = (TextView) ll.findViewById(R.id.sectionTitle);

            //获取各view所对应的SectionTitle
            tv.setText(  "\n"+"  Section:" + currentdata.getID()+ " : " + currentdata.getType()+"\n");



            return ll;
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.section_list);


        Test test = theTest;

        //通过theTest获取section的arraylist，存入data中
        data =test.getSections();


        lv= (ListView) findViewById(R.id.listviewofsectionlist);
        lv.setAdapter(sectionadapter);

    }
}
