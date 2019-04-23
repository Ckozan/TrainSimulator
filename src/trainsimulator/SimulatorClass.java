
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kyle
 */
public class SimulatorClass implements Runnable {
    
    public int currentTime = 0;
    private int ticketPrice;
    private int dayNum = 0;
    private ArrayList<String> currentSchedule = new ArrayList<String>();
    //should me this 2d so that first value is train id and 2nd value and next stop

    @Override
    public void run() 
    {
        boolean shouldContinue = true; 
       
        ticketPrice = database.getTicketPrice();//or something like that
        setWeather();
        while(shouldContinue)
        {
            if(Breakpoint.checkBreakpoint())//check for breakpoints here
            {
                try {
                    wait();//should work for making this thread pause
                } catch (InterruptedException ex) {
                    Logger.getLogger(SimulatorClass.class.getName()).log(Level.SEVERE, null, ex);
                }
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
    
    public boolean tick() {
        // Update world (train locations, and others)
        updateWorld();
        currentTime += 1;
        
        // Check if we need to pause and get user input
        if (currentTime % 14400 == 0) {
            dayNum++;
            return false;
        }
        return true;
    }

    public void updateWorld() {
        
        
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
    
    private void getSchedule(int scheduleType)//if 0, its normal schedule, 1 is weekend,and 3 is holiday
    {
        //will have to retunr/hold the schedule in some way so its easier to iterate through. 
        currentSchedule.add("schedule");//this should call and in some way put the schedule in this array for ease of use
    }
    
    public void updateRoutes()//could maybe be private
    {
        //call the algorithm to update trains routes
        for(int i = 0; i < currentSchedule.size(); i++)
        {
            //iterate through list incrementing trains inn database and their class
        }
    }
    
    public void updateStats()
    {
        //send all relevant info to the databse
        
    }
    
    public void updatePassengers()//maybe should have a parameter of what train it is effecting
    {
        //how many off the train and how many added
    }
    
    public void setWeather()
    {
        Random randGen = new Random();
        //get track array list from whatever ryan class then iterate through and set the tracks weather and what time the weather is happening(days). 
        //then put it into database
        for(arrayList)
        {
            int weatherNum = randGen.nextInt(1000)+1;//should give a number between 1 and 1000
            weatherNum = weatherToInt(weatherNum);
            //update into 
            //then put wetaher num into current part of databse
            //stored in 
            //iterate to next
        }
    }
    
    private int weatherToInt(int number)
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
    public String intToWeather(int number)
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
    
    public void passengerBoarding(String train)//boards passengers onto trai
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
    
    public void passengerUnloading(String train)
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
