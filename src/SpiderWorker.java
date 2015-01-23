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
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                continue;
            }
            owner.getSpiderDone().workerBegin();
            processWorkload(target);
            owner.getSpiderDone().workerEnd();
        }
    }
    private void processWorkload(String url)
    {
        isBusy = true;
        onProcessWorkload(url);
        isBusy = false;
    }
    protected void onProcessWorkload(String url)
    {

    }
}
