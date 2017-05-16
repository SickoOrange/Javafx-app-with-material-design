package org.tum.project.test;

import Cef.DocumentRoot;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.DOMReader;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Yin Ya on 2017/5/16.
 */
public class TestXML {


    public Document read(String fileName) throws MalformedURLException, DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(new File(fileName));

        DOMReader reader1 = new DOMReader();
        return document;
    }

    public Element getRootElement(Document doc) {
        return doc.getRootElement();
    }

    public static Document createDocument() {
        Document document = DocumentHelper.createDocument();
        Element projects = document.addElement("projects");

        Element project = projects.addElement("project");

        project.addElement("name").setText("first project");
        project.addElement("moduleName").setText("hello");
        project.addElement("fifoName").setText("world");
        project.addElement("traceFlits").setText("i am robert!");


        Element project2 = projects.addElement("project");

        project2.addElement("name").setText("first project2");
        project2.addElement("moduleName").setText("hello2");
        project2.addElement("fifoName").setText("world2");
        project2.addElement("traceFlits").setText("i am robert!2");


        return document;
    }

    public static void writeToXml(Document document) throws IOException {
        String resource = TestXML.class.getResource("../config").getFile();
        System.out.println(resource);
        System.out.println(new File(String.valueOf(resource)).exists());
        //String path = "C:/Users/SickoOrange/Desktop/Editor/Tum-Master-Thsis/out/production/Tum-Master-Thsis/org/tum" +
          //      "/project/config/";
        /*File file = new File(path);
        if (!file.exists()) {
            System.out.println("文件 不存在 创建文件");
            file.mkdir();
        } else {
            System.out.println("文件存在");
        }*/
     /*   XMLWriter writer = new XMLWriter(new FileWriter(file));
        writer.write(document);
        writer.close();
        System.out.println("write finish");*/
    }

    public static void main(String[] args) throws IOException {

        Document document = createDocument();
        writeToXml(document);

    }

}
