import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import sun.security.krb5.internal.PAData;

import java.io.*;
import java.text.ParseException;

public class HotFixV2 {


    private static final String userAgent="Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_0) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11";
    private static final String offSet="https://ypk.99.com.cn/DrugInstructions/";
    public static final String[] item={"批准文号","通用名称","英文名称",
            "生产企业","功效主治","化学成分",
            "药理作用","药物相互作用","不良反应",
            "禁忌症","用法用量","药品贮藏",
            "注意事项"};
    private final static int N=176975;
    private final static String Path="F:\\AllRecord.txt";
    private final static String NoDrugPath="F:\\emptyRecord.txt";
    private static JSONObject JSReader(String txtPath) throws IOException{
        File file=new File(txtPath);
        FileReader fr=new FileReader(file);
        BufferedReader br=new BufferedReader(fr);
        JSONObject j=JSON.parseObject(br.readLine().substring(1));
        return j;
    }
    private static void JSWriter(JSONObject jsonObject,String filePath) throws IOException {
        File f=new File(filePath);
        if(f.exists())
            f.delete();
        f.createNewFile();
        FileWriter fw=new FileWriter(f);
        BufferedWriter bw=new BufferedWriter(fw);
        bw.write(jsonObject.toString());
        bw.close();
        fw.close();
    }

    private static boolean hasNum(JSONObject jsonObject,int num){
        return jsonObject.getString(String.valueOf(num)) != null;
    }

    private static JSONObject getDrug(String url) throws IOException,ParseException,RuntimeException,InterruptedException,Exception {
        Thread.sleep(250);
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
            throw new ParseException(url+" ,Format not correct , maybe banned.",0);
        }
        jsonObject.put("网址",url);
        return jsonObject;
    }


    public static void main(String[] args) {
        try {
            JSONObject existsData=JSReader(Path);
            JSONObject passed=JSReader(NoDrugPath);
            for (int i = 1; i < N; i++) {
                String url=offSet+i+".html";
                try {
                    if(!hasNum(existsData,i))
                        if(!hasNum(passed,i)) {
                            System.out.println("Still");
                            //JSONObject drug = getDrug(url);
                            //existsData.put(String.valueOf(i),drug.toString());
                            //System.out.println("成功了。。"+url);
                        }
                }
                /*catch (IOException e) {
                    System.out.println("Jsoup读取失败？\n\t·"+e.toString());
                    Thread.sleep(2000);
                    i--;
                } catch (ParseException e) {
                    System.out.println("解析失败，大概是被屏蔽。\n\t·"+e.toString());
                } catch (RuntimeException e) {
                    System.out.println("没有对应的药品，存入passed数组。\n\t·"+e.toString());
                    passed.put(String.valueOf(i),url);
                }*/



                catch (Exception e){
                    System.out.println("蜜汁错误，可能是网卡了，需要等待后重新处理一次。\n\t·"+e.toString());
                    Thread.sleep(2000);
                    i--;
                }
            }
            JSWriter(existsData, Path);
            JSWriter(passed,NoDrugPath);
        }catch (IOException e){
            System.out.println("读写文件出错了，炸了。");
        }catch (InterruptedException e){
            System.out.println("睡觉出错了，炸了。");
        }
    }


}
