import com.alibaba.fastjson.JSONObject;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainClass {

    static final String userAgent="Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_3_3 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8J2 Safari/6533.18.5" ;
    static final String mainSite="http://drugs.dxy.cn";
    static final String httpHead="http:";
    static final String[] itemKey={
            "DrugTitle","Manufacturer","GeneralName"
            ,"EnglishName","TradeName","Category1"
            ,"Category2","Ingredients","Indication"
            ,"Usage","BadEffect","Contraindicant"
            ,"Attention"};
    public static void Func1()throws Exception{

        int count=0;
        JSONObject Database=new JSONObject();
        String homePage=mainSite+"/index.htm";
        Document pageMain= Jsoup.connect(homePage).userAgent(userAgent).get();
        Element eleMain=pageMain.body().getElementsByClass("g-list").get(0);
        for (Element e:eleMain.getElementsByTag("a")) {
            if (e.attr("href").contains("category")){
                String Level1=e.text();
                //e.attr指向一级目录，打开是二级目录列表
                Sleep(500,"Now Processing Level 1 ");
                Document pageSub=Jsoup.connect(httpHead+e.attr("href")).userAgent(userAgent).get();
                Element eleSub=pageSub.body().getElementsByClass("g-list").get(0);
                for (Element element:eleSub.getElementsByTag("a")){
                    if(element.attr("href").contains("category")){
                        String Level2=element.text();
                        //element.attr指向二级目录，打开是药品条目
                        Sleep(600,"Now Processing Level 2");
                        Document pageList=Jsoup.connect(httpHead+element.attr("href")).userAgent(userAgent).get();
                        List<String> drugAddr=new ArrayList<>();
                        Element navimax=pageList.body().getElementsByClass("page").get(0).getElementsByTag("a").last();
                        int maxPage=Integer.parseInt(navimax.attr("href").split("=")[1]);
                        for (int i = 1; i < maxPage; i++) {
                            Sleep(500,"Now Processing Level 2 list");
                            Elements List=Jsoup.connect(httpHead+element.attr("href")+"?page="+i).userAgent(userAgent).get().body().getElementsByTag("section");
                            for (Element lo:List) {
                                String tem=lo.getElementsByTag("h3").get(0).getElementsByTag("a").get(0).attr("href").trim();
                                if(tem.equals("")||tem.length()==0);
                                else drugAddr.add(httpHead+tem);
                            }
                        }
                        for (String ad:drugAddr) {
                            count++;
                            if(count%40==0)
                                Thread.sleep(120000);
                            Database.put("Drug_" + count, DrugToDetails(ad, Level1, Level2).toString());
                        }//将二级目录药品放入数据库
                    }
                }//得到二级目录
            }
        }//得到一级目录
        Database.put("count",count);
        File file=new File("F:\\Results.txt");
        if(file.exists()) file.delete();
        file.createNewFile();
        BufferedWriter bw=new BufferedWriter(new FileWriter(file));
        bw.write(Database.toString());
        bw.close();
    }

    public static JSONObject DrugToDetails(String address,String level1,String level2)throws Exception{
        JSONObject jsonObject=new JSONObject();
        Sleep(500,"Now processing Drug : "+address);
        Document det=Jsoup.connect(address).userAgent(userAgent).get();
        Element element=det.getElementsByClass("med-tit med-detail-tit").first();
        jsonObject.put(itemKey[0],element.getElementsByTag("h3").text());
        jsonObject.put(itemKey[1],element.getElementsByTag("p").text());
        //category
        jsonObject.put(itemKey[5],level1);
        jsonObject.put(itemKey[6],level2);
        Elements els=det.getElementsByTag("section");
        for (Element words:els){
            String firstP=words.getElementsByTag("p").first().text();
            if(firstP.contains("药品名称")){
                jsonObject.put(itemKey[2],words.getElementsByTag("p").get(1).text().split("：")[1]);
                jsonObject.put(itemKey[3],words.getElementsByTag("p").get(2).text().split("：")[1]);
                jsonObject.put(itemKey[4],words.getElementsByTag("p").get(3).text().split("：")[1]);
            }
            else {
                String sv=words.text()
                        .replaceAll(firstP,"")
                        .replaceAll("</li>","")
                        .replaceAll("<li>","")
                        .replaceAll("</p>","")
                        .replaceAll("<p>","")
                        .replaceAll("</strong>","]")
                        .replaceAll("<strong>","[")
                        .replaceAll("</ol>","")
                        .replaceAll("<ol>","");
                if (firstP.contains("成分")) {
                    jsonObject.put(itemKey[7],sv);
                } else if (firstP.contains("适应症")) {
                    jsonObject.put(itemKey[8],sv);
                } else if (firstP.contains("用法用量")) {
                    jsonObject.put(itemKey[9],sv);
                } else if (firstP.contains("不良反应")) {
                    jsonObject.put(itemKey[10],sv);
                } else if (firstP.contains("禁忌")) {
                    jsonObject.put(itemKey[11],sv);
                } else if (firstP.contains("注意事项")) {
                    jsonObject.put(itemKey[12],sv);
                }
            }
        }
        return jsonObject;
    }

    public static void Sleep(long milliseconds, final String words){
        System.out.println(words);
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        try{
            Func1();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
