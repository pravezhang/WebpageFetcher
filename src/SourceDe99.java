
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.text.ParseException;


public class SourceDe99 {

    private static final String userAgent="Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_0) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11";
    private static final String offSet="https://ypk.99.com.cn/DrugInstructions/";
    private static final String[] item={"批准文号","通用名称","英文名称",
                                        "生产企业","功效主治","化学成分",
                                        "药理作用","药物相互作用","不良反应",
                                        "禁忌症","用法用量","药品贮藏",
                                        "注意事项"};
    private final static int N=176975;
    private final static String Path="F:\\AllRecord.txt";
    private final static String NoDrugPath="F:\\emptyRecord.txt";

    private static JSONObject Grab(String txtPath) throws Exception{
        File file=new File(txtPath);
        FileReader fr=new FileReader(file);
        BufferedReader br=new BufferedReader(fr);
        return JSON.parseObject(br.readLine());
    }

    private static JSONObject getData(int d) throws IOException,RuntimeException,ParseException {
        String url=offSet+d+".html";
        Document document= Jsoup.connect(url).userAgent(userAgent).get();
        String title=document.title();
        if(title.startsWith("说明书"))
            throw new RuntimeException(url+" has no corresponding drug.");
        JSONObject jsonObject=new JSONObject();
        try {
            Element element=document.getElementsByClass("drug_sms_l").first();
            Elements tbody=element.getElementsByTag("table").first().getElementsByTag("tr");
            for (int i = 0; i <13 ; i++)
                jsonObject.put(item[i],tbody.get(i).getAllElements().last().text());
        }
        catch (Exception e){
            throw new ParseException(url+" ,Format not correct , maybe banned.",d);
        }
        jsonObject.put("网址",url);
        return jsonObject;

    }

    public static void main(String[] args) {
        JSONObject jsonObject=new JSONObject();
        JSONObject errorObject=new JSONObject();
        JSONObject emptyObject=new JSONObject();
        for (int i = 1; i < 176975; i++) {
            JSONObject js;
            try {
                js=getData(i);
                if(!js.isEmpty()){
                    System.out.println("Catch success , number = "+i);
                    jsonObject.put(String.valueOf(i),js.toString());
                }
                Thread.sleep(333);
                if(i%2000==0)
                    Write(jsonObject.toString(),""+(i/2000));
            }
            catch (IOException e1){
                System.out.println("蜜汁出错,"+e1.toString());
                //13996 13997 没录入。
            }
            catch (RuntimeException e2){
                System.out.println(e2.toString());
                emptyObject.put(String.valueOf(i),offSet+i+".html");
            }
            catch (ParseException e3){
                errorObject.put(String.valueOf(i),offSet+i+".html");
            }
            catch (InterruptedException e4){
                System.out.println("睡觉有错误");
            }
            catch (WriteOutExcetion e5){
                System.out.println(e5.toString());
            }
        }
        try {
            Write(emptyObject.toString(),"emptyRecord");
            Write(errorObject.toString(),"errorRecord");
            Write(jsonObject.toString(),"AllRecord");
        } catch (WriteOutExcetion | IOException e) {
            e.printStackTrace();
        }
    }

    private static class WriteOutExcetion extends Exception{
        public WriteOutExcetion(String message) {
            super(message);
        }
    }

    private static void Write(String s,String filename) throws WriteOutExcetion,IOException{
        File file=new File("F:\\"+filename+".txt");
        if(file.exists())
            if(!file.delete())
                throw new WriteOutExcetion("Delete file error");
        if(!file.createNewFile())
            throw new WriteOutExcetion("Create file error");
        FileWriter fw=new FileWriter(file);
        BufferedWriter bw=new BufferedWriter(fw);
        bw.write(s);
        bw.close();
        fw.close();
    }
}
