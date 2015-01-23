import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.*;
import java.net.URL;

/**
 * Created by evilatom on 15-1-19.
 */
public class HttpUtils
{
    static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; rv:12.0) Gecko/20100101 Firefox/12.0";
    public static String downloadHtmlPage(HttpClient hc, String url) throws SpiderException
    {
        HttpGet get = new HttpGet(url);
        get.setHeader("User-Agent",USER_AGENT);
        try
        {
            HttpResponse resp = hc.execute(get);
            int status = resp.getStatusLine().getStatusCode();

            if(status == HttpStatus.SC_UNAUTHORIZED)
            {
                throw new SpiderException("need authorization");
            }
            else if(status != HttpStatus.SC_OK)
            {
                throw new SpiderException("failed to downloadHtmlPage the page");
            }
            else
            {
                HttpEntity entity = resp.getEntity();
                InputStream in = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                StringBuilder sb = new StringBuilder();
                String line = reader.readLine();
                while(line != null)
                {
                    sb.append(line);
                    sb.append('\n');
                    line = reader.readLine();
                }
                in.close();
                return sb.toString();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return "";
    }
    public static void downloadImage(HttpClient hc,String url)
    {
        HttpGet get = new HttpGet(url);
        get.setHeader("User-Agent",USER_AGENT);
        try
        {
            HttpResponse resp = hc.execute(get);
            HttpEntity e = resp.getEntity();
            InputStream in = e.getContent();
            URL u = new URL(url);
            String path = u.getPath();
            saveFile(path,in);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public static void saveFile(String path,InputStream in)
    {
        path = "/home/evilatom/web" + path;
        byte[] buf = new byte[512];
        File f = new File(path);
        File p = f.getParentFile();
        if(!p.exists())
            p.mkdirs();
        try
        {
            if (!f.exists())
            {
                f.createNewFile();
            }
            OutputStream out = new FileOutputStream(f);
            int cnt = in.read(buf);
            while(cnt >= 0)
            {
                out.write(buf,0,cnt);
                cnt = in.read(buf);
            }
            out.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public static String trimSearchString(String url)
    {
        if(url == null || "".equals(url))
            return "";
        int index = url.indexOf("?");
        if(index < 0)
            return url;
        return url.substring(0,index - 1);
    }
}
