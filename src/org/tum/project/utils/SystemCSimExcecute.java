package org.tum.project.utils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Yin Ya on 2017/5/27.
 */
public class SystemCSimExcecute {
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

    public static void execute(String cmd, String path) {
        try {
            String[] envs = new String[1];
            envs[0] = "LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:"+ "systemPath" + "/lib-linux64";

            Process p = Runtime.getRuntime().exec(cmd, envs, new File(path));
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
}
