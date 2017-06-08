package org.tum.project.dataservice;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.util.mxEvent;
import com.mxgraph.view.mxGraph;
import org.tum.project.bean.FlitsInfo;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * This class is used to convert flit information to graph with jgraphx and jFrame
 * Created by Yin Ya on 2017/5/11.
 */
public class TraceJframe {
    private StringBuffer traceInfo;

    public TraceJframe(StringBuffer traceInfo) {
        this.traceInfo = traceInfo;
    }

    public TraceJframe() {

    }


    /**
     * no parameter for this method, so the specified trace information need to be given at the constructor
     * give Parameter is StringBuffer
     */
    public void runTrace() {

        //set the container for trace flit
        JFrame jFrame = new JFrame();
        jFrame.setSize(500, 500);
        jFrame.setLocation(200, 200);
        jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        //create the trace graph
        mxGraph graph = new mxGraph();
        mxGraphComponent graphComponent = new mxGraphComponent(graph);

        jFrame.getContentPane().add(graphComponent, BorderLayout.CENTER);
        jFrame.setVisible(true);

        //graph setting
        Object parent = graph.getDefaultParent();
        graph.getModel().beginUpdate();

        //handle the trace information
        String base = traceInfo.toString();
        String[] baseSplits = base.split("--->");

        //module name
        ArrayList<Object> vertex = new ArrayList<>();

        //position time
        ArrayList<String> time = new ArrayList<>();

        //generate the vertex and insert to the graph
        for (int i = 0; i < baseSplits.length; i++) {
            String[] vertexArr = baseSplits[i].split(",");
            String title = "";
            if (i == 0 || i == baseSplits.length - 1) {
                title = vertexArr[1] + "\n" + vertexArr[0];
            } else {
                //title = vertexArr[1];
                title = vertexArr[1] + "\n" + vertexArr[0];
            }
            Object obj = graph.insertVertex(parent, "start", title, 200, 50, 100, 50);
            vertex.add(obj);
            time.add(vertexArr[0]);
        }

        //generate edge between the vertex and insert to th graph
        for (int i = 0; i < baseSplits.length - 1; i++) {
            graph.insertEdge(parent, null, time.get(i + 1), vertex.get(i), vertex.get(i + 1));
        }

        graph.getModel().endUpdate();


        //algorithm setting
        autoLayout(graph, graphComponent);

    }


    /**
     * give the specified trace information and try to visualize
     *
     * @param cloneTraceInformation trace information
     */
    public mxGraphComponent runTrace(List<FlitsInfo> cloneTraceInformation) {
        //create a container to contain the graph
        JPanel panel = new JPanel();
        //527 405
        panel.setSize(520, 400);

        //create the trace graph
        mxGraph graph = new mxGraph();
        mxGraphComponent graphComponent = new mxGraphComponent(graph);


        //jFrame.getContentPane().add(graphComponent, BorderLayout.CENTER);
        //jFrame.setVisible(true);


        //graph setting
        Object parent = graph.getDefaultParent();
        graph.getModel().beginUpdate();


        //module name
        ArrayList<Object> vertex = new ArrayList<>();

        //position time
        ArrayList<String> time = new ArrayList<>();

        //generate the vertex and insert to the graph
        for (FlitsInfo info : cloneTraceInformation) {
            String[] split = info.getFlitPosition().split("\\.");
            String name = split[0];
            double flitsTime = info.getFlitsTime();
            Object obj = graph.insertVertex(parent, "start", name, 200, 50, 100, 50);
            vertex.add(obj);
            time.add(String.valueOf(flitsTime));

        }

        //generate edge between the vertex and insert to th graph
        for (int i = 0; i < cloneTraceInformation.size()-1; i++) {
            graph.insertEdge(parent, null, time.get(i)+"ns->"+time.get(i + 1)+"ns", vertex.get(i), vertex.get(i + 1));
        }

        graph.getModel().endUpdate();


        //algorithm setting
        autoLayout(graph, graphComponent);
        panel.add(graphComponent);
        return graphComponent;
    }


    /**
     * algorithm setting for the graph
     *
     * @param graph          praph with vertex and edge
     * @param graphComponent container component for graph
     */
    private void autoLayout(mxGraph graph, mxGraphComponent graphComponent) {

        //define layout
        mxIGraphLayout layout = new mxCircleLayout(graph);

        //using morphing
        graph.getModel().beginUpdate();

        //execute the layout algorithm
        layout.execute(graph.getDefaultParent());

        mxMorphing morphing = new mxMorphing(graphComponent, 20, 1.5, 20);

        morphing.addListener(mxEvent.DONE, (o, mxEventObject) -> {
            //when the morphing finish, then call this method
            System.out.println("done");
            graph.getModel().endUpdate();
        });

        //execute morphing
        morphing.startAnimation();

    }


}

