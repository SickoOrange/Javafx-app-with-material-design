package org.tum.project.CefModelEditor;

import Cef.*;
import Cef.util.CefResourceFactoryImpl;
import com.mxgraph.layout.*;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.swing.util.mxMouseAdapter;
import com.mxgraph.util.mxEvent;
import com.mxgraph.view.mxGraph;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.tum.project.callback.JFrameCallback;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is the class used to handle the domain model editor like Cef Model
 * Visualization
 * Edit
 */
public class CefVisualizationService {


    private mxGraph graph;
    private mxGraphComponent graphComponent;
    private JFrame jFrame;
    private ArrayList<Object> vertexList;
    private HashMap<BigInteger, Object> edgeList;


    private JFrameCallback jFrameCallbacK;
    private DocumentRoot documentRoot;

    private Object popData;


    /**
     * set the callback for receive the popData from jFrame
     *
     * @param jFrameCallbacK
     */
    public void setJframeCallback(JFrameCallback jFrameCallbacK) {
        this.jFrameCallbacK = jFrameCallbacK;
    }

    /**
     * open the file chooser to select a xml file
     *
     * @param mainActivityStage documentRoot for main ui
     * @param action            open oder save
     */
    public void handleCefEditor(Stage mainActivityStage, String action) {
        FileChooser fileChooser = new FileChooser();
        switch (action) {
            case "open":
                fileChooser.setTitle("Open Resource File");
                File selectFile = fileChooser.showOpenDialog(mainActivityStage);
                documentRoot = getMetalDocumentRoot(selectFile.getAbsolutePath());
                initGraph();
                visualization(documentRoot);
                break;
            case "save":
                fileChooser.setTitle("Open Resource File");
                File saveFile = fileChooser.showSaveDialog(mainActivityStage);
                break;

        }

    }

    public void test() {
        DocumentRoot root = getMetalDocumentRoot("C:\\Users\\SickoOrange\\Desktop\\multimedia_4x4_routed.xml");
        initGraph();
        visualization(root);
    }

    ;

    /**
     * init the setting for generate a graph
     */
    private void initGraph() {
        jFrame = new JFrame();
        jFrame.setSize(800, 800);
        jFrame.setLocation(300, 200);
        jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);


        graph = new mxGraph();
        //Vertices and Edges (Cells) not editable by default in JGraph
        graph.setCellsEditable(false);
        graph.setCellsResizable(false);
        graphComponent = new mxGraphComponent(graph);
        //Vertices and Edges (Cells) not movable by default in JGraph
        graphComponent.setDragEnabled(false);

        jFrame.getContentPane().add(graphComponent, BorderLayout.CENTER);
        jFrame.setVisible(true);

        //set the mouse listener to the graphComponent
        //capture the double click event
        //capture the selected cell
        graphComponent.getGraphControl().addMouseListener(new mxMouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                //mouse event=> double click
                if (mouseEvent.getClickCount() == 2) {

                    Object selectedCell = graphComponent.getCellAt(mouseEvent.getX(), mouseEvent.getY());
                    if (selectedCell != null) {
                        if (isVertex(selectedCell)) {
                            //its vertex
                            //properties: block port ports list
                            String blockName = graph.convertValueToString(selectedCell);
                            popData = blockName;
                        }
                        if (isEdge(selectedCell)) {
                            //its edge
                            //properties: link, source port, destination port
                            BigInteger linkId = getEdgeId(selectedCell);
                            popData = linkId;

                        }


                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                jFrameCallbacK.popData(popData);
                            }
                        });


                    }

                }
                super.mouseReleased(mouseEvent);
            }
        });
    }


    /**
     * select cell is edge?
     *
     * @param selectedCell select cell
     * @return true edge
     */
    private boolean isEdge(Object selectedCell) {
        for (Map.Entry<BigInteger, Object> entry : edgeList.entrySet()) {
            if (entry.getValue() == selectedCell) {
                return true;
            }
        }
        return false;
    }

    /**
     * get edge id
     *
     * @param selectedCell edge
     * @return link id
     */
    private BigInteger getEdgeId(Object selectedCell) {
        for (Map.Entry<BigInteger, Object> entry : edgeList.entrySet()) {
            if (entry.getValue() == selectedCell) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * select cell is vertex?
     *
     * @param selectedCell select cell
     * @return true vertex
     */
    private boolean isVertex(Object selectedCell) {
        if (vertexList.size() != 0) {
            for (Object o : vertexList) {
                if (o == selectedCell) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * visualization for this model in the graph
     *
     * @param root cef document documentRoot
     */
    private void visualization(DocumentRoot root) {

        Object parent = graph.getDefaultParent();
        graph.getModel().beginUpdate();

        //get all block list
        EList<BlockType> blocksList = root.getCef().getSystem().getBlocks().getBlock();
        vertexList = new ArrayList<>();
        for (BlockType block : blocksList) {
            BigInteger blockType = block.getBlockType();
            Object vertex = null;
            if (blockType.toString().equals("0")) {
                //0- router style round

                vertex = graph.insertVertex(parent, null, block.getName(), 20, 20, 80, 30, "shape=ellipse;perimeter=100;whiteSpace=wrap;fillColor=green");
            } else if (blockType.toString().equals("1")) {
                //1- processing element
                vertex = graph.insertVertex(parent, null, block.getName(), 20, 20, 80, 30);
            }
            vertexList.add(vertex);

        }

        //get all link list
        EList<LinkType> linksList = root.getCef().getSystem().getLinks().getLink();
        edgeList = new HashMap<>();
        for (LinkType linkType : linksList) {
            BigInteger sourcePortId = linkType.getSourcePortId();
            BigInteger destinationPortId = linkType.getDestinationPortId();

            BlockType sourceBlock = findBlockFromPortId(sourcePortId, blocksList);
            BlockType destinationBlock = findBlockFromPortId(destinationPortId, blocksList);

            //get vertex from block name
            Object sourceVertex = getVertexFromBlock(sourceBlock, vertexList);
            Object destinationVertex = getVertexFromBlock(destinationBlock, vertexList);
            Object edge = graph.insertEdge(parent, null, linkType.getName(), sourceVertex, destinationVertex);
            edgeList.put(linkType.getId(), edge);
        }


        graph.getModel().endUpdate();

        morphGraph(graph, graphComponent);
    }


    /**
     * get the vertex from the block name
     *
     * @param block
     * @param vertexList
     * @return Object vertex
     */
    private Object getVertexFromBlock(BlockType block, ArrayList<Object> vertexList) {
        for (Object o : vertexList) {
            String vertexName = graph.convertValueToString(o);
            if (block.getName().equals(vertexName)) {
                //find the vertex in the graph
                return o;
            }
        }
        return null;
    }


    /**
     * find the block, that contain this port id
     *
     * @param portId     portId
     * @param blocksList
     * @return BlockType  blockType
     */
    private BlockType findBlockFromPortId(BigInteger portId, EList<BlockType> blocksList) {
        for (BlockType blockType : blocksList) {
            EList<PortType> portsList = blockType.getPorts().getPort();
            for (PortType portType : portsList) {
                BigInteger id = portType.getId();
                if (id.compareTo(portId) == 0) {
                    //find the block
                    return blockType;
                }
            }

        }

        return null;
    }

    /**
     * define the layout algorithmus for the graph
     *
     * @param graph          graph
     * @param graphComponent graphComponent
     */
    private void morphGraph(mxGraph graph, mxGraphComponent graphComponent) {
        // define layout
        mxIGraphLayout layout = new mxOrganicLayout(graph);

        // layout using morphing
        graph.getModel().beginUpdate();
        try {
            layout.execute(graph.getDefaultParent());
        } finally {
            mxMorphing morph = new mxMorphing(graphComponent, 10, 1.5, 10);


            morph.addListener(mxEvent.DONE, (arg0, arg1) -> {
                graph.getModel().endUpdate();
            });

            morph.startAnimation();
        }
    }


    /**
     * get the document documentRoot from the selected xml file
     *
     * @param absolutePath file absolute path
     * @return documentRoot
     */
    private DocumentRoot getMetalDocumentRoot(String absolutePath) {
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
                .put(Resource.Factory.Registry.DEFAULT_EXTENSION, new CefResourceFactoryImpl());
        resourceSet.getPackageRegistry().put(CefPackage.eNS_URI, CefPackage.eINSTANCE);
        Resource resource = resourceSet.getResource(org.eclipse.emf.common.util.URI.createFileURI(
                absolutePath), true);
        return (DocumentRoot) resource.getContents().get(0);

    }

    public void getDefaultContent() {
        Platform.runLater(() -> jFrameCallbacK.popData(popData));
    }

    /**
     * find block properties by block name
     *
     * @param name block name
     */
    public BlockType findBlockByName(String name) {
        EList<BlockType> blockList = documentRoot.getCef().getSystem().getBlocks().getBlock();
        for (BlockType blockType : blockList) {
            if (blockType.getName().equals(name)) {
                return blockType;
            }
        }
        return null;
    }
}