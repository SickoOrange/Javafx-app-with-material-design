package org.tum.project.CefModelEditor;

import Cef.*;
import Cef.util.CefResourceFactoryImpl;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.mxgraph.layout.*;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.swing.util.mxMouseAdapter;
import com.mxgraph.util.mxEvent;
import com.mxgraph.view.mxGraph;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.*;
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
import java.io.IOException;
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
     * set the callback for receive the popFrameData from jFrame
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
    public String handleCefEditor(Stage mainActivityStage, String action) {
        FileChooser fileChooser = new FileChooser();
        File selectFile = null;
        switch (action) {
            case "open":
                fileChooser.setTitle("Open Resource File");
                selectFile = fileChooser.showOpenDialog(mainActivityStage);
                documentRoot = getMetalDocumentRoot(selectFile.getAbsolutePath());

                visualization(documentRoot);
                break;
            case "save":
                fileChooser.setTitle("Open Resource File");
                selectFile = fileChooser.showSaveDialog(mainActivityStage);
                break;

        }
        if (selectFile != null) {
            return selectFile.getAbsolutePath();
        } else {
            return null;
        }

    }

    /**
     * test method
     */
    public void test() {
        DocumentRoot root = getMetalDocumentRoot("C:\\Users\\SickoOrange\\Desktop\\multimedia_4x4_routed.xml");
        initGraph();
        visualization(root);
    }


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


                        Platform.runLater(() -> jFrameCallbacK.popFrameData(popData));


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
        initGraph();

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
            Object edge = graph.insertEdge(parent, null, "linkid: " + linkType.getId(), sourceVertex,
                    destinationVertex);
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
        mxIGraphLayout layout = new mxFastOrganicLayout(graph);
        // layout using morphing
        graph.getModel().beginUpdate();
        try {
            layout.execute(graph.getDefaultParent());
            new mxParallelEdgeLayout(graph).execute(graph.getDefaultParent());
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
        Platform.runLater(() -> jFrameCallbacK.popFrameData(popData));
    }

    /**
     * find block properties by block name
     *
     * @param name block name
     */
    public BlockType findBlockByName(String name) {
        EList<BlockType> blockList = documentRoot.getCef().getSystem().getBlocks().getBlock();
        return CefModifyUtils.findBlockByName(name, blockList);
    }


    /**
     * find the link type from the document root by link id
     *
     * @param o link id
     * @return linkType+sourceBlock+destinationBlock
     */
    public Object[] findLinkandBlockByLinkId(BigInteger o) {
        EList<LinkType> linksList = documentRoot.getCef().getSystem().getLinks().getLink();
        EList<BlockType> blocksList = documentRoot.getCef().getSystem().getBlocks().getBlock();
        //Generate the linksList array that kann hold linkType, sourceBlock, and destinationBlock
        Object[] linkResource = new Object[3];
        for (LinkType linkType : linksList) {
            if (linkType.getId().compareTo(o) == 0) {
                //find the link, that we need from the link id
                BigInteger sourcePortId = linkType.getSourcePortId();
                BigInteger destinationPortId = linkType.getDestinationPortId();
                BlockType sourceBlock = findBlockFromPortId(sourcePortId, blocksList);
                BlockType destinationBlock = findBlockFromPortId(destinationPortId, blocksList);
                linkResource[0] = linkType;
                linkResource[1] = sourceBlock;
                linkResource[2] = destinationBlock;
                return linkResource;
            }
        }

        return null;
    }


    /**
     * return a block pane,that we can edit the properties for this block
     *
     * @param block blockType
     * @param tag   source oder destination block
     * @return blockpane
     */
    public AnchorPane getBlockPane(BlockType block, String tag) throws IOException {
        AnchorPane blockPane = FXMLLoader.load(getClass().getResource("../layout/block.fxml"));
        //pane container for edit group
        ObservableList<Node> editGroupForBlock = ((Pane) blockPane.getChildren().get(0)).getChildren();
        for (Node node : editGroupForBlock) {
            if (node instanceof JFXTextField) {
                JFXTextField textField = (JFXTextField) node;
                //get text filed id
                String id = textField.getId();
                id = id.split("_")[1];
                switch (id) {
                    case "name":
                        textField.setText(block.getName());
                        break;
                    case "id":
                        textField.setText(String.valueOf(block.getId()));
                        break;
                    case "blockType":
                        textField.setText(String.valueOf(block.getBlockType()));
                        break;
                    case "layer":
                        textField.setText(String.valueOf(block.getLayer()));
                        break;
                }
            }
        }

        //mark the block source or destination
        if (tag != null) {
            JFXButton node = (JFXButton) blockPane.getChildren().get(1);
            node.setText(tag + "\n" + "BlockType");
        }

        return blockPane;
    }


    /**
     * get all port pane that belong to this block
     *
     * @param block blockType
     * @return portPaneList that belong to this block
     * @throws IOException
     */
    public ArrayList<AnchorPane> getPortPane(BlockType block) throws IOException {
        ArrayList<AnchorPane> portPaneList = new ArrayList<>();
        EList<PortType> portList = block.getPorts().getPort();
        for (PortType port : portList) {
            AnchorPane portPane = FXMLLoader.load(getClass().getResource("../layout/port.fxml"));
            ObservableList<Node> editGroupForPort = ((Pane) portPane.getChildren().get(0)).getChildren();
            for (Node node : editGroupForPort) {
                if (node instanceof JFXTextField) {
                    JFXTextField textField = (JFXTextField) node;
                    String id = textField.getId().split("_")[1];
                    System.out.println(id);
                    switch (id) {
                        case "id":
                            textField.setText(String.valueOf(port.getId()));
                            break;
                        case "protocol":
                            System.out.println(": " + port.getProtocol());
                            textField.setText(port.getProtocol());
                            break;
                        case "maxOutstandingTransactions":
                            textField.setText(String.valueOf(port.getMaxOutstandingTransactions()));
                            break;
                        case "addressWidth":
                            textField.setText(String.valueOf(port.getAddressWidth()));
                            break;
                        case "readDataWidth":
                            textField.setText(String.valueOf(port.getReadDataWidth()));
                            break;
                        case "writeDataWidth":
                            textField.setText(String.valueOf(port.getWriteDataWidth()));
                            break;
                        case "flitWidth":
                            textField.setText(String.valueOf(port.getFlitWidth()));
                            break;
                        case "positionX":
                            textField.setText(String.valueOf(port.getPositionX()));
                            break;
                        case "positionY":
                            textField.setText(String.valueOf(port.getPositionY()));
                            break;
                        case "domainId":
                            textField.setText(String.valueOf(port.getDomainId()));
                            break;
                    }

                }
            }

            //add port pane to the container
            portPaneList.add(portPane);
        }

        return portPaneList;
    }

    //get link pane from the link Type that we can edit the properties for this link
    public AnchorPane getLinkPane(LinkType linkType) throws IOException {
        AnchorPane linkPane = FXMLLoader.load(getClass().getResource("../layout/link.fxml"));
        //pane container for edit group
        ObservableList<Node> editGroupForBlock = ((Pane) linkPane.getChildren().get(0)).getChildren();
        for (Node node : editGroupForBlock) {
            if (node instanceof JFXTextField) {
                JFXTextField textField = (JFXTextField) node;
                //get text filed id
                String id = textField.getId();
                id = id.split("_")[1];
                switch (id) {
                    case "name":
                        textField.setText(linkType.getName());
                        break;
                    case "id":
                        textField.setText(String.valueOf(linkType.getId()));
                        break;
                    case "sourcePortId":
                        textField.setText(String.valueOf(linkType.getSourcePortId()));
                        break;
                    case "destinationPortId":
                        textField.setText(String.valueOf(linkType.getDestinationPortId()));
                        break;
                    case "carriesSourceClock":
                        textField.setText(String.valueOf(linkType.isCarriesSourceClock()));
                        break;
                    case "carriesSourceReset":
                        textField.setText(String.valueOf(linkType.isCarriesSourceReset()));
                        break;
                    case "linkLengthEstimation":
                        textField.setText(String.valueOf(linkType.getLinkLengthEstimation()));
                        break;
                    case "auxiliaryForwardWires":
                        textField.setText(String.valueOf(linkType.getAuxiliaryForwardWires()));
                        break;
                    case "auxiliaryBackwardWires":
                        textField.setText(String.valueOf(linkType.getAuxiliaryBackwardWires()));
                        break;
                }
            }
        }

        return linkPane;
    }


    public void popCommand(String command) {
        jFrameCallbacK.popFrameData(command);
    }


    /**
     * add a add_block_cardView to the content
     *
     * @param vBox
     */
    public void addBlockCard(VBox vBox) throws IOException {
        AnchorPane blockPane = FXMLLoader.load(getClass().getResource("../layout/modify_block_event.fxml"));
        vBox.getChildren().add(blockPane);
    }

    /**
     * add a port_edit_card to the layout
     *
     * @param vBox
     */
    public void addPortCard(VBox vBox) throws IOException {
        AnchorPane portPane = FXMLLoader.load(getClass().getResource("../layout/modify_port_event.fxml"));
        vBox.getChildren().add(portPane);
    }

    /**
     * add a link_edit_card to the layout
     *
     * @param vBox
     */
    public void addLinkCard(VBox vBox) throws IOException {
        AnchorPane portPane = FXMLLoader.load(getClass().getResource("../layout/modify_link_event.fxml"));
        vBox.getChildren().add(portPane);
    }

    /**
     * scan all the Block(Vertex) in the graph
     * add the delete block pane in the layout
     *
     * @param vBox
     */
    public void addDeleteBlockCard(VBox vBox) {
        if (documentRoot == null) {
            return;
        }

        new Thread(() -> {
            EList<BlockType> blocksList = documentRoot.getCef().getSystem().getBlocks().getBlock();
            int blockCounter = blocksList.size();
            //calculate how many hbox we need, to storage the delete Block Pane
            //every hbox can hold maximal 6 delete block pane
            HBox[] boxArray = new HBox[blockCounter / 5 + 1];
            for (int i = 0; i < boxArray.length; i++) {
                boxArray[i] = new HBox();
            }

            for (BlockType block : blocksList) {
                int index = blocksList.indexOf(block);
                AnchorPane deleteBlockPane = null;
                try {
                    deleteBlockPane = FXMLLoader.load(getClass().getResource("../layout/delete_block_event.fxml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //pane container for edit group
                ObservableList<Node> editGroupForBlock = ((Pane) deleteBlockPane.getChildren().get(0)).getChildren();
                for (Node node : editGroupForBlock) {
                    if (node instanceof JFXTextField) {
                        JFXTextField textField = (JFXTextField) node;
                        //get text filed id
                        String id = textField.getId();
                        id = id.split("_")[1];
                        switch (id) {
                            case "name":
                                textField.setText(block.getName());
                                break;
                            case "id":
                                textField.setText(String.valueOf(block.getId()));
                                break;
                            case "blockType":
                                textField.setText(String.valueOf(block.getBlockType()));
                                break;
                            case "layer":
                                textField.setText(String.valueOf(block.getLayer()));
                                break;
                        }
                    }
                }


                int mod = index / 5;
                boxArray[mod].getChildren().add(deleteBlockPane);
            }
            Platform.runLater(() -> {
                for (HBox hBox : boxArray) {
                    vBox.getChildren().add(hBox);
                }

            });

        }).start();


    }

    /**
     * scan all the Link(Edge) in the graph
     * add the delete block pane in the layout
     *
     * @param vBox
     */
    public void addDeleteLinkCard(VBox vBox) {
        if (documentRoot == null) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                EList<LinkType> linkList = documentRoot.getCef().getSystem().getLinks().getLink();
                int blockCounter = linkList.size();
                //calculate how many hbox we need, to storage the delete Block Pane
                //every hbox can hold maximal 6 delete block pane
                HBox[] boxArray = new HBox[blockCounter / 5 + 1];
                for (int i = 0; i < boxArray.length; i++) {
                    boxArray[i] = new HBox();
                }

                for (LinkType link : linkList) {
                    int index = linkList.indexOf(link);
                    AnchorPane deleteBlockPane = null;
                    try {
                        deleteBlockPane = FXMLLoader.load(getClass().getResource("../layout/delete_link_event.fxml"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //pane container for edit group
                    ObservableList<Node> editGroupForBlock = ((Pane) deleteBlockPane.getChildren().get(0)).getChildren();
                    for (Node node : editGroupForBlock) {
                        if (node instanceof JFXTextField) {
                            JFXTextField textField = (JFXTextField) node;
                            //get text filed id
                            String id = textField.getId();
                            id = id.split("_")[1];
                            switch (id) {
                                case "name":
                                    textField.setText(link.getName());
                                    break;
                                case "id":
                                    textField.setText(String.valueOf(link.getId()));
                                    break;
                                case "sourcePortId":
                                    textField.setText(String.valueOf(link.getSourcePortId()));
                                    break;
                                case "destinationPortId":
                                    textField.setText(String.valueOf(link.getDestinationPortId()));
                                    break;
                                case "carriesSourceClock":
                                    textField.setText(String.valueOf(link.isCarriesSourceClock()));
                                    break;
                                case "carriesSourceReset":
                                    textField.setText(String.valueOf(link.isCarriesSourceReset()));
                                    break;
                                case "linkLengthEstimation":
                                    textField.setText(String.valueOf(link.getLinkLengthEstimation()));
                                    break;
                                case "auxiliaryForwardWires":
                                    textField.setText(String.valueOf(link.getAuxiliaryForwardWires()));
                                    break;
                                case "auxiliaryBackwardWires":
                                    textField.setText(String.valueOf(link.getAuxiliaryBackwardWires()));
                                    break;
                            }
                        }
                    }


                    int mod = index / 5;
                    boxArray[mod].getChildren().add(deleteBlockPane);
                }
                Platform.runLater(() -> {
                    for (HBox hBox : boxArray) {
                        vBox.getChildren().add(hBox);
                    }
                });

            }
        }).start();
    }


    /**
     * save the element to the document root
     */
    public void save() {
        // TODO: 2017/5/15  save the document to the file, update the graph,

        if (documentRoot != null) {
            graph.clearSelection();
            visualization(documentRoot);
        } else {
            CefModifyUtils.alertDialog("can't save the file, pls select a xml file first");
        }

    }

    public void addBlockToDocumentRoot(BlockType newBlockType) {
        if (documentRoot != null) {
            CefModifyUtils.addBlock(documentRoot, newBlockType);
        } else {
            CefModifyUtils.alertDialog("please select a xml file");
        }

    }


    public void addLinkToDocumentRoot(LinkType newLinkType) {
        if (documentRoot != null) {
            CefModifyUtils.addLinkToDocumentRoot(documentRoot, newLinkType);
        } else {
            CefModifyUtils.alertDialog("please select a xml file");
        }
    }

    public void deleteBlock(String blockName) {
        ArrayList<LinkType> linkList = CefModifyUtils.findLinkByBlockName(blockName, documentRoot);
        System.out.println("delete block and link: " + linkList.size());
        for (LinkType linkType : linkList) {
            deleteLink(linkType.getId());
        }
        CefModifyUtils.deleteBlock(blockName, documentRoot);
    }

    public void deleteLink(BigInteger linkId) {
        CefModifyUtils.deleteLink(linkId, documentRoot);
    }
}