package com;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Crawlers {
    public static void downloadVideo(int pageindex) throws IOException {
        //https://ibaotu.com/shipin/7-0-0-0-0-2.html
        //https://ibaotu.com/shipin/7-0-0-0-0-3.html
        //观察以上链接，可以实现分页爬取内容
        String url = "https://ibaotu.com/shipin/";
        //打开浏览器
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //请求类型
        HttpGet httpGet = new HttpGet(url);
        //响应的请求
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        //判断响应是否正常 响应码200就是正常的
        if (httpResponse.getStatusLine().getStatusCode() == 200) {
            //获取响应体并把它给对象
            HttpEntity entity = httpResponse.getEntity();
            //这个工具类会把响应体对象一字符串的形式输出，按照utf-8编码格式
            String content = EntityUtils.toString(entity, "UTF-8");
            //jsoup解析字符串
            Document document = Jsoup.parse(content);
            //选中目标内容，选择相应的标签
            Elements elements = document.select("div.media-list div.video-play video");
            //输出这个"div.media-list div.video-play video"标签下的url
           // System.out.println(elements.get(0));


            for (int i = 0; i < elements.size(); i++) {
                Element element = elements.get(i);
                //获取src标签中的内容
                String attr = element.attr("src");
                //输出具体的内容
                //System.out.println(attr);
                //拼接http
                CloseableHttpResponse httpResponse1 = httpClient.execute(new HttpGet("http:" + attr));
                HttpEntity entity1 = httpResponse1.getEntity();
                //读取的不是元素，是内容
                InputStream inputStream = entity1.getContent();
                //使用file工具类将内容复制到本地指定目录下
                FileUtils.copyToFile(inputStream, new File("D://input/" + pageindex + "-" + i + ".mp4"));
                //关闭流
                inputStream.close();
            }

            pageindex++;
            downloadVideo(pageindex);//递归

            //以上代码只能实现网站页面一页视频的爬取，如果网站有多页，需要根据每一页的视频定位爬取

            // System.out.println(content);
        }

    }

    public static void main(String[] args) throws IOException {
        downloadVideo(1);
    }
}
