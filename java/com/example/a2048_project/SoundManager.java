package com.example.a2048_project;

import android.content.Context;
import android.media.SoundPool;

public class SoundManager {
    private static SoundPool soundPool;
    private static int soundMove, soundMerge;
    private static float sfxVolume = 1.0f;

    //initialise the sound
    public static void init(Context context) {
        soundPool = new SoundPool.Builder().setMaxStreams(2).build();

        soundMove = soundPool.load(context, R.raw.fast_woosh, 1);
        soundMerge = soundPool.load(context, R.raw.pop_fusion, 1);
    }

    //function to play the move sound
    public static void playMoveSound() {
        if (soundPool != null) {
            soundPool.play(soundMove, sfxVolume, sfxVolume, 0, 0, 1);
        }
    }

    //function to play the merge sound
    public static void playMergeSound() {
        if (soundPool != null) {
            soundPool.play(soundMerge, sfxVolume, sfxVolume, 0, 0, 1);
        }
    }

    public static void setSfxVolume(float volume) {
        sfxVolume = volume;
    }

    //release the sound
    public static void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}
