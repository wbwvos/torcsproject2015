package trainNeuralNetwork;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;

public class datahandler {
	double[][] inputarray;
	double[][] outputarray;
	public datahandler(){
		
	}
	
	public double[][][] readcsv(String csvFile){
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		
		
		try {
			
			BufferedReader brlines = new BufferedReader(new FileReader(csvFile));
			int lines = 0;
			int counter = 0;
			int numberofinputs = 28;
			int noutputs = 3;
			line = brlines.readLine();
			while ((line = brlines.readLine()) != null) {
				lines++;
			}
			brlines.close();
			
			inputarray = new double [lines][numberofinputs];
			outputarray = new double [lines][noutputs];
			
			br = new BufferedReader(new FileReader(csvFile));
			line = br.readLine();
			while ((line = br.readLine()) != null) {
				
			        // use comma as separator
				String[] value = line.split(cvsSplitBy);
				
				for(int i=0; i < value.length; i++){
					if(i < numberofinputs){
						if(value[i] == ""){
							inputarray[counter][i] = 0.0;
						}
						inputarray[counter][i] = Double.parseDouble(value[i]);
					}
					else{
						outputarray[counter][i-numberofinputs] = Double.parseDouble(value[i]);
					}
				}
				counter++;
			}

		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		} 
		finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println("Done");
		double[][][] data = new double[2][][];
		data[0]= inputarray;
		data[1]= outputarray;
		return data;
	  }
}
