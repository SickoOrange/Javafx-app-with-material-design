package org.tum.project.test;

import Cef.CefPackage;
import Cef.CefType;
import Cef.DocumentRoot;
import Cef.util.CefResourceFactoryImpl;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Yin Ya on 2017/5/16.
 */
public class CefToSystemC {
    public static void main(String[] args) {

        File file = new File("C:\\Users\\SickoOrange\\Desktop\\multimedia_4x4_routed.xml");
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
                Resource.Factory.Registry.DEFAULT_EXTENSION, new CefResourceFactoryImpl());
        resourceSet.getPackageRegistry().put(CefPackage.eNS_URI, CefPackage.eINSTANCE);
        Resource resource1 = resourceSet.createResource(URI.createFileURI(file.getAbsolutePath()));
        try {
            resource1.load(null);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        DocumentRoot documentRoot = (DocumentRoot) resource1.getContents().get(0);
        CefType cef = documentRoot.getCef();

        Configuration config = new Configuration();
        config.setClassForTemplateLoading(CefToSystemC.class, "");
        config.setAPIBuiltinEnabled(true);

        Template template = null;
        FileWriter out = null;
        try {
            template = config.getTemplate("cefToSystemcTemplate.ftl");
            //File file = new File("test.out");
            File targetFile = new File("C:\\Users\\SickoOrange\\Desktop\\","genSimGraph.cpp");

            out = new FileWriter(targetFile);
            template.process(cef, out);
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }

        System.out.println("finish !");

    }
}
