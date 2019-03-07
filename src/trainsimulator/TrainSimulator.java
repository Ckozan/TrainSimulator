package trainsimulator;

public class TrainSimulator {

    private static int currentTime = 0;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        boolean shouldContinue = false; 
        while (true) {
            shouldContinue = tick();
            if (!shouldContinue) {
                break;
            }
        }
    }

    /*
     * tick() function
     * return True if continuing
     * return False if should stop simulation
     */
    public static boolean tick() {
        // Update world (train locations, and others)
        boolean shouldStop = updateWorld();
        TrainSimulator.currentTime += 1;
        
        // Check if we need to pause and get user input
        if (TrainSimulator.currentTime % 14400 == 0) {
            System.out.println("A day passed - chekc with user!");
            return false;
        }
        return true;
    }

    public static boolean updateWorld() {
        System.out.print('.');
        return true;
        // update train locations and status
        // update routing for trains
        // update passenger statuses
    }
}
