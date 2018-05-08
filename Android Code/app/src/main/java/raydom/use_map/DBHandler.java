package raydom.use_map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by LeeSunjun on 2017-07-18.
 */

public class DBHandler {
    DBOpenHelper helper;
    SQLiteDatabase db;

    //초기화
    public DBHandler(Context context) {
        helper = new DBOpenHelper(context, "MoU.db",null,1);
    }

    //open
    public static DBHandler open(Context context) {
        return new DBHandler(context);
    }

    //close
    public void close() {
        db.close();
    }

    //insert
    public void insert(int id, double lt, double lg, String name, String url, int bm, int category) {
        db = helper.getWritableDatabase(); // db 객체를 얻어온다. 쓰기 가능

        ContentValues values = new ContentValues();

        values.put("ID", id);
        values.put("latitude", lt);
        values.put("longitude", lg);
        values.put("name", name);
        values.put("url", url);
        values.put("bm", bm);
        values.put("category", category);

        db.insert("Marker", null, values);

        Log.d("insert","id : "+id);
    }

    public void insert_personal(double lt, double lg, String name) {
        db = helper.getWritableDatabase(); // db 객체를 얻어온다. 쓰기 가능

        ContentValues values = new ContentValues();

        values.put("latitude", lt);
        values.put("longitude", lg);
        values.put("name", name);

        db.insert("personal", null, values);

        Log.d("insert","name : "+ name);
    }

    public void insert_diy(int id, double lt, double lg, String name, String url, int category) {
        db = helper.getWritableDatabase(); // db 객체를 얻어온다. 쓰기 가능

        ContentValues values = new ContentValues();

        values.put("id",id);
        values.put("latitude", lt);
        values.put("longitude", lg);
        values.put("name", name);
        values.put("url", url);
        values.put("category", category);
        values.put("valid", 1);

        db.insert("DiyMarker", null, values);

        Log.d("insert","name : " + name);
    }

    //select all
    public Cursor select_all() {

        db = helper.getReadableDatabase(); // db객체를 얻어온다. 읽기 전용
        Cursor c = db.query("Marker", null, null, null, null, null, null);

        return c;
    }

    public Cursor select_all_personal() {

        db = helper.getReadableDatabase(); // db객체를 얻어온다. 읽기 전용

        Log.d("insert", "personal in11");

        Cursor c = db.query("personal", null, null, null, null, null, null);

        Log.d("insert", "personal in");

        return c;
    }

    public Cursor select_all_diy() {

        db = helper.getReadableDatabase(); // db객체를 얻어온다. 읽기 전용
        Cursor c = db.query("DiyMarker", null, null, null, null, null, null);

        return c;
    }

    //select specific
    public Cursor select_marker(double lt , double lg) {
        db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("select * from Marker where latitude ="+ lt +" and longitude+"+ lg , null);

        return c;
    }

    public Cursor select_category(int category) {
        db = helper.getReadableDatabase();
        Cursor c;

        if(category == 5) {
            c = db.rawQuery("select * from Marker where bm = 1", null);
        } else {
            c = db.rawQuery("select * from Marker where category=" + category, null);
        }

        Log.d("ddbb","int sc");

        return c;
    }

    public Cursor select_category_diy(int category) {
        db = helper.getReadableDatabase();
        Cursor c;

        c = db.rawQuery("select * from DiyMarker where category=" + category, null);

        Log.d("ddbb","int sc");

        return c;
    }

    //update
    public void set_personal (int id, int category, int bm) {
        db = helper.getWritableDatabase(); //db 객체를 얻어온다. 쓰기가능

        if(bm == 1) {
            db.execSQL("Update Marker set bm = 0 where id=" + id + " and category=" + category);
        } else {
            db.execSQL("Update Marker set bm = 1 where id=" + id + " and category=" + category);
        }

        Log.d("ddbb", "1023");
    }

    public void set_valid (int id, int category, int valid) {
        db = helper.getWritableDatabase(); //db 객체를 얻어온다. 쓰기가능

        db.execSQL("Update DiyMarker set valid = " + valid + " where id=" + id + " and category=" + category);

        Log.d("ddbb", "valid setting");
    }

    //delete
    public void delete (double id) {
        db = helper.getWritableDatabase();
        db.delete("Marker", "id=?", new String[]{String.valueOf(id)});
        Log.i("ddbb","정상적으로 삭제 되었습니다.");
    }

    //for Login

    public void insert_login(String id, String name, String url) {
        db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("ID", id);
        values.put("name", name);
        values.put("profile", url);

        db.insert("Login", null, values);
    }

    public Cursor get_login_info() {
        db = helper.getReadableDatabase(); // db객체를 얻어온다. 읽기 전용
        Cursor c = db.rawQuery("select * from Login", null);

        return c;
    }

    public Cursor get_diy(double lt, double lg) {
        db = helper.getReadableDatabase(); // db객체를 얻어온다. 읽기 전용
        Cursor c = db.rawQuery("select * from DiyMarker where latitude = "+ lt + " and longitude = " + lg, null);

        return c;
    }

    public void set_profile (String url) {
        db = helper.getWritableDatabase(); //db 객체를 얻어온다. 쓰기가능

        db.execSQL("Update Login set profile = '" + url + "'");

        Log.d("ddbb", "1023");
    }

    public void delete_login () {
        db = helper.getWritableDatabase();
        db.delete("Login",null,null);
        Log.i("ddbb","정상적으로 삭제 되었습니다.");
    }

    public void delete_personal () {
        db = helper.getWritableDatabase();
        db.delete("personal",null,null);
        Log.i("ddbb","정상적으로 삭제 되었습니다.");
    }
}
