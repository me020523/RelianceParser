/**
 * Created by evilatom on 14-11-8.
 */
public interface ISpiderReportable
{
    /**
     * 发现手机链接时被调用
     * @param url
     * @return
     */
    public boolean foundPhoneLink(String url);

    /**
     * 发现翻页链接时被调用
     * @param url
     * @return
     */
    public boolean foundPageLink(String url);

    /**
     *完成手机详情爬取任务时调用
     * @param url
     * @return
     */
    public boolean completePhoneWorkload(String url);

    /**
     * 完成手机列表页面爬取任务时调用
     * @param url
     * @return
     */
    public boolean completePageWorkload(String url);

    public boolean foundPhoneInfo(PhoneInfo pi);
}
