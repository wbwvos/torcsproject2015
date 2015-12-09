
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import cicontest.torcs.genome.IGenome;
//import trainNeuralNetwork.NeuralNetwork;
import trainNeuralNetwork.NeuralNetwork;

public class DefaultDriverGenome implements IGenome {
	
    private static final long serialVersionUID = 6534186543165341653L;
    
    private NeuralNetwork myNN;
    private NeuralNetwork accelerateNN;
    private NeuralNetwork breakNN;
    private NeuralNetwork steeringNN;
    private NeuralNetwork speedNN;
    private NeuralNetwork positionNN;
    
    public DefaultDriverGenome() {
    	this.accelerateNN = deserializeNN("AccelerateNN.ser");
    	this.breakNN = deserializeNN("BreakNN.ser");
    	this.steeringNN = deserializeNN("SteeringNN.ser");
    	//this.myNN = deserializeNN("SupaFlyNN.ser");
        //this.speedNN = deserializeNN("speedNN.ser");
        //this.positionNN = deserializeNN("positionNN.ser");
    }
    
    //private NeuralNetwork NeatNN = deserializeNN("NEATDriver.ser");
    
    public NeuralNetwork getMyNN() {
        return myNN;
    }
    
    public NeuralNetwork getAccelerateNN() {
        return accelerateNN;
    }
    public NeuralNetwork getBreakNN() {
        return breakNN;
    }
    public NeuralNetwork getSteeringNN() {
        return steeringNN;
    }
    
    public NeuralNetwork getSpeedNN() {
    	return this.speedNN;
    }
    
    public NeuralNetwork getPositionNN() {
    	return this.positionNN;
    }
    
    //public NeuralNetwork getNeatNN() {
    //	return this.NeatNN;
    //}

    public static NeuralNetwork deserializeNN(String network){
		try{
			 FileInputStream fileIn = new FileInputStream("serialized networks/" + network);
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	         NeuralNetwork neuralnetwork = (NeuralNetwork) in.readObject();
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

