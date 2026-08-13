package com.example.a2048_project;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public class MyApplication extends Application {

    private int activityReferences = 0;
    private boolean isActivityChangingConfigurations = false;

    @Override
    public void onCreate() {
        super.onCreate();

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            //We start the music if there is an activity showing
            @Override
            public void onActivityStarted(Activity activity) {
                if (++activityReferences == 1 && !isActivityChangingConfigurations) {
                    if (!(activity instanceof Game2048)) {
                        MusicManager.startMenuMusic(activity);
                    }
                }
            }

            //we stop the music if no activity is shown
            @Override
            public void onActivityStopped(Activity activity) {
                isActivityChangingConfigurations = activity.isChangingConfigurations();
                if (--activityReferences == 0 && !isActivityChangingConfigurations) {
                    MusicManager.stopMenuMusic();
                }
            }

            // Les autres méthodes ne sont pas utiles ici
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}
            public void onActivityResumed(Activity activity) {}
            public void onActivityPaused(Activity activity) {}
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}
            public void onActivityDestroyed(Activity activity) {}
        });
    }
}
