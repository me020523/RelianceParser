import org.apache.http.client.HttpClient;

/**
 * Created by Administrator on 2015/1/23.
 */
public class SpiderWorker extends Thread
{
    protected RelianceSpider owner = null;
    protected HttpClient hc = null;

    public SpiderWorker(RelianceSpider owner,HttpClient hc)
    {
        this.owner = owner;
        this.hc = hc;
    }

    protected IWorkLoadStorable getWorkload()
    {
        return null;
    }

    @Override
    public void run()
    {

    }
}
