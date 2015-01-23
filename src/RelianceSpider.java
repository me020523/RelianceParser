/**
 * Created by Administrator on 2015/1/23.
 */

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import java.util.ArrayList;
import java.util.Map;

/**
 * 一个简单的爬虫，用于爬取Reliance网站上的手机信息
 *  http://www.reliancedigital.in/
 */
public class RelianceSpider extends Thread implements ISpiderReportable
{
    private final int DEFAULT_PAGE_WORKER_COUNT = 10;
    private final int DEFAULT_PHONE_WORKER_COUNT = 20;
    private String startPage = "";
    private SpiderInternalWorkLoad pageWorkload = null;
    private SpiderInternalWorkLoad phoneWorkload = null;
    private SpiderDone spiderDone = null;
    private ArrayList<PhoneInfo> phoneInfos = new ArrayList<PhoneInfo>();

    private int total = 0;

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
        }while(pageWorkload.getRemainingCount() != 0 || phoneWorkload.getRemainingCount() != 0);

        System.out.println(Thread.currentThread().getName() + "已完成");

        for(int i = 0; i < pageWorkers.size(); i++)
            pageWorkers.get(i).stop();
        for(int i = 0; i < phoneWorkers.size(); i++)
            phoneWorkers.get(i).stop();


        System.out.println("-----------------------------------------------");
        //保存PhoneInfo信息
        for(int i = 0; i < phoneInfos.size();i++)
        {
            PhoneInfo pi = phoneInfos.get(i);
            System.out.println(pi.getOverView());
            for(int j = 0; j < pi.getImgs().size(); j++)
            {
                System.out.println(pi.getImgs().get(j));
            }
            for(String e : pi.getPhoneSpeces().keySet())
            {
                for(int j = 0; j < pi.getPhoneSpeces().get(e).size();j++)
                {
                    System.out.println(pi.getPhoneSpeces().get(e.toString()).get(j).getName() + " = " +
                                    pi.getPhoneSpeces().get(e.toString()).get(j).getValue());
                }
            }
            System.out.println("-------------------------------------------");
        }
        System.out.println("总共手机数: " + phoneInfos.size());
    }

    @Override
    public boolean foundPhoneLink(String url) {
        phoneWorkload.addWorkload(url);
        return true;
    }

    @Override
    public boolean foundPageLink(String url) {
        pageWorkload.addWorkload(url);
        return true;
    }

    @Override
    public boolean completePhoneWorkload(String url)
    {
        synchronized (this)
        {
            ++total;
            System.out.println("已获取手机数: " +  total);
        }
        phoneWorkload.completeWorkload(url,false);
        return true;
    }

    @Override
    public boolean completePageWorkload(String url) {

        pageWorkload.completeWorkload(url,false);
        return true;
    }

    @Override
    public boolean foundPhoneInfo(PhoneInfo pi)
    {
        synchronized (this)
        {
            phoneInfos.add(pi);
        }
        return true;
    }
}
