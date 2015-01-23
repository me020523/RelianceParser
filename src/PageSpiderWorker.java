/**
 * Created by Administrator on 2015/1/23.
 */

import org.apache.http.client.HttpClient;

/**
 * 用于解析手机列表页面，从中提取手机详情链接及翻页链接
 */
public class PageSpiderWorker extends SpiderWorker
{

    public PageSpiderWorker(RelianceSpider owner, HttpClient hc) {
        super(owner, hc);
    }

    @Override
    protected IWorkLoadStorable getWorkload() {
        return owner.getPageWorkload();
    }
}
