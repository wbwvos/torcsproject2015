import java.io.FileWriter;
import java.io.IOException;

import cicontest.torcs.client.Action;

public class trainingdatawriter {
	
	public FileWriter writer;
	public String filename1;
	
	public trainingdatawriter(String filename){
		filename1 = filename;
		try {
			writer = new FileWriter(filename1);
		} catch (IOException e) {
			System.out.print("failed to write csv");
			e.printStackTrace();
		}
		
		initializeCsvFile();
	}
	
	public void initializeCsvFile(){
		try{
			for(int i = 0; i<19; i++){
				int j = i+1;
				writer.append("frontdistances"+j);
				writer.append(',');
			}
				
		    writer.append("Speed");
		    writer.append(',');
		    
		    writer.append("accelerate");
		    writer.append(',');
		    writer.append("brake");
		    writer.append(',');
		    writer.append("steering");
		    writer.append(',');
		    writer.append("target speed");
		    writer.append(',');
		    
		    writer.append("\n");
		    //System.out.print("Labels appended");
		    writer.flush();
		    
		}
		catch(IOException e)
		{
			System.out.print("could not initialize csv");
		    e.printStackTrace();
		} 
	}
	public void appendtoCsvFile(double[] frontSensors,double getspeed, Action action, double targetSpeed){
		/*try{
			writer = new FileWriter(filename1);
		}
		catch (IOException e){
			System.out.print("failed to write csv");
			e.printStackTrace();
		}*/
		String[] datastring = new String[frontSensors.length+1];
    	String[] actionstring= new String[4];
    	for(int i=0;i<frontSensors.length;i++){
    		datastring[i] = String.valueOf(frontSensors[i]);
    	}
    	datastring[frontSensors.length]= String.valueOf(getspeed);
    	//System.out.println(datastring.length);
    	actionstring[0] = String.valueOf(action.accelerate);
    	actionstring[1] = String.valueOf(action.brake);
    	actionstring[2] = String.valueOf(action.steering);
    	actionstring[3] = String.valueOf(targetSpeed);
    	
			try
			{
			    
				for(int i=0; i< datastring.length; i++){
					writer.append(datastring[i]);
				    writer.append(',');
				}
				
				for(int i=0; i< actionstring.length; i++){
					writer.append(actionstring[i]);
				    writer.append(',');
				}
				
			    writer.append('\n');
					
			    //generate whatever data you want
					
			    writer.flush();
			    
			}
			catch(IOException e)
			{
				System.out.print("could not append to csv");
			    e.printStackTrace();
			} 
	    
	}
	
	public String extractvalues(double[] array){
    	int length = array.length;
    	
    	String result = "";
    	if (length == 0) {
    		return result;
    	}
    	result += String.valueOf(array[0]);
    	for(int i=1;i<length; i++){
    		result += "," + String.valueOf(array[i]);	
    	}
    	return result;
    }
	
}
