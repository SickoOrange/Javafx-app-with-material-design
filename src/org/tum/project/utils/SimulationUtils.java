package org.tum.project.utils;

import Cef.CefType;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import javafx.application.Platform;
import org.tum.project.CefModelEditor.CefModifyUtils;
import org.tum.project.testbench.TestBenchStageController;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * noc simulation utils
 * Created by Yin Ya on 2017/5/27.
 */
public class SimulationUtils {
    public static void compile(String path) {

        String s = null;
        try {
            Process process = Runtime.getRuntime().exec("make", null,
                    new File(path));

            try {
                process.waitFor();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void execute(String cmd, String nocPath,String libPath) {
        try {
            String[] envs = new String[1];
            envs[0] = "LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:" + libPath + "/lib-linux64";

            Process p = Runtime.getRuntime().exec(cmd, envs, new File(nocPath));
            try {
                p.waitFor();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }

    }

    /**
     * generate a test bench cpp file with the select getCefFile file and save it to the target location
     *
     * @param cefPath  cef path string
     * @param savePath save the cpp file in this path
     */
    public static void generateTestFile(String cefPath, String savePath) {
        CefType cef = CefModifyUtils.getCef(new File(cefPath));
        Configuration config = new Configuration();
        config.setClassForTemplateLoading(TestBenchStageController.class, "");
        config.setAPIBuiltinEnabled(true);
        System.out.println(cef);
        Template template = null;
        FileWriter out = null;
        try {
            template = config.getTemplate("cefToSystemcTemplate.ftl");
            out = new FileWriter(new File(savePath,"genSimGraph.cpp"));
            template.process(cef, out);
        } catch (IOException | TemplateException e1) {
            e1.printStackTrace();
        }
    }

}

