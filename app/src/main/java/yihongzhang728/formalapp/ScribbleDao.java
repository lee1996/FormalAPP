package yihongzhang728.formalapp;

//import android.arch.persistence.room.Dao;
//import android.arch.persistence.room.Delete;
//import android.arch.persistence.room.Insert;
//import android.arch.persistence.room.Query;

//import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

import android.content.Context;

/**
 * Created by a87 on 2017/12/23.
 */

public class ScribbleDao {

    private Context context;
    private ScribbleDatabase scribbleDatabase;

    public ScribbleDao(Context context) {
        this.context = context;
        scribbleDatabase = new ScribbleDatabase(context);
    }

    /*
    @Query("SELECT * FROM Scribble WHERE parent_id LIKE :parent_id AND user_id LIKE :user_id AND type LIKE :type LIMIT 1")
    Scribble findScribble(String user_id, String parent_id, String type);

    @Insert
    void insertAll(Scribble... scribbles);

    @Insert (onConflict = REPLACE)
    void addScribble(Scribble scribble);

    @Delete
    void delete(Scribble scribble);
    */
}


