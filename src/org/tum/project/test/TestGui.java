package org.tum.project.test;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.util.mxEvent;
import com.mxgraph.view.mxGraph;
import com.sun.org.apache.xerces.internal.dom.PSVIAttrNSImpl;
import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Yin Ya on 2017/5/19.
 */
public class TestGui extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        final SwingNode swingNode = new SwingNode();

        createSwingContent(swingNode);

        StackPane pane = new StackPane();
        pane.getChildren().add(swingNode);

        primaryStage.setTitle("Swing in JavaFX");
        primaryStage.setScene(new Scene(pane, 250, 150));
        primaryStage.show();
    }

    private void createSwingContent(SwingNode swingNode) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
//                JFrame frame = new JFrame();
//                frame.setTitle("hello wolrd");
//                frame.setContentPane(new JButton("CLICK ME"));

                final mxGraph graph = new mxGraph();

                mxGraphComponent graphComponent = new mxGraphComponent(graph);
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


               // JPanel panel = new JPanel();
               // panel.add();
                //panel.add(graphComponent);
                swingNode.setContent(graphComponent);
            }


        });
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
