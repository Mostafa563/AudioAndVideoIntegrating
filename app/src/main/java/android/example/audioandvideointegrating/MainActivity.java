package android.example.audioandvideointegrating;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener {

    //UI components
    private VideoView myVideoView;
    private Button btnPlayVideo, btnPlayMusic, btnPauseMusic, btnStartStop;
    private MediaController myMC;
    private MediaPlayer myMP;
    private SeekBar seekBarVolume, seekBarMove;

    private AudioManager myAM;

    private Timer myTimer;   //this object (THREAD) is for the moving bar of the song

    private boolean isStart = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myVideoView = findViewById(R.id.myVideoView);
        btnPlayVideo = findViewById(R.id.btnPlayVideo);
        btnPlayMusic = findViewById(R.id.btnPlayMusic);
        btnPauseMusic = findViewById(R.id.btnPauseMusic);
        seekBarVolume = findViewById(R.id.seekBarVolume);
        seekBarMove = findViewById(R.id.seekBarMove);

        btnStartStop = findViewById(R.id.btnStartStop);


        btnPlayVideo.setOnClickListener(MainActivity.this);
        btnPlayMusic.setOnClickListener(MainActivity.this);
        btnPauseMusic.setOnClickListener(MainActivity.this);

        btnStartStop.setOnClickListener(MainActivity.this);



        myMC = new MediaController(MainActivity.this);

        myMP = MediaPlayer.create(this,R.raw.mymusic);

        myAM = ( AudioManager ) getSystemService(AUDIO_SERVICE);

        int maximumVolumeOfUserDevice = myAM.
                getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        int currentVolumeOfUserDevice = myAM.
                getStreamVolume(AudioManager.STREAM_MUSIC);

//        int minimumVolumeOfUserDevice = myAM.
//                getStreamMinVolume(AudioManager.STREAM_MUSIC);

        seekBarVolume.setMax(maximumVolumeOfUserDevice);
        seekBarVolume.setProgress(currentVolumeOfUserDevice);

        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if(fromUser){

//                    Toast.makeText(MainActivity.this, Integer.toString(progress), Toast.LENGTH_SHORT).show();
                    myAM.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);


                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarMove.setOnSeekBarChangeListener(this);
        seekBarMove.setMax(myMP.getDuration());

        myMP.setOnCompletionListener(this);


    }

    @Override
    public void onClick(View btnView) {

        switch (btnView.getId()) {
            case R.id.btnPlayVideo :
                 Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.myvid);
                 myVideoView.setVideoURI(videoUri);
                 myVideoView.setMediaController(myMC);   //set the media controller
                 myMC.setAnchorView(myVideoView);        //anchor the media controller to the video space
                 myVideoView.start();


                 break;

            case R.id.btnPlayMusic :
                 myMP.start();
                 myTimer = new Timer();
                 myTimer.scheduleAtFixedRate(new TimerTask() {
                     @Override
                     public void run() {

                         seekBarMove.setProgress(myMP.getCurrentPosition());

                     }
                 }, 0, 1000);

                 isStart = false;
                 break;

            case R.id.btnPauseMusic :

                 myMP.pause();
                 myTimer.cancel();

                 isStart = true;
                 break;

            case R.id.btnStartStop :
                if(isStart){
                    myMP.start();
                    myTimer = new Timer();
                    myTimer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {

                            seekBarMove.setProgress(myMP.getCurrentPosition());

                        }
                    }, 0, 1000);
                    isStart = !isStart;

                }else{
                    myMP.pause();
                    myTimer.cancel();
                    isStart = !isStart;
                }
                break;



        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if (fromUser) {

//            Toast.makeText(this, progress+"" ,Toast.LENGTH_SHORT).show();
            myMP.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

        myMP.pause();   //to pause the sound when seeking
        if(isStart){
            myMP.start();
        }

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        myMP.start();  //to resume the sound after release
        if(isStart){
            myMP.pause();
        }

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        myTimer.cancel();
        Toast.makeText(this, "Music is Ended !", Toast.LENGTH_SHORT).show();

    }
}
