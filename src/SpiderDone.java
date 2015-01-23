/**
 * Created by evilatom on 14-11-8.
 */

/**
 * 用于跟踪Spider中正在运行的线程，主要关注以下方面
 *      1) 第一个线程的加入(工作开始)
 *      2) 新线程的加入
 *      3) 线程的退出
 *      4) 所有线程的退出(工作完成)
 */
public class SpiderDone
{
    private boolean isStarted = false;
    private int activeThreads = 0;


    /**
     * 等待第一个线程启动
     */
    public synchronized void waitBegin()
    {
        try
        {
            while(!isStarted)
            {
                wait();
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    /**
     *所有线程执行完毕
     */
    public synchronized void waitDone()
    {
        try
        {
            while (activeThreads > 0) {
                this.wait();
            }
            isStarted = false;
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    /**
     *worker线程启动
     */
    public synchronized void workerBegin()
    {
        activeThreads++;
        isStarted = true;
        notify();
    }
    /**
     *worker线程完成工作
     */
    public synchronized void workerEnd()
    {
        --activeThreads;
        notify();
    }
    /**
     * 重新设置该WorkDone
     */
    public synchronized void reset()
    {
        --activeThreads;
    }
}
