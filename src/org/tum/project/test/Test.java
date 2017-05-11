package org.tum.project.test;

import com.mxgraph.layout.*;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.orthogonal.mxOrthogonalLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.view.mxGraph;

import javax.swing.*;
import java.awt.*;

public class Test extends JFrame {

        public static void main(String[] args) {
                JFrame f = new JFrame();
                f.setSize(800, 800);
                f.setLocation(300, 200);
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                final mxGraph graph = new mxGraph();

                mxGraphComponent graphComponent = new mxGraphComponent(graph);
                f.getContentPane().add(graphComponent, BorderLayout.CENTER);

                f.setVisible(true);
                Object parent = graph.getDefaultParent();

                graph.getModel().beginUpdate();
                try {
                        Object start = graph.insertVertex(parent, "jjj", "222", 100,
                                        100, 180, 130);
                        for (int i = 0; i < 10; i++) {
                                Object a = graph.insertVertex(parent, "A" + i, "A" + i, 100,
                                                100, 180, 130);
                                graph.insertEdge(parent, null, "E" + i, start, a);

                                Object b = graph.insertVertex(parent, "B" + i, "B" + i, 100,
                                                100, 180, 130);
                                graph.insertEdge(parent, null, "E" + i, a, b);
                                start = a;
                        }
                } finally {
                        graph.getModel().endUpdate();
                }

                morphGraph(graph, graphComponent);
        }


        private static void morphGraph(mxGraph graph,
                        mxGraphComponent graphComponent) {
                // define layout
                mxIGraphLayout layout = new mxHierarchicalLayout(graph);

                // layout using morphing
                graph.getModel().beginUpdate();
                try {
                        layout.execute(graph.getDefaultParent());
                } finally {
                        mxMorphing morph = new mxMorphing(graphComponent, 20, 1.5, 20);


                        morph.addListener(mxEvent.DONE, (arg0, arg1) -> {
                                graph.getModel().endUpdate();
                                // fitViewport();
                        });

                        morph.startAnimation();
                }

        }
}