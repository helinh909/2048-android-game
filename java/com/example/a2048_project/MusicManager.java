package com.example.a2048_project;

import android.content.Context;
import android.media.MediaPlayer;

//Manager of the music
public class MusicManager {
    private static MediaPlayer mediaPlayer; //instance of mediaPlayer
    private static float volume = 1.0f;

    //start the music of the menu
    public static void startMenuMusic(Context context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context.getApplicationContext(), R.raw.menu_music);
            mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(volume, volume);
        }
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    //stop the menu music
    public static void stopMenuMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    //start the game music
    public static void startGameMusic(Context context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context.getApplicationContext(), R.raw.game_music);
            mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(volume, volume);
        }
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    //stop the game Music
    public static void stopMusic() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    //to know if a music is already playing
    public static boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public static void setVolume(float vol) {
        volume = vol;
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume, volume);
        }
    }

    public static float getVolume() {
        return volume;
    }
}
