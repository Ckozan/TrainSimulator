/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator375.pkg2;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

/**
 *
 * @author John McInnes
 */
public class Simulator3752 extends Application {
    //final ObservableList trnCountDB = FXCollections.observableArrayList();
    @Override
    public void start(Stage stage) throws Exception {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
        Parent root = loader.load();
        
        FXMLDocumentController control = (FXMLDocumentController)loader.getController();
        //control.init(primaryStage);
        
        Scene scene = new Scene(root);
        //ComboBox trainChoices = new ComboBox(trnCountDB);
        stage.setScene(scene);
        stage.show();
        
        //currently here in teh start, the file explorer launches immediately. Needs changing ..
        /*FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.showOpenDialog(stage);*/
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
