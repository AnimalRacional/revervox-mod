package com.example.revervoxmod.taskscheduler;

public class Task {
    private final long whenRun;
    private final Runnable method;
    public Task(long whenRun, Runnable method){
        this.whenRun = whenRun;
        this.method = method;
    }

    public boolean checkRun(long time){
        if(time >= whenRun) {
            method.run();
            return true;
        }
        return false;
    }
}
