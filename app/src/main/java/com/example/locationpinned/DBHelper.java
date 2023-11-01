package com.example.locationpinned;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "location.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "Locations";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "address_name";
    private static final String COLUMN_LAT = "address_latitude";
    private static final String COLUMN_LONG = "address_longitude";


    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Creating database with columns for address, latitude and longitude
        String query = "CREATE TABLE " + TABLE_NAME +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_LAT + " REAL, " +
                COLUMN_LONG + " REAL);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop previous database
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    //Receives new address object from Add Location fragment and inserts parameters into database
    public void createAddress(AddressObject address) {

        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_TITLE, address.getLocationName());
        contentValues.put(COLUMN_LAT, address.getLatitude());
        contentValues.put(COLUMN_LONG, address.getLongitude());

        db.insert(TABLE_NAME, null, contentValues);
        db.close();

    }

    //Populates main page with addresses from database
    public List<AddressObject> getAllAddresses() {

        List<AddressObject> addressObjectList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                AddressObject address = new AddressObject();
                address.setAddressID(cursor.getInt(0));
                address.setLocationName(cursor.getString(1));
                address.setLatitude(cursor.getDouble(2));
                address.setLongitude(cursor.getDouble(3));

                addressObjectList.add(address);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return addressObjectList;

    }

    //Queries database for searching specific addresses
    public List<AddressObject> getAddressByName(String addressQuery) {
        List<AddressObject> addressObjectList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TITLE + " LIKE ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{"%" + addressQuery + "%"});

        if (cursor.moveToFirst()) {
            do {
                AddressObject address = new AddressObject();
                address.setAddressID(cursor.getInt(0));
                address.setLocationName(cursor.getString(1));
                address.setLatitude(cursor.getDouble(2));
                address.setLongitude(cursor.getDouble(3));

                addressObjectList.add(address);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return addressObjectList;
    }

    //Modifies database for editing existing addresses
    public void editAddress(AddressObject address) {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TITLE, address.getLocationName());
        contentValues.put(COLUMN_LAT, address.getLatitude());
        contentValues.put(COLUMN_LONG, address.getLongitude());

        db.update(TABLE_NAME, contentValues, "_id = ?", new String[]{String.valueOf(address.getAddressID())});

    }

    //Deletes address by id from database
    void deleteAddress(int addressID) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(TABLE_NAME, "_id = ?", new String[]{Integer.toString(addressID)});

    }

    /*Drops current database contents and reloads coordinates saved in res/raw/coordinates.txt,
     * then performs reverse geocoding and adds to the database. */
    public void reloadDatabase(Context context) throws IOException {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME + ";");

        InputStream inputStream = context.getResources().openRawResource(R.raw.coordinates);

        //Reading coordinates from coordinate text file
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            double latitude = Double.parseDouble(parts[0]);
            double longitude = Double.parseDouble(parts[1]);

            GeocoderMaker geocoderMaker = new GeocoderMaker(latitude, longitude, context);

            ContentValues values = new ContentValues();
            values.put(COLUMN_TITLE, geocoderMaker.geocodedAddress());
            values.put(COLUMN_LAT, latitude);
            values.put(COLUMN_LONG, longitude);
            db.insert(TABLE_NAME, null, values);
        }

        db.close();

    }


}
