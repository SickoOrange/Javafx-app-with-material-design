package org.tum.project.dashboard_controller.simulationPane;

import javafx.animation.RotateTransition;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.tum.project.dashboard_controller.SimulationController;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by heylbly on 17-6-4.
 */
public class SimulationProgressController implements Initializable {
    @FXML
    private ImageView img2;

    @FXML
    private ImageView img1;
    @FXML
    private ImageView img4;

    @FXML
    private ImageView img3;

    @FXML
    private ImageView img6;

    @FXML
    private ImageView img5;

    @FXML
    private Label lbl1;

    @FXML
    private Label lbl4;

    @FXML
    private Label lbl5;

    @FXML
    private Label lbl2;

    @FXML
    private Label lbl3;

    @FXML
    private Text txt6;

    @FXML
    private Text txt4;

    @FXML
    private Text txt5;

    @FXML
    private Text txt2;

    @FXML
    private Text txt3;

    @FXML
    private Text txt1;
    private RotateTransition[] animationArray;
    private boolean needPlayForCompile = true;
    private boolean needPlayForSimulation = true;
    private boolean needPlayForAnalyze = true;
    private boolean needPlayForGenerate = true;
    private String tippsFor4;
    private String tippsFor6;
    private String tippsFor5;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SimulationController.registerSelfToController(this, getClass().getName());

        ImageView[] imageViews = {img1, img2, img3, img4, img5, img6};
        animationArray = new RotateTransition[6];
        for (int i = 0; i < 6; i++) {
            RotateTransition rotateTransition = new RotateTransition(Duration.seconds(2), imageViews[i]);
            rotateTransition.setFromAngle(0);
            rotateTransition.setToAngle(720);
            rotateTransition.setAutoReverse(false);
            animationArray[i] = rotateTransition;
        }


    }


    private URL getResourceUrl(String resource) {
        return SimulationProgressController.class.getResource("./imgs/" + resource);
    }

    /**
     * 1.step
     */
    public void startAnimation1(String tipps) {
        img1.setImage(new Image(String.valueOf(getResourceUrl("/synching.png"))));
        txt1.setText(tipps);
        animationArray[0].play();
        animationArray[0].setOnFinished(event -> {
            animationArray[0].play();
        });
    }

    /**
     * 2.step
     *
     * @param tipps
     */
    public void startAnimation2(String tipps) {
        animationArray[0].stop();
        img1.setImage(new Image(String.valueOf(getResourceUrl("/ok.png"))));
        lbl1.setStyle("-fx-background-color:#47A563");

        img2.setImage(new Image(String.valueOf(getResourceUrl("/synching.png"))));
        txt2.setText(tipps);
        animationArray[1].play();
        animationArray[1].setOnFinished(event -> animationArray[1].play());
    }

    /**
     * 3.step
     *
     * @param tipps
     */
    public void startAnimation3(String tipps) {
        animationArray[1].stop();
        img2.setImage(new Image(String.valueOf(getResourceUrl("/ok.png"))));
        lbl2.setStyle("-fx-background-color:#47A563");

        img3.setImage(new Image(String.valueOf(getResourceUrl("/synching.png"))));
        txt3.setText(tipps);
        animationArray[2].play();
        animationArray[2].setOnFinished(event -> {
            if (needPlayForGenerate) {
                animationArray[2].play();
            } else {
                img3.setImage(new Image(String.valueOf(getResourceUrl("/ok.png"))));
                lbl3.setStyle("-fx-background-color:#47A563");

                img4.setImage(new Image(String.valueOf(getResourceUrl("/synching.png"))));
                txt4.setText(tippsFor4);
                animationArray[3].play();
                animationArray[3].setOnFinished(event0 -> {
                    if (needPlayForCompile) {
                        animationArray[3].play();
                    } else {
                        img4.setImage(new Image(String.valueOf(getResourceUrl("/ok.png"))));
                        lbl4.setStyle("-fx-background-color:#47A563");
                        img5.setImage(new Image(String.valueOf(getResourceUrl("/synching.png"))));
                        txt5.setText(tippsFor5);
                        animationArray[4].play();
                        animationArray[4].setOnFinished(event2 -> {
                            if (needPlayForSimulation) {
                                animationArray[4].play();
                            } else {
                                img5.setImage(new Image(String.valueOf(getResourceUrl("/ok.png"))));
                                lbl5.setStyle("-fx-background-color:#47A563");
                                img6.setImage(new Image(String.valueOf(getResourceUrl("/synching.png"))));
                                txt6.setText(tippsFor6);
                                animationArray[5].play();
                                animationArray[5].setOnFinished(event3 -> {
                                    if (needPlayForAnalyze) {
                                        animationArray[5].play();
                                    } else {
                                        img6.setImage(new Image(String.valueOf(getResourceUrl("/ok.png"))));
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    /**
     * 4.step
     *
     * @param tipps
     */
    public void startAnimation4(String tipps) {
        needPlayForGenerate = false;
        tippsFor4 = tipps;
    }

    /**
     * 5.step
     *
     * @param tipps
     */
    public void startAnimation5(String tipps) {
        needPlayForCompile = false;
        tippsFor5 = tipps;
    }

    /**
     * 6.step
     *
     * @param tipps
     */
    public void startAnimation6(String tipps) {
        needPlayForSimulation = false;
        tippsFor6 = tipps;
    }

    public void stopAnimation6() {
        needPlayForAnalyze = false;
    }


    /**
     * clear all the label and image
     */
    public void clear() {
        img1.setImage(new Image(String.valueOf(getResourceUrl("/clock.png"))));
        img2.setImage(new Image(String.valueOf(getResourceUrl("/clock.png"))));
        img3.setImage(new Image(String.valueOf(getResourceUrl("/clock.png"))));
        img4.setImage(new Image(String.valueOf(getResourceUrl("/clock.png"))));
        img5.setImage(new Image(String.valueOf(getResourceUrl("/clock.png"))));
        img6.setImage(new Image(String.valueOf(getResourceUrl("/clock.png"))));

        txt1.setText("");
        txt2.setText("");
        txt3.setText("");
        txt4.setText("");
        txt5.setText("");
        txt6.setText("");

        lbl1.setStyle("-fx-background-color:#BCBCBC");
        lbl2.setStyle("-fx-background-color:#BCBCBC");
        lbl3.setStyle("-fx-background-color:#BCBCBC");
        lbl4.setStyle("-fx-background-color:#BCBCBC");
        lbl5.setStyle("-fx-background-color:#BCBCBC");

    }
}
