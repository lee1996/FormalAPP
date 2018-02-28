package yihongzhang728.formalapp;

//import android.arch.persistence.room.Entity;
//import android.arch.persistence.room.Index;
//import android.arch.persistence.room.PrimaryKey;

//import com.onyx.android.sdk.scribble.data.TouchPointList;

import java.util.ArrayList;

/**
 * Created by a87 on 2017/12/23.

@Entity(indices = {@Index(value = {"type","parent_id", "user_id"},
        unique = true)})
public class Scribble {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String type;
    public String parent_id;
    public String user_id;
    public String list;

    public Scribble(String type, String parent_id, String user_id, String list){
        this.type = type;
        this.parent_id = parent_id;
        this.user_id = user_id;
        this.list = list;

    }

    public String getType(){
        return this.type;

    }

    public String getParent_id(){
        return this.parent_id;
    }

    public String getUser_id(){
        return this.user_id;
    }

    public String getList(){
        return this.list;
    }
}
 */