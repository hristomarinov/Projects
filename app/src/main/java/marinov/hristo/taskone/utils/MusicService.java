package marinov.hristo.taskone.utils;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import marinov.hristo.taskone.R;

/**
 * @author HristoMarinov (christo_marinov@abv.bg).
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private int currentSongPos;
    private String songTitle = "";
    private MediaPlayer player;
    private ArrayList<Song> songs;
    private final IBinder musicBinder = new MusicBinder();
    private static final int NOTIFY_ID = 1;

    public void onCreate() {
        super.onCreate();

        currentSongPos = 0;
        player = new MediaPlayer();

        initMusicPlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }

    public void initMusicPlayer() {
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setList(ArrayList<Song> songList) {
        songs = songList;
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.release();

        return false;
    }

    public void playSong() {
        player.reset();

        Song playSong = songs.get(currentSongPos);
        songTitle = String.format("%s - %s", playSong.getArtist(), playSong.getTitle());

        String playSongPath = playSong.getPath();
        String uri = "android.resource://" + getPackageName() + "/raw/" + playSongPath;

        try {
            player.setDataSource(getApplicationContext(), Uri.parse(uri));
            player.prepareAsync();
        } catch (IOException e) {
            Log.w(this.getClass().getName(), e.getMessage());
        }
    }

    public void setSong(int songIndex) {
        currentSongPos = songIndex;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (player.getCurrentPosition() > 0) {
            mp.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();

        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_play_circle_outline)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle(getString(R.string.playing))
                .setContentText(songTitle);
        Notification not = builder.build();
        startForeground(NOTIFY_ID, not);
    }

    public int getPosition() {
        return player.getCurrentPosition();
    }

    public int getDuration() {
        return player.getDuration();
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public void pausePlayer() {
        player.pause();
    }

    public void seek(int pos) {
        player.seekTo(pos);
    }

    public void start() {
        player.start();
    }

    public void playPrev() {
        currentSongPos--;
        if (currentSongPos < 0) currentSongPos = songs.size() - 1;
        playSong();
    }

    public void playNext() {
        currentSongPos++;
        if (currentSongPos >= songs.size()) currentSongPos = 0;
        playSong();
    }
}