package com.aviatorex.playnsor;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Playlist extends ListActivity {
    public ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_playlist);
        ArrayList<HashMap<String, String>> songsListData = new ArrayList<HashMap<String, String>>();
        SongManager plm = new SongManager();
        this.songsList = plm.getPlayList();
        for (int i = 0; i < songsList.size(); i++) {
            // creating new HashMap
            HashMap<String, String> song = songsList.get(i);

            // adding HashList to ArrayList
            songsListData.add(song);
        }

        ListAdapter adapter = new SimpleAdapter(this, songsListData, R.layout.list_item, new String[] {"Title"}, new int[] {R.id.SongTitle});
        setListAdapter(adapter);
        int currentIndex = (int) getIntent().getExtras().get("Index");

        ListView lv = getListView();
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setSelection(currentIndex);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("Index", position);
                setResult(100, i);
                finish();
            }
        });
    }
}
