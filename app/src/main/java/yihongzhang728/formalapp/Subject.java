package yihongzhang728.formalapp;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by a87 on 2017/12/13.
 */

public class Subject implements Serializable{
    private String ID; //Subject的ID
    private String Subject_Name;// 科目的名称

    public Subject(String id, String test_Name) {
        ID = id;
        Subject_Name = test_Name;

    }
    public String getSubject_Name() {
        return Subject_Name;
    }
}
