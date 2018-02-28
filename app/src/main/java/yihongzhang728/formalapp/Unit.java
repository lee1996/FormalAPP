package yihongzhang728.formalapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.pdf.PdfRenderer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnDrawListener;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;
import com.github.barteksc.pdfviewer.listener.OnTapListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileDescriptor;
import java.util.ArrayList;

public class Unit extends AppCompatActivity implements DownloadCallback ,OnPageChangeListener
        ,OnLoadCompleteListener, OnDrawListener,OnErrorListener {

    private String mUnit_id;


    private NetworkFragment mNetworkFragment;
    private boolean mDownloading = false;
    public static JSONObject mUnit = new JSONObject();

    @ViewById
    PDFView pdfView;
    @ViewById
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit);

        Intent i = getIntent();
        mUnit_id = i.getStringExtra("unit_id");

        mNetworkFragment  = NetworkFragment.getInstance(getSupportFragmentManager());

    }

    @Override
    public void onStart() {
        super.onStart();

        //Setup the Action Bar
        //ActionBar bar = getActionBar();
        //bar.setDisplayHomeAsUpEnabled(false);

        //downloadSubjects();

        final PDFView pdfView = (PDFView)this.findViewById(R.id.pdfView);
        final TextView text= (TextView) this.findViewById(R.id.pos);
        final TextView pageCount= (TextView) this.findViewById(R.id.pageCount);
        final EditText jump= (EditText) this.findViewById(R.id.jump);
        final Button sure= (Button) this.findViewById(R.id.sure);
        final InputMethodManager imm= (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        WindowManager wm= (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        final int width=wm.getDefaultDisplay().getWidth();
        pdfView.fromAsset("AP Chapter 3 Test.pdf")// all pages are displayed by default
                .enableSwipe(false)
                .onTap(new OnTapListener() {
                    @Override
                    public boolean onTap(MotionEvent e) {
                        float x=e.getX();
                        if (x>width/2)
                            if (pdfView.getCurrentPage()!=pdfView.getPageCount()-1) {
                                pdfView.jumpTo(pdfView.getCurrentPage() + 1);
                                text.setText("第"+(pdfView.getCurrentPage()+1)+"页");
                            }
                        if (x<width/2)
                            if (pdfView.getCurrentPage()!=0) {
                                pdfView.jumpTo(pdfView.getCurrentPage() - 1);
                                text.setText("第"+(pdfView.getCurrentPage()+1)+"页");
                            }
                        return false;
                    }
                })
                .swipeHorizontal(true)
                .enableDoubletap(true)
                .defaultPage(0)
                .onLoad(new OnLoadCompleteListener() {
                    @Override
                    public void loadComplete(int nbPages) {
                        pageCount.setText("，共"+pdfView.getPageCount()+"页");
                        sure.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String  str=jump.getText().toString();
                                int des=Integer.parseInt(str);
                                if (des>0&&des<=pdfView.getPageCount()){
                                    pdfView.jumpTo(des-1);
                                    text.setText("第"+(pdfView.getCurrentPage()+1)+"页");
                                }
                                imm.hideSoftInputFromWindow(jump.getWindowToken(),0);
                            }
                        });
                    }
                })
                .onPageChange(this)
                .onError(this)
                .enableAnnotationRendering(false)
                .load();


    }

    private void downloadSubjects(){

        String function = "findUnitByID";
        String para = mUnit_id;
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
        mUnit = new JSONObject();
        data_string = data_string.substring(1,data_string.length()-1);

        data_string = data_string.replace("\\\"","\"");
        data_string = data_string.replace("\\\\/","/");
        data_string = data_string.replace("\\u","u");
        data_string = data_string.replace("\\\\r\\\\n","\\n");
        data_string = data_string.replace("\\\\n","     ");
        data_string = data_string.replace("%","%25");
        data_string = data_string.replace("'","\'");

        data_string = data_string.replace("&nbsp;"," ");
        //data_string = data_string.replace("&"," ");
        data_string = data_string.replace("\\\\u2019","\'");

        //data_string = data_string.replace("amp"," ");



        mUnit = new JSONObject(data_string);


        /*
        WebView content= (WebView) findViewById(R.id.textContent);
        //主布局中的listview的adpater的设置
        String contentString = mUnit.getString("unit_text");

        String CSS = "<style type=\"text/css\">"+
        "body {  font-family: Helvetica, arial, sans-serif;  font-size: 14px;  line-height: 1.6;  padding-top: 10px;  padding-bottom: 10px;  background-color: white;  padding: 8px; color:#505050;}body > *:first-child {  margin-top: 0 !important; }body > *:last-child {  margin-bottom: 0 !important; }a {  color: #4183C4; }a.absent {  color: #cc0000; }a.anchor {  display: block;  padding-left: 30px;  margin-left: -30px;  cursor: pointer;  position: absolute;  top: 0;  left: 0;  bottom: 0; }h1, h2, h3, h4, h5, h6 {  margin: 20px 0 10px;  padding: 0;  font-weight: bold;  -webkit-font-smoothing: antialiased;  cursor: text;  position: relative; }h1:hover a.anchor, h2:hover a.anchor, h3:hover a.anchor, h4:hover a.anchor, h5:hover a.anchor, h6:hover a.anchor {  background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAA09pVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMy1jMDExIDY2LjE0NTY2MSwgMjAxMi8wMi8wNi0xNDo1NjoyNyAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNiAoMTMuMCAyMDEyMDMwNS5tLjQxNSAyMDEyLzAzLzA1OjIxOjAwOjAwKSAgKE1hY2ludG9zaCkiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6OUM2NjlDQjI4ODBGMTFFMTg1ODlEODNERDJBRjUwQTQiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6OUM2NjlDQjM4ODBGMTFFMTg1ODlEODNERDJBRjUwQTQiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDo5QzY2OUNCMDg4MEYxMUUxODU4OUQ4M0REMkFGNTBBNCIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDo5QzY2OUNCMTg4MEYxMUUxODU4OUQ4M0REMkFGNTBBNCIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/PsQhXeAAAABfSURBVHjaYvz//z8DJYCRUgMYQAbAMBQIAvEqkBQWXI6sHqwHiwG70TTBxGaiWwjCTGgOUgJiF1J8wMRAIUA34B4Q76HUBelAfJYSA0CuMIEaRP8wGIkGMA54bgQIMACAmkXJi0hKJQAAAABJRU5ErkJggg==) no-repeat 10px center;  text-decoration: none; }h1 tt, h1 code {  font-size: inherit; }h2 tt, h2 code {  font-size: inherit; }h3 tt, h3 code {  font-size: inherit; }h4 tt, h4 code {  font-size: inherit; }h5 tt, h5 code {  font-size: inherit; }h6 tt, h6 code {  font-size: inherit; }h1 {  font-size: 20px;  color: #505050; }h2 {  font-size: 16px;  border-bottom: 1px solid #cccccc;  color: #505050; }h3 {  font-size: 18px; }h4 {  font-size: 16px; }h5 {  font-size: 14px; }h6 {  color: #777777;  font-size: 14px; }p, blockquote, ul, ol, dl, li, table, pre {  margin: 15px 0; }hr {  background: transparent url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAYAAAAECAYAAACtBE5DAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyJpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMC1jMDYwIDYxLjEzNDc3NywgMjAxMC8wMi8xMi0xNzozMjowMCAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNSBNYWNpbnRvc2giIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6OENDRjNBN0E2NTZBMTFFMEI3QjRBODM4NzJDMjlGNDgiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6OENDRjNBN0I2NTZBMTFFMEI3QjRBODM4NzJDMjlGNDgiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDo4Q0NGM0E3ODY1NkExMUUwQjdCNEE4Mzg3MkMyOUY0OCIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDo4Q0NGM0E3OTY1NkExMUUwQjdCNEE4Mzg3MkMyOUY0OCIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/PqqezsUAAAAfSURBVHjaYmRABcYwBiM2QSA4y4hNEKYDQxAEAAIMAHNGAzhkPOlYAAAAAElFTkSuQmCC) repeat-x 0 0;  border: 0 none;  color: #cccccc;  height: 4px;  padding: 0;}body > h2:first-child {  margin-top: 0;  padding-top: 0; }body > h1:first-child {  margin-top: 0;  padding-top: 0; }  body > h1:first-child + h2 {    margin-top: 0;    padding-top: 0; }body > h3:first-child, body > h4:first-child, body > h5:first-child, body > h6:first-child {  margin-top: 0;  padding-top: 0; }a:first-child h1, a:first-child h2, a:first-child h3, a:first-child h4, a:first-child h5, a:first-child h6 {  margin-top: 0;  padding-top: 0; }h1 p, h2 p, h3 p, h4 p, h5 p, h6 p {  margin-top: 0; }li p.first {  display: inline-block; }li {  margin: 0; }ul, ol {  padding-left: 30px; }ul :first-child, ol :first-child {  margin-top: 0; }dl {  padding: 0; }  dl dt {    font-size: 14px;    font-weight: bold;    font-style: italic;    padding: 0;    margin: 15px 0 5px; }    dl dt:first-child {      padding: 0; }    dl dt > :first-child {      margin-top: 0; }    dl dt > :last-child {      margin-bottom: 0; }  dl dd {    margin: 0 0 15px;    padding: 0 15px; }    dl dd > :first-child {      margin-top: 0; }    dl dd > :last-child {      margin-bottom: 0; }blockquote {  border-left: 4px solid #dddddd;  padding: 0 15px;  color: #777777; }  blockquote > :first-child {    margin-top: 0; }  blockquote > :last-child {    margin-bottom: 0; }table {  padding: 0;border-collapse: collapse; }  table tr {    border-top: 1px solid #cccccc;    background-color: white;    margin: 0;    padding: 0; }    table tr:nth-child(2n) {      background-color: #f8f8f8; }    table tr th {      font-weight: bold;      border: 1px solid #cccccc;      margin: 0;      padding: 6px 13px; }    table tr td {      border: 1px solid #cccccc;      margin: 0;      padding: 6px 13px; }    table tr th :first-child, table tr td :first-child {      margin-top: 0; }    table tr th :last-child, table tr td :last-child {      margin-bottom: 0; }img {  max-width: 100%; }span.frame {  display: block;  overflow: hidden; }  span.frame > span {    border: 1px solid #dddddd;    display: block;    float: left;    overflow: hidden;    margin: 13px 0 0;    padding: 7px;    width: auto; }  span.frame span img {    display: block;    float: left; }  span.frame span span {    clear: both;    color: #333333;    display: block;    padding: 5px 0 0; }span.align-center {  display: block;  overflow: hidden;  clear: both; }  span.align-center > span {    display: block;    overflow: hidden;    margin: 13px auto 0;    text-align: center; }  span.align-center span img {    margin: 0 auto;    text-align: center; }span.align-right {  display: block;  overflow: hidden;  clear: both; }  span.align-right > span {    display: block;    overflow: hidden;    margin: 13px 0 0;    text-align: right; }  span.align-right span img {    margin: 0;    text-align: right; }span.float-left {  display: block;  margin-right: 13px;  overflow: hidden;  float: left; }  span.float-left span {    margin: 13px 0 0; }span.float-right {  display: block;  margin-left: 13px;  overflow: hidden;  float: right; }  span.float-right > span {    display: block;    overflow: hidden;    margin: 13px auto 0;    text-align: right; }code, tt {  margin: 0 2px;  padding: 0 5px;  white-space: nowrap;  border: 1px solid #eaeaea;  background-color: #f8f8f8;  border-radius: 3px; }pre code {  margin: 0;  padding: 0;  white-space: pre;  border: none;  background: transparent; }.highlight pre {  background-color: #f8f8f8;  border: 1px solid #cccccc;  font-size: 13px;  line-height: 19px;  overflow: auto;  padding: 6px 10px;  border-radius: 3px; }pre {  background-color: #f8f8f8;  border: 1px solid #cccccc;  font-size: 13px;  line-height: 19px;  overflow: auto;  padding: 6px 10px;  border-radius: 3px; }  pre code, pre tt {    background-color: transparent;    border: none; }sup {    font-size: 0.83em;    vertical-align: super;    line-height: 0;}* {	-webkit-print-color-adjust: exact;}@media screen and (min-width: 914px) {    body {        width: 854px;        margin:0 auto;    }}@media print {	table, pre {		page-break-inside: avoid;	}	pre {		word-wrap: break-word;	}}"+
        "h3.remark {font-size:12px;margin:0px 0px 0px 0px;font-family:sans-serif;color:#c8c8c8}"+
        "h3.footer {font-size:12px;margin:0px 0px 0px 0px;font-family:sans-serif;color:#c8c8c8}"+
        "h3.header {font-size:12px;margin:0px 0px 0px 0px;font-family:sans-serif;color:#505050}"+
        "a:link {text-decoration:none;}"+
        "a:visited {text-decoration:none;}"+
        "a.original {}"+
        "p {font-size:18px;line-height:150%;font-family:sans-serif;color:505050}"+
        "div {font-size:18px;line-height:150%;font-family:sans-serif;color:505050}"+
        "span {font-size:18px;line-height:150%;font-family:sans-serif;color:505050}"+
        "ul {font-family:sans-serif;color:505050}"+

        "</style>";
        contentString = CSS + contentString;
        content.loadData(contentString,"text/html;charset=UTF-8",null);

        //content.setText(mUnit.getString("unit_text"));
        */
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

    @Override
    public void loadComplete(int nbPages) {

    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        //Toast.makeText(this,"第"+page+"页",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(Throwable t) {
        //Log.e(TAG, "Cannot load page " + page);
    }


    /**
     * 加载完成回调
     *  总共的页数
     */

    @Override
    public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {
        // Toast.makeText( MainActivity.this ,  "pageWidth= " + pageWidth + "
        // pageHeight= " + pageHeight + " displayedPage="  + displayedPage , Toast.LENGTH_SHORT).show();
    }


}
