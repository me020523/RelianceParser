/**
 * Created by Administrator on 2015/1/23.
 */

import org.apache.http.client.HttpClient;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.CssSelectorNodeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.*;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    protected String getWorkload()
    {
        return owner.getPhoneWorkload().assignWorkload();
    }

    @Override
    protected void onProcessWorkload(HttpClient hc, String url)
    {
        try
        {
            System.out.println("start to parsing " + url + " by " + Thread.currentThread().getName());
            String html = HttpUtils.downloadHtmlPage(hc,url);
            Parser parser = new Parser();
            parser.setInputHTML(html);
            parser.setURL(url);
            parser.setEncoding("UTF-8");

            NodeFilter body = new NodeClassFilter(BodyTag.class);
            NodeList bodyList = parser.extractAllNodesThatMatch(body);

            PhoneInfo pi = new PhoneInfo();
            //获取图片信息
            CssSelectorNodeFilter cf = new CssSelectorNodeFilter(".more-views");
            NodeList list = bodyList.extractAllNodesThatMatch(cf,true);
            NodeFilter imgNF = new NodeClassFilter(LinkTag.class);
            list = list.extractAllNodesThatMatch(imgNF,true);
            pi.setImgs(getPhoneImgs(list));
            //获取Overview信息
            //parser.reset();
            cf = new CssSelectorNodeFilter(".std");
            list = bodyList.extractAllNodesThatMatch(cf,true);
            SimpleNodeIterator si = list.elements();
            if(si.hasMoreNodes())
            {
                pi.setOverView(getPhoneOverview(si.nextNode()));
            }
            //获取Spec信息
            //parser.reset();
            cf = new CssSelectorNodeFilter("#product_tabs_additional_tabbed_contents");
            list = bodyList.extractAllNodesThatMatch(cf,true);
            si = list.elements();
            if(si.hasMoreNodes())
            {
                pi.setPhoneSpeces(getPhoneSpec(si.nextNode()));
            }

            if(pi.getImgs() != null && pi.getOverView() != null && pi.getPhoneSpeces() != null)
                owner.foundPhoneInfo(pi);
            //完成当前任务
            owner.completePhoneWorkload(url);
            System.out.println("finish parsing " + url + " by " + Thread.currentThread().getName());
        }
        catch (SpiderException e)
        {
            e.printStackTrace();
        } catch (ParserException e) {
            e.printStackTrace();
        }
    }

    protected List<String> getPhoneImgs(NodeList list)
    {
        SimpleNodeIterator si = list.elements();
        Node n = null;
        ArrayList<String> imgs = new ArrayList<String>();
        while (si.hasMoreNodes())
        {
            n = si.nextNode();
            if(! (n instanceof LinkTag))
                continue;
            LinkTag l = (LinkTag)n;
            String onMouseOver = l.getAttribute("onmouseover");
            int begin = onMouseOver.indexOf("http");
            int end = onMouseOver.indexOf(";");
            String imgUrl = onMouseOver.substring(begin,end - 1);
            System.out.println("found image: " + imgUrl);
            imgs.add(imgUrl);
        }
        return imgs;
    }
    protected String getPhoneOverview(Node n)
    {
        NodeList children = n.getChildren();
        NodeFilter f = new NodeClassFilter(TextNode.class);
        NodeList list = children.extractAllNodesThatMatch(f,true);
        StringBuilder sb = new StringBuilder();
        SimpleNodeIterator si = list.elements();
        Node tmp = null;
        String v = "";
        while (si.hasMoreNodes())
        {
            tmp = si.nextNode();
            if(!(tmp instanceof TextNode))
                continue;
            TextNode t = (TextNode)tmp;
            v = t.getText();
            v = v.replace("\n","");
            v = v.replace("\r","");
            v = v.trim();
            if(!"".equals(v))
            {
                sb.append(v);
                sb.append("\n");
            }
        }
        System.out.println(sb.toString());
        return sb.toString();
    }
    protected Map<String,List<PhoneSpec>> getPhoneSpec(Node n)
    {
        NodeList list = n.getChildren();
        NodeFilter a = new NodeClassFilter(HeadingTag.class);
        NodeFilter b = new NodeClassFilter(TableTag.class);
        OrFilter f = new OrFilter(a,b);
        list = list.extractAllNodesThatMatch(f,true);
        SimpleNodeIterator si = list.elements();
        Node tmp = null;

        HashMap<String,List<PhoneSpec>> map = new HashMap<String, List<PhoneSpec>>();
        while(si.hasMoreNodes())
        {
            tmp = si.nextNode();
            if(!(tmp instanceof HeadingTag))
                continue;
            //获取spec类型
            HeadingTag ht = (HeadingTag)tmp;
            String type = "";
            SimpleNodeIterator i = ht.getChildren().elements();
            while(i.hasMoreNodes())
            {
                Node in = i.nextNode();
                if(!(in instanceof TextNode))
                    continue;
                TextNode tn = (TextNode)in;
                String v = tn.getText();
                v = v.replace("\n","");
                v = v.replace("r","");
                if(!"".equals(v))
                    type = v;
            }
            if("".equals(type))
                continue;
            if(!si.hasMoreNodes())
                continue;
            tmp = si.nextNode();
            while(!(tmp instanceof TableTag))
            {
                if(!si.hasMoreNodes())
                    break;
                tmp = si.nextNode();
            }
            if(!si.hasMoreNodes())
                continue;
            //从table中获取属性值
            CssSelectorNodeFilter ca = new CssSelectorNodeFilter(".label");
            CssSelectorNodeFilter cb = new CssSelectorNodeFilter(".data");
            i = tmp.getChildren().extractAllNodesThatMatch(new OrFilter(ca,cb),true).elements();
            ArrayList<PhoneSpec> specList = new ArrayList<PhoneSpec>();
            while (i.hasMoreNodes())
            {
                Node in = i.nextNode();
                if(!(in instanceof TableHeader))
                    continue;
                NodeFilter nf = new NodeClassFilter(TextNode.class);
                SimpleNodeIterator ii = in.getChildren().extractAllNodesThatMatch(nf,true).elements();
                StringBuilder sb = new StringBuilder();
                while(ii.hasMoreNodes())
                {
                    Node iin = ii.nextNode();
                    String v = "";
                    if(!(iin instanceof TextNode))
                        continue;
                    TextNode t = (TextNode)iin;
                    v = t.getText();
                    v = v.replace("\n","");
                    v = v.replace("\r","");
                    v = v.trim();
                    if(!"".equals(v))
                    {
                        sb.append(v);
                        sb.append(" ");
                    }
                }
                if(sb.length() == 0)
                    continue;
                PhoneSpec spec = new PhoneSpec();
                spec.setName(sb.toString());

                if(!i.hasMoreNodes())
                    continue;
                in = i.nextNode();
                while(!(in instanceof TableColumn))
                {
                    if(!i.hasMoreNodes())
                        break;
                    in = i.nextNode();
                }
                ii = in.getChildren().extractAllNodesThatMatch(nf,true).elements();
                sb = new StringBuilder();
                while(ii.hasMoreNodes())
                {
                    Node iin = ii.nextNode();
                    String v = "";
                    if(!(iin instanceof TextNode))
                        continue;
                    TextNode t = (TextNode)iin;
                    v = t.getText();
                    v = v.replace("\n","");
                    v = v.replace("\r","");
                    v = v.trim();
                    if(!"".equals(v))
                    {
                        sb.append(v);
                        sb.append("\n");
                    }
                }
                if(sb.length() == 0)
                    continue;
                spec.setValue(sb.toString());
                specList.add(spec);
            }
            map.put(type,specList);
        }
        return map;
    }
}
