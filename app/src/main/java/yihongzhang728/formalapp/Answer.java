package yihongzhang728.formalapp;

import java.util.Date;

/**
 * Created by phoenix on 2017/7/10.
 */

public class Answer {

    private String ID;
    private String QuestionID ;//作答题目的ID
    private String StudentID ;//作答学生的ID
    private Date AnswerTime ;//作答的时间
    private int Answer;// 作答的答案



    public String getQuestionID() {
        return QuestionID;
    }

    public void setQuestionID(String questionID) {
        QuestionID = questionID;
    }

    public String getStudentID() {
        return StudentID;
    }

    public void setStudentID(String studentID) {
        StudentID = studentID;
    }

    public Date getAnswerTime() {
        return AnswerTime;
    }

    public void setAnswerTime(Date answerTime) {
        AnswerTime = answerTime;
    }

    public int getAnswer() {

        return Answer;
    }

    public void setAnswer(int answer) {
        Answer = answer;
    }

    public Answer(String id, String questionID, String studentID, Date answerTime, int answer) {
        ID = id;


        QuestionID = questionID;
        StudentID = studentID;
        AnswerTime = answerTime;
        Answer = answer;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
