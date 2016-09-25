package marinov.hristo.taskone.main;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;

import java.util.ArrayList;

import marinov.hristo.taskone.R;
import marinov.hristo.taskone.utils.ExampleDecoration;
import marinov.hristo.taskone.utils.IRecycleViewSelectedItem;
import marinov.hristo.taskone.utils.MusicService;
import marinov.hristo.taskone.utils.Song;
import marinov.hristo.taskone.utils.SpotifyAdapter;

/**
 * @author HristoMarinov (christo_marinov@abv.bg).
 */
public class MainActivity extends AppCompatActivity implements IRecycleViewSelectedItem, MediaPlayerControl {

    private Intent playIntent;
    private MediaController controller;
    private ArrayList<Song> songList;
    private boolean musicBound = false;
    private boolean paused = false, playbackPaused = false;
    private MusicService musicService;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    // Dummy data
    String[] artistList = {"Eagles", "Faithless", "Lil Wayne", "DJ Snake, AlunaGeorge"};
    String[] titleList = {"Hotel California", "Insomnia", "Lollipop", "You Know You Like It"};
    String[] pathList = {"hotel_california", "insomnia", "lollipop", "you_know_you_like_it"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewSpotify);

        songList = new ArrayList<Song>();
        mLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new SpotifyAdapter(songList, this);
        mRecyclerView.setAdapter(mAdapter);

        ExampleDecoration recycleViewCustomDecoration = new ExampleDecoration(this);
        mRecyclerView.addItemDecoration(recycleViewCustomDecoration);

        loadList();
        setController();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (paused) {
            setController();
            paused = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        controller.hide();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        onServiceStopped();
    }

    /**
     * Load list with dummy information
     */
    private void loadList() {
        for (int i = 0; i < titleList.length; i++) {
            Song song = new Song(i + 1, titleList[i], artistList[i], pathList[i]);
            songList.add(song);
        }
    }

    @Override
    public void onItemSelected(int position) {
        if (controller.isShowing())
            controller.hide();

        musicService.setSong(position);
        musicService.playSong();
        if (playbackPaused) {
            setController();
            playbackPaused = false;
        }
        controller.show(0);
    }

    private void setController() {
        controller = new MediaController(this);

        //set Previous and Next button listeners
        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });

        controller.setMediaPlayer(this);
        controller.setAnchorView(findViewById(R.id.recyclerViewSpotify));
        controller.setEnabled(true);
    }

    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;

            musicService = binder.getService();
            musicService.setList(songList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    public void onServiceStopped() {
        if (playIntent == null)
            playIntent = new Intent(this, MusicService.class);

        musicService = null;
        unbindService(musicConnection);
        stopService(playIntent);
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (musicService != null && musicBound && musicService.isPlaying())
            return musicService.getPosition();
        else return 0;
    }

    @Override
    public int getDuration() {
        if (musicService != null && musicBound && musicService.isPlaying())
            return musicService.getDuration();
        else return 0;
    }

    @Override
    public boolean isPlaying() {
        return musicService != null && musicBound && musicService.isPlaying();
    }

    @Override
    public void pause() {
        playbackPaused = true;
        musicService.pausePlayer();
    }

    @Override
    public void seekTo(int pos) {
        musicService.seek(pos);
    }

    @Override
    public void start() {
        musicService.start();
    }

    private void playNext() {
        musicService.playNext();
        if (playbackPaused) {
            setController();
            playbackPaused = false;
        }
        controller.show(0);
    }

    private void playPrev() {
        musicService.playPrev();
        if (playbackPaused) {
            setController();
            playbackPaused = false;
        }
        controller.show(0);
    }
}
