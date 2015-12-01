package trainNeuralNetwork;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import org.encog.engine.network.activation.ActivationLinear;
import org.encog.neural.networks.BasicNetwork;

public class mainhandler {
		
	NeuralNetwork NN;
	
	public static void main(String args[]){
		trainDirectNetwork();
//	    trainIndirectNetworks();
//		trainNEATNetwork();
	}

	public static void serializeNN(NeuralNetwork neuralnetwork, String fileName){
		try{
			FileOutputStream fileOut = new FileOutputStream("serialized networks/" + fileName);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(neuralnetwork);
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
		String validationcsv = "data/Defaultdriver/Forza.csv";
		datahandler handler = new datahandler();
		List<List<List<Double>>> validationdata = handler.readCSV(validationcsv, 28, 3, false);
		double[][] inputvalidation = transformListsToArrays(validationdata.get(0));
		double[][] outputvalidation = transformListsToArrays(validationdata.get(1));

		NeuralNetwork NN = new NeuralNetwork(inputvalidation, outputvalidation);
		
		double[][] input;
		double[][] output;
		
		int epoch = 0;
		
		File dir = new File("data/Defaultdriver/");
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			while(epoch < 1){
				for(File files : directoryListing){
					List<List<List<Double>>> data = handler.readCSV(files.getAbsolutePath(), 28, 3, false);
					input = transformListsToArrays(data.get(0));
					output = transformListsToArrays(data.get(1));
					NN.trainInitializedNN(input, output, inputvalidation, outputvalidation);
				}
				epoch++;
			}
		} 
		else if(!dir.isDirectory()){
		    System.out.print("file path does not lead to a directory");
		    
		}
		serializeNN(NN, "DefaultDriver3.ser");
	}

	private static void trainIndirectNetworks() {
		datahandler handler = new datahandler();
		List<List<List<Double>>> data = handler.readCSV("data/SimpleDriver/AalborgAlpine12Brondehachnew.csv", 28, 2, true);
		double[][] inputs = transformListsToArrays(data.get(0));
		double[][] outputs = transformListsToArrays(data.get(1));
		double[][] transposedOutputs = new double[outputs[0].length][outputs.length];
		for (int row = 0; row < transposedOutputs.length; ++row) {
			for (int col = 0; col < transposedOutputs[0].length; ++col) {
				transposedOutputs[row][col] = outputs[col][row];
			}

		}
		//ignore speed
		for (int row = 0; row < inputs.length; ++row) {
			inputs[row][0] = 0;
		}
		NeuralNetwork speedNN = new NeuralNetwork();
		speedNN.setActivation(new ActivationLinear());
		speedNN.setMins(handler.getMins());
		speedNN.setMaxes(handler.getMaxes());
		speedNN.train(inputs, transposedOutputs[0], 300, true);
		serializeNN(speedNN, "speedNN.ser");
		
		NeuralNetwork positionNN = new NeuralNetwork();
		positionNN.setActivation(new ActivationLinear());
		positionNN.setMins(handler.getMins());
		positionNN.setMaxes(handler.getMaxes());
		positionNN.train(inputs, transposedOutputs[1], 300, false);
		serializeNN(positionNN, "positionNN.ser");
	}
	
	private static void trainNEATNetwork() {
		String validationcsv = "data/Defaultdriver/Forza.csv";
		datahandler handler = new datahandler();
		List<List<List<Double>>> validationdata = handler.readCSV(validationcsv, 28, 3, false);
		double[][] inputvalidation = transformListsToArrays(validationdata.get(0));
		double[][] outputvalidation = transformListsToArrays(validationdata.get(1));

		NeuralNetwork NN = new NeuralNetwork(inputvalidation, outputvalidation);
		
		double[][] input;
		double[][] output;
		
		int epoch = 0;
		
		File dir = new File("data/SimpleDriverAll/");
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			while(epoch < 1){
				for(File files : directoryListing){
					List<List<List<Double>>> data = handler.readCSV(files.getAbsolutePath(), 28, 3, false);
					input = transformListsToArrays(data.get(0));
					output = transformListsToArrays(data.get(1));
					NN.trainNEAT(input, output, inputvalidation, outputvalidation);
				}
				epoch++;
			}
		} 
		else if(!dir.isDirectory()){
		    System.out.print("file path does not lead to a directory");
		    
		}
		serializeNN(NN, "NEATDriver.ser");
	}
}
