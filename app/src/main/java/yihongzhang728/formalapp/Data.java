package yihongzhang728.formalapp;

//import android.app.Application;
//import android.arch.persistence.db.SupportSQLiteDatabase;
//import android.arch.persistence.room.Room;
//import android.arch.persistence.room.migration.Migration;

/**
 * Created by a87 on 2017/12/23.


public class Data extends Application {

    public ScribbleDatabase db;
    public ScribbleDatabase getDB(){
        return this.db;
    }

    public void setDB(ScribbleDatabase db){
        this.db = db;
    }
    @Override
    public void onCreate(){
        //db = Room.databaseBuilder(getApplicationContext(),
                //ScribbleDatabase.class, "user_scribble").addMigrations(MIGRATION_1_2).build();;
        super.onCreate();


    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `Scribble` (`id` TEXT, `parent_id` TEXT, `user_id` TEXT, `type` TEXT, "
                    + "`list` TEXT, PRIMARY KEY(`id`))");
        }
    };
}
 */