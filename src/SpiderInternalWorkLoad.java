/**
 * Created by evilatom on 14-11-8.
 */

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 将作业存放至内存中
 */
public class SpiderInternalWorkLoad implements IWorkLoadStorable
{
    //作业完成列表
    private HashMap<String,Character> complete = new HashMap<String, Character>();
    //等待作业列表
    private ArrayList<String> waiting = new ArrayList<String>();
    //运行作业列表
    private ArrayList<String> running = new ArrayList<String>();

    @Override
    synchronized public int getRemainingCount() {
        return waiting.size();
    }

    @Override
    public void addWorkload(String url) {
        if(getUrlStatus(url) != UNKNOWN)
            return;
        waiting.add(url);
    }

    @Override
    synchronized public String assignWorkload() {
        if(waiting.size() == 0)
            return null;
        String url = waiting.remove(0);
        running.add(url);
        return url;
    }

    @Override
    synchronized public void completeWorkload(String url, boolean error) {
        int index = running.indexOf(url);
        if(index < 0)
            return;
        running.remove(index);
        if(error)
            complete.put(url,COMPLETE);
        else
            complete.put(url,ERROR);
    }

    @Override
    synchronized public void clear() {
        waiting.clear();
        running.clear();
        complete.clear();
    }

    @Override
    synchronized public char getUrlStatus(String url)
    {
        if(waiting.contains(url))
            return WAITING;
        if(running.contains(url))
            return RUNNING;
        if(complete.containsKey(url))
            return complete.get(url);
        return UNKNOWN;
    }
}
