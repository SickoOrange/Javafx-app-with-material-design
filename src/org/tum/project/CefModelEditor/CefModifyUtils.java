package org.tum.project.CefModelEditor;

import Cef.BlockType;
import Cef.DocumentRoot;
import Cef.LinkType;
import Cef.PortType;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.eclipse.emf.common.util.EList;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Optional;

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


    public static Optional<ButtonType> alertDialog(String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText(null);
        alert.setContentText(content);
        return alert.showAndWait();

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

        } else {
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

    /**
     * delete a block from the document root
     *
     * @param blockName    given block name, that need to be deleted
     * @param documentRoot document root
     */
    public static void deleteBlock(String blockName, DocumentRoot documentRoot) {
        EList<BlockType> blockList = documentRoot.getCef().getSystem().getBlocks().getBlock();
        BlockType findBlock = findBlockByName(blockName, blockList);
        blockList.remove(findBlock);
        System.out.println("delete block success");


    }


    /**
     * find block by name in the document root
     *
     * @param blockName block name
     * @param blockList block list in the document root
     * @return block type
     */
    public static BlockType findBlockByName(String blockName, EList<BlockType> blockList) {
        for (BlockType blockType : blockList) {
            if (blockType.getName().equals(blockName)) {
                return blockType;
            }
        }
        return null;
    }


    /**
     * delete a link from document root
     *
     * @param linkId       link id
     * @param documentRoot document root
     */
    public static void deleteLink(BigInteger linkId, DocumentRoot documentRoot) {
        EList<LinkType> linkList = documentRoot.getCef().getSystem().getLinks().getLink();
        LinkType linkType = findLinkById(linkId, linkList);
        linkList.remove(linkType);
        System.out.println("delete link success");
    }

    /**
     * find a link by in in the document root
     *
     * @param linkId   link id
     * @param linkList link list from document root
     * @return link type
     */
    private static LinkType findLinkById(BigInteger linkId, EList<LinkType> linkList) {
        for (LinkType linkType : linkList) {
            if (linkType.getId().compareTo(linkId) == 0) {
                //find this link
                return linkType;
            }
        }
        return null;
    }

    /**
     * find a link list in the graph, that this block hold.
     *
     * @param blockName    block name
     * @param documentRoot document root
     * @return link list
     */
    public static ArrayList<LinkType> findLinkByBlockName(String blockName, DocumentRoot documentRoot) {
        EList<BlockType> blockList = documentRoot.getCef().getSystem().getBlocks().getBlock();
        EList<LinkType> linkList = documentRoot.getCef().getSystem().getLinks().getLink();
        ArrayList<LinkType> targetLinkList = new ArrayList<>();
        BlockType block = findBlockByName(blockName, blockList);
        //all port that this block hold
        EList<PortType> portList = block.getPorts().getPort();
        for (PortType portType : portList) {
            BigInteger portId = portType.getId();
            for (LinkType linkType : linkList) {
                if (linkType.getSourcePortId().compareTo(portId) == 0) {
                    targetLinkList.add(linkType);
                    break;
                }
                if (linkType.getDestinationPortId().compareTo(portId) == 0) {
                    targetLinkList.add(linkType);
                    break;
                }
            }
        }
        return targetLinkList;
    }


}
