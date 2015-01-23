/**
 * Created by evilatom on 14-11-8.
 */

/**
 * 允许定制Spider行为的接口
 * 主要工作
 *      1) 组织访问过的和将要访问的链接列表
 */
public interface IWorkLoadStorable
{
    char RUNNING = 'R';
    char ERROR = 'E';
    char WAITING = 'W';
    char COMPLETE = 'C';
    char UNKNOWN = 'U';

    /**
     * 返回剩余的任务数
     * @return
     */
    public int getRemainingCount();
    /**
     * 将一个url添加到任务队列中，并将其状态置为WAITING
     * @param url
     */
    public void addWorkload(String url);

    /**
     * 从任务队列中取出一个URL，并将其状态置为RUNNING
     */
    public String assignWorkload();

    /**
     * 将url标记为完成或ERROR
     * @param url
     * @param error
     */
    public void completeWorkload(String url,boolean error);

    /**
     * 清空任务队列
     */
    public void clear();

    /**
     *获取指定url的状态
     * @param url
     * @return
     */
    public  char getUrlStatus(String url);
}
