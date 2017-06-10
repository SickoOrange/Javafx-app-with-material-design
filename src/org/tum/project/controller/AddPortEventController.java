package org.tum.project.controller;

import Cef.BlockType;
import Cef.CefFactory;
import Cef.PortType;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.tum.project.utils.CefModifyUtils;
import org.tum.project.dataservice.CefVisualizationService;

import java.math.BigInteger;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Yin Ya on 2017/5/14.
 */
public class AddPortEventController implements Initializable {
    @FXML
    private JFXTextField addPort_positionY;

    @FXML
    private JFXTextField addPort_writeDataWidth;

    @FXML
    private JFXTextField addPort_protocol;

    @FXML
    private JFXTextField addPort_maxOutStandingTransaction;

    @FXML
    private JFXButton submit;


    @FXML
    private JFXTextField addPort_id;

    @FXML
    private JFXTextField addPort_flitWidth;

    @FXML
    private JFXTextField addPort_readDataWidth;

    @FXML
    private JFXTextField addPort_adressWidth;

    @FXML
    private JFXTextField addPort_domainId;

    @FXML
    private JFXTextField addPort_positionX;

    @FXML
    private JFXTextField blockName;
    private CefVisualizationService cefVisualizationService;


    @FXML
    void submitPortInfo(ActionEvent event) {
        if ((!addPort_id.getText().equals("")) && (!blockName.getText().equals(""))) {
            String name = blockName.getText();
            BlockType targetBlock = cefVisualizationService.findBlockByName(name);
            PortType newPortType = CefFactory.eINSTANCE.createPortType();
            newPortType.setId(addPort_id.getText().length() == 0 ? new BigInteger("0") : new BigInteger(addPort_id.getText()));
            newPortType.setProtocol(addPort_protocol.getText().length() == 0 ? null : addPort_protocol.getText());
            newPortType.setMaxOutstandingTransactions(addPort_maxOutStandingTransaction.getText().length() == 0 ? new BigInteger
                    ("0") : new BigInteger(addPort_maxOutStandingTransaction.getText()));
            newPortType.setAddressWidth(addPort_adressWidth.getText().length() == 0 ? new BigInteger("0") : new BigInteger(addPort_adressWidth
                    .getText()));
            newPortType.setReadDataWidth(addPort_readDataWidth.getText().length() == 0 ? new BigInteger("0") : new BigInteger
                    (addPort_readDataWidth
                            .getText()));
            newPortType.setWriteDataWidth(addPort_writeDataWidth.getText().length() == 0 ? new BigInteger("0") : new BigInteger
                    (addPort_writeDataWidth
                            .getText()));
            newPortType.setFlitWidth(addPort_flitWidth.getText().length() == 0 ? new BigInteger("0") : new BigInteger
                    (addPort_flitWidth
                            .getText()));
            newPortType.setPositionX(addPort_positionX.getText().length() == 0 ? new Double("0") : new Double(
                    (addPort_positionX).getText()));
            newPortType.setPositionY(addPort_positionY.getText().length() == 0 ? new Double("0") : new Double(
                    (addPort_positionY).getText()));
            newPortType.setDomainId(addPort_domainId.getText().length() == 0 ? new BigInteger("0") : new BigInteger
                    (addPort_domainId
                            .getText()));

            //            newBlockType.setName(blockName);
//            newBlockType.setId(addBlock_id.getText().length() == 0 ? new BigInteger("0") : new BigInteger(addBlock_id
//                    .getText()));
//            newBlockType.setBlockType(addBlock_blockType.getText().length() == 0 ? new BigInteger("0") : new BigInteger(addBlock_blockType
//                    .getText()));
//            newBlockType.setLayer(addBlock_layer.getText().length() == 0 ? new BigInteger("0") : new BigInteger(addBlock_layer
//                    .getText()));

            // cefVisualizationService.addBlockToDocumentRoot(newBlockType);

            CefModifyUtils.addPortToBlock(targetBlock, newPortType);

        } else {
            CefModifyUtils.alertDialog("port id and block can't be null");
        }

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cefVisualizationService = MainController.getCefVisualizationService();
    }
}
