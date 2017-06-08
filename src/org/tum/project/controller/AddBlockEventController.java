package org.tum.project.controller;

import Cef.BlockType;
import Cef.CefFactory;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.tum.project.CefModelEditor.CefModifyUtils;
import org.tum.project.dataservice.CefVisualizationService;

import java.math.BigInteger;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * add port pane
 * submit to modify the document root
 * Created by Yin Ya on 2017/5/14.
 */
public class AddBlockEventController implements Initializable {

    @FXML
    private JFXTextField addBlock_name;

    @FXML
    private JFXTextField addBlock_id;

    @FXML
    private JFXTextField addBlock_blockType;

    @FXML
    private JFXTextField addBlock_layer;
    private CefVisualizationService cefVisualizationService;


    /**
     * add a new block to the document root
     *
     * @param event
     */
    @FXML
    void submit(ActionEvent event) {
        System.out.println("block submit");
        if (!addBlock_name.getText().equals("")) {
            String blockName = addBlock_name.getText();
            BlockType newBlockType = CefFactory.eINSTANCE.createBlockType();
            newBlockType.setName(blockName);
            newBlockType.setId(addBlock_id.getText().length() == 0 ? new BigInteger("0") : new BigInteger(addBlock_id
                    .getText()));
            newBlockType.setBlockType(addBlock_blockType.getText().length() == 0 ? new BigInteger("0") : new BigInteger(addBlock_blockType
                    .getText()));
            newBlockType.setLayer(addBlock_layer.getText().length() == 0 ? new BigInteger("0") : new BigInteger(addBlock_layer
                    .getText()));

            cefVisualizationService.addBlockToDocumentRoot(newBlockType);


        } else {
            CefModifyUtils.alertDialog("Block Name can't be null");
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //do nothing
        cefVisualizationService = MainController.getCefVisualizationService();
    }
}
