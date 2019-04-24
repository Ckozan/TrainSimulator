
import java.util.ArrayList;
import java.util.HashMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kyle
 */
public class Breakpoint {
    //i dont think this has to be imported to the simulator or anything cause it should all be on the same package. but if its not this is gonna be annoying
    private ArrayList<String[]> list = new ArrayList<String[]>();//holds the breakpoints currently set
    //should make the list 2d with the 2nd dimension being filled with a set array of string that can indicate what type of breakpoint im looking at first ebfore checking for it
    
    
    public Breakpoint()
    {
        //i dont think anything should happen here
    }
    public void setBreakpoint(String[] conditions)//should have multiple of these to be able to add different types of breakpoints
    {
        list.add(conditions);
    }
    
    public boolean checkBreakpoint()
    {
        // i dont know what the fuck happened but apparently none of this got updated when i just tried to commit and i lost everything. FUCK
        boolean found = false;
        
        if(list.isEmpty())
        {
            return found;
        }
        else
        {
            for(int i = 0; i < list.size(); i++)
            {
                String[] currentArr = list.get(i);
                //[0] = type of condition.
                if(currentArr[0] == "1")//specified time is reached
                { //[1] = specifiedTime
                    if(currentArr[1] == SimulatorClass.getCurrentTime() +"")//assuming its been made into the apporpriate format at some point in time
                    {
                        found = true;//could probably pause thread here, but wait for cams thread code for that
                        updateDatabase();
                    }
                }
                /*
                else if(currentArr[0] == "2")//station reaches a specific number of visits
                {//[1] = stationID, [2] = times visited
                    //need qury of station id getting visited number
                    if(currentArr[2] == queryResult +"")
                    {
                        found = true;
                    }
                }
                else if(currentArr[0] == "3")//track used a specific number of times
                {//[1] = trackID, [2] = times visited
                    //need query of track id getting visited number
                    if(currentArr[2] == queryResult +"")
                    {
                        found = true;
                    }
                }
                else if(currentArr[0] == "4")//train reaches a specific station
                {//[1] = trainid, [2] = stationid
                    //query for the current train position. 
                    //though this may be kept in a train class cause i think thats what ryan is doing. so id be able to just look at that
                    if(result == stationID)
                    {
                        found = true;
                    }
                }
                else if(currentArr[0] == "5")//train travels a specific distance
                {//[1] trainid, [2] = distance
                    //same thing as above with query vs train class
                    if(result == distance)//these are both strings
                    {
                        found = true;
                    }
                }
                else if(currentArr[0] == "6")
                {
                    //this is a breakpoint for collision. so since that should never happen, for now i'm leaving it blank
                }
                else if(currentArr[0] == "7")//train route complete
                {//[1] = trainid
                    //query for if complete
                    if(result = true)
                    {
                        found = true
                    }
                }
                        */
            }
        }
        return found;
    }
    
    
    private void updateDatabase()//probably need stuff in parameters
    {
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("Description", "Specified Time was Reached");
        map.put("Minute", SimulatorCLass.getCurrentTime() +"");
        map.put("Day", SimulatorClass.getDayNum() +"");
        map.put("Field1", SimulatorCLass.getCurrentTime() +"");
        map.put("Field2", null);//none for Specific Time
        
        MySQL.Insert("Breakpoints", map);
    }
}
