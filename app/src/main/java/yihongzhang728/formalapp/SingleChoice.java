package yihongzhang728.formalapp;

import java.util.ArrayList;

/**
 * Created by phoenix on 2017/7/10.
 */

public class SingleChoice {


    private String ID;//题目的ID
    private String Question;//题干
    private ArrayList<String> Options;//选项的集合
    private int CorrectAnswer; //正确答案的位置0,1,2,3
    private String Solution; //答案解析


    public SingleChoice(String ID, String question, ArrayList<String> options, int correctAnswer, String solution) {
        this.ID = ID;
        Question = question;
        Options = options;
        CorrectAnswer = correctAnswer;
        Solution = solution;
    }


    public String getID() {
        return ID;
    }

    public String getQuestion() {
        return Question;
    }

    public ArrayList<String> getOptions() {
        return Options;
    }

    public int getCorrectAnswer() {
        return CorrectAnswer;
    }

    public String getSolution() {
        return Solution;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public void setOptions(ArrayList<String> options) {
        Options = options;
    }

    public void setCorrectAnswer(int correctAnswer) {
        CorrectAnswer = correctAnswer;
    }

    public void setSolution(String solution) {
        Solution = solution;
    }
}
