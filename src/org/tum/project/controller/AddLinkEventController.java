package org.tum.project.controller;

import Cef.CefFactory;
import Cef.LinkType;
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
 * add link pane
 * submit to modify the document root
 * Created by Yin Ya on 2017/5/14.
 */
public class AddLinkEventController implements Initializable {
    @FXML
    private JFXTextField addLink_carriesSourceClock;

    @FXML
    private JFXTextField addLink_carriesSourceReset;

    @FXML
    private JFXTextField addLink_linkLengthEstimation;

    @FXML
    private JFXTextField addLink_auxiliaryForwardWires;

    @FXML
    private JFXTextField addLink_destinationPortId;

    @FXML
    private JFXTextField addLink_sourcePortId;

    @FXML
    private JFXTextField addLink_name;

    @FXML
    private JFXTextField addLink_id;

    @FXML
    private JFXTextField addLink_auxiliaryBackwardWires;
    private CefVisualizationService cefVisualizationService;

    @FXML
    void submit(ActionEvent event) {
        System.out.println("add link submit");
        if ((!addLink_id.getText().equals("")) && (!addLink_sourcePortId.getText().equals("")) && (!addLink_destinationPortId.getText()
                .equals(""))) {
            LinkType newLinkType = CefFactory.eINSTANCE.createLinkType();
            newLinkType.setName(addLink_name.getText().length() == 0 ? null : addLink_name.getText());
            newLinkType.setId(addLink_id.getText().length() == 0 ? new BigInteger("0") : new BigInteger(addLink_id.getText()));
            newLinkType.setSourcePortId(addLink_sourcePortId.getText().length() == 0 ? new BigInteger("0") : new BigInteger
                    (addLink_sourcePortId.getText
                            ()));
            newLinkType.setDestinationPortId(addLink_destinationPortId.getText().length() == 0 ? new BigInteger("0") : new BigInteger
                    (addLink_destinationPortId.getText
                            ()));

            newLinkType.setCarriesSourceClock(addLink_carriesSourceClock.getText().length() == 0 ? Boolean.FALSE : Boolean.valueOf(addLink_carriesSourceClock.getText()));

            newLinkType.setCarriesSourceReset(addLink_carriesSourceReset.getText().length() == 0 ? Boolean.FALSE : Boolean.valueOf
                    (addLink_carriesSourceReset.getText()));

            newLinkType.setLinkLengthEstimation(addLink_linkLengthEstimation.getText().length() == 0 ? new Double(0) : new
                    Double(addLink_linkLengthEstimation.getText()));

            newLinkType.setAuxiliaryForwardWires(addLink_auxiliaryForwardWires.getText().length() == 0 ? new BigInteger("0") : new
                    BigInteger
                    (addLink_auxiliaryForwardWires.getText
                            ()));

            newLinkType.setAuxiliaryBackwardWires(addLink_auxiliaryBackwardWires.getText().length() == 0 ? new BigInteger("0") : new
                    BigInteger
                    (addLink_auxiliaryBackwardWires.getText
                            ()));

            cefVisualizationService.addLinkToDocumentRoot(newLinkType);


        } else {
            CefModifyUtils.alertDialog("link id, source port id and destination id can't be null");
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cefVisualizationService = MainController.getCefVisualizationService();

    }
}
