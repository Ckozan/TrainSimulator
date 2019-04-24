
import java.util.ArrayList;
import java.util.HashMap;
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
    
    private static int currentTime = 0;
    private int ticketPrice;
    private static int dayNum = 1;

    @Override
    public void run() 
    {
        boolean shouldContinue = true; 
       
        //ticketPrice = database.getTicketPrice();//SELECT TicketPirce FROM Station WHERE 1=1
        setWeather();
        routeManager.Initialize();
	

        while(shouldContinue)
        {
                shouldContinue = tick();//tick updates world each time it is called.
                if(!shouldContinue)//goers in if its the end of the day
                {
                   //show options screen here
                
                }
                //no else because as long as tick has everything to update in it there shoiuld be nothing else happening here
            }
        }
    
    
    public static boolean tick() {
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

    public static void updateWorld() 
    {
        
        routeManager.checkRouteComplete(currentTime);
    }
    
    /*
    public void updatePassengers()//maybe should have a parameter of what train it is effecting
    {
        //how many off the train and how many added
        ArrayList<train> list = getTrains();//get trains from train class
        for(int i = 0; i < list.size(); i++)
        {
            if(list.current train at station)
            {
                if(list.current train just got to station)
                {
                    passengerUnloading(list.get(i).trainID);
                    passengerBoarding(list.get(i).trainID);
                }
            }
        }
    }
    */
    private static void setWeather()
    {
        Random randGen = new Random();
        //get track array list from whatever ryan class then iterate through and set the tracks weather and what time the weather is happening(days). 
        //then put it into database
        
        for(int i = 0; i < Tracks.getSize(); i++)
        {
            //need to do this so many times for hours
            for(int j = 0; j < 24; j++)
            {
                Track current = Tracks.getTrackByIndex();
                int[] weatherTypes = new int[24];
                int weatherNum = randGen.nextInt(1000)+1;//should give a number between 1 and 1000
                weatherNum = weatherToInt(weatherNum);
                weatherTypes[i] = weatherNum;
                current.setWeather(weatherTypes);//set weather from ryans stuff
                uploadWeatherDB(weatherTypes, current.getID());
            }
        }
    }
    private static void uploadWeatherDB(int[] weather[], String trackID)
    {
     for(int i = 0; i < 24; i++)
     {
         HashMap<String, String> map = new HashMap<String,String>();
         map.put("WeatherID", weather[i]+"");
         map.put("TrackID5", trackID);
         map.put("Day", dayNum+"");
         map.put("MinuteStarted", i*60+"");
         map.put("MinuteEnded", (i+1)*60+"");
         
         MySQL.Instert("Weather_History", map);
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
    
    /*
    public void passengerBoarding(String trainID)//boards passengers onto trai
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
    
    
    public void passengerUnloading(String trainID)
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
    */
    
    public static int getDayNum()
    {
        return dayNum;
    }
    
    public static int getCurrentTime()
    {
        return currentTime;
    }
}
