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
    private URL parentPath;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SimulationController.registerSelfToController(this, getClass().getName());

        ImageView[] imageViews = {img1, img2, img3, img4, img5, img6};
        animationArray = new RotateTransition[6];
        for (int i = 0; i < 6; i++) {
            RotateTransition rotateTransition = new RotateTransition(Duration.seconds(3), imageViews[i]);
            rotateTransition.setFromAngle(0);
            rotateTransition.setToAngle(720);
            rotateTransition.setAutoReverse(false);
            animationArray[i] = rotateTransition;
        }

//
//        // Set up sequential plays
//        rotateTransition1.setOnFinished((ActionEvent event1) -> {
//            img1.setImage(new Image("imgs/ok.png"));
//            lbl1.setStyle("-fx-background-color:#47A563");
//            img2.setImage(new Image("imgs/synching.png"));
//            txt2.setText("Pre-scan Operations");
//            rotateTransition2.play();
//
//        });
//        rotateTransition1.play();
//
//
//        rotateTransition2.setOnFinished((ActionEvent event2) -> {
//            img2.setImage(new Image("imgs/ok.png"));
//            lbl2.setStyle("-fx-background-color:#47A563");
//            img3.setImage(new Image("imgs/synching.png"));
//            txt3.setText("Scanning Memory");
//            rotateTransition3.play();
//        });
//
//        rotateTransition3.setOnFinished((ActionEvent event2) -> {
//            img3.setImage(new Image("imgs/ok.png"));
//            lbl3.setStyle("-fx-background-color:#47A563");
//            img4.setImage(new Image("imgs/synching.png"));
//            txt4.setText("Scanning Registry");
//            rotateTransition4.play();
//        });
//
//        rotateTransition4.setOnFinished((ActionEvent event2) -> {
//            img4.setImage(new Image("imgs/ok.png"));
//            lbl4.setStyle("-fx-background-color:#47A563");
//            img5.setImage(new Image("imgs/synching.png"));
//            txt5.setText("Scanning file system");
//            rotateTransition5.play();
//        });
//
//        rotateTransition5.setOnFinished((ActionEvent event2) -> {
//            img5.setImage(new Image("imgs/ok.png"));
//            lbl5.setStyle("-fx-background-color:#47A563");
//            img6.setImage(new Image("imgs/synching.png"));
//            txt6.setText("Heuristics Analysis");
//            rotateTransition6.play();
//        });
//        rotateTransition6.setOnFinished((ActionEvent event2) -> {
//            img6.setImage(new Image("imgs/ok.png"));
//
//        });

    }


    private URL getResourceUrl(String resource) {
        return SimulationProgressController.class.getResource("./imgs/" + resource);
    }

    /**
     * first step
     */
    public void startAnimation1(String tipps) {
        img1.setImage(new Image(String.valueOf(getResourceUrl("/synching.png"))));
        txt1.setText(tipps);
        animationArray[0].play();
        animationArray[0].setOnFinished(event -> {
            animationArray[0].play();
        });
    }


    public void startAnimation2(String tipps) {
        animationArray[0].stop();
        img1.setImage(new Image(String.valueOf(getResourceUrl("/ok.png"))));
        lbl1.setStyle("-fx-background-color:#47A563");

        img2.setImage(new Image(String.valueOf(getResourceUrl("/synching.png"))));
        txt2.setText(tipps);
        animationArray[1].play();
        animationArray[1].setOnFinished(event -> animationArray[1].play());
    }

    public void startAnimation3(String tipps) {
        animationArray[1].stop();
        img2.setImage(new Image(String.valueOf(getResourceUrl("/ok.png"))));
        lbl2.setStyle("-fx-background-color:#47A563");

        img3.setImage(new Image(String.valueOf(getResourceUrl("/synching.png"))));
        txt3.setText(tipps);
        animationArray[2].play();
        animationArray[2].setOnFinished(event -> animationArray[2].play());
    }

    public void startAnimation4(String tipps) {
        animationArray[2].stop();
        ObservableMap<String, Duration> cuePoints = animationArray[2].getCuePoints();
        for (Map.Entry<String, Duration> entry : cuePoints.entrySet()) {
            System.out.println(entry.getKey() + "time" + entry.getValue());
        }
        img3.setImage(new Image(String.valueOf(getResourceUrl("/ok.png"))));
        lbl3.setStyle("-fx-background-color:#47A563");

        img4.setImage(new Image(String.valueOf(getResourceUrl("/synching.png"))));
        txt4.setText(tipps);
        animationArray[3].play();
        animationArray[3].setOnFinished(event -> animationArray[3].play());
    }

    public void startAnimation5(String tipps) {
        animationArray[3].stop();
        img4.setImage(new Image(String.valueOf(getResourceUrl("/ok.png"))));
        lbl4.setStyle("-fx-background-color:#47A563");

        img5.setImage(new Image(String.valueOf(getResourceUrl("/synching.png"))));
        txt5.setText(tipps);
        animationArray[4].play();
        animationArray[4].setOnFinished(event -> animationArray[4].play());
    }

    public void startAnimation6(String tipps) {
        animationArray[4].stop();
        img5.setImage(new Image(String.valueOf(getResourceUrl("/ok.png"))));
        lbl5.setStyle("-fx-background-color:#47A563");

        img6.setImage(new Image(String.valueOf(getResourceUrl("/synching.png"))));
        txt6.setText(tipps);
        animationArray[5].play();
        animationArray[5].setOnFinished(event -> animationArray[5].play());
    }
}
