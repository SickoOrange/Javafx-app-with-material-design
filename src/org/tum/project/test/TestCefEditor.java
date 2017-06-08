package org.tum.project.test;

import java.util.ArrayList;


public class TestCefEditor {
    public static void main(String[] args) {
//        CefVisualizationService cefVisualizationService = new CefVisualizationService();
//        cefVisualizationService.test();
        //   System.out.println(new BigInteger("0"));

        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            arrayList.add(i);
        }
        System.out.println(arrayList);
        arrayList.add(0,50);
        System.out.println(arrayList);
    }
}
