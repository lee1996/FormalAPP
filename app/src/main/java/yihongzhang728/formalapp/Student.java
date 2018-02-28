package yihongzhang728.formalapp;


import com.google.gson.Gson;

public class Student {


    private String Name; //学生的姓名
    private String ID;  //学生的ID


    public Student(String Name, String ID){

       this.Name = Name;
        this.ID = ID;
    }

    public String getID() {
        return ID;

    }

    public String getName() {
        return Name;
    }

    public void setID(String ID) {
        this.ID = ID;
    }


    public void setName(String name) {
        Name = name;
    }
}
