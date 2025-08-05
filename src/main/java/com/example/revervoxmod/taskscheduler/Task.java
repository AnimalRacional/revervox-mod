package com.example.revervoxmod.taskscheduler;

import com.example.revervoxmod.RevervoxMod;

public class Task {
    private final long whenRun;
    private final Runnable method;
    public Task(long whenRun, Runnable method){
        this.whenRun = whenRun;
        this.method = method;
    }

    public boolean checkRun(long time){
        RevervoxMod.LOGGER.info("checking: {} needs {}", time, whenRun);
        if(time >= whenRun) {
            method.run();
            return true;
        }
        return false;
    }
}
