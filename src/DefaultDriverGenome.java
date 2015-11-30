
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.encog.neural.networks.BasicNetwork;

import cicontest.torcs.genome.IGenome;
//import trainNeuralNetwork.NeuralNetwork;
import trainNeuralNetwork.NeuralNetwork;

public class DefaultDriverGenome implements IGenome {
    private static final long serialVersionUID = 6534186543165341653L;
    
    private NeuralNetwork myNN = new NeuralNetwork(deserializeNN("neuralnetwork.ser"));
    
    private NeuralNetwork speedNN = new NeuralNetwork(deserializeNN("speedNN.ser"));
    private NeuralNetwork positionNN = new NeuralNetwork(deserializeNN("positionNN.ser"));
    
    private NeuralNetwork NeatNN = new NeuralNetwork(deserializeNN("NEATDriver.ser"));
    
    public NeuralNetwork getMyNN() {
        return myNN;
    }
    
    public NeuralNetwork getSpeedNN() {
    	return this.speedNN;
    }
    
    public NeuralNetwork getPositionNN() {
    	return this.positionNN;
    }
    
    public NeuralNetwork getNeatNN() {
    	return this.NeatNN;
    }
    
    public static BasicNetwork deserializeNN(String network){
		try{
			 FileInputStream fileIn = new FileInputStream("serialized networks/" + network);
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	         BasicNetwork neuralnetwork = (BasicNetwork) in.readObject();
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
	         System.out.println("Class not found");
	         c.printStackTrace();
	         return null;
	      }
	    
	}
}

