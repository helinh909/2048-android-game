package com.example.a2048_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;


public class OptionActivity extends BaseActivity {
    private SeekBar seekBarMusic, seekBarSfx;
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "SoundSettings";
    private static final String KEY_MUSIC_VOLUME = "music_volume";
    private static final String KEY_SFX_VOLUME = "sfx_volume";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initialyse all the textview, seekbar...
        setContentView(R.layout.activity_option);
        seekBarMusic = findViewById(R.id.seekBarMusic);
        seekBarSfx = findViewById(R.id.seekBarSfx);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int musicVolume = prefs.getInt(KEY_MUSIC_VOLUME, 100);
        int sfxVolume = prefs.getInt(KEY_SFX_VOLUME, 100);

        seekBarMusic.setProgress(musicVolume);
        seekBarSfx.setProgress(sfxVolume);

        float musicVolFloat = musicVolume / 100f;
        MusicManager.setVolume(musicVolFloat);

        RadioGroup themeRadioGroup = findViewById(R.id.themeRadioGroup);
        RadioButton radioLight = findViewById(R.id.radioLight);
        RadioButton radioDark = findViewById(R.id.radioDark);
        RadioButton radioColorful = findViewById(R.id.radioColorful);

        SharedPreferences prefsTheme = getSharedPreferences("app_settings", MODE_PRIVATE);
        int savedTheme = prefsTheme.getInt("app_theme", 0);
        //get the saved theme
        switch (savedTheme) {
            case 0:
                radioLight.setChecked(true);
                break;
            case 1:
                radioDark.setChecked(true);
                break;
            case 2:
                radioColorful.setChecked(true);
                break;
        }

        //radio button for the choice of the theme
        themeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int selectedTheme = 0;
            if (checkedId == R.id.radioDark) selectedTheme = 1;
            else if (checkedId == R.id.radioColorful) selectedTheme = 2;

            prefsTheme.edit().putInt("app_theme", selectedTheme).apply();
            finish();
            startActivity(getIntent());
        });


        //save all the info and go back to the main activity
        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int newMusicVolume = seekBarMusic.getProgress();
                int newSfxVolume = seekBarSfx.getProgress();

                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt(KEY_MUSIC_VOLUME, newMusicVolume);
                editor.putInt(KEY_SFX_VOLUME, newSfxVolume);
                editor.apply();

                MusicManager.setVolume(newMusicVolume / 100f);
                SoundManager.setSfxVolume(newSfxVolume / 100f);

                Intent resultIntent = new Intent();
                resultIntent.putExtra("themeChanged", true);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}
