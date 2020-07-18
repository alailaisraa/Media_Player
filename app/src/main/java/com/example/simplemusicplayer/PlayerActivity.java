package com.example.simplemusicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    TextView textView;
    ImageButton btn_pause, btn_next, btn_prev;
    SeekBar seekBar;
    static MediaPlayer mediaPlayer;
    int position;
    String songName;
    ArrayList<File> songslist;
    Thread updateSeekBar;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        textView = findViewById(R.id.songLabel);
        btn_prev =  findViewById(R.id.prev);
        btn_pause =  findViewById(R.id.pause);
        btn_next =  findViewById(R.id.btn_next);
        seekBar = findViewById(R.id.seekbarview);
        updateSeekBar = new Thread(){
            public void run(){
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;

                while (currentPosition < totalDuration){
                    try{
                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        };
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        songslist = (ArrayList)bundle.getParcelableArrayList("songs");
        songName = i.getStringExtra("songname");
        textView.setText(songName);
        textView.setSelected(true);
        position = i.getIntExtra("pos", 0);

        Uri u = Uri.parse(songslist.get(position).toString());

        mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
        mediaPlayer.start();

        seekBar.setMax(mediaPlayer.getDuration());
        updateSeekBar.start();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged (SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch (SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch (SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                seekBar.setMax(mediaPlayer.getDuration());
                if(mediaPlayer.isPlaying()){
                    btn_pause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                    mediaPlayer.pause();
                }else {
                    btn_pause.setImageResource(R.drawable.ic_pause);
                    mediaPlayer.start();
                }
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                mediaPlayer.start();
                mediaPlayer.release();
                position = (position+1) & songslist.size();
                Uri u = Uri.parse(songslist.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
                songName = songslist.get(position).getName().toString();
                textView.setText(songName);
                mediaPlayer.start();
            }
        });

        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position-1)< 0) ? (songslist.size()-1) : (position-1);
                Uri u = Uri.parse(songslist.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
                songName = songslist.get(position).getName().toString();
                textView.setText(songName);
                mediaPlayer.start();
            }
        });

    }
}