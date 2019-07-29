package com.z3pipe.z3location.task;

/***
 * 保持程序持续运行的线程
 * @author zhengzhuanzi
 */
public class WatchDogThread extends Thread {
    protected volatile boolean isExit = false;
    @Override
    public synchronized void start() {
        if (getState() != State.NEW) {
            return;
        }

        isExit = false;
        this.setName(this.getClass().getName());
        super.start();
    }

    /**
     * 停止
     */
    public void abort() {
        isExit = true;
        this.interrupt();
    }

    @Override
    public String toString() {
        return this.getClass().getName();
    }
}
