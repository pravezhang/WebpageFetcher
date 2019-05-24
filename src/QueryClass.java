import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class QueryClass {


    public static final String[] item={"批准文号","通用名称","英文名称",
            "生产企业","功效主治","化学成分",
            "药理作用","药物相互作用","不良反应",
            "禁忌症","用法用量","药品贮藏",
            "注意事项"};

    private static JSONObject[] getByPartialName(JSONObject database,String name){
        List<JSONObject> res=new ArrayList<>();
        for (String key:database.keySet()){
            JSONObject jt= JSON.parseObject(new String(Base64.decode(database.getString(key))));
            if(jt.getString(item[1]).contains(name))
                res.add(jt);
        }
        JSONObject[] jres=new JSONObject[res.size()];
        res.toArray(jres);
        return jres;
    }

    private static JSONObject readDB(String filePath) throws Exception {
        return JSON.parseObject(new BufferedReader(new FileReader(new File(filePath))).readLine());
    }

    private static String listDrug(JSONObject j){
        StringBuilder sb=new StringBuilder();
        for (String s:item){
            sb.append(s+"：\t"+j.getString(s));
            sb.append("\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        try {
            JSONObject db=readDB("F:\\DB.txt");
            JSONObject[] res=getByPartialName(db,"万艾可");
            for (JSONObject r:res)
                System.out.println(listDrug(r));

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
