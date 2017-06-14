package org.tum.project.utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.tum.project.bean.ProjectInfo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * utils for handle xml file
 * Created by Yin Ya on 2017/5/16.
 */
public class xmlUtils {

    public static void main(String[] args) throws IOException {
        File projectXml = createAndGetProjectXmlFile();
        try {
            Document document = readDocument(projectXml.getAbsolutePath());
            Element rootElement = document.getRootElement();
            System.out.println(rootElement.hasContent());
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }


    /**
     * Create an project information xml file, and if the file already exists, do nothing
     * If the file does not exist, create a new file
     *
     * @return return the xml file
     */
    public static File createAndGetProjectXmlFile() {
        String projectPath = xmlUtils.class.getResource("../").getFile();
        System.out.println(projectPath);
        File file = new File(projectPath + File.separator + "projectInfo.xml");
        if (!file.exists()) {
            try {
                boolean is = file.createNewFile();
                System.out.println("file create: " + is);
                writeToXml(createDocument(), file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(file.exists());
        return file;
    }

    /**
     * create a default document for project information xml file
     *
     * @return default document with root tag
     */
    private static Document createDocument() {
        Document document = DocumentHelper.createDocument();
        document.addElement("root");

        return document;
    }

    /**
     * write the document to the given file
     *
     * @param document document
     * @param file     given file
     * @throws IOException io exception
     */
    private static void writeToXml(Document document, File file) throws IOException {

        XMLWriter writer = new XMLWriter(new FileWriter(file));
        writer.write(document);
        writer.close();
        System.out.println("write finish");
    }


    /**
     * read a document for given file name
     *
     * @param fileName given file name
     * @return document
     * @throws MalformedURLException
     * @throws DocumentException
     */
    public static Document readDocument(String fileName) throws MalformedURLException, DocumentException {
        SAXReader reader = new SAXReader();
        return reader.read(new File(fileName));
    }

    /**
     * get the content from document
     *
     * @param document document
     */
    public static ArrayList<ProjectInfo> getAllProjectFromDocument(Document document) {
        ArrayList<ProjectInfo> projectInfos = new ArrayList<>();
        Element rootElement = document.getRootElement();
        Iterator<Element> elementIterator = rootElement.elementIterator();

        while (elementIterator.hasNext()) {
            Element project = elementIterator.next();
            ProjectInfo info = new ProjectInfo();
            info.setSimulationFile(project.element("projectName").getText());
            info.setDataBankName(project.element("dataBankName").getText());
            info.setModuleTableName(project.element("moduleName").getText());
            info.setFifoTableName(project.element("fifoName").getText());
            info.setFastfifoTabelName(project.element("fastfifoName").getText());
            info.setLoadFactor(project.element("loadFactor").getText());
            info.setSampleFrequency(project.element("sampleFrequency").getText());
            projectInfos.add(info);
        }
        Collections.reverse(projectInfos);
        return projectInfos;
    }


    /**
     * write the project information to the project xml file
     *
     * @param info project info
     */
    public static void writeToDocument(ProjectInfo info) {
        try {
            File file = createAndGetProjectXmlFile();
            Document document = readDocument(file.getAbsolutePath());
            Element rootElement = document.getRootElement();
            Element project = rootElement.addElement("project");
            project.addElement("projectName").setText(info.getSimulationFile());
            project.addElement("dataBankName").setText(info.getDataBankName());
            project.addElement("moduleName").setText(info.getModuleTableName().toLowerCase());
            project.addElement("fifoName").setText(info.getFifoTableName().toLowerCase());
            project.addElement("fastfifoName").setText(info.getFastfifoTabelName().toLowerCase());
            project.addElement("loadFactor").setText(info.getLoadFactor());
            project.addElement("sampleFrequency").setText(info.getSampleFrequency());
            XMLWriter writer = new XMLWriter(new FileWriter(file));
            writer.write(document);
            writer.close();
            System.out.println("write project info to file finish" + "\n" + "project info:" + "\n" + info.toString());
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }

    }


}
