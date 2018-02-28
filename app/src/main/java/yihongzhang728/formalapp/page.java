package yihongzhang728.formalapp;

import com.onyx.android.sdk.scribble.data.TouchPointList;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by a87 on 2017/12/23.
 */

public class page implements Serializable{
    private ArrayList<float[]> strokes = new ArrayList<>();

    public ArrayList<float[]> getStrokes(){
        return this.strokes;
    }
    public void removeStroke(int index){
        this.strokes.remove(index);
    }

    public void addStroke(float[] stroke){
        this.strokes.add(stroke);
    }

}
