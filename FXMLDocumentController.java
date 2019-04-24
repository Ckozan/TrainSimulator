/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator375.pkg2;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
//import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import trainsimulator.io.InputReader;
import simulator375.pkg2.sql.MySQL;

/**
 *
 * @author John McInnes
 */
public class FXMLDocumentController implements Initializable {
    /** NOTES:
     * database connected
     *  FOR HUBS button, we still want "hubspot" glyphname: find the lib
     *  FOR File Explorer, we still want strict button activation 
     * 
     * 
     */
    private Stage stage007;
    
    @FXML
    private Pane pnl_home, pnl_sts, pnl_hub,pnl_stn,pnl_trk,pnl_trn, pnl_route,pnl_brk,pnl_opt;
    @FXML
    private JFXButton btn_home,btn_sts, btn_hub, btn_stn, btn_trk, btn_trn,btn_route, btn_brk,btn_opt;
    @FXML
    private JFXButton btn_GO;
    @FXML
    private Text filePathField;
    @FXML
    private JFXComboBox trnChoices, hubChoices,stnChoices,trkChoices, routeChoicesPsngr, routeChoicesFrgt;
    
    private ObservableList<String> allRoutePasCountDB = FXCollections.observableArrayList();
    private ObservableList<String> allRouteFrtCountDB = FXCollections.observableArrayList();
    private ObservableList<String> allTrnCountDB = FXCollections.observableArrayList();
    private ObservableList<String> allStnCountDB = FXCollections.observableArrayList();
    private ObservableList<String> trnCountDB = FXCollections.observableArrayList();
     private ObservableList<String> allTrkCountDB = FXCollections.observableArrayList();
    @FXML
    private MenuItem menu_opnFile;
    @FXML
    private Text trainCountText, homeHubValTxt,topSpeedValTxt,distanceValTxt,cargoTypeTxt, capacityValTxt;
    @FXML
    private Text randomOnMinVal, randomOffMinVal, randomOffMaxVal, randomOnMaxVal, ticketPriceVal, arrivalTimeVal,
            reRouteVal, currentStopVal,trackLengthVal, trkEndLocVal,trkStartLocVal,trkEndStnLocVal, trkStartStnLocVal,
            trkSpeedLmtVal;
    
    @FXML
    private JFXButton addTrnBtn;
    private TextArea newTrainID;
    
    @FXML 
    private TextArea newTrainHomeHub, newTrainTopSpeed, newTrainCapacity, newTrainType;
        
    @FXML
    private void doThis() {
        System.out.println("do it work");
    }
    @FXML
    private void openHome(){
         pnl_home.toFront();
    }
    @FXML
    private void openStats(){
         pnl_sts.toFront();
    }
    @FXML
    private void openHub(){
        pnl_hub.toFront();
    }
    @FXML
    private void openStation(){
          pnl_stn.toFront();
    }
    @FXML 
    private void openTrain(){
        pnl_trn.toFront();
    }
    @FXML 
    private void openTrack(){
        pnl_trk.toFront();
    }
    
    @FXML
    private void openRoute() {
        pnl_route.toFront();
    }
    @FXML
    private void openBreaks(){
         pnl_brk.toFront();
    }
    @FXML 
    private void openOptions(){
        pnl_opt.toFront();
    }
    
    
    @FXML
    private void startSim() {
        new Thread(() -> {
            TrainSimulator.main(null);
        }).start();
    }
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
         
        //Connect to DB
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/cis_375_schema", "root", "");
            System.out.println("we connected");
            for(int i  = 1; i < 15; i++) {
           //connection.prepareStatement("INSERT INTO `Hub` (`HubID`, `Active`) VALUES ('"+i+"', '0')").execute();
            }
        } catch (SQLException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //annon class which gets called when comboBox item is selected
        // change "hubchoices" respectfully
        // change the query respectively
       hubChoices.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    ResultSet set =MySQL.Select("train", "`HomeHubID` = '"+newValue+"'", "`TrainID`", "`Active`");
                    int count = 0;
                    while(set.next()) {
                        set.getString("TrainID");
                       count++ ;
                    }
                    trainCountText.setText(count+"");
                } catch (SQLException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });//end of HUBCHOICES anon class
       
         //annon class which gets called when comboBox item is selected
        // change "hubchoices" respectfully
        // change the query respectively
       trnChoices.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    ResultSet set =MySQL.Select("train", "`TrainID` = '"+newValue+"'", "`Capacity`", "`Active`");
                    while(set.next()){
                    System.out.println(set);
                    System.out.println("Changing Capacity");
                    capacityValTxt.setText(set.getString("Capacity"));
                    //trainCountText.setText(set);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });//end of TRAINCHOICES anon class to change Capacity
       
        trnChoices.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    ResultSet set =MySQL.Select("train", "`TrainID` = '"+newValue+"'", "`HomeHubID`", "`Active`");
                    System.out.println("Changing Home Hub");
                   while(set.next()){
                    homeHubValTxt.setText(set.getString("HomeHubID"));
                    //trainCountText.setText(set);
                   }
                } catch (SQLException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });//end of TRAINCHOICES anon class to change HomeHub
        
        trnChoices.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    ResultSet set =MySQL.Select("train", "`TrainID` = '"+newValue+"'", "`TopSpeed`", "`Active`");
                    System.out.println("Changing Home Hub");
                   while(set.next()){
                    topSpeedValTxt.setText(set.getString("TopSpeed"));
                    //trainCountText.setText(set);
                   }
                } catch (SQLException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });//end of TRAINCHOICES anon class to change TopSpeed
        
        trnChoices.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    ResultSet set =MySQL.Select("train_progress", "`TrainID` = '"+newValue+"'", "`DistanceTraveled`", "`Active`");
                    System.out.println("Changing Dist Traveled");
                   while(set.next()){
                    distanceValTxt.setText(set.getString("DistanceTraveled"));
                    //trainCountText.setText(set);
                   }
                } catch (SQLException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });//end of TRAINCHOICES anon class to change Distance Traveled
        
        trnChoices.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    ResultSet set =MySQL.Select("Train", "`TrainID` = '"+newValue+"'", "`Freight`", "`Active`");
                    System.out.println("Changing Train Type");
                   while(set.next()){
                    if(set.getString("Freight").equalsIgnoreCase("1")){
                    cargoTypeTxt.setText("Freight");
                    }else{cargoTypeTxt.setText("Passenger");}
                    //trainCountText.setText(set);
                   }
                } catch (SQLException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });//end of TRAINCHOICES anon class to change Distance Traveled
        
        stnChoices.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    ResultSet set =MySQL.Select("Station", "`StationID` = '"+newValue+"'", "`Random_On_Min`", "`Active`");
                    System.out.println("Changing Minimum On");
                   while(set.next()){
                     randomOnMinVal.setText(set.getString("Random_On_Min"));
                    //trainCountText.setText(set);
                   }//CHANGES RANDOM ON MINIMUM
                } catch (SQLException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
                 try {
                    ResultSet set =MySQL.Select("Station", "`StationID` = '"+newValue+"'", "`Random_Off_Min`", "`Active`");
                    System.out.println("Changing Minimum Off");
                   while(set.next()){
                     randomOffMinVal.setText(set.getString("Random_Off_Min"));
                    //trainCountText.setText(set);
                   } //CHANGES RANDOM OFF MINIMUM
                } catch (SQLException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
                 try {
                    ResultSet set =MySQL.Select("Station", "`StationID` = '"+newValue+"'", "`Random_On_Max`", "`Active`");
                    System.out.println("Changing Maximumm On");
                   while(set.next()){
                     randomOnMaxVal.setText(set.getString("Random_On_Max"));
                    //trainCountText.setText(set);
                   }//CHANGES RANDOM ON MAXIMUM
                } catch (SQLException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
                 try {
                    ResultSet set =MySQL.Select("Station", "`StationID` = '"+newValue+"'", "`Random_Off_Max`", "`Active`");
                    System.out.println("Changing Maximumm Off");
                   while(set.next()){
                     randomOffMaxVal.setText(set.getString("Random_Off_Max"));
                    //trainCountText.setText(set);
                   } //CHANGES RANDOM OFF MAXIMUM
                } catch (SQLException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
                 try {
                    ResultSet set =MySQL.Select("Station", "`StationID` = '"+newValue+"'", "`Ticket_Price`", "`Active`");
                    System.out.println("Changing Ticket Price");
                   while(set.next()){
                     ticketPriceVal.setText(set.getString("Ticket_Price"));
                    //trainCountText.setText(set);
                   } // CHANGES TICKET PRICE
                } catch (SQLException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });//end of STATIONCHOICES anon class to change ON/OFF VALS, TICKET PRICES,
        
        routeChoicesPsngr.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    ResultSet set =MySQL.Select("Passenger_Route_Progress", "`RouteID4` = '"+newValue+"'", "`ArrivalTime`", "`Active`");
                    System.out.println("Changing Arrival Time");
                   while(set.next()){
                     arrivalTimeVal.setText(set.getString("ArrivalTime"));
                    
                   }//CHANGES PASSENGER ARRIVAL TIME
                } catch (SQLException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                try {
                    ResultSet set =MySQL.Select("Passenger_Route_Progress", "`RouteID4` = '"+newValue+"'", "`RerouteCounter`", "`Active`");
                    System.out.println("Changing ReRoute Count");
                   while(set.next()){
                     reRouteVal.setText(set.getString("RerouteCounter"));
                    
                   }//CHANGES PASSENGER REROUTE
                } catch (SQLException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                 try {
                    ResultSet set =MySQL.Select("Passenger_Route_Progress", "`RouteID4` = '"+newValue+"'", "`StopNumber`", "`Active`");
                    System.out.println("Changing ReRoute Count");
                   while(set.next()){
                     currentStopVal.setText(set.getString("StopNumber"));
                    
                   }//CHANGES PASSENGER REROUTE
                } catch (SQLException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
                
        });
         routeChoicesFrgt.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    ResultSet set =MySQL.Select("Freight_Route_Progress", "`RouteID4` = '"+newValue+"'", "`ArrivalTime`", "`Active`");
                    System.out.println("Changing Arrival Time");
                   while(set.next()){
                     arrivalTimeVal.setText(set.getString("ArrivalTime"));
                    
                   }//CHANGES FREIGHT ARRIVAL TIME
                } catch (SQLException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                try {
                    ResultSet set =MySQL.Select("Freight_Route_Progress", "`RouteID4` = '"+newValue+"'", "`RerouteCounter`", "`Active`");
                    System.out.println("Changing ReRoute Count");
                   while(set.next()){
                     reRouteVal.setText(set.getString("RerouteCounter"));
                    
                   }//CHANGES FREIGHT REROUTE
                } catch (SQLException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                 try {
                    ResultSet set =MySQL.Select("Freight_Route_Progress", "`RouteID4` = '"+newValue+"'", "`StopNumber`", "`Active`");
                    System.out.println("Changing Stop Number");
                   while(set.next()){
                     currentStopVal.setText(set.getString("StopNumber"));
                    
                   }//CHANGES STOP NUMBER
                } catch (SQLException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
                
        });
         
         trkChoices.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    ResultSet set =MySQL.Select("Track", "`TrackID` = '"+newValue+"'", "`Length`", "`Active`");
                    System.out.println("Changing Track Length");
                   while(set.next()){
                     trackLengthVal.setText(set.getString("Length"));
                    
                   }//CHANGES TRACK LENGTH
                } catch (SQLException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                try {
                    ResultSet set =MySQL.Select("Track", "`TrackID` = '"+newValue+"'", "`EndLocHubID`", "`Active`");
                    System.out.println("Changing Track Start Location");
                   while(set.next()){
                     trkEndLocVal.setText(set.getString("EndLocHubID"));
                    
                   }//CHANGES TRACK END HUB
                } catch (SQLException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
                   
                try {
                    ResultSet set =MySQL.Select("Track", "`TrackID` = '"+newValue+"'", "`StartLocHubID`", "`Active`");
                    System.out.println("Changing Track Start Location");
                   while(set.next()){
                     trkStartLocVal.setText(set.getString("StartLocHubID"));
                    
                   }//CHANGES TRACK START HUB
                } catch (SQLException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                 try {
                    ResultSet set =MySQL.Select("Track", "`TrackID` = '"+newValue+"'", "`EndLocStationID`", "`Active`");
                    System.out.println("Changing Track Start Location");
                   while(set.next()){
                     trkEndStnLocVal.setText(set.getString("EndLocStationID"));
                    
                   }//CHANGES TRACK STATION END
                } catch (SQLException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
                   
                try {
                    ResultSet set =MySQL.Select("Track", "`TrackID` = '"+newValue+"'", "`StartLocStationID`", "`Active`");
                    System.out.println("Changing Track Start Location");
                   while(set.next()){
                     trkStartStnLocVal.setText(set.getString("StartLocStationID"));
                    
                   }//CHANGES TRACK STATION START
                } catch (SQLException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                try {
                    ResultSet set =MySQL.Select("Track", "`TrackID` = '"+newValue+"'", "`SpeedLimit`", "`Active`");
                    System.out.println("Changing Track Speed Limit");
                   while(set.next()){
                     trkSpeedLmtVal.setText(set.getString("SpeedLimit"));
                    
                   }//CHANGES TRACK STATION START
                } catch (SQLException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }  
                
        });
         
    }
                 
                

    @FXML
   
    public void openFile (){
        //opens file explorer
        System.out.println("Open File");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
       //System.out.println(fileChooser.showOpenDialog(stage007).getAbsolutePath());
        String path = (fileChooser.showOpenDialog(stage007).getAbsolutePath()); 
        filePathField.setText(path);
        try {
            new InputReader(new File(path));
        } catch (SQLException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void init(Stage primaryStage) /*display browse window*/{
        this.stage007 = stage007;
    }

    private void errMsg()/*empty*/ {
       try { FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ErrorWindow.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        stage.show();
       } catch (Exception e){
           System.err.println(e.getMessage());
       
       }
        
    }

    @FXML
    private void fillTrnComboBox()/*completed fills TRAIN comboBox*/ {
    //clears obsrv list so comboBOx isnt overflowed
        trnChoices.getItems().clear();
        //removes any previous queries so there isnt overflow
        allTrnCountDB.clear();
        ResultSet results;    
        try
        {    
            results = MySQL.connection.prepareStatement("Select `TrainID` FROM `Train` WHERE 1").executeQuery();    //QUERY
            
            while(results.next()) //FILLS LIST WITH RESULTS 
        {
            allTrnCountDB.add(results.getString("TrainID")); //ADD EACH ID
        }
            
            for(String item : allTrnCountDB) {
                System.out.println("Train"+item); //SIMPLE SYSTEM OUTPUT
            }
            
            trnChoices.getItems().addAll(allTrnCountDB); //ADD TO THE COMBOBOX FROM THE LIST
        }
        catch(SQLException err) //CATCH ERROR
        {
            System.out.println(err);
        }

        
    }
    
    @FXML
    private void fillHubComboBox()/*completedfills HUB comboBox*/ {
        //clears obsrv list so comboBOx isnt overflowed
        hubChoices.getItems().clear();
        //removes any previous queries so there isnt overflow
        trnCountDB.clear();
            System.out.println("is this shit runnin");
        ResultSet results;    
        try
        {    
            results = MySQL.connection.prepareStatement("Select `HubID` FROM `Hub` WHERE 1").executeQuery();    //QUERY
            
            while(results.next()) //FILLS LIST WITH RESULTS 
        {
            trnCountDB.add(results.getString("HubID")); //ADD EACH ID
        }
            
            for(String item : trnCountDB) {
                System.out.println("Hub"+item); //SIMPLE SYSTEM OUTPUT
            }
            
            hubChoices.getItems().addAll(trnCountDB); //ADD TO THE COMBOBOX FROM THE LIST
        }
        catch(SQLException err) //CATCH ERROR
        {
            System.out.println(err);
        }

        //this blocked out code is for adding to the combobox
    }

    @FXML
    private void fillRoutePsngrComboBox()/*completed fills PASSENGER ROUTE comboBox*/  {
         //clears obsrv list so comboBOx isnt overflowed
        routeChoicesPsngr.getItems().clear();
        //removes any previous queries so there isnt overflow
        allRoutePasCountDB.clear();
            //System.out.println("is this shit runnin");
        ResultSet results;    
        try
        {    
            results = MySQL.connection.prepareStatement("Select `RouteID` FROM `Passenger_Route` WHERE 1").executeQuery();    //QUERY
            
            while(results.next()) //FILLS LIST WITH RESULTS 
        {
            allRoutePasCountDB.add(results.getString("RouteID")); //ADD EACH ID
        }
            
            for(String item : allRoutePasCountDB) {
                System.out.println("Route"+item); //SIMPLE SYSTEM OUTPUT
            }
            
            routeChoicesPsngr.getItems().addAll(allRoutePasCountDB); //ADD TO THE COMBOBOX FROM THE LIST
        }
        catch(SQLException err) //CATCH ERROR
        {
            System.out.println(err);
        }

    }

    @FXML
    private void fillRouteFrgtComboBox()/*completed fills FREIGHT ROUTE comboBox*/  {
         //clears obsrv list so comboBOx isnt overflowed
        routeChoicesFrgt.getItems().clear();
        //removes any previous queries so there isnt overflow
        allRouteFrtCountDB.clear();
            System.out.println("is this shit runnin");
        ResultSet results;    
        try
        {    
            results = MySQL.connection.prepareStatement("Select `RouteID` FROM `Freight_Route` WHERE 1").executeQuery();    //QUERY
            
            while(results.next()) //FILLS LIST WITH RESULTS 
        {
            allRouteFrtCountDB.add(results.getString("RouteID")); //ADD EACH ID
        }
            
            for(String item : allRouteFrtCountDB) {
                System.out.println("Route"+item); //SIMPLE SYSTEM OUTPUT
            }
            
            routeChoicesFrgt.getItems().addAll(allRouteFrtCountDB); //ADD TO THE COMBOBOX FROM THE LIST
        }
        catch(SQLException err) //CATCH ERROR
        {
            System.out.println(err);
        }

    }

    @FXML
    private void fillStnComboBox()/*completed fills STATION comboBox*/ {
         //clears obsrv list so comboBOx isnt overflowed
        stnChoices.getItems().clear();
        //removes any previous queries so there isnt overflow
        allStnCountDB.clear();
            System.out.println("is this shit runnin");
        ResultSet results;    
        try
        {    
            results = MySQL.connection.prepareStatement("Select `StationID` FROM `Station` WHERE 1").executeQuery();    //QUERY
            
            while(results.next()) //FILLS LIST WITH RESULTS 
        {
            allStnCountDB.add(results.getString("StationID")); //ADD EACH ID
        }
            
            for(String item : allStnCountDB) {
                System.out.println("Station"+item); //SIMPLE SYSTEM OUTPUT
            }
            
            stnChoices.getItems().addAll(allStnCountDB); //ADD TO THE COMBOBOX FROM THE LIST
        }
        catch(SQLException err) //CATCH ERROR
        {
            System.out.println(err);
        }

    }

    @FXML
    private void fillTrkComboBox()/*completed fills TRACK comboBox*/ {
        //clears obsrv list so comboBOx isnt overflowed
        trkChoices.getItems().clear();
        //removes any previous queries so there isnt overflow
        allTrkCountDB.clear();
            System.out.println("is this shit runnin");
        ResultSet results;    
        try
        {    
            results = MySQL.connection.prepareStatement("Select `TrackID` FROM `Track` WHERE 1").executeQuery();    //QUERY
            
            while(results.next()) //FILLS LIST WITH RESULTS 
        {
            allTrkCountDB.add(results.getString("TrackID")); //ADD EACH ID
        }
            
            for(String item : allTrkCountDB) {
                System.out.println("Track"+item); //SIMPLE SYSTEM OUTPUT
            }
            
            trkChoices.getItems().addAll(allTrkCountDB); //ADD TO THE COMBOBOX FROM THE LIST
        }
        catch(SQLException err) //CATCH ERROR
        {
            System.out.println(err);
        }

    }

    @FXML
    private void addTrnBtn() {
        
       
       // enter the call to add train window
       try { FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("addTrain.fxml"));
        Parent root2 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root2));
        stage.show();
       } catch (Exception e){
           System.err.println(e.getMessage());
       
        }
       
       
    }

    /*private void sendNewTrain() {
         HashMap<String, String> entries = new HashMap<>();
        entries.put("`TrainID`", newTrainID.getText());
        entries.put("`Freight`", newTrainType.getText());
        entries.put("`HomeHubID`", newTrainHomeHub.getText());
        entries.put("`Capacity`", newTrainCapacity.getText());
        entries.put("`TopSpeed`", newTrainTopSpeed.getText());
        entries.put("`Active`", "1");
        try {
            MySQL.Insert("train", entries);
        } catch (SQLException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        //String query = "INSERT INTO `train`(`TrainID`, `Freight`, `HomeHubID`, `Capacity`, `TopSpeed`, `Active`) VALUES"
        //HashMap<String, String> entries = new HashMap<>();
        /*entries.put("`TrainID`", "69");
        entries.put("`Freight`", "1");
        entries.put("`HomeHubID`", "1");
        entries.put("`Capacity`", "69");
        entries.put("`TopSpeed`", "100");
        entries.put("`Active`", "1");*/
        
        // code to display add train info
        
       /* try {
            MySQL.Insert("train", entries);
        } catch (SQLException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/


    @FXML
    private void handleButtonAction(MouseEvent event) {
    }

  
    
}
