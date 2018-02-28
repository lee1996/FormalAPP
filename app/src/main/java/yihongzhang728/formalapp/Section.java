package yihongzhang728.formalapp;

import java.util.ArrayList;

/**
 * Created by phoenix on 2017/7/10.
 */

public class Section {




    private String ID;
    private String AudioAddress;
    private String Description ;//练习的提示，每个Section开头的   话
    private String Passage ;//阅读 解的 章，完型的 章，单选题 的Section则为空
    private ArrayList<SingleChoice> Questions;// 这个Section包含的选择题的ID， 如  篇阅读下 的5道问题
    private String Type;

    public Section(String id, String audioAddress, String description, String passage, ArrayList<SingleChoice> questions, String type) {
        ID = id;
        AudioAddress = audioAddress;
        Description = description;
        Passage = passage;
        Questions = questions;
        Type = type;
    }



    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPassage() {
        return Passage;
    }

    public void setPassage(String passage) {
        Passage = passage;
    }



    public void setQuestions(ArrayList<SingleChoice> questions) {
        Questions = questions;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getAudioAddress() {
        return AudioAddress;
    }

    public void setAudioAddress(String audioAddress) {
        AudioAddress = audioAddress;
    }

    public ArrayList<SingleChoice> getQuestions() {
        return Questions;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
