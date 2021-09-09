package gov.zambonortedev.znqrscanner.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import gov.zambonortedev.znqrscanner.models.Config;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME="dcermqrreader";
    private static final int DB_VERSION=1;
    private Context mContext;

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE config " +
                        "(config_key text PRIMARY KEY, " +
                        "config_value text)"
        );

        db.execSQL(
                "INSERT INTO config " +
                        "(config_key, config_value) " +
                        "VALUES " +
                        "('BASE_URL', 'http://10.10.60.97/covid19monitoring/');");

    }

    // Add

    public boolean addConfig(Config config) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("config_key", config.getKey());
        contentValues.put("config_value", config.getValue());
        return db.insert("config", null, contentValues) > 0;
    }

    // View

    public Config getConfig(String key) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM config where config_key='" + key + "'", null);
        res.moveToFirst();
        String config_key = res.getString(res.getColumnIndex("config_key"));
        String config_value = res.getString(res.getColumnIndex("config_value"));
        //  db.close();
        return new Config(config_key, config_value);
    }

    public List<Config> getAllConfig() {
        List<Config> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM config ", null);

        res.moveToFirst();
        while (!res.isAfterLast()) {
            Config config = new Config();
            config.setKey(res.getString(res.getColumnIndex("config_key")));
            config.setValue(res.getString(res.getColumnIndex("config_value")));
            list.add(config);
            res.moveToNext();
        }
        return list;
    }

    // Update Config

    public boolean updateConfig(String key, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("config_value", value);
        return db.update("config", contentValues, "config_key = ? ", new String[]{(key)}) > 0;
    }

    public boolean deleteConfig(String key) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("config", "config_key=" + key, null) > 0;
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        mContext.deleteDatabase(DB_NAME);
        onCreate(db);
    }


    public String getBaseURL() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM config where config_key='BASE_URL'", null);
        res.moveToFirst();
        return res.getString(res.getColumnIndex("config_value"));
    }

}
