package com.z3pipe.z3location.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Environment;

import com.z3pipe.z3location.model.Position;
import com.z3pipe.z3location.util.Constants;

import java.io.File;

/**
 * @author zhengzhuanzi
 * @link https://www.z3pipe.com
 * @date 2019-04-10
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "userposition.db";
    public static final String DATABASE_PATH = Environment.getExternalStorageDirectory().getPath()+"/SOP/db";

    public interface DatabaseHandler<T> {
        /**
         * 数据操作完成
         *
         * @param success
         * @param result
         */
        void onComplete(boolean success, T result);
    }

    private static abstract class DatabaseAsyncTask<T> extends AsyncTask<Void, Void, T> {

        private DatabaseHandler<T> handler;
        private RuntimeException error;

        public DatabaseAsyncTask(DatabaseHandler<T> handler) {
            this.handler = handler;
        }

        @Override
        protected T doInBackground(Void... params) {
            try {
                return executeMethod();
            } catch (RuntimeException error) {
                this.error = error;
                return null;
            }
        }

        protected abstract T executeMethod();

        @Override
        protected void onPostExecute(T result) {
            handler.onComplete(error == null, result);
        }
    }

    private SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_PATH +"/"+DATABASE_NAME, null, DATABASE_VERSION);
        File file = new File(DATABASE_PATH);
        if(!file.exists()){
            file.mkdirs();
        }
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE position (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "X REAL," +
                "Y REAL," +
                "LON REAL," +
                "LAT REAL," +
                "ACCURACY REAL," +
                "BATTERY TEXT," +
                "USERID INTEGER," +
                "SPEED REAL," +
                "TIME TEXT," +
                "UPLOAD_TIME INTEGER," +
                "STATUS INTEGER," +
                "POINT_CHECK_FLAG INTEGER," +
                "LINE_CHECK_FLAG INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS position;");
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS position;");
        onCreate(db);
    }

    public void insertPosition(Position position) {
        ContentValues values = new ContentValues();
        values.put("X",position.getX());
        values.put("Y",position.getY());
        values.put("LON", position.getLon());
        values.put("LAT", position.getLat());
        values.put("ACCURACY", position.getAccuracy());
        values.put("BATTERY", position.getBattery());
        values.put("USERID", position.getUserId());
        values.put("UPLOAD_TIME", position.getTime());
        values.put("SPEED", position.getSpeed());
        values.put("STATUS", position.getState());
        db.insertOrThrow("position", null, values);
    }

    public void insertPositionAsync(final Position position, DatabaseHandler<Void> handler) {
        new DatabaseAsyncTask<Void>(handler) {
            @Override
            protected Void executeMethod() {
                insertPosition(position);
                return null;
            }
        }.execute();
    }

    public Position selectPosition() {
        Position position = new Position();

        Cursor cursor = db.rawQuery("SELECT * FROM position WHERE STATUS = '0' ORDER BY _id  LIMIT 1", null);
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                position.setId(cursor.getLong(cursor.getColumnIndex("_id")));
                //position.setDeviceId(cursor.getString(cursor.getColumnIndex("deviceId")));
                position.setTime(cursor.getLong(cursor.getColumnIndex("UPLOAD_TIME")));
                position.setLat(cursor.getDouble(cursor.getColumnIndex("LAT")));
                position.setLon(cursor.getDouble(cursor.getColumnIndex("LON")));
                position.setX(cursor.getDouble(cursor.getColumnIndex("X")));
                position.setY(cursor.getDouble(cursor.getColumnIndex("Y")));
                position.setSpeed(cursor.getDouble(cursor.getColumnIndex("SPEED")));
                position.setAccuracy(cursor.getDouble(cursor.getColumnIndex("ACCURACY")));
                position.setBattery(cursor.getString(cursor.getColumnIndex("BATTERY")));
                position.setUserId(cursor.getInt(cursor.getColumnIndex("USERID"))+"");
                position.setState(cursor.getInt(cursor.getColumnIndex("STATUS")));
                position.setUserName(Constants.USER_NAME);
                position.setTrueName(Constants.TRUE_NAME);
            } else {
                return null;
            }
        } finally {
            if (cursor != null){
                cursor.close();
            }
        }

        return position;
    }

    public void selectPositionAsync(DatabaseHandler<Position> handler) {
        new DatabaseAsyncTask<Position>(handler) {
            @Override
            protected Position executeMethod() {
                return selectPosition();
            }
        }.execute();
    }

    public void deletePosition(long id) {
        if (db.delete("position", "_id = ?", new String[]{String.valueOf(id)}) != 1) {
            throw new SQLException();
        }
    }

    public void updatePosition(long id) {
        ContentValues values = new ContentValues();
        values.put("STATUS",1);
        if (db.update("position", values,"_id = ?", new String[]{String.valueOf(id)}) != 1) {
            throw new SQLException();
        }
    }

    public void deletePositionAsync(final long id, DatabaseHandler<Void> handler) {
        new DatabaseAsyncTask<Void>(handler) {
            @Override
            protected Void executeMethod() {
                deletePosition(id);
                return null;
            }
        }.execute();
    }

    public void updatePositionAsync(final long id, DatabaseHandler<Void> handler) {
        new DatabaseAsyncTask<Void>(handler) {
            @Override
            protected Void executeMethod() {
                updatePosition(id);
                return null;
            }
        }.execute();
    }

}
