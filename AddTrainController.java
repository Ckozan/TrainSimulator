/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator375.pkg2;

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import simulator375.pkg2.sql.MySQL;

/**
 * FXML Controller class
 *
 * @author John McInnes
 */
public class AddTrainController implements Initializable {

    @FXML
    private TextField newTrainID;
    @FXML
    private TextField newTrainHomeHub;
    @FXML
    private TextField newTrainCapacity;
    @FXML
    private TextField newTrainTopSpeed;
    @FXML
    private TextField newTrainType;
    @FXML
    private JFXButton cmpltAddTrain;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void sendNewTrain() {
         HashMap<String, String> entries = new HashMap<>();
        entries.put("`TrainID`", newTrainID.getText());
        entries.put("`Freight`", newTrainType.getText());
        entries.put("`HomeHubID`", newTrainHomeHub.getText());
        entries.put("`Capacity`", newTrainCapacity.getText());
        entries.put("`TopSpeed`", newTrainTopSpeed.getText());
        entries.put("`Active`", "1");
        try {
            MySQL.Insert("train", entries);
            System.out.println("train sent");
        } catch (SQLException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
}
