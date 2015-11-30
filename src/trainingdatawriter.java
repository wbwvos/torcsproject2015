import java.io.FileWriter;
import java.io.IOException;

import cicontest.torcs.client.Action;
import cicontest.torcs.client.SensorModel;

public class trainingdatawriter {
	
	public FileWriter writer;

	public trainingdatawriter(String filename){
		try {
			writer = new FileWriter(filename);
		} catch (IOException e) {
			System.out.print("failed to write csv");
			e.printStackTrace();
		}
		
		initializeCsvFile();
	}
	
	public void initializeCsvFile(){
		try{
			writer.append("Speed");
		    writer.append(',');
		    writer.append("AngleToTrackAxis");
		    writer.append(',');
		    writer.append("DistanceFromStartLine");
		    writer.append(',');
		    writer.append("TrackEdgeSensors");
		    writer.append(',');
		    writer.append("FocusSensors");
		    writer.append(',');
		    writer.append("TrackPosition");
		    writer.append(',');
		    
		    writer.append("accelerate");
		    writer.append(',');
		    writer.append("brake");
		    writer.append(',');
		    writer.append("steering");
		    writer.append(',');
		    
		    writer.append("\n");
		    
		}
		catch(IOException e)
		{
			System.out.print("could not initialize csv");
		    e.printStackTrace();
		} 
	}
	public void appendtoCsvFile(SensorModel sensors, Action action)
	   {
		
		String[] datastring = new String[6];
    	String[] actionstring= new String[3];
    	datastring[0] = String.valueOf(sensors.getSpeed());
    	datastring[1] = String.valueOf(sensors.getAngleToTrackAxis());
    	datastring[2] = String.valueOf(sensors.getDistanceFromStartLine());

    	datastring[3] = extractvalues(sensors.getTrackEdgeSensors());
    	datastring[4] = extractvalues(sensors.getFocusSensors());
    	
    	datastring[5] = String.valueOf(sensors.getTrackPosition());

    	actionstring[0] = String.valueOf(action.accelerate);
    	actionstring[1] = String.valueOf(action.brake);
    	actionstring[2] = String.valueOf(action.steering);
    	
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
			    //writer.close();
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
