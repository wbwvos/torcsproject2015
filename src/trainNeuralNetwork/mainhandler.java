package trainNeuralNetwork;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.neural.networks.BasicNetwork;

public class mainhandler {
		
	NeuralNetwork NN;
	
	public static void main(String args[]){
//		trainDirectNetwork();
//		trainThreeNetworks();
		trainTwoNetworks();
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
		String validationcsv = "trainingdata/Spring-2.csv";
		datahandler handler = new datahandler();
		List<List<List<Double>>> validationdata = handler.readCSV(validationcsv, 0, 20, 20, 3, false);
		double[][] inputvalidation = transformListsToArrays(validationdata.get(0));
		double[][] outputvalidation = transformListsToArrays(validationdata.get(1));

		NeuralNetwork NN = new NeuralNetwork(inputvalidation, outputvalidation);
		
		double[][] input;
		double[][] output;
		
		int epoch = 0;
		
		File dir = new File("trainingdata/");
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			while(epoch < 1){
				for(File files : directoryListing){
					List<List<List<Double>>> data = handler.readCSV(files.getAbsolutePath(), 0, 20, 20, 3, false);
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
		serializeNN(NN, "SupaFlyNN.ser");
	}
	private static void trainThreeNetworks(){
		String validationcsv = "trainingdata/Spring-2.csv";
		datahandler handler = new datahandler();
		
		List<List<List<Double>>> validationdataAccelerate = handler.readCSV(validationcsv,0, 20, 20, 1, false);
		double[][] inputvalidationAccelerate = transformListsToArrays(validationdataAccelerate.get(0));
		double[][] outputvalidationAccelerate = transformListsToArrays(validationdataAccelerate.get(1));
		
		List<List<List<Double>>> validationdataSpeed = handler.readCSV(validationcsv,0, 20, 21, 1, false);
		double[][] inputvalidationBreak = transformListsToArrays(validationdataSpeed.get(0));
		double[][] outputvalidationBreak = transformListsToArrays(validationdataSpeed.get(1));
		
		List<List<List<Double>>> validationdata = handler.readCSV(validationcsv, 0, 19, 22, 1, false);
		double[][] inputvalidation = transformListsToArrays(validationdata.get(0));
		double[][] outputvalidation = transformListsToArrays(validationdata.get(1));
		
		NeuralNetwork AccelerateNN = new NeuralNetwork(inputvalidationAccelerate, outputvalidationAccelerate, new ActivationSigmoid());
		NeuralNetwork BreakNN = new NeuralNetwork(inputvalidationBreak, outputvalidationBreak, new ActivationSigmoid());
		NeuralNetwork SteeringNN = new NeuralNetwork(inputvalidation, outputvalidation, new ActivationTANH());
		
		double[][] inputAccelerate;
		double[][] outputAccelerate;
		double[][] inputBreak;
		double[][] outputBreak;
		double[][] inputSteering;
		double[][] outputSteering;
		
		int epoch = 0;
		
		File dir = new File("trainingdata/");
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			while(epoch < 1){
				for(File files : directoryListing){
					List<List<List<Double>>> dataAccelerate = handler.readCSV(files.getAbsolutePath(),0, 20, 20, 1, false);
					inputAccelerate = transformListsToArrays(dataAccelerate.get(0));
					outputAccelerate = transformListsToArrays(dataAccelerate.get(1));
					
					List<List<List<Double>>> dataBreak = handler.readCSV(files.getAbsolutePath(),0, 20, 21, 1, false);
					inputBreak = transformListsToArrays(dataBreak.get(0));
					outputBreak = transformListsToArrays(dataBreak.get(1));
					
					List<List<List<Double>>> dataSteering = handler.readCSV(files.getAbsolutePath(), 0, 19, 22, 1, false);
					inputSteering = transformListsToArrays(dataSteering.get(0));
					outputSteering = transformListsToArrays(dataSteering.get(1));
					
					System.out.println("AccelerateNN:");
					AccelerateNN.trainInitializedNN(inputAccelerate, outputAccelerate, inputvalidationAccelerate, outputvalidationAccelerate);
					System.out.println("BreakNN:");
					BreakNN.trainInitializedNN(inputBreak, outputBreak, inputvalidationBreak, outputvalidationBreak);
					System.out.println("SteeringNN:");
					SteeringNN.trainInitializedNN(inputSteering, outputSteering, inputvalidation, outputvalidation);
				}
				epoch++;
			}
		} 
		else if(!dir.isDirectory()){
		    System.out.print("file path does not lead to a directory");
		    
		}
		serializeNN(AccelerateNN, "AccelerateNN.ser");
		serializeNN(BreakNN, "BreakNN.ser");
		serializeNN(SteeringNN, "SteeringNN.ser");
	}
	
	private static void trainTwoNetworks(){
		String validationcsv = "trainingdata/Spring-2.csv";
		datahandler handler = new datahandler();
		
		List<List<List<Double>>> validationdataTargetSpeed = handler.readCSV(validationcsv,0, 19, 23, 1, false);
		double[][] inputvalidationTargetSpeed = transformListsToArrays(validationdataTargetSpeed.get(0));
		double[][] outputvalidationTargetSpeed = transformListsToArrays(validationdataTargetSpeed.get(1));
		
		List<List<List<Double>>> validationdata = handler.readCSV(validationcsv, 0, 19, 22, 1, false);
		double[][] inputvalidation = transformListsToArrays(validationdata.get(0));
		double[][] outputvalidation = transformListsToArrays(validationdata.get(1));
		
		NeuralNetwork TargetSpeedNN = new NeuralNetwork(inputvalidationTargetSpeed, outputvalidationTargetSpeed, new ActivationLinear());
		NeuralNetwork SteeringNN = new NeuralNetwork(inputvalidation, outputvalidation, new ActivationTANH());
		
		double[][] inputTargetSpeed;
		double[][] outputTargetSpeed;
		double[][] inputSteering;
		double[][] outputSteering;
		
		int epoch = 0;
		
		File dir = new File("trainingdata/");
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			while(epoch < 1){
				for(File files : directoryListing){
					List<List<List<Double>>> dataTargetSpeed = handler.readCSV(files.getAbsolutePath(),0, 19, 23, 1, false);
					inputTargetSpeed = transformListsToArrays(dataTargetSpeed.get(0));
					outputTargetSpeed = transformListsToArrays(dataTargetSpeed.get(1));

					List<List<List<Double>>> dataSteering = handler.readCSV(files.getAbsolutePath(), 0, 19, 22, 1, false);
					inputSteering = transformListsToArrays(dataSteering.get(0));
					outputSteering = transformListsToArrays(dataSteering.get(1));
					
					System.out.println("AccelerateNN:");
					TargetSpeedNN.trainInitializedNN(inputTargetSpeed, outputTargetSpeed, inputvalidationTargetSpeed, outputvalidationTargetSpeed);
					System.out.println("SteeringNN:");
					SteeringNN.trainInitializedNN(inputSteering, outputSteering, inputvalidation, outputvalidation);
				}
				epoch++;
			}
		} 
		else if(!dir.isDirectory()){
		    System.out.print("file path does not lead to a directory");
		    
		}
		serializeNN(TargetSpeedNN, "TargetSpeedTwoNN.ser");
		serializeNN(SteeringNN, "SteeringTwoNN.ser");
	}
	private static void trainIndirectNetworks() {
		datahandler handler = new datahandler();
		List<List<List<Double>>> data = handler.readCSV("data/SimpleDriver/AalborgAlpine12Brondehachnew.csv", 0, 28, 28, 3, false);
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
		List<List<List<Double>>> validationdata = handler.readCSV(validationcsv, 0, 28, 28, 3, false);
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
					List<List<List<Double>>> data = handler.readCSV(files.getAbsolutePath(), 0, 28, 28, 3, false);
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
