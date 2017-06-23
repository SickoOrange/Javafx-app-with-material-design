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
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
    public Object[] findLinkAndBlockByLinkId(BigInteger o) {
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
//            Object edge = graph.insertEdge(parent, null, "linkid: " + linkType.getId(), sourceVertex,
//                    destinationVertex);
            Object edge = graph.insertEdge(parent, null, null, sourceVertex,
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
                } else if (mouseEvent.getButton() == 1) {
                    // mouse left click
                    Object selectedCell = graphComponent.getCellAt(mouseEvent.getX(), mouseEvent.getY());
                    if (selectedCell != null) {
                        if (isVertex(selectedCell)) {
                            //its vertex
                            //properties: block port ports list
                            String blockName = graph.convertValueToString(selectedCell);
                            BlockType block = findBlockByName(blockName);
                            Platform.runLater(() -> setBlockPropertiesAndAddPane(block));
                        }
                        if (isEdge(selectedCell)) {
                            //its edge
                            //properties: link, source port, destination port
                            BigInteger linkId = getEdgeId(selectedCell);
                            Object[] resource = findLinkAndBlockByLinkId(linkId);
                            LinkType linkType = (LinkType) resource[0];
                            Platform.runLater(() -> setLinkPropertiesAndAddPane(linkType));
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
                JFXButton sourcePortBtn = null;
                JFXButton destinationPortBtn = null;
                String elementDescriber = null;
                if (obj instanceof String) {
                    elementDescriber = (String) obj;
                } else if (obj instanceof BigInteger) {
                    BigInteger linkId = (BigInteger) obj;
                    elementDescriber = linkId.toString();
                    //elementDescriber: integer=> link type  need to find the source and destination port information
                    Object[] linkResource = findLinkAndBlockByLinkId(linkId);
                    LinkType linkType = (LinkType) linkResource[0];
                    sourcePortBtn = new JFXButton("source port properties");
                    //find the source port information and provide the pane to edit his properties
                    sourcePortBtn.setOnAction(event -> {
                        BigInteger sourcePortId = linkType.getSourcePortId();
                        BlockType sourceBlock = (BlockType) linkResource[1];
                        PortType sourcePort = CefModifyUtils.getPort(sourcePortId, sourceBlock, documentRoot);
                        setPortPropertiesAndAddPane(sourcePort);
                    });

                    destinationPortBtn = new JFXButton("destination port properties");
                    destinationPortBtn.setOnAction(event -> {
                        BigInteger destinationPortId = linkType.getDestinationPortId();
                        BlockType destinationBlock = (BlockType) linkResource[2];
                        PortType destinationPort = CefModifyUtils.getPort(destinationPortId, destinationBlock, documentRoot);
                        setPortPropertiesAndAddPane(destinationPort);
                    });
                }

                //elementDescriber: integer=> link type  need to find the source and destination port information
                //elementDescriber: string=> block type
                JFXButton deleteElementBtn = new JFXButton("Delete Element=> " + prefix + elementDescriber);
                boxContent.getChildren().add(deleteElementBtn);
                deleteElementBtn.setOnAction(event -> {
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


                if ((sourcePortBtn != null) && (destinationPortBtn != null)) {
                    boxContent.getChildren().addAll(sourcePortBtn, destinationPortBtn);
                }


                break;
        }

        JFXButton addBlockBtn = new JFXButton("Add Block Type");
        addBlockBtn.setOnAction(event -> {
            setBlockPropertiesAndAddPane(null);

        });
        JFXButton addLinkBtn = new JFXButton("Add Link Type");
        addLinkBtn.setOnAction(event -> {
            setLinkPropertiesAndAddPane(null);

        });
        boxContent.getChildren().addAll(addBlockBtn, addLinkBtn);
        boxContent.setAlignment(Pos.CENTER);
        layout.setBody(boxContent);

        dialog = new JFXDialog(dialogHolder, layout, JFXDialog.DialogTransition.CENTER, true);
        layout.setHeading(new Label("Cef Control Pane"));
        JFXButton cancel = new JFXButton("Cancel");
        cancel.setOnAction(event -> dialog.close());
        JFXButton btn_ok = new JFXButton("Ok");
        btn_ok.setOnAction(event -> {
            //collect the properties information of the block type or link type
            CefEditorController cefEditorController = (CefEditorController) DashBoardController.getDataServiceInstance(CefEditorController.class.getName());

            Pane container = cefEditorController.getAddPropertiesContainer();
            if (container.getChildren().get(0) instanceof VBox) {
                //this is a block properties
                VBox box = (VBox) container.getChildren().get(0);
                collectBlockProperties(box);

            } else if (container.getChildren().get(0) instanceof ScrollPane) {
                //this is a link properties
                ScrollPane pane = (ScrollPane) container.getChildren().get(0);

                if (pane.getId().equals("addLinkPane")) {
                    VBox box = (VBox) pane.getContent();
                    collectLinkProperties(box);
                }

                if (pane.getId().equals("addPortPane")) {
                    VBox box = (VBox) pane.getContent();
                    collectPortProperties(box);
                }

            }
            dialog.close();

        });
        layout.setActions(cancel, btn_ok);
        dialog.show();
    }

    /**
     * collect the information of a port type and submit to the ecore file
     *
     * @param box
     */
    private void collectPortProperties(VBox box) {
        String label_id = ((JFXTextField) box.getChildren().get(1)).getText();
        String label_protocol = ((JFXTextField) box.getChildren().get(2)).getText();
        String label_maxoutstanding = ((JFXTextField) box.getChildren().get(3)).getText();
        String label_addressWidth = ((JFXTextField) box.getChildren().get(4)).getText();
        String label_readDataWidth = ((JFXTextField) box.getChildren().get(5)).getText();
        String label_writeDataWidth = ((JFXTextField) box.getChildren().get(6)).getText();
        String label_flitsWidth = ((JFXTextField) box.getChildren().get(7)).getText();
        String label_positionX = ((JFXTextField) box.getChildren().get(8)).getText();
        String label_positionY = ((JFXTextField) box.getChildren().get(9)).getText();
        String label_DomainId = ((JFXTextField) box.getChildren().get(10)).getText();

        if (!label_id.equals("")) {
            PortType modifyPort = CefFactory.eINSTANCE.createPortType();

            modifyPort.setId(label_id.length() == 0 ? null : new BigInteger(label_id));
            modifyPort.setProtocol(label_protocol.length() == 0 ? null : label_protocol);
            modifyPort.setMaxOutstandingTransactions(label_maxoutstanding.length() == 0 ? null : new BigInteger(label_maxoutstanding));
            modifyPort.setAddressWidth(label_addressWidth.length() == 0 ? null : new BigInteger(label_addressWidth));
            modifyPort.setReadDataWidth(label_readDataWidth.length() == 0 ? null : new BigInteger(label_readDataWidth));
            modifyPort.setWriteDataWidth(label_writeDataWidth.length() == 0 ? null : new BigInteger(label_writeDataWidth));
            modifyPort.setFlitWidth(label_flitsWidth.length() == 0 ? null : new BigInteger(label_flitsWidth));
            modifyPort.setPositionX(label_positionX.length() == 0 ? null : new Double(label_positionX));
            modifyPort.setPositionY(label_positionY.length() == 0 ? null : new Double(label_positionY));
            modifyPort.setDomainId(label_DomainId.length() == 0 ? null : new BigInteger(label_DomainId));

            if (documentRoot != null) {
                EList<BlockType> blocksList = documentRoot.getCef().getSystem().getBlocks().getBlock();
                BlockType targetBlock = findBlockFromPortId(modifyPort.getId(), blocksList);
                CefModifyUtils.addPortToBlock(targetBlock, modifyPort);
            }
        } else {
            CefModifyUtils.alertDialog("port id can't be null");
        }

    }


    /**
     * collect the information of a link type and submit to the ecore file
     *
     * @param box
     */
    private void collectLinkProperties(VBox box) {

        String name = ((JFXTextField) box.getChildren().get(1)).getText();
        String id = ((JFXTextField) box.getChildren().get(2)).getText();
        String sourcePortId = ((JFXTextField) box.getChildren().get(3)).getText();
        String destinationPortId = ((JFXTextField) box.getChildren().get(4)).getText();
        String carriesSourceClock = ((JFXTextField) box.getChildren().get(5)).getText();
        String carriesSourceReset = ((JFXTextField) box.getChildren().get(6)).getText();
        String linkLengthEstimation = ((JFXTextField) box.getChildren().get(7)).getText();
        String auxilliaryForwardWires = ((JFXTextField) box.getChildren().get(8)).getText();
        String auxilliaryBackwardWires = ((JFXTextField) box.getChildren().get(9)).getText();


        if ((!id.equals("")) && (!sourcePortId.equals("")) && (!destinationPortId.equals(""))) {
            LinkType newLinkType = CefFactory.eINSTANCE.createLinkType();
            newLinkType.setName(name.length() == 0 ? null : name);
            newLinkType.setId(id.length() == 0 ? new BigInteger("0") : new BigInteger(id));
            newLinkType.setSourcePortId(sourcePortId.length() == 0 ? new BigInteger("0") : new BigInteger
                    (sourcePortId));
            newLinkType.setDestinationPortId(destinationPortId.length() == 0 ? new BigInteger("0") : new BigInteger
                    (destinationPortId));

            newLinkType.setCarriesSourceClock(carriesSourceClock.length() == 0 ? Boolean.FALSE : Boolean.valueOf(carriesSourceClock));

            newLinkType.setCarriesSourceReset(carriesSourceReset.length() == 0 ? Boolean.FALSE : Boolean.valueOf
                    (carriesSourceReset));

            newLinkType.setLinkLengthEstimation(linkLengthEstimation.length() == 0 ? new Double(0) : new
                    Double(linkLengthEstimation));

            newLinkType.setAuxiliaryForwardWires(auxilliaryForwardWires.length() == 0 ? new BigInteger("0") : new
                    BigInteger
                    (auxilliaryForwardWires));

            newLinkType.setAuxiliaryBackwardWires(auxilliaryBackwardWires.length() == 0 ? new BigInteger("0") : new
                    BigInteger
                    (auxilliaryBackwardWires));

            System.out.println("new link type:\n" + newLinkType);
            addLinkToDocumentRoot(newLinkType);


        } else {
            CefModifyUtils.alertDialog("link id, source port id and destination id can't be null");
        }

    }


    /**
     * collect the information of a block type and submit to the ecore file
     *
     * @param box
     */
    private void collectBlockProperties(VBox box) {
        String name = ((JFXTextField) box.getChildren().get(1)).getText();
        String id = ((JFXTextField) box.getChildren().get(2)).getText();
        String blockType = ((JFXTextField) box.getChildren().get(3)).getText();
        String layer = ((JFXTextField) box.getChildren().get(4)).getText();
        if (!name.equals("")) {
            BlockType newBlockType = CefFactory.eINSTANCE.createBlockType();
            newBlockType.setName(name);
            newBlockType.setId(id.length() == 0 ? new BigInteger("0") : new BigInteger(id));
            newBlockType.setBlockType(blockType.length() == 0 ? new BigInteger("0") : new BigInteger(blockType));
            newBlockType.setLayer(layer.length() == 0 ? new BigInteger("0") : new BigInteger(layer));
            System.out.println("new block type:\n" + newBlockType);
            addBlockToDocumentRoot(newBlockType);


        } else {
            CefModifyUtils.alertDialog("Block Name can't be null");
        }
    }

    /**
     * create a pane that hold all properties, which a port type has
     * user can edit the properties in this pane
     * after editing, user can direct update this port type to the cef ecore dile
     *
     * @param port port type
     */
    private void setPortPropertiesAndAddPane(PortType port) {
        try {
            CefEditorController cefEditorController = (CefEditorController) DashBoardController.getDataServiceInstance(CefEditorController.class.getName());

            Pane container = cefEditorController.getAddPropertiesContainer();
            container.getChildren().clear();
            javafx.scene.control.ScrollPane addPortPane = FXMLLoader.load(getClass().getResource("../dashboard_controller/addPort.fxml"));
            addPortPane.setId("addPortPane");
            fillPortPaneContent(addPortPane, port);
            addPortPane.setLayoutX(18);
            addPortPane.setLayoutY(20);
            addPortPane.setFitToWidth(true);
            container.getChildren().add(addPortPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * fill the existing properties to the pane
     *
     * @param pane port pane
     * @param port port type
     */
    private void fillPortPaneContent(ScrollPane pane, PortType port) {
        if (port == null) {
            return;
        }
        if (pane.getContent() instanceof VBox) {
            VBox box = (VBox) pane.getContent();
            JFXTextField label_id = (JFXTextField) box.getChildren().get(1);
            label_id.setText(String.valueOf(port.getId()));

            JFXTextField label_protocol = (JFXTextField) box.getChildren().get(2);
            label_protocol.setText(String.valueOf(port.getProtocol()));

            JFXTextField label_maxoutstanding = (JFXTextField) box.getChildren().get(3);
            label_maxoutstanding.setText(String.valueOf(port.getMaxOutstandingTransactions()));

            JFXTextField label_addressWidth = (JFXTextField) box.getChildren().get(4);
            label_addressWidth.setText(String.valueOf(port.getAddressWidth()));

            JFXTextField label_readDataWidth = (JFXTextField) box.getChildren().get(5);
            label_readDataWidth.setText(String.valueOf(port.getReadDataWidth()));

            JFXTextField label_writeDataWidth = (JFXTextField) box.getChildren().get(6);
            label_writeDataWidth.setText(String.valueOf(port.getWriteDataWidth()));

            JFXTextField label_flitsWidth = (JFXTextField) box.getChildren().get(7);
            label_flitsWidth.setText(String.valueOf(port.getFlitWidth()));

            JFXTextField label_positionX = (JFXTextField) box.getChildren().get(8);
            label_positionX.setText(String.valueOf(port.getPositionX()));

            JFXTextField label_positionY = (JFXTextField) box.getChildren().get(9);
            label_positionY.setText(String.valueOf(port.getPositionY()));

            JFXTextField label_DomainId = (JFXTextField) box.getChildren().get(10);
            label_DomainId.setText(String.valueOf(port.getDomainId()));

        }
    }

    /**
     * create a pane that hold all properties,which a link type has
     * user can edit the properties in this pane
     * after editing , user can direct add or update this link type to the cef ecore file
     *
     * @param linkType
     */
    private void setLinkPropertiesAndAddPane(LinkType linkType) {
        try {
            CefEditorController cefEditorController = (CefEditorController) DashBoardController.getDataServiceInstance(CefEditorController.class.getName());

            Pane container = cefEditorController.getAddPropertiesContainer();
            container.getChildren().clear();
            javafx.scene.control.ScrollPane addLinkPane = FXMLLoader.load(getClass().getResource("../dashboard_controller/addLink.fxml"));
            fillLinkPaneContent(addLinkPane, linkType);
            addLinkPane.setId("addLinkPane");
            addLinkPane.setLayoutX(18);
            addLinkPane.setLayoutY(20);
            addLinkPane.setFitToWidth(true);
            container.getChildren().add(addLinkPane);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * fill the existing properties to the pane
     *
     * @param pane     add link pane
     * @param linkType link type
     */
    private void fillLinkPaneContent(ScrollPane pane, LinkType linkType) {
        if (linkType == null) {
            return;
        }
        if (pane.getContent() instanceof VBox) {
            VBox box = (VBox) pane.getContent();
            JFXTextField label_name = (JFXTextField) box.getChildren().get(1);
            label_name.setText(String.valueOf(linkType.getName()));

            JFXTextField label_id = (JFXTextField) box.getChildren().get(2);
            label_id.setText(String.valueOf(linkType.getId()));

            JFXTextField label_sourcePortId = (JFXTextField) box.getChildren().get(3);
            label_sourcePortId.setText(String.valueOf(linkType.getSourcePortId()));

            JFXTextField label_targetPortId = (JFXTextField) box.getChildren().get(4);
            label_targetPortId.setText(String.valueOf(linkType.getDestinationPortId()));

            JFXTextField label_isCarriesSourceClock = (JFXTextField) box.getChildren().get(5);
            label_isCarriesSourceClock.setText(String.valueOf(linkType.isCarriesSourceClock()));

            JFXTextField label_isCarriesSourceReset = (JFXTextField) box.getChildren().get(6);
            label_isCarriesSourceReset.setText(String.valueOf(linkType.isCarriesSourceReset()));

            JFXTextField label_linkLengthEstimation = (JFXTextField) box.getChildren().get(7);
            label_linkLengthEstimation.setText(String.valueOf(linkType.getLinkLengthEstimation()));

            JFXTextField label_auxiliaryForwardWires = (JFXTextField) box.getChildren().get(8);
            label_auxiliaryForwardWires.setText(String.valueOf(linkType.getAuxiliaryForwardWires()));

            JFXTextField label_auxiliaryBackwardWires = (JFXTextField) box.getChildren().get(9);
            label_auxiliaryBackwardWires.setText(String.valueOf(linkType.getAuxiliaryBackwardWires()));

        }
    }

    /**
     * create a pane that hold all properties,which a block type has
     * user can edit the properties in this pane
     * after editing , user can direct add this block type to the cef ecore file
     *
     * @param block
     */
    private void setBlockPropertiesAndAddPane(BlockType block) {
        try {
            CefEditorController cefEditorController = (CefEditorController) DashBoardController.getDataServiceInstance(CefEditorController.class.getName());

            Pane container = cefEditorController.getAddPropertiesContainer();
            container.getChildren().clear();
            VBox addBlockPane = FXMLLoader.load(getClass().getResource("../dashboard_controller/addBlock.fxml"));
            fillBlockPaneContent(addBlockPane, block);
            addBlockPane.setLayoutX(18);
            addBlockPane.setLayoutY(20);
            addBlockPane.setFillWidth(true);
            container.getChildren().add(addBlockPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * fill the existing properties to the pane
     *
     * @param addBlockPane add block pane
     * @param block        block type
     */
    private void fillBlockPaneContent(VBox addBlockPane, BlockType block) {
        if (block == null) {
            return;
        }
        JFXTextField label_name = (JFXTextField) addBlockPane.getChildren().get(1);
        label_name.setText(String.valueOf(block.getName()));

        JFXTextField label_id = (JFXTextField) addBlockPane.getChildren().get(2);
        label_id.setText(String.valueOf(block.getId()));

        JFXTextField label_blockType = (JFXTextField) addBlockPane.getChildren().get(3);
        label_blockType.setText(String.valueOf(block.getBlockType()));

        JFXTextField label_layer = (JFXTextField) addBlockPane.getChildren().get(4);
        label_layer.setText(String.valueOf(block.getLayer()));

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