import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class HotFixV3Base64 {
    public static void main(String[] args) {
        try {
            BufferedReader br=new BufferedReader(new FileReader(new File("F:\\329\\yes.txt")));
            JSONObject jo= JSON.parseObject(br.readLine());
            int t=-1;
            for (String key:jo.keySet()){
                t++;
                String base64ed=jo.getString(key);
                JSONObject jjs=JSON.parseObject(new String(Base64.decode(base64ed)));
                if(t%20000==0) {
                    System.out.println(base64ed);
                    System.out.println(jjs.toString());
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
