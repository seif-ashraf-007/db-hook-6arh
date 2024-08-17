package me.seif.dB_Hook_PL.models;

public class BotTaskData {

    private String event;
    private String username;
    private String args1;
    private String args2;

    public BotTaskData( String event, String username, String args1, String args2) {

        this.event = event;
        this.username = username;
        this.args1 = args1;
        this.args2 = args2;
    }

    public String getEvent() {
        return event;
    }

    public String getUsername() {
        return username;
    }

    public String getArgs1() {
        return args1;
    }

    public String getArgs2() {
        return args2;
    }

    private void test() {

    }
}