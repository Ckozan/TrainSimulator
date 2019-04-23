package trainsimulator;
import java.util.Random;

public class TrainSimulator {

    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Thread sim = new Thread(new SimulatorClass());//think i may have to creat a simulator class constructor for this to work.
        sim.start();
        //now just need gui stuff
        
    }
    
}
