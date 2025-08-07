package com.example.revervoxmod.taskscheduler;

import com.example.revervoxmod.RevervoxMod;

public class TaskScheduler {
    private Task nextTask;
    private long time;

    public TaskScheduler(){
        nextTask = null;
        time = 0;
    }

    public void tick(){
        time++;
        if(nextTask != null){
            Task cur = nextTask;
            while(cur != null && cur.getTime() <= time){
                cur.run();
                cur = cur.getNext();
            }
            nextTask = cur;
        }
    }

    public long getTime(){ return time; }

    public void schedule(Runnable method, long ticks){
        long timeToRun = time + ticks;
        Task toInsert = new Task(timeToRun, method);
        Task last = null;
        Task cur = nextTask;
        if(cur != null){
            while(cur.getTime() <= timeToRun){
                Task next = cur.getNext();
                if(next == null){
                    cur.setNext(toInsert);
                    return;
                } else {
                    last = cur;
                    cur = cur.getNext();
                }
            }
            if(last == null){
                nextTask = toInsert;
                nextTask.setNext(cur);
            } else {
                toInsert.setNext(cur);
                last.setNext(toInsert);
            }
        } else {
            nextTask = toInsert;
        }
    }

    public void scheduleAt(Runnable method, long when){
        schedule(method, when - time);
    }

    public void debug(){
        Task cur = nextTask;
        while(cur != null){
            RevervoxMod.LOGGER.debug("Executed at: {}", cur.getTime());
            cur.run();
            cur = cur.getNext();
        }
    }
}
