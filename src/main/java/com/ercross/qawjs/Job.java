package com.ercross.qawjs;

//In the context of this app, a job is a call ready for transcription
public class Job {

    private final double callId;
    private final float callDuration;
    private final String callUrl;

    public Job(double callId, float callDuration, String callUrl) {
        this.callId = callId;
        this.callDuration = callDuration;
        this.callUrl = callUrl;
    }

    public float getCallDuration() {
        return callDuration;
    }

    public double getCallId () {return  callId;}

    public String getCallUrl() {
        return callUrl;
    }

    @Override
    public String toString () {
        return "CallId: " + callId + "\nDuration: " + callDuration;
    }
}
