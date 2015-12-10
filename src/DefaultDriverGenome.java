
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import cicontest.torcs.genome.IGenome;
//import trainNeuralNetwork.NeuralNetwork;
import trainNeuralNetwork.NeuralNetwork;

public class DefaultDriverGenome implements IGenome {
	
    private static final long serialVersionUID = 6534186543165341653L;
    
    private NeuralNetwork myNN;
    
    //surprise surprise!!!
    //for some weird reason when I delete these some class in CI controller crashes
    private NeuralNetwork steeringNN;
    private NeuralNetwork targetSpeedNN;
    
    public DefaultDriverGenome() {
    	this.myNN = deserializeNN("SteeringTwoNN.mem");
    }
    
    public NeuralNetwork getMyNN() {
        return myNN;
    }
    
    public NeuralNetwork deserializeNN(String network){
    	try{
    		InputStream fileIn = DefaultDriverAlgorithm.class.getResourceAsStream("memory/" + network);
//    		FileInputStream fileIn = new FileInputStream("memory/" + network);
    		ObjectInputStream in = new ObjectInputStream(fileIn);
    		NeuralNetwork neuralnetwork = (NeuralNetwork) in.readObject();
    		in.close();
    		fileIn.close();
//    		System.out.println("Deserialized Neuralnetwork");
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

