package trainNeuralNetwork;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.neural.networks.BasicNetwork;

public class mainhandler {
		
	
	public static void main(String args[]){
		trainDirectNetwork();
//	    trainIndirectNetworks();
				
	}
	
	public static void serializeNN(NeuralNetwork neuralnetwork, String fileName){
		try{
			FileOutputStream fileOut = new FileOutputStream("serialized networks/" + fileName);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(neuralnetwork.network);
	        out.close();
	        fileOut.close();
	        System.out.printf("Serialized data is saved in " + fileName);
		}
		catch(IOException i){
			i.printStackTrace();
		}
	}
	
	public static BasicNetwork deserializeNN(BasicNetwork neuralnetwork){
		try{
	         FileInputStream fileIn = new FileInputStream("serialized networks/neuralnetworkTrial.ser");
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
	
	private static double[][] transformListsToArrays(List<List<Double>> lists) {
		if (lists.isEmpty()) {
			return new double[0][0];
		}
		double[][] arrays = new double[lists.size()][lists.get(0).size()];
		int outerI = 0;
		for (List<Double> list : lists) {
			int innerI = 0;
			for (double number : list) {
				arrays[outerI][innerI] = number;
				++innerI;
			}
			++outerI;
		}
		return arrays;
	}
	
	private static void trainDirectNetwork() {
		datahandler handler = new datahandler();
		List<List<List<Double>>> data = handler.readCSV("Self generated training data/Spring1.csv", 28, 3);
		double[][] inputs = transformListsToArrays(data.get(0));
		double[][] outputs = transformListsToArrays(data.get(1));
		NeuralNetwork neuralNetwork = new NeuralNetwork();
		neuralNetwork.setActivation(new ActivationTANH());
		neuralNetwork.train(inputs, outputs);
		serializeNN(neuralNetwork, "neuralnetwork.ser");
	}
	
	private static void trainIndirectNetworks() {
		datahandler handler = new datahandler();
		List<List<List<Double>>> data = handler.readCSV("Self generated training data/Spring1new.csv", 28, 2);
		double[][] inputs = transformListsToArrays(data.get(0));
		double[][] outputs = transformListsToArrays(data.get(1));
		double[][] transposedOutputs = new double[outputs[0].length][outputs.length];
		for (int row = 0; row < transposedOutputs.length; ++row) {
			for (int col = 0; col < transposedOutputs[0].length; ++col) {
				transposedOutputs[row][col] = outputs[col][row];
			}
		}
		NeuralNetwork speedNN = new NeuralNetwork();
		speedNN.setActivation(new ActivationLinear());
		speedNN.train(inputs, transposedOutputs[0], 200);
		serializeNN(speedNN, "speedNN.ser");
		
		NeuralNetwork positionNN = new NeuralNetwork();
		positionNN.setActivation(new ActivationLinear());
		positionNN.train(inputs, transposedOutputs[1], 100);
		serializeNN(positionNN, "positionNN.ser");
	}
}
