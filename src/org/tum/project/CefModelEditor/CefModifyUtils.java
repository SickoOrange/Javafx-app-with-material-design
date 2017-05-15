package org.tum.project.CefModelEditor;

import Cef.BlockType;
import Cef.DocumentRoot;
import Cef.LinkType;
import Cef.PortType;
import javafx.scene.control.Alert;
import org.eclipse.emf.common.util.EList;

import java.math.BigInteger;

/**
 * document root modify utils
 * Created by Yin Ya on 2017/5/15.
 */
public class CefModifyUtils {

    /**
     * add the block to the document root
     *
     * @param documentRoot document root
     * @param newBlockType new block type
     */
    public static void addBlock(DocumentRoot documentRoot, BlockType newBlockType) {
        EList<BlockType> blocks = documentRoot.getCef().getSystem().getBlocks().getBlock();
        //System.out.println(blocks.size());
        boolean adding = false;
        for (BlockType block : blocks) {
            if (block.getName().equals(newBlockType.getName())) {
                //block existing
                block.setId(newBlockType.getId());
                block.setBlockType(newBlockType.getBlockType());
                block.setLayer(newBlockType.getLayer());
                adding = false;
                break;
            } else {
                // adding new block to the document
                adding = true;
            }
        }

        if (adding) {
            System.out.println("add new type");
            blocks.add(newBlockType);
        }
    }


    public static void alertDialog(String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Look, a Warning Dialog");
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * add the port to the block
     *
     * @param targetBlock target block
     * @param newPortType new port, need to add to the block
     */
    public static void addPortToBlock(BlockType targetBlock, PortType newPortType) {
        EList<PortType> portList = targetBlock.getPorts().getPort();
        boolean adding = false;
        for (PortType portType : portList) {
            if (portType.getId().compareTo(newPortType.getId()) == 0) {
                //find a port
                System.out.println("find a port");
                portType.setId(newPortType.getId());
                portType.setProtocol(newPortType.getProtocol());
                portType.setMaxOutstandingTransactions(newPortType.getMaxOutstandingTransactions());
                portType.setAddressWidth(newPortType.getAddressWidth());
                portType.setReadDataWidth(newPortType.getReadDataWidth());
                portType.setWriteDataWidth(newPortType.getWriteDataWidth());
                portType.setFlitWidth(newPortType.getFlitWidth());
                portType.setPositionX(newPortType.getPositionX());
                portType.setPositionY(newPortType.getPositionY());
                portType.setDomainId(newPortType.getDomainId());
                adding = false;
                break;
            } else {
                adding = true;
            }
        }

        if (adding) {
            System.out.println("add a port");
            portList.add(newPortType);
        }

    }

    /**
     * add a link to document root
     *
     * @param documentRoot document root
     * @param newLinkType  new link type
     */
    public static void addLinkToDocumentRoot(DocumentRoot documentRoot, LinkType newLinkType) {
        System.out.println("read to add");
        EList<LinkType> linkList = documentRoot.getCef().getSystem().getLinks().getLink();
        boolean findSource = findPort(newLinkType.getSourcePortId(), documentRoot);
        boolean findDestination = findPort(newLinkType.getDestinationPortId(), documentRoot);
        boolean adding = false;
        if (findSource && findDestination) {
            //source port and destination port exist
            System.out.println("can add new link");
            for (LinkType linkType : linkList) {
                if (linkType.getId().compareTo(newLinkType.getId()) == 0) {
                    //find this link in the document root
                    //update the link
                    System.out.println("update link");
                    linkType.setId(newLinkType.getId());
                    linkType.setName(newLinkType.getName());
                    linkType.setSourcePortId(newLinkType.getSourcePortId());
                    linkType.setDestinationPortId(newLinkType.getDestinationPortId());
                    linkType.setCarriesSourceClock(newLinkType.isCarriesSourceClock());
                    linkType.setCarriesSourceReset(newLinkType.isCarriesSourceReset());
                    linkType.setLinkLengthEstimation(newLinkType.getLinkLengthEstimation());
                    linkType.setAuxiliaryBackwardWires(newLinkType.getAuxiliaryBackwardWires());
                    linkType.setAuxiliaryForwardWires(newLinkType.getAuxiliaryForwardWires());
                    adding = false;
                    break;
                } else {
                    adding = true;
                }
            }

            if (adding) {
                //add a new
                System.out.println("add new link");
                linkList.add(newLinkType);
            }

        }else {
            alertDialog("cant add link,because source port and target port can't be found");
        }

    }

    /**
     * hether it can add this link
     * Only source or destination port exist, then can we add it to the document root
     *
     * @param id           port id
     * @param documentRoot document root
     * @return true can add this link
     */
    private static boolean findPort(BigInteger id, DocumentRoot documentRoot) {
        boolean findId = false;
        EList<BlockType> blockList = documentRoot.getCef().getSystem().getBlocks().getBlock();
        for (BlockType blockType : blockList) {
            EList<PortType> port = blockType.getPorts().getPort();
            for (PortType portType : port) {
                if (portType.getId().compareTo(id) == 0) {
                    findId = true;
                    break;
                }
            }
        }
        return findId;
    }
}
