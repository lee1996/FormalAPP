package yihongzhang728.formalapp;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by phoenix on 2017/7/10.
 */

public class Test implements Serializable{


    private String ID; //Test的ID
    private String Test_Name;// 考试/练习的名称
    private String Description;// 考试/练习的介绍
    private ArrayList<Section> Sections ;//这次考试/练习下附的联系， 如 块单 选，2篇阅读，2篇完型

    public Test(String id, String test_Name, String description, ArrayList<Section> sections) {
        ID = id;
        Test_Name = test_Name;
        Description = description;
        Sections = sections;
    }



    public String getTest_Name() {
        return Test_Name;
    }

    public void setTest_Name(String test_Name) {
        Test_Name = test_Name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }



    public void setSections(ArrayList<Section> sections) {
        Sections = sections;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public ArrayList<Section> getSections() {
        return Sections;
    }
}
