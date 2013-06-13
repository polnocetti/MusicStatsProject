package uy.com.polnocetti.musicstats.database;

import android.app.Activity;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by pol on 6/10/13.
 */
public class MusicStatsDatabase extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "uy.com.polnocetti.musicstats.db";
    private static String DATABASE_PATH = "/data/data/uy.com.polnocetti.musicstats/databases/";

    private SQLiteDatabase mDb;

    private final Context mContext;

    private boolean mCreateDatabase = false;
    private boolean mUpgradeDatabase = false;

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access
     * the application's assets and resources
     * @param context
     */
    public MusicStatsDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);

        mContext = context;
    }

    public void initializeDatabase() {

        if(mUpgradeDatabase) {
            mContext.deleteDatabase(DATABASE_NAME);
        }

        if(mCreateDatabase || mUpgradeDatabase) {
            try {
                copyDatabase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }

    }

    private void copyDatabase() throws IOException {
        close();

        InputStream input = mContext.getAssets().open(DATABASE_NAME);

        String outFileName = DATABASE_PATH + DATABASE_NAME;

        OutputStream output = new FileOutputStream(outFileName);

        // Transfer bytes from the input file to the output file
        byte[] buffer = new byte[1024];
        int length;
        while ((length = input.read(buffer)) > 0) {
            output.write(buffer, 0, length);
        }

        output.flush();
        output.close();
        input.close();

        getWritableDatabase().close();
    }

    public MusicStatsDatabase open() throws SQLException {
        mDb = getReadableDatabase();
        return this;
    }

    public void CleanUp() {
        mDb.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        mCreateDatabase = true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        mUpgradeDatabase = true;
    }

    public void createSong(String track, String artist, String album){

    }

}