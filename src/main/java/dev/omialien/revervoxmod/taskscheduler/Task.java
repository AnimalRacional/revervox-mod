package dev.omialien.revervoxmod.taskscheduler;

public class Task {
    private final long whenRun;
    private final Runnable method;
    private Task nextTask;
    public Task(long whenRun, Runnable method){
        this.whenRun = whenRun;
        this.method = method;
        nextTask = null;
    }

    public Task(long whenRun, Runnable method, Task next){
        this.whenRun = whenRun;
        this.method = method;
        nextTask = next;
    }

    public void run(){
        method.run();
    }

    public void setNext(Task next){
        this.nextTask = next;
    }

    public Task getNext(){ return nextTask; }

    public long getTime(){
        return whenRun;
    }
}
