import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Query {
    public static void main(String[] args) {
        try {
            BufferedReader br=new BufferedReader(new FileReader(new File("F:\\AllRecord.txt")));
            BufferedReader nr=new BufferedReader(new FileReader(new File("F:\\errorRecord.txt")));
            String s1=br.readLine();
            String s2=nr.readLine();
            System.out.println();
            //s1.replaceAll("\\\"","\"");
            System.out.println("{  "+charCount(s1,'{'));
            System.out.println("}  "+charCount(s1,'}'));
            System.out.println("gap  "+stringCount(s1,"\\\"}\",\""));
            System.out.println("gap inside  "+stringCount(s1,"\":\""));
            String[] strs=s1.split("\",\"");
            strs[0]=strs[0]+"\"";
            strs[strs.length-1]="\""+strs[strs.length-1];
            for (int i = 1; i < strs.length-1; i++) {
                strs[i]="\""+strs[i]+"\"";
            }
            List<String> wrongRec=new ArrayList<>();
            for(String s:strs){
                String[] sss=s.split("\":\"");
                String te=removeSlash(sss[1].substring(0,sss[1].length()-1));
                try {
                    JSONObject jp= JSON.parseObject(te);
                }catch (Exception e){
                    wrongRec.add(s);
                }
            }

            System.out.println("错误一共有 "+wrongRec.size() +"个");
            int gxzz=0;
            BufferedReader brn=new BufferedReader(new FileReader(new File("F:\\329\\maybeOK.txt")));
            JSONObject jo= JSON.parseObject(brn.readLine());
            for (int i = 0; i < wrongRec.size(); i++) {
                String s=wrongRec.get(i);
                String[] cps=s.split("\":\"");
                String number=cps[0].substring(1);
                String jsc=removeSlash(removeSSSQ(cps[1].substring(0,cps[1].length()-1)));
                try{
                    JSONObject jacs=JSON.parseObject(jsc);
                    jo.put(number,Base64.encode(jacs.toString().getBytes()));
                }catch (Exception e){
                    System.out.println(i);
                }
            }
            BufferedWriter bw=new BufferedWriter(new FileWriter(new File("F:\\329\\yes.txt")));
            bw.write(jo.toString());
            bw.close();



            /*
            BufferedWriter bw=new BufferedWriter(new FileWriter(new File("F:\\329\\maybeOK.txt")));
            bw.write(jj.toString());
            bw.close();

            */

            /*
            BufferedWriter bw=new BufferedWriter(new FileWriter(new File("F:\\329\\WithCRLF.txt")));
            for(String s:strs)
                bw.write(s+"\n");
            bw.close();
            */
            /*
            int a1=s1.length();
            int a2=s2.length();
            int t1=0,t2=0;
            char[] cs1=s1.toCharArray();
            char[] cs2=s2.toCharArray();
            boolean firstFlag=false;
            boolean next=false;*/


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static String removeSlash(String s){
        char[] csss=s.toCharArray();
        StringBuilder sb=new StringBuilder();
        for (char c:csss) {
            if (c == '\\') continue;
            else
                sb.append(c);
        }
        return sb.toString();
    }

    private static String removeSSSQ(String s){
        int l1=s.length();
        String s2= s.replace("\\\\\\\"","");
        return s2.length()==l1?s2:removeSSSQ(s2);
    }


    private static int charCount(String s,char ch){
        int i=0;
        for (char  c:s.toCharArray())
            if(ch==c)
                i++;
        return i;
    }

    private static int stringCount(String s,String c){
        int l=0;
        char[] cs=s.toCharArray();
        char[] cc=c.toCharArray();
        boolean f;
        for (int i = 0; i < cs.length-cc.length+1; i++) {
            f=true;
            for (int j = 0; j < cc.length; j++) {
                if(cs[i+j]!=cc[j]) {f=false;break;}
            }
            if(f)
                l++;
        }
        return l;
    }



}
