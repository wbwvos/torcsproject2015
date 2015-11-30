package trainNeuralNetwork;

import cicontest.torcs.client.SensorModel;

import org.encog.*;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;


public class NeuralNetwork{
	
	BasicNetwork network;
	private ActivationFunction activation;
	
	public NeuralNetwork() {
		this.network = new BasicNetwork();
	}
	
	public NeuralNetwork(double[][] inputdata, double[][] outputdata){
		trainNN(inputdata, outputdata);
	}
	
	public NeuralNetwork(BasicNetwork SerializedNetwork){
		network = SerializedNetwork;
	}
	
	public void setActivation(ActivationFunction activation) {
		this.activation = activation;
	}
	
	public void trainNN(double[][] inputdata, double[][] outputdata){ 
		
		// read datasets
		
		//double input[][] = {{1.0, 1.2, 1.8},{1.3, 2.5, 1.7},{1.3, 4.2, 2.3},{2.2, 3.1, 4.5}};
		//double output[][] = {{1.0},{0.0},{1.0},{0.0}};
		
		MLDataSet trainingSet = new BasicMLDataSet(inputdata, outputdata);
		
		MLDataSet validationSet = new BasicMLDataSet(inputdata, outputdata);
		 
		// configure the neural networks
		network = new BasicNetwork(); 
		 
		int hiddenLayerNeuronsCount = 100;
		 
		network.addLayer(new BasicLayer(null, true, inputdata[0].length));
		network.addLayer(new BasicLayer(this.activation, true, hiddenLayerNeuronsCount));
		network.addLayer(new BasicLayer(this.activation, false, outputdata[0].length));
		 
		network.getStructure().finalizeStructure();
		network.reset();
		 
		// train the neural network
		 
		final Propagation train = new ResilientPropagation(network, trainingSet);
		 
		int epochsCount = 200;
		 
		for(int epoch = 1; epoch <= epochsCount; epoch++){
			train.iteration();
			System.out.print(epoch + ": " + network.calculateError(validationSet) + "\n");
		}
		 
		train.finishTraining();
		 
		// calculate error on validation set
		 
		double error = network.calculateError(validationSet);
		
		System.out.print("Error:" + error);
		//double[] classifyDouble = {20.8291,0.551491,200.173,11.7288,21.0026,68.3699,0.360499,0.087496,0.0500069,0.0351949,0.0273127,0.0224547,0.0191879,0.0168629,0.0151433,0.0138375,0.0128289,0.0120427,0.0114294,0.0105954,0.0100575,0.0118036,-1.0,-1.0,-1.0,-1.0,-1.0,0.997989};
		//,1.0,0.0,-0.9884749846419609
		//MLData classifyData = new BasicMLData(classifyDouble);
		//MLData classified = network.compute(classifyData);
		//System.out.println("OUTPUT:" + classified);
		 
		//Encog.getInstance().shutdown();
		
		//writeObject(network);
	}
	
	public void train(double[][] inputs, double[][] outputs){ 
		
		MLDataSet trainingSet = new BasicMLDataSet(inputs, outputs);
		
		MLDataSet validationSet = new BasicMLDataSet(inputs, outputs);
		 
		// configure the neural networks
		network = new BasicNetwork(); 
		 
		int hiddenLayerNeuronsCount = 100;
		 
		network.addLayer(new BasicLayer(null, true, inputs[0].length));
		network.addLayer(new BasicLayer(this.activation, true, hiddenLayerNeuronsCount));
		network.addLayer(new BasicLayer(this.activation, false, outputs[0].length));
		 
		network.getStructure().finalizeStructure();
		network.reset();
		 
		// train the neural network
		 
		final Propagation train = new ResilientPropagation(network, trainingSet);
		 
		int epochsCount = 200;
		 
		for(int epoch = 1; epoch <= epochsCount; ++epoch){
			train.iteration();
			System.out.print(epoch + ": " + network.calculateError(validationSet) + "\n");
		}
		 
		train.finishTraining();
		 
		// calculate error on validation set
		 
		double error = network.calculateError(validationSet);
		
		System.out.print("Error:" + error);
		//double[] classifyDouble = {20.8291,0.551491,200.173,11.7288,21.0026,68.3699,0.360499,0.087496,0.0500069,0.0351949,0.0273127,0.0224547,0.0191879,0.0168629,0.0151433,0.0138375,0.0128289,0.0120427,0.0114294,0.0105954,0.0100575,0.0118036,-1.0,-1.0,-1.0,-1.0,-1.0,0.997989};
		//,1.0,0.0,-0.9884749846419609
		//MLData classifyData = new BasicMLData(classifyDouble);
		//MLData classified = network.compute(classifyData);
		//System.out.println("OUTPUT:" + classified);
		 
		//Encog.getInstance().shutdown();
		
		//writeObject(network);
	}
	
	public void train(double[][] inputs, double[] outputs, int epochsCount){ 
		
		double[][] ideal = new double[outputs.length][1];
		for (int row = 0; row < ideal.length; ++row) {
			ideal[row][0] = outputs[row];
		}
		
		MLDataSet trainingSet = new BasicMLDataSet(inputs, ideal);
		
		MLDataSet validationSet = new BasicMLDataSet(inputs, ideal);
		 
		// configure the neural networks
		network = new BasicNetwork(); 
		 
//		int hiddenLayerNeuronsCount = 100;
		 
		network.addLayer(new BasicLayer(null, true, inputs[0].length));
//		network.addLayer(new BasicLayer(this.activation, true, hiddenLayerNeuronsCount));
		network.addLayer(new BasicLayer(this.activation, false, ideal[0].length));
		 
		network.getStructure().finalizeStructure();
		network.reset();
		 
		// train the neural network
		 
		final Propagation train = new ResilientPropagation(network, trainingSet);
		 
		for(int epoch = 1; epoch <= epochsCount; ++epoch){
			train.iteration();
			System.out.print(epoch + ": " + network.calculateError(validationSet) + "\n");
		}
		 
		train.finishTraining();
		 
		// calculate error on validation set
		 
		double error = network.calculateError(validationSet);
		
		System.out.print("Error:" + error);
		//double[] classifyDouble = {20.8291,0.551491,200.173,11.7288,21.0026,68.3699,0.360499,0.087496,0.0500069,0.0351949,0.0273127,0.0224547,0.0191879,0.0168629,0.0151433,0.0138375,0.0128289,0.0120427,0.0114294,0.0105954,0.0100575,0.0118036,-1.0,-1.0,-1.0,-1.0,-1.0,0.997989};
		//,1.0,0.0,-0.9884749846419609
		//MLData classifyData = new BasicMLData(classifyDouble);
		//MLData classified = network.compute(classifyData);
		//System.out.println("OUTPUT:" + classified);
		 
		//Encog.getInstance().shutdown();
		
		//writeObject(network);
	}
	
	public double[] useNN(SensorModel sensors){
		double[] input = transformInput(sensors);
		//double[] classifyDouble = {20.8291,0.551491,200.173,11.7288,21.0026,68.3699,0.360499,0.087496,0.0500069,0.0351949,0.0273127,0.0224547,0.0191879,0.0168629,0.0151433,0.0138375,0.0128289,0.0120427,0.0114294,0.0105954,0.0100575,0.0118036,-1.0,-1.0,-1.0,-1.0,-1.0,0.997989};
		//,1.0,0.0,-0.9884749846419609
		MLData classifyData = new BasicMLData(input);
		MLData classified = network.compute(classifyData);
		//System.out.println("OUTPUT:" + classified);
		Encog.getInstance().shutdown();
		
		return classified.getData();
	}
	
	public double predict(SensorModel sensors) {
		double[] input = transformInput(sensors);
		MLData data = new BasicMLData(input);
		MLData output = network.compute(data);
		Encog.getInstance().shutdown();
		
		return output.getData(0);
	}
	
	private double[] transformInput(SensorModel sensors){
		double[] out = new double[28];
		int index = -1;
		out[++index] = sensors.getSpeed();
		out[++index] = sensors.getAngleToTrackAxis();
		out[++index] = sensors.getDistanceFromStartLine();
		double[] TrackEdgeSensors = sensors.getTrackEdgeSensors();
		for (double sensor : TrackEdgeSensors){
			out[++index] = sensor;
		}
		double[] FocusSensors = sensors.getFocusSensors();
		for (double Edgesensor : FocusSensors){
			out[++index] = Edgesensor;
		}
		out[++index] = sensors.getTrackPosition();
		
		
		return out;
	}
	
}
