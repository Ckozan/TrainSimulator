package trainsimulator;

public class TrainSimulator {

    private static int currentTime = 0;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        boolean shouldContinue = true; 
        
        /* two options for this I see. we have a while loop for keep going until user says stop, and an inside for loop that doesnt need the tick funtion
        or we leave it like this and just let the while loop get checked every second after a tick
        */
        while(shouldContinue)
        {
            if()//check for breakpoints here
            {
                //if found, stop and show what was found
            }
            else//if not found run the simulation like normal
            {
                shouldContinue = tick();//tick updates world each time it is called.
                if(!shouldContinue)//goers in if its the end of the day
                {
                   //show options screen here
                
                    //check last if user wants to go anohter day. if not leave should continue as false, otherwise make it true
                
                }
                //no else because as long as tick has everything to update in it there shoiuld be nothing else happening here
            }
        }
    }

    /*
     * tick() function
     * return True if continuing
     * return False if should stop simulation
	 *
     */
    public static boolean tick() {
        // Update world (train locations, and others)
        updateWorld();
        TrainSimulator.currentTime += 1;
        
        // Check if we need to pause and get user input
        if (TrainSimulator.currentTime % 14400 == 0) {
            System.out.println("A day passed - check with user!");
            return false;
        }
        return true;
    }

    public static void updateWorld() {
        System.out.print('.');
        
        // update train locations and status
        // update routing for trains
        // update passenger statuses
        
        //check if holiday/weekend first (probably based on days, so once we know how we can write something here)
            //grab either normal or holiday or weekend schedule
                int whichType = 0;
                getSchedule(0);
        
        //check for weather (Either in schedule or basedon days)
        boolean badWeather = false;//assum,ing there is onlky bad or good weather. otherwise I would recomend ints
        //increment trains based on weather
        if(badWeather)
        {
            //increment based on bad weather
        }
        else
        {
            //increment trains         
        }
        updatePassengers();
        updateStats();
    }
    
    private static void getSchedule(int scheduleType)//if 0, its normal schedule, 1 is weekend,and 3 is holiday
    {
        //will have to retunr/hold the schedule in some way so its easier to iterate through. 
    }
    
    public static boolean breakpointReached()
    {
        boolean answer = false;
        //check if breakpoint was reached
        //may need to have a parameter for the function that is the break[point condition its checking if true
        return answer;
    }
    
    public static void updateRoutes()//could maybe be private
    {
        //call the algorithm to update trains routes
    }
    
    public static void updateStats()
    {
        //send all relevant info to the databse
        
    }
    
    public static void updatePassengers()//maybe should have a parameter of what train it is effecting
    {
        //how many off the train and how many added
    }
    
    
}
