import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HotFixV1 {
    private final static int N=176975;
    private final static String Path="F:\\AllRecord.txt";//TODO
    private final static String NoDrugPath="F:\\emptyRecord.txt";
    private static JSONObject Grab(String txtPath) throws Exception{
        File file=new File(txtPath);
        FileReader fr=new FileReader(file);
        BufferedReader br=new BufferedReader(fr);
        return JSON.parseObject(br.readLine());
    }
    public static void main(String[] args) {
        try{
            JSONObject j=Grab(Path);
            JSONObject e=Grab(NoDrugPath);
            List<Integer> li=new ArrayList<>();

            for (int i = 1; i < N; i++) {
                if(!hasNum(j,i))
                    if(!hasNum(e,i))
                        li.add(i);
            }

            System.out.println(li.size());
            File file=new File("F:\\Uncatched.txt");

            if(file.exists()) file.delete();
            file.createNewFile();
            FileWriter fw=new FileWriter(file);
            BufferedWriter bw =new BufferedWriter(fw);
            for(Integer i:li)
                bw.write(i+"\n");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean hasNum(JSONObject jsonObject,int num){
        return jsonObject.getString(String.valueOf(num)) != null;
    }



}
