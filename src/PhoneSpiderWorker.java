/**
 * Created by Administrator on 2015/1/23.
 */

import org.apache.http.client.HttpClient;

/**
 * 从手机详情页面提取手机详情
 */
public class PhoneSpiderWorker extends SpiderWorker
{

    public PhoneSpiderWorker(RelianceSpider owner, HttpClient hc)
    {
        super(owner, hc);
    }

    @Override
    protected IWorkLoadStorable getWorkload()
    {
        return owner.getPhoneWorkload();
    }
}
