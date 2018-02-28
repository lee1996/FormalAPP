package yihongzhang728.formalapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class MenuList extends AppCompatActivity {
    private String mSubject_ID;
    private String mSubject_name;
    private Context mContext = this;

    private ListView lv;
    public static ArrayList<String> mMenuList = new ArrayList<String>(){};

    private BaseAdapter menuAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mMenuList.size();
        }

        @Override
        public String getItem(int position) {
            return mMenuList.get(position);
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
                ll = (LinearLayout) LayoutInflater.from(MenuList.this).inflate(R.layout.menu_item,null);
            }

            final String currentdata = getItem(position);

            //当每一个view被点击时，启动相应的section界面
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    viewForMenuItem(currentdata);

                }
            });



            //获取各view所对应的TestTitle
            TextView tv = (TextView) ll.findViewById(R.id.menuItemTitle);
            int ID =   mContext.getResources().getIdentifier(currentdata,"string",mContext.getPackageName()
            );

                tv.setText(ID);

            return ll;
        }
    };

    //根界面
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_list);


        //Setup the Intent
        Intent intent = getIntent();
        mSubject_ID = intent.getStringExtra("subject_id");
        mSubject_name = intent.getStringExtra("subject_name");

        lv = (ListView) findViewById(R.id.listViewOfMenuList);

        //主布局中的listview的adpater的设置
        lv.setAdapter(menuAdapter);

        /*


        findViewById(R.id.writeHomework).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent writeHomework = new Intent(MenuList.this,GeneralList.class));
                writeHomework.putExtra("subject_id",mSubject_ID);
                writeHomework.putExtra("subject_name",mSubject_name);
                startActivity(writeHomework);
            }
        });

        */
    }

    private void viewForMenuItem(String model){
        if (model.equals("textbook")){

            Intent i = new Intent(MenuList.this,TextbookList.class);
            i.putExtra("subject_id",mSubject_ID);
            startActivity(i);
        }
    }
}
