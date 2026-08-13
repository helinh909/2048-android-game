package com.example.a2048_project;

//class that deal with the basic info of the daily challenge
public class DailyChallengeActivity {
    public int targetScore;
    public long timeLimitMillis;
    public long startTimeMillis;

    public DailyChallengeActivity(int targetScore, long timeLimitMillis) {
        this.targetScore = targetScore;
        this.timeLimitMillis = timeLimitMillis;
        this.startTimeMillis = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - startTimeMillis > timeLimitMillis;
    }
}
