package org.tum.project.dataservice;

import Cef.*;
import Cef.util.CefResourceFactoryImpl;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
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
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.tum.project.dashboard_controller.CefEditorController;
import org.tum.project.dashboard_controller.DashBoardController;
import org.tum.project.utils.CefModifyUtils;
import org.tum.project.callback.JFrameCallback;
import org.tum.project.controller.MainController;
import org.tum.project.utils.Utils;

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
    private AnchorPane handleButtonGroup;
    private javafx.scene.control.ScrollPane cefEditorRoot;
    private VBox cefEditorVBox;
    private StackPane dialogHolder;
    private JFXDialog dialog;
    private File file;


    /**
     * set the callback for receive the popFrameData from jFrame
     *
     * @param jFrameCallbacK
     */
    public void setJframeCallback(JFrameCallback jFrameCallbacK) {
        this.jFrameCallbacK = jFrameCallbacK;
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
     * vBox  *
     *
     * @param
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
            if (blocksList.size() == 0) {
                return;
            }
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
                if (linkList.size() == 0) {
                    return;
                }
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
    @Deprecated
    public void save() {
        if (documentRoot != null) {
            //save the document
            File file = Utils.openFileChooser("save", MainController.getStage());
            documentRoot = CefModifyUtils.saveCef(this.documentRoot.getCef(), file);

            graph.clearSelection();
            visualization(documentRoot);
        } else {
            CefModifyUtils.alertDialog("can't save the file, pls select a xml file first");
        }

    }

    /**
     * save the element to the document root
     */
    public Object[] saveFile() {
        File file = null;
        Object[] objects = new Object[2];
        if (documentRoot != null) {
            //save the document

            file = Utils.openFileChooser("save", MainController.getStage());
            objects[0] = file.getAbsolutePath();
            documentRoot = CefModifyUtils.saveCef(this.documentRoot.getCef(), file);

            graph.clearSelection();
            //visualization(documentRoot);
            mxGraphComponent target = visualizationWithPane(documentRoot);
            objects[1] = target;
        } else {
            CefModifyUtils.alertDialog("can't save the file, pls select a xml file first");
        }


        return objects;

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


    /**
     * open the file chooser to select a xml file
     * and start visualization with the specified file(cef ecore model)
     * this method is not appropriate with the new material design user interface
     * its deprecated and only be used for use old user interface
     *
     * @param mainActivityStage documentRoot for main ui
     * @param action            open oder save
     */
    @Deprecated
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
     * start visualization with specified file name(cef ecore file)
     * this method is be added for new material ui==>CefEditorController
     *
     * @param absolutePath file path
     * @param sp_editor    stack pane that will hold a material dialog and show the edit part for Ecore file
     */

    public mxGraphComponent startVisualization(String absolutePath, StackPane sp_editor) {
        documentRoot = getMetalDocumentRoot(absolutePath);
        dialogHolder = sp_editor;

        return visualizationWithPane(documentRoot);
    }

    /**
     * get the root element => documentRoot from the selected xml file
     *
     * @param absolutePath cef ecore file absolute path
     * @return documentRoot root element for this file
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


    /**
     * visualization for this model in the graph
     * its appropriate only with old user interface
     *
     * @param root cef document documentRoot
     */
    @Deprecated
    private void visualization(DocumentRoot root) {
        initGraph();

        Object parent = graph.getDefaultParent();
        graph.getModel().beginUpdate();

        //get all block list
        EList<BlockType> blocksList = root.getCef().getSystem().getBlocks().getBlock();
        if (blocksList.size() == 0) {
            System.out.println("null block");
            return;
        }
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
        if (linksList.size() == 0) {
            System.out.println("null link");
            return;
        }
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
     * visualization for this model in the graph
     *
     * @param root cef document documentRoot
     */
    private mxGraphComponent visualizationWithPane(DocumentRoot root) {
        initGraphForNewUI();

        Object parent = graph.getDefaultParent();
        graph.getModel().beginUpdate();

        //get all block list
        EList<BlockType> blocksList = root.getCef().getSystem().getBlocks().getBlock();
        if (blocksList.size() == 0) {
            System.out.println("null block");
            return null;
        }
        vertexList = new ArrayList<>();
        for (BlockType block : blocksList) {
            BigInteger blockType = block.getBlockType();
            Object vertex = null;
            if (blockType.toString().equals("0")) {
                //0- router style round

                vertex = graph.insertVertex(parent, null, block.getName(), 20, 20, 80, 30);
            } else if (blockType.toString().equals("1")) {
                //1- processing element
                vertex = graph.insertVertex(parent, null, block.getName(), 20, 20, 80, 30);
            }
            vertexList.add(vertex);

        }

        //get all link list
        EList<LinkType> linksList = root.getCef().getSystem().getLinks().getLink();
        if (linksList.size() == 0) {
            System.out.println("null link");
            return null;
        }
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
        return graphComponent;
    }


    /**
     * init the setting for generate a graph
     */
    @Deprecated
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
     * init the setting for generate a graph
     * only be used for new material user interface
     */
    // TODO: 17-6-8 适配新UI
    private void initGraphForNewUI() {

        graph = new mxGraph();
        //Vertices and Edges (Cells) not editable by default in JGraph
        graph.setCellsEditable(false);
        graph.setCellsResizable(false);
        graphComponent = new mxGraphComponent(graph);
        //Vertices and Edges (Cells) not movable by default in JGraph
        graphComponent.setDragEnabled(false);


        //set the mouse listener to the graphComponent
        //capture the double click event
        //capture the selected cell
        graphComponent.getGraphControl().addMouseListener(new mxMouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                //mouse event=> double click for specified element in the grapg
                System.out.println(mouseEvent.getButton());
                System.out.println(mouseEvent.getPoint());

                //button type 1=> mouse left, 2=> mouse middle, 3=> mouse right
                if (mouseEvent.getButton() == 3) {
                    Object selectedCell = graphComponent.getCellAt(mouseEvent.getX(), mouseEvent.getY());
                    if (selectedCell == null) {
                        //The clicked panel does not contain any element
                        //The default dialog behavior
                        //Add a new block or link
                        Platform.runLater(() -> showDialogWithContent("default", null, null));


                    } else {
                        if (isVertex(selectedCell)) {
                            //its vertex
                            //properties: block port ports list
                            String blockName = graph.convertValueToString(selectedCell);
                            System.out.println(blockName);
                            popData = blockName;
                            Platform.runLater(() -> showDialogWithContent("content", "BlockType: ", blockName));
                        }
                        if (isEdge(selectedCell)) {
                            //its edge
                            //properties: link, source port, destination port
                            BigInteger linkId = getEdgeId(selectedCell);
                            System.out.println(linkId);
                            popData = linkId;
                            Platform.runLater(() -> showDialogWithContent("content", "Link ID: ", linkId));


                        }
                    }
                }
                super.mouseReleased(mouseEvent);
            }
        });
    }

    /**
     * create a material design type dialog action
     * in this action has behavior as follows:
     * default: add block, add link
     * with content: add block, add link, delete element
     *
     * @param flag   set dialog content as default oder as "with content"
     * @param obj    information for the element, which need to be deleted
     * @param prefix indicate its block or link
     */
    private void showDialogWithContent(String flag, String prefix, Object obj) {
        JFXDialogLayout layout = new JFXDialogLayout();
        VBox boxContent = new VBox(5);
        switch (flag) {
            case "default":
                break;
            case "content":
                String describe = null;
                if (obj instanceof String) {
                    describe = (String) obj;
                } else if (obj instanceof BigInteger) {
                    BigInteger linkId = (BigInteger) obj;
                    describe = linkId.toString();
                }


                JFXButton button = new JFXButton("Delete Element=> " + prefix + describe);
                boxContent.getChildren().add(button);
                button.setOnAction(event -> {
                    try {
                        deleteElement(obj);
                    } catch (NullPointerException e) {
                        ObservableList<Node> children = dialog.getChildren();
                        System.out.println(children.size());
                        Text text = new Text("Delete failed, Please try again after refreshing!");
                        text.setFill(Color.RED);
                        boxContent.getChildren().add(text);
                    }
                });

                break;
        }

        JFXButton addBlock = new JFXButton("Add Block Type");
        addBlock.setOnAction(event -> {
            setBlockPropertiesAndAdd();
        });
        JFXButton addLink = new JFXButton("Add Link Type");
        addLink.setOnAction(event -> {
            setLinkPropertiesAndAdd();
        });
        boxContent.getChildren().addAll(addBlock, addLink);
        boxContent.setAlignment(Pos.CENTER);
        layout.setBody(boxContent);

        dialog = new JFXDialog(dialogHolder, layout, JFXDialog.DialogTransition.CENTER, true);
        layout.setHeading(new Label("Cef Control Pane"));
        JFXButton cancel = new JFXButton("Cancel");
        cancel.setOnAction(event -> dialog.close());
        JFXButton btn_ok = new JFXButton("Ok");
        btn_ok.setOnAction(event -> {
            // TODO: 17-6-9 collecting the information
        });
        layout.setActions(cancel, btn_ok);
        dialog.show();
    }

    /**
     * create a pane that hold all properties,which a link type has
     * user can edit the properties in this pane
     * after editing , user can direct add this link type to the cef ecore file
     */
    private void setLinkPropertiesAndAdd() {
        try {
            CefEditorController cefEditorController = (CefEditorController) DashBoardController.getDataServiceInstance(CefEditorController.class.getName());

            Pane container = cefEditorController.getAddPropertiesContainer();
            container.getChildren().clear();
            System.out.println("add link pane");
            javafx.scene.control.ScrollPane addBlockPane = FXMLLoader.load(getClass().getResource("../dashboard_controller/addLink.fxml"));
            addBlockPane.setLayoutX(18);
            addBlockPane.setLayoutY(20);
            container.getChildren().add(addBlockPane);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * create a pane that hold all properties,which a block type has
     * user can edit the properties in this pane
     * after editing , user can direct add this block type to the cef ecore file
     */
    private void setBlockPropertiesAndAdd() {
        try {
            CefEditorController cefEditorController = (CefEditorController) DashBoardController.getDataServiceInstance(CefEditorController.class.getName());

            Pane container = cefEditorController.getAddPropertiesContainer();
            container.getChildren().clear();
            System.out.println("add block pane");
            VBox addBlockPane = FXMLLoader.load(getClass().getResource("../dashboard_controller/addBlock.fxml"));
            addBlockPane.setLayoutX(18);
            addBlockPane.setLayoutY(20);
            container.getChildren().add(addBlockPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * delete the element in this graph
     *
     * @param obj obj indicate the type of the element
     */
    private void deleteElement(Object obj) {
        if (obj instanceof String) {
            deleteBlock((String) obj);
        } else if (obj instanceof BigInteger) {
            deleteLink((BigInteger) obj);
        }
        dialog.close();

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
     * refresh the diagram
     *
     * @return return the mx graph component
     */
    public mxGraphComponent refresh() {
        return visualizationWithPane(documentRoot);
    }
}