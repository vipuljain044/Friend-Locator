package com.vipuljain044.friendlocator;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLdb {

    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_CODE = "code";
    private static final String DATABASE_NAME = "db";
    private static final String DATABASE_TABLE = "tn";
    private static final int DATABASE_VERSION = 1;

    private HelperSQL ourHelper;
    private final Context ourContext;
    private SQLiteDatabase ourDatabase;

    public SQLdb(Context ourContext)
    {
        this.ourContext = ourContext;
    }

    public SQLdb open(){
        ourHelper=new HelperSQL(ourContext);
        ourDatabase=ourHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        ourHelper.close();
    }

    public void insertEntry(String name_insert, String code_insert) {
        ourDatabase.execSQL("INSERT INTO tn(name,code) VALUES('" + name_insert + "','" + code_insert + "');");
    }

    public String[] getFriendList() {
        int i=0;
        Cursor resultSet = ourDatabase.rawQuery("Select * from " + DATABASE_TABLE, null);
        String info[]=new String[resultSet.getCount()];
        resultSet.moveToFirst();
        do{
            info[i]=resultSet.getString(1);
            resultSet.moveToNext();
            i++;
        }while(resultSet.isLast());
        return info;
    }
    public String getCode(int pos) {
        int i=0;
        Cursor resultSet = ourDatabase.rawQuery("Select * from " + DATABASE_TABLE, null);
        String info="";
        resultSet.moveToFirst();
        do{
            if(i==pos) {
                info = resultSet.getString(2);
                return info;
            }
            resultSet.moveToNext();
            i++;
        }while(resultSet.isLast());
        return info;
    }

    public void onDelete(){
        ourDatabase.execSQL("DROP TABLE tn;");
    }

    public int getDetail(){
        Boolean info=false;
        Cursor resultSet = ourDatabase.rawQuery("Select * from " + DATABASE_TABLE, null);
        try {
            return resultSet.getCount();
        }
        catch(Exception e)
        {
            Log.e("getDetail",e.toString());
        }
        return 0;
    }

    private static class HelperSQL extends SQLiteOpenHelper {

        public HelperSQL(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS tn(_id INTEGER PRIMARY KEY AUTOINCREMENT , name TEXT NOT NULL , code TEXT NOT NULL );");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE);
            onCreate(db);
        }
    }

}