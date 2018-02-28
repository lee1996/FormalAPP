package yihongzhang728.formalapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

//显示Test的界面
public class GeneralList extends AppCompatActivity {


    private ListView lv;

    //用于储存各个Test属性的arraylist
    private ArrayList<Test> data = new ArrayList<Test>(){

    };
    public static Test theTest;

    //自定义一个adapter
    private BaseAdapter generaladapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Test getItem(int position) {
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
                ll = (LinearLayout) LayoutInflater.from(GeneralList.this).inflate(R.layout.test_sample,null);
            }



            //当每一个view被点击时，启动相应的section界面
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(GeneralList.this, sectionlist.class);
                    startActivity(i);

                }
            });

            Test currentdata = getItem(position);


            //获取各view所对应的TestTitle
            TextView tv = (TextView) ll.findViewById(R.id.testTitle);
            tv.setText(  "\n" + "  "+currentdata.getTest_Name() +"\n");




            return ll;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //主布局设置
        setContentView(R.layout.general_list);

        readURL("http://121.201.13.90/host/bookTest.php");




    }









    public void readURL(String url){
        new AsyncTask<String,Void,String>(){
            @Override
            protected String doInBackground(String... params) {
                try {
                    URL url = new URL(params[0]);
                    URLConnection connection = url.openConnection();
                    InputStream is = connection.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    String line;
                    StringBuilder builder = new StringBuilder();
                    while ((line = br.readLine())!=null)
                    {
                        builder.append(line);

                    }

                    br.close();
                    isr.close();
                    is.close();

                    Gson gson = new Gson();
                    String str = builder.toString();
                    Test test = gson.fromJson(str,Test.class);

                    //将数据变成Json并定义成为一个static object，命名为"theTest"，让所有activity都可以自由使用
                    theTest = test;

                    data.add(theTest);



                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                lv= (ListView) findViewById(R.id.listviewofgenerallist);

                //主布局中的listview的adpater的设置
                lv.setAdapter(generaladapter);

            }

            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);
            }

            @Override
            protected void onCancelled(String s) {
                super.onCancelled(s);
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
            }



        }.execute(url);


    }

}
