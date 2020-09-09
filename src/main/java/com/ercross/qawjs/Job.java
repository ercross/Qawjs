package com.ercross.qawjs;

//In the context of this app, a job is a call ready for transcription
public class Job {

    private final long callId;
    private final float callDuration;
    private final String callUrl;

    public Job(long callId, float callDuration, String callUrl) {
        this.callId = callId;
        this.callDuration = callDuration;
        this.callUrl = callUrl;
    }

    public float getCallDuration() {
        return callDuration;
    }

    public long getCallId () {return  callId;}

    public String getCallUrl() {
        return callUrl;
    }

    @Override
    public String toString () {
        return "CallId: " + callId + "  Duration: " + callDuration + " minutes";
    }
}
