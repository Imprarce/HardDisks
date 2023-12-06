package com.example.harddisks.MainPages.HelpFunc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

import java.util.ArrayList;
import java.util.List;

public class DiskDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "disk_database";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_NAME = "disks";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_IMAGE_URL = "image_url";
    private static final String COLUMN_MODEL = "model";
    private static final String COLUMN_MANUFACTURER_CODE = "manufacturer_code";
    private static final String COLUMN_CAPACITY = "capacity";
    private static final String COLUMN_SPEED_INTERFACE = "speed_interface";
    private static final String COLUMN_SPINDLE_SPEED = "spindle_speed";
    private static final String COLUMN_CACHE_SIZE = "cache_size";
    private static final String COLUMN_HAS_RAID = "has_raid";

    public DiskDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_IMAGE_URL + " TEXT, " +
                COLUMN_MODEL + " TEXT, " +
                COLUMN_MANUFACTURER_CODE + " TEXT UNIQUE, " +
                COLUMN_CAPACITY + " REAL, " +
                COLUMN_SPEED_INTERFACE + " INTEGER, " +
                COLUMN_SPINDLE_SPEED + " INTEGER, " +
                COLUMN_CACHE_SIZE + " INTEGER, " +
                COLUMN_HAS_RAID + " INTEGER)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addDisk(DiskDataClass disk, boolean fromSomeOperation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IMAGE_URL, disk.getImageUrl());
        values.put(COLUMN_MODEL, disk.getModel());
        values.put(COLUMN_MANUFACTURER_CODE, disk.getManufacturerCode());
        values.put(COLUMN_CAPACITY, disk.getCapacity());
        values.put(COLUMN_SPEED_INTERFACE, disk.getSpeedInterface());
        values.put(COLUMN_SPINDLE_SPEED, disk.getSpindleSpeed());
        values.put(COLUMN_CACHE_SIZE, disk.getCacheSize());
        values.put(COLUMN_HAS_RAID, disk.hasRaid() ? 1 : 0);

        long result = db.insert(TABLE_NAME, null, values);
        if(!fromSomeOperation) db.close();
        return result != -1;
    }

    public boolean isManufacturerCodeUnique(String manufacturerCode) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_MANUFACTURER_CODE + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{manufacturerCode});
        boolean isUnique = cursor.getCount() == 0;
        cursor.close();
        db.close();
        return isUnique;
    }

    public void loadDisksFromFirebase(List<DiskDataClass> disks) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // Очищаем текущие данные перед загрузкой
            db.delete(TABLE_NAME, null, null);

            // Вставляем новые данные из списка
            for (DiskDataClass disk : disks) {
                addDisk(disk, true);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            db.endTransaction();
            db.close();
        }
    }

    @SuppressLint("Range")
    public List<DiskDataClass> getAllDisks() {
        List<DiskDataClass> diskList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Создаем объект DiskDataClass и заполняем его данными из курсора
                    DiskDataClass disk = new DiskDataClass();
                    disk.setImageUrl(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URL)));
                    disk.setModel(cursor.getString(cursor.getColumnIndex(COLUMN_MODEL)));
                    disk.setManufacturerCode(cursor.getString(cursor.getColumnIndex(COLUMN_MANUFACTURER_CODE)));
                    disk.setSpeedInterface(cursor.getInt(cursor.getColumnIndex(COLUMN_SPEED_INTERFACE)));
                    disk.setCapacity(cursor.getInt(cursor.getColumnIndex(COLUMN_CAPACITY)));
                    disk.setSpindleSpeed(cursor.getInt(cursor.getColumnIndex(COLUMN_SPINDLE_SPEED)));
                    disk.setCacheSize(cursor.getInt(cursor.getColumnIndex(COLUMN_CACHE_SIZE)));
                    disk.setHasRaid(cursor.getInt(cursor.getColumnIndex(COLUMN_HAS_RAID)) == 1);

                    // Добавляем диск в список
                    diskList.add(disk);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return diskList;
    }

    @SuppressLint("Range")
    public DiskDataClass getDiskByManufacturerCode(String manufacturerCode) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        DiskDataClass diskFavorite = null;

        try {
            String selection = COLUMN_MANUFACTURER_CODE + "=?";
            String[] selectionArgs = {manufacturerCode};

            cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                diskFavorite = new DiskDataClass();
                diskFavorite.setImageUrl(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URL)));
                diskFavorite.setModel(cursor.getString(cursor.getColumnIndex(COLUMN_MODEL)));
                diskFavorite.setManufacturerCode(cursor.getString(cursor.getColumnIndex(COLUMN_MANUFACTURER_CODE)));
                diskFavorite.setSpeedInterface(cursor.getInt(cursor.getColumnIndex(COLUMN_SPEED_INTERFACE)));
                diskFavorite.setCapacity(cursor.getInt(cursor.getColumnIndex(COLUMN_CAPACITY)));
                diskFavorite.setSpindleSpeed(cursor.getInt(cursor.getColumnIndex(COLUMN_SPINDLE_SPEED)));
                diskFavorite.setCacheSize(cursor.getInt(cursor.getColumnIndex(COLUMN_CACHE_SIZE)));
                diskFavorite.setHasRaid(cursor.getInt(cursor.getColumnIndex(COLUMN_HAS_RAID)) == 1);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return diskFavorite;
    }
}
