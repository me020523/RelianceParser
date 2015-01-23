/**
 * Created by Administrator on 2015/1/23.
 */

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

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

    public RelianceSpider(String startPage)
    {
        pageWorkload = new SpiderInternalWorkLoad();
        phoneWorkload = new SpiderInternalWorkLoad();
        spiderDone = new SpiderDone();
        this.startPage = startPage;
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
    public void run() {

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
