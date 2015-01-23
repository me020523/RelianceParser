/**
 * Created by Administrator on 2015/1/23.
 */

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.util.ArrayList;

/**
 * 一个简单的爬虫，用于爬取Reliance网站上的手机信息
 *  http://www.reliancedigital.in/
 */
public class RelianceSpider extends Thread implements ISpiderReportable
{
    private final int DEFAULT_PAGE_WORKER_COUNT = 5;
    private final int DEFAULT_PHONE_WORKER_COUNT = 10;
    private String startPage = "";
    private SpiderInternalWorkLoad pageWorkload = null;
    private SpiderInternalWorkLoad phoneWorkload = null;
    private SpiderDone spiderDone = null;

    private CloseableHttpClient hc = HttpClients.custom()
            .setConnectionManager(new PoolingHttpClientConnectionManager())
            .build();

    private ArrayList<SpiderWorker> pageWorkers = new ArrayList<SpiderWorker>();
    private ArrayList<SpiderWorker> phoneWorkers = new ArrayList<SpiderWorker>();

    public RelianceSpider(String startPage)
    {
        pageWorkload = new SpiderInternalWorkLoad();
        phoneWorkload = new SpiderInternalWorkLoad();
        spiderDone = new SpiderDone();
        this.startPage = startPage;

        //创建工作线程
        for(int i = 0; i < DEFAULT_PAGE_WORKER_COUNT; i++)
        {
            pageWorkers.add(new PageSpiderWorker(this,hc));
        }
        for(int i = 0; i < DEFAULT_PHONE_WORKER_COUNT; i++)
        {
            phoneWorkers.add(new PhoneSpiderWorker(this,hc));
        }
    }
    public SpiderDone getSpiderDone()
    {
        return this.spiderDone;
    }
    public SpiderInternalWorkLoad getPageWorkload()
    {
        return this.pageWorkload;
    }
    public SpiderInternalWorkLoad getPhoneWorkload()
    {
        return this.phoneWorkload;
    }

    @Override
    public void run()
    {
        pageWorkload.addWorkload(startPage);
        for(int i = 0; i < pageWorkers.size(); i++)
            pageWorkers.get(i).start();
        for(int i = 0; i < phoneWorkers.size(); i++)
            phoneWorkers.get(i).start();
        do
        {
            spiderDone.waitBegin();
            System.out.println("Spider is running");
            spiderDone.waitDone();
        }while(pageWorkload.getRemainingCount() == 0 && phoneWorkload.getRemainingCount() == 0);
    }

    @Override
    public boolean foundPhoneLink(String url) {
        return false;
    }

    @Override
    public boolean foundPageLink(String url) {
        return false;
    }
}
