package trainNeuralNetwork;

public class mainhandler {
	public static void main(String args[]){
		datahandler handler = new datahandler();
		double[][][] data = handler.readcsv("C:/Users/Wolf/Desktop/torcs data/Aalborg.csv");
		double[][] inputarray =data[0];
		double[][] outputarray =data[1];
		System.out.print(inputarray.length);
		NeuralNetwork nn = new NeuralNetwork();
		nn.NN(inputarray, outputarray);
	}
}
