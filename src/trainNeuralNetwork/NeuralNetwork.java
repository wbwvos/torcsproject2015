package trainNeuralNetwork;

import java.io.Serializable;

import org.encog.*;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.ml.CalculateScore;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.NEATUtil;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.TrainingSetScore;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;


public class NeuralNetwork implements Serializable {
	
	/**
	 * default serialVersionID
	 */
	private static final long serialVersionUID = 1L;
	
	private BasicNetwork network;
	private ActivationFunction activation;
	
	private double[] mins;
	private double[] maxes;
	
	public NeuralNetwork() {
		this.network = new BasicNetwork();
	}
	public NeuralNetwork(double[][] inputdata, double[][] outputdata, ActivationFunction givenActivation){
		activation = givenActivation;
		initNN(inputdata, outputdata);
	}
	
	public NeuralNetwork(double[][] inputdata, double[][] outputdata){
		initNN(inputdata, outputdata);
	}
	
	public NeuralNetwork(BasicNetwork SerializedNetwork){
		network = SerializedNetwork;
	}
	
	public void setActivation(ActivationFunction activation) {
		this.activation = activation;
	}
	
	public BasicNetwork getNetwork() {
		return this.network;
	}
	
	public void setMins(double[] mins) {
		this.mins = mins;
	}
	
	public void setMaxes(double[] maxes) {
		this.maxes = maxes;
	}
	
	public void initNN(double[][] inputdata, double[][] outputdata){ 
		// configure the neural networks
		network = new BasicNetwork(); 
		 
		int hiddenLayerNeuronsCount = 10;
		 
		network.addLayer(new BasicLayer(null, true, inputdata[0].length));
		network.addLayer(new BasicLayer(activation, true, hiddenLayerNeuronsCount));
		network.addLayer(new BasicLayer(activation, false, outputdata[0].length));
		 
		network.getStructure().finalizeStructure();
		network.reset();
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
	}
		 
	public void trainInitializedNN(double[][] inputtraining, double[][] outputtraining, double[][] inputvalidation, double[][] outputvalidation){

		MLDataSet trainingSet = new BasicMLDataSet(inputtraining, outputtraining);

		MLDataSet validationSet = new BasicMLDataSet(inputvalidation, outputvalidation);

		final Propagation train = new ResilientPropagation(network, trainingSet);

		int epochsCount = 200;

		for(int epoch = 1; epoch <= epochsCount; epoch++){
			train.iteration();
		}

		train.finishTraining();

		// calculate error on validation set

		double error = network.calculateError(validationSet);
		System.out.println("Error:" + error);
	}
	
	public void train(double[][] inputs, double[] outputs, int epochsCount, boolean hiddenLayer){ 
		
//		for (int row = 0; row < inputs.length; ++row) {
//			for (int col = 0; col < inputs[0].length; ++col) {
//				System.out.print(inputs[row][col] + ',');
//			}
//			System.out.println();
//		}
		
		double[][] ideal = new double[outputs.length][1];
		for (int row = 0; row < ideal.length; ++row) {
			ideal[row][0] = outputs[row];
		}
		
		MLDataSet trainingSet = new BasicMLDataSet(inputs, ideal);
		
		MLDataSet validationSet = new BasicMLDataSet(inputs, ideal);
		 
		// configure the neural networks
		network = new BasicNetwork(); 
		 
		int hiddenLayerNeuronsCount = 56;
		 
		network.addLayer(new BasicLayer(null, true, inputs[0].length));
		if (hiddenLayer) {
			network.addLayer(new BasicLayer(this.activation, true, hiddenLayerNeuronsCount));
		}
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

	public void trainNEAT(double[][] inputtraining, double[][] outputtraining, double[][] inputvalidation, double[][] outputvalidation){

		MLDataSet trainingSet = new BasicMLDataSet(inputtraining, outputtraining);
		
		MLDataSet validationSet = new BasicMLDataSet(inputvalidation, outputvalidation);
		
		NEATPopulation pop = new NEATPopulation(2,1,1000);
		pop.setInitialConnectionDensity(1.0);// not required, but speeds training
		pop.reset();
		
		CalculateScore score = new TrainingSetScore(trainingSet);
		
		final EvolutionaryAlgorithm train = NEATUtil.constructNEATTrainer(pop,score);
		
		int i = 0;
		do {
			i = i + 1;
			train.iteration();
			System.out.println("Epoch #" + train.getIteration() + " Error:" + train.getError()+ ", Species:" + pop.getSpecies().size());
		} while(i < 25); //train.getError() > 0.001

		NEATNetwork network = (NEATNetwork)train.getCODEC().decode(train.getBestGenome());

		// test the neural network
		//System.out.println("Neural Network Results:");
		//EncogUtility.evaluate(network, validationSet);
		
		Encog.getInstance().shutdown();
		// calculate error on validation set
		double error = network.calculateError(validationSet);
		System.out.print("Error:" + error);
		
	}
	
	public double[] useNEAT(NEATNetwork network){

		
		double[] values = new double[3];
		values[0] = 0.0;
		values[1] = 0.0;
		values[2] = 0.0; 
		return values;
	}
	
	public double predict(double[] input) {
//		datahandler.normalize(inputs, this.mins, this.maxes);
		MLData data = new BasicMLData(input);
		MLData output = network.compute(data);
		Encog.getInstance().shutdown();
		
		return output.getData(0);
	}
}
