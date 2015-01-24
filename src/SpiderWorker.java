import org.apache.http.client.HttpClient;

/**
 * Created by Administrator on 2015/1/23.
 */
public class SpiderWorker extends Thread
{
    protected RelianceSpider owner = null;
    protected HttpClient hc = null;
    protected String target = "";
    protected boolean isBusy = false;

    public SpiderWorker(RelianceSpider owner,HttpClient hc)
    {
        this.owner = owner;
        this.hc = hc;
    }

    protected String getWorkload()
    {
        return null;
    }

    @Override
    public void run()
    {
        for(;;)
        {
            target = getWorkload();
            if (target == null)
            {
                try
                {
                    Thread.sleep(200);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                    continue;
                }
                continue;
            }
            owner.getSpiderDone().workerBegin();
            processWorkload(target);
            owner.getSpiderDone().workerEnd();
            System.out.println("finish a workload by " + Thread.currentThread().getName());
        }
    }
    private void processWorkload(String url)
    {
        isBusy = true;
        onProcessWorkload(hc,url);
        isBusy = false;
    }
    protected void onProcessWorkload(HttpClient hc, String url)
    {

    }
}
