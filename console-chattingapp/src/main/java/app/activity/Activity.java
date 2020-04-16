package app.activity;

public abstract class Activity<T> {

    public void run(){
        throw new RuntimeException("run 미구현");
    }

    public void runWithObject(T t){
        throw new RuntimeException("runWithObject 미구현");
    }

    public void start(){
        run();
    }

    public void startWithObject(T t){
        runWithObject(t);
    }

}
