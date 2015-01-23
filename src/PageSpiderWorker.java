/**
 * Created by Administrator on 2015/1/23.
 */

import org.apache.http.client.HttpClient;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.CssSelectorNodeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;

/**
 * 用于解析手机列表页面，从中提取手机详情链接及翻页链接
 */
public class PageSpiderWorker extends SpiderWorker
{

    public PageSpiderWorker(RelianceSpider owner, HttpClient hc) {
        super(owner, hc);
    }

    @Override
    protected String getWorkload()
    {
        return owner.getPageWorkload().assignWorkload();
    }

    @Override
    protected void onProcessWorkload(HttpClient hc, String url)
    {
        try
        {
            String html = HttpUtils.downloadHtmlPage(hc,url);

            Parser parser = new Parser();
            parser.setInputHTML(html);
            parser.setURL(url);
            parser.setEncoding("UTF-8");

            //爬取手机列表区
            CssSelectorNodeFilter phoneSF = new CssSelectorNodeFilter(".product-image");
            NodeList phoneList = parser.extractAllNodesThatMatch(phoneSF);
            getPhoneLinks(phoneList);
            //爬取翻页区
            parser.reset();
            CssSelectorNodeFilter pageSF = new CssSelectorNodeFilter(".pages");
            NodeList pageList = parser.extractAllNodesThatMatch(pageSF);
            SimpleNodeIterator si = pageList.elements();
            if(si.hasMoreNodes())
            {
                getPageLinks(si.nextNode());
            }

            //完成当前workload
            owner.completePageWorkload(url);
        }
        catch (SpiderException e)
        {
            e.printStackTrace();
        }
        catch (ParserException e)
        {
            e.printStackTrace();
        }
    }
    protected  void getPhoneLinks(NodeList list)
    {
        SimpleNodeIterator si = list.elements();
        Node n = null;
        while(si.hasMoreNodes())
        {
            n = si.nextNode();
            if(n instanceof LinkTag)
            {
                LinkTag l = (LinkTag)n;
                System.out.println("found phone: " + l.getLink());
                owner.foundPhoneLink(l.getLink());
            }
        }
    }
    protected void getPageLinks(Node pages)
    {
        NodeList children = pages.getChildren();
        NodeFilter nf = new NodeClassFilter(LinkTag.class);
        NodeList list = children.extractAllNodesThatMatch(nf,true);
        SimpleNodeIterator si = list.elements();
        Node n = null;
        while(si.hasMoreNodes())
        {
            n = si.nextNode();
            if(n instanceof LinkTag)
            {
                LinkTag l = (LinkTag)n;
                System.out.println("found page: " + l.getLink().replace("&amp;","&"));
                owner.foundPageLink(l.getLink().replace("&amp;","&"));
            }
        }
    }
}
