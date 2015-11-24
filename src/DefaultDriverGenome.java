
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.encog.neural.networks.BasicNetwork;

import cicontest.torcs.genome.IGenome;
//import trainNeuralNetwork.NeuralNetwork;
import trainNeuralNetwork.NeuralNetwork;

public class DefaultDriverGenome implements IGenome {
    private static final long serialVersionUID = 6534186543165341653L;
    private NeuralNetwork myNN = new NeuralNetwork(deserializeNN(null));
    public NeuralNetwork getMyNN() {
        return myNN;
    }
    
    public static BasicNetwork deserializeNN(BasicNetwork neuralnetwork){
		try{
			 System.out.println(System.getProperty("user.dir"));
	         FileInputStream fileIn = new FileInputStream("serialized networks\\neuralnetwork.ser");
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
	         System.out.println("Class not found");
	         c.printStackTrace();
	         return null;
	      }
	    
	}
}

