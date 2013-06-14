package uy.com.polnocetti.musicstats.database;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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


    private static final String DATABASE_NAME = "uy.com.polnocetti.musicstats.sqlite";
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

    public void newSong(String artist, String album, String track) {

        int [] result = songExists(artist, album, track);

        int songId = result[0];
        int artistId = result[1];
        int albumId = result[2];

        if (songId != 0){
            updateSong(songId, artistId, albumId);
        }else{
            createSong(artist, album, track);
        }

    }

    private void updateSong(int songId, int artistId, int albumId) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("songcount", getSongCount(songId) + 1);
        db.update("song", cv, "songid = ?", new String[] { Integer.toString(songId) });

        cv = new ContentValues();
        cv.put("artistsongcount", getSongCount(songId) + 1);
        db.update("artist", cv, "artistid = ?", new String[] { Integer.toString(songId) });

    }

    private int getSongCount(int songId) {

        SQLiteDatabase db = getReadableDatabase();
        Cursor result = db.rawQuery("select songcount from song where songid = " + Integer.toString(songId), null);

        if (result.moveToFirst()) {
            return result.getInt(0);
        }
        result.close();

        return 0;

    }

    private void createSong(String artist, String album, String track) {

        int artistId, albumId;

        if (getArtist(artist) == 0){
            artistId = createArtist(artist);
        }else{
            artistId = getArtist(artist);
        }

        if(getAlbum(album, artistId) == 0){
            albumId = createAlbum(album, artistId);
        }else{
            albumId = getAlbum(album, artistId);
        }

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("songname", track);
        cv.put("songartistid", artistId);
        cv.put("songalbumid", albumId);
        cv.put("songcount", 1);
        db.insert("song", null, cv);

    }

    private int createAlbum(String album, int artistId) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("albumname", album);
        cv.put("albumartistid", artistId);
        db.insert("album", null, cv);

        return getAlbum(album, artistId);

    }

    private int getAlbum(String album, int artistId) {

        SQLiteDatabase db = getReadableDatabase();
        Cursor result = db.rawQuery("select albumid from album where albumname like '" + album + "' and albumartistid = " + Integer.toString(artistId), null);

        if (result.moveToFirst()) {
            return result.getInt(0);
        }
        result.close();

        return 0;

    }

    private int createArtist(String artist) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("artistname", artist);
        db.insert("artist", null, cv);

        return getArtist(artist);
    }

    private int getArtist(String artist) {

        SQLiteDatabase db = getReadableDatabase();
        Cursor result = db.rawQuery("select artistid from artist where artistname like '" + artist + "'", null);

        if (result.moveToFirst()) {
            return result.getInt(0);
        }
        result.close();

        return 0;

    }

    private int[] songExists(String artist, String album, String track) {

        int[] resultado = new int[3];

        SQLiteDatabase db = getReadableDatabase();
        Cursor result = db.rawQuery(
                "select songid, artistid, albumid " +
                        "from song, artist, album " +
                        "where songalbumid = albumid " +
                        "and songartistid = artistid " +
                        "and songname like '" + track + "' " +
                        "and albumname like '" + album + "' " +
                        "and artistname like '" + artist + "'", null);

        if (result.moveToFirst()) {
            resultado[0] = result.getInt(0);
            resultado[1] = result.getInt(1);
            resultado[2] = result.getInt(2);
        }
        result.close();

        return resultado;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        mUpgradeDatabase = true;
    }


}