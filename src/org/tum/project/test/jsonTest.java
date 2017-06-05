package org.tum.project.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jdk.nashorn.internal.ir.debug.JSONWriter;
import org.tum.project.bean.ProjectInfo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by heylbly on 17-6-5.
 */
public class jsonTest {

    public static void main(String[] args) {

        ProjectInfo info = new ProjectInfo();
        info.setSimulationFile("hello");
        info.setDataBankName("dbname");
        info.setModuleTableName("mtname");
        info.setFifoTableName("ftname");
        info.setFastfifoTabelName("fftname");

        testObj obj = new testObj();
        obj.id = "12";
        obj.name = "hello";
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        System.out.println(gson.toJson(info));
        try {
            FileWriter writer = new FileWriter("test.json");
            writer.write(gson.toJson(info));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


//        File file = new File("test.json");
//        String jsonStr = "[{a:1,b:{b1:[{a:2},{a:1}]},c:3},{a:1},{b:1}]";
//        JSONArray jsonObj = JSONArray.fromObject(jsonStr);
//        char[] stack = new char[1024]; // 存放括号，如 "{","}","[","]"
//        int top = -1;
//
//        String string = jsonObj.toString();
//        StringBuffer sb = new StringBuffer();
//        char[] charArray = string.toCharArray();
//        for (int i = 0; i < charArray.length; i++) {
//            char c = charArray[i];
//            if ('{' == c || '[' == c) {
//                stack[++top] = c; // 将括号添加到数组中，这个可以简单理解为栈的入栈
//                sb.append(charArray[i] + "\n");
//                for (int j = 0; j <= top; j++) {
//                    sb.append("\t");
//                }
//                continue;
//            }
//            if ((i + 1) <= (charArray.length - 1)) {
//                char d = charArray[i+1];
//                if ('}' == d || ']' == d) {
//                    top--; // 将数组的最后一个有效内容位置下标减 1，可以简单的理解为将栈顶数据弹出
//                    sb.append(charArray[i] + "\n");
//                    for (int j = 0; j <= top; j++) {
//                        sb.append("\t");
//                    }
//                    continue;
//                }
//            }
//            if (',' == c) {
//                sb.append(charArray[i] + "\n");
//                for (int j = 0; j <= top; j++) {
//                    sb.append("\t");
//                }
//                continue;
//            }
//            sb.append(c);
//        }
//
//        Writer write = new FileWriter(file);
//        write.write(sb.toString());
//        write.flush();
//        write.close();
    }
}
