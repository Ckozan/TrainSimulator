
import java.util.ArrayList;

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
        return true;
    }
    
    
    
}
