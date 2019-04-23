package trainsimulator;
import java.util.Random;

public class TrainSimulator {

    private static int currentTime = 0;
    private static int ticketPrice;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        boolean shouldContinue = true; 
        
        
        /*
        IMPORTANT
        
        it looks like to thread we need to make simualtor its own class (Thatd actually work better for a thread)
        but idk if this is true when also dealing with a gui
        this also makes pausing really simple i think cause we can pause either our own thread, or have the gui maybe be chacking for breakpoints 
        and pause our simulator thread when needed
        */
        //get ticket price here
        ticketPrice = database.getTicketPrice();//or something like that
        
        /* two options for this I see. we have a while loop for keep going until user says stop, and an inside for loop that doesnt need the tick funtion
        or we leave it like this and just let the while loop get checked every second after a tick
        */
        while(shouldContinue)
        {
            if(Breakpoint.checkBreakpoint())//check for breakpoints here
            {
                //if found, stop and show what was found
                //pause screen. other types of stuff
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
            //enter the options screen
            setWeather();//this should set weather for the next day. may as well do it here while they are looking at the options screen
            return false;
        }
        return true;
    }

    public static void updateWorld() {
        System.out.print('.');
        
        
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
    
    //can just be dealt with via function from breakpoint class
    /*
    public static boolean breakpointReached()
    {
        boolean answer = false;
        //check if breakpoint was reached
        //may need to have a parameter for the function that is the break[point condition its checking if true
        return answer;
    }
    */
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
    
    public static void setWeather()
    {
        Random randGen = new Random();
        for()//however many tracks there are. ask if there is a way to get amount, or if i can just iterate through the whole database
        {
            int weatherNum = randGen.nextInt(1000)+1;//should give a number between 1 and 1000
            weatherNum = weatherToInt(weatherNum);
            //then put wetaher num into current part of databse
            //iterate to next
        }
    }
    
    private static int weatherToInt(int number)
    {
        int answer = 0;//sunny answer aka default
        if(number > 520)//everything but sunny
        {
            if(number <= 790)//tests fo rif its in the rain category
            {
                if(number < 721)
                {
                    answer = 1; //light rain
                }
                else if(number < 781)
                {
                    answer = 2; //medium rain
                }
                else
                {
                    answer = 3; // heavy rain
                }
            }
            else //test for if its in the snow category
            {
                if(number < 941)
                {
                    answer = 4; //light snow
                }
                else if(number < 1000)
                {
                    answer = 5; //medium snow
                }
                else
                {
                    answer = 6; // heavy snow
                }
            }
        }
        return answer;
    }
    
    //this is just to convert the numbers 0-6 into a string of what type of weather that is
    public static String intToWeather(int number)
    {
        String answer = "Not Found"; 
        switch(number)
        {
            case 0:
                answer = "Sunny";
                break;
            case 1:
                answer = "Light Rain";
                break;
            case 2:
                answer = "Medium Rain";
                break;
            case 3:
                answer = "Heavy Rain";
                break;
            case 4:
                answer = "Light Snow";
                break;
            case 5:
                answer = "Medium Snow";
                break;
            case 6:
                answer = "Heavy Snow";
                break;
            
        }
        return answer;
    }
    
    public static void passengerBoarding(String train)//boards passengers onto trai
    {
        if(NotFull)//idk how to figure out last stop right this second
        {
            //percentage of people currently on
            Random ranGen = new Random();
            int percentInt = ranGen.nextInt(1001);
            double percentDouble = percentInt /1000;
            int passengerAvailable = passenegrAmount - maxPassengers;
            int amount = (int)(percentDouble * passengerAvailable);//truncates so always rounds down
            CurrentTrain.passengeramount= passengeramount - amount;
        }
        else
        {
            //do nothing
        }
    }
    
    public static void passengerUnloading(String train)
    {
        if(lastStop)//idk how to figure out last stop right this second
        {
            currentTrain.passengeramount = 0;
        }
        else
        {
            //percentage of people currently on
            Random ranGen = new Random();
            int percentInt = ranGen.nextInt(1001);
            double percentDouble = percentInt /1000;
            int amount = (int)(percentDouble * passenegrAmount);//truncates so always rounds down
            CurrentTrain.passengeramount= passengeramount - amount;
        }
    }
}
