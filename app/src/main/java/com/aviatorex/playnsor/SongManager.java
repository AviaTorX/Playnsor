package com.aviatorex.playnsor;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by avi on 1/8/16.
 */
public class SongManager {
    private ArrayList<HashMap<String, String>> SongList = new ArrayList<HashMap<String, String>>();
    public SongManager(){

    }
    public ArrayList<HashMap<String, String>> getPlayList(){
        String InternalCard = Environment.getExternalStorageDirectory().toString();
        File file = new File(InternalCard);
        File[] files = file.listFiles();
        for(File f : files){

            if(f.isFile()){
                HashMap<String, String> temp = new HashMap<String, String>();
                String name = f.getName();
                if(name.endsWith(".mp3") || name.endsWith(".MP3")){
                    temp.put("Title", f.getName());
                    temp.put("Path", f.getPath());
                    SongList.add(temp);
                    //Toast.makeText(this, f.getName(), Toast.LENGTH_SHORT).show();
                }
            }
            if(f.isDirectory()){
                if(f.getName().equals("Music")){
                    File[] internalMusic = f.listFiles();
                    for(File song : internalMusic){
                        HashMap<String, String> temp = new HashMap<String, String>();
                        String name = song.getName();
                        if(name.endsWith(".mp3") || name.endsWith(".MP3")){
                            temp.put("Title", song.getName());
                            temp.put("Path", song.getPath());
                            SongList.add(temp);
                            //Toast.makeText(this, song.getName(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

        }
        String ExternalCard = "/storage/sdcard1/";
        File ExternalFile = new File(ExternalCard);
        File[] Extern = ExternalFile.listFiles();
        for(File f : Extern){
            if(f.isFile()){
                HashMap<String, String> temp = new HashMap<String, String>();
                String name = f.getName();
                if(name.endsWith(".mp3") || name.endsWith(".MP3")){
                    temp.put("Title", f.getName());
                    temp.put("Path", f.getPath());
                    SongList.add(temp);
                    //Toast.makeText(this, f.getName(), Toast.LENGTH_SHORT).show();
                }
            }
            if(f.isDirectory()){
                if(f.getName().equals("Music")){
                    File[] internalMusic = f.listFiles();
                    for(File song : internalMusic){
                        HashMap<String, String> temp = new HashMap<String, String>();
                        String name = song.getName();
                        if(name.endsWith(".mp3") || name.endsWith(".MP3")){
                            temp.put("Title", song.getName());
                            temp.put("Path", song.getPath());
                            SongList.add(temp);
                            //Toast.makeText(this, song.getName(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }
        return SongList;
    }
}
