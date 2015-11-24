package trainNeuralNetwork;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.encog.neural.networks.BasicNetwork;

public class mainhandler {
		
	
	public static void main(String args[]){
		datahandler handler = new datahandler();
		double[][][] data = handler.readcsv("..\\..\\Self generated training data\\Aalborg.csv");
		double[][] inputarray =data[0];
		double[][] outputarray =data[1];
		//System.out.print(inputarray.length);
		//NeuralNetwork neuralnetwork = new NeuralNetwork(inputarray, outputarray);
		//serializeNN(neuralnetwork);
		
		//NeuralNetwork neuralnetwork = null;
		//neuralnetwork = deserializeNN(neuralnetwork);
		//neuralnetwork.useNN();
				
	}
	
	public static void serializeNN(NeuralNetwork neuralnetwork){
		try{
			FileOutputStream fileOut = new FileOutputStream("..\\..\\serialized networks\\neuralnetwork.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(neuralnetwork.network);
	        out.close();
	        fileOut.close();
	        System.out.printf("Serialized data is saved in neuralnetwork.ser");
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
