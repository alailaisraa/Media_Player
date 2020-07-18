package com.example.simplemusicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    String[] items;
    boolean doubleclick = false;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listofsongs);
        runcheck();
    }

    public void onBackPressed(){
        if (doubleclick){
            android.os.Process.killProcess(Process.myPid());
        }
        doubleclick = true;
        Toast.makeText(this, "Press back again to Exit", Toast.LENGTH_SHORT).show();
    }

    private void runcheck ( ) {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted (PermissionGrantedResponse permissionGrantedResponse) {
                        display();
                    }

                    @Override
                    public void onPermissionDenied (PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown (PermissionRequest permission, PermissionToken token) {
                        token.cancelPermissionRequest();
                    }
                }).check();

    }
    final ArrayList<File> findsongs (File file){
         ArrayList<File> arrayList = new ArrayList<>();
         File[] files = file.listFiles();

         for (File singlefile: files){
             if (singlefile.isDirectory() & !singlefile.isHidden()){
                 arrayList.addAll(findsongs(singlefile));
                 Collections.sort(arrayList);
             }else if (singlefile.getName().endsWith("mp3") | singlefile.getName().endsWith(".wav")){
                 arrayList.add(singlefile);
                 Collections.sort(arrayList);
             }
         }
         return arrayList;
    }
    private void display () {
        final ArrayList<File> mysongs = findsongs(Environment.getExternalStorageDirectory());
        items = new String[mysongs.size()];
        for (int i = 0 ; i < mysongs.size(); i++){
            items[i] = mysongs.get(i).getName().replace(".mp3","").replace(".wav","");
            if (items.length > 0){
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
                listView.setAdapter(arrayAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
                        String songname = listView.getItemAtPosition(position).toString();
                        startActivity(new Intent(getApplicationContext(), PlayerActivity.class)
                        .putExtra("songs", mysongs)
                        .putExtra("songName", songname)
                        .putExtra("pos", position));
                    }
                });
            }else {
                Toast.makeText(this, "No files found", Toast.LENGTH_SHORT).show();
            }
        }
    }
}