package com.example.revervoxmod.taskscheduler;

import com.example.revervoxmod.RevervoxMod;

import java.util.LinkedList;
import java.util.List;

public class TaskScheduler {
    private final List<Task> tasks;
    private long time;

    public TaskScheduler(){
        tasks = new LinkedList<>();
        time = 0;
    }

    public void tick(){
        time++;
        for(Task task : tasks){
            if(task.checkRun(time)){
                tasks.remove(task);
                RevervoxMod.LOGGER.info("Ran task at {}", time);
            }
        }
    }

    public long getTime(){ return time; }

    public void schedule(Runnable method, long ticks){
        tasks.add(new Task(time + ticks, method));
        RevervoxMod.LOGGER.info("Scheduled task for {}", time + ticks);
    }

    public void scheduleAt(Runnable method, long when){
        schedule(method, when - time);
    }
}
