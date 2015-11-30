package trainNeuralNetwork;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.encog.neural.networks.BasicNetwork;

public class mainhandler {
		
	NeuralNetwork NN;
	
	public static void main(String args[]){
		String validationcsv = "C:/Users/Kasper/Documents/Master/Compuational Intelligence/Self generated training data/Defaultdriver/Forza.csv";
		
		datahandler handler = new datahandler();
		double[][][] data = handler.readcsv("Self generated training data/Aalborg.csv");
//		double[][] inputarray =data[0];
//		double[][] outputarray =data[1];
		//System.out.print(inputarray.length);
		//NeuralNetwork neuralnetwork = new NeuralNetwork(inputarray, outputarray);
		//serializeNN(neuralnetwork);
		
		double[][][] validationdata = handler.readcsv(validationcsv);
		double[][] inputvalidation = validationdata[0];
		double[][] outputvalidation = validationdata[1];
		

		NeuralNetwork NN = new NeuralNetwork(inputvalidation, outputvalidation);
		
		double[][] input;
		double[][] output;
		
		int epoch = 0;
		
		File dir = new File("C:/Users/Kasper/Documents/Master/Compuational Intelligence/Self generated training data/Defaultdriver/");
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			while(epoch < 1){
				for(File files : directoryListing){
					data = handler.readcsv(files.getAbsolutePath());
					input = data[0];
					output = data[1];
					NN.trainInitializedNN(input, output, inputvalidation, outputvalidation);
				}
				epoch++;
			}
		} 
		else if(!dir.isDirectory()){
		    System.out.print("file path does not lead to a directory");
		    
		}
		serializeNN(NN.network);	
	}
	
	public static void serializeNN(BasicNetwork neuralnetwork){
		try{
			FileOutputStream fileOut = new FileOutputStream("serialized networks/DefaultDriver2.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(neuralnetwork);
	        out.close();
	        fileOut.close();
	        System.out.printf("Serialized data is saved in DefaultDriver2.ser");
		}
		catch(IOException i){
			i.printStackTrace();
		}
	}
	
	public static BasicNetwork deserializeNN(BasicNetwork neuralnetwork){
		try{
	         FileInputStream fileIn = new FileInputStream("..\\..\\serialized networks\\neuralnetwork.ser");
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	         neuralnetwork = (BasicNetwork) in.readObject();
	         in.close();
	         fileIn.close();
	         System.out.println("Deserialized Neuralnetwork");
	         return neuralnetwork;
	      }
		catch(IOException i){
	         i.printStackTrace();
	         return null;
	      }
		catch(ClassNotFoundException c){
	         System.out.println("Employee class not found");
	         c.printStackTrace();
	         return null;
	      }
	    
	}	
}
