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

    public TraceJframe(StringBuffer traceInfo) {
        StringBuffer traceInfo1 = traceInfo;
    }

    public TraceJframe() {

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

