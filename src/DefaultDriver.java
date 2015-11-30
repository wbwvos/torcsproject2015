import cicontest.algorithm.abstracts.AbstractDriver;
import cicontest.torcs.client.Action;
import cicontest.torcs.client.SensorModel;
import cicontest.torcs.genome.IGenome;
//import trainNeuralNetwork.NeuralNetwork;
import trainNeuralNetwork.NeuralNetwork;

public class DefaultDriver extends AbstractDriver {
	
//	trainingdatawriter datawriter = new trainingdatawriter("Self generated training data/trainingdata.csv");

    private NeuralNetwork MyNN;
    
    @Override
    public void control(Action action, SensorModel sensors) {
	double[] values = getValues(sensors);
	
	System.out.print("\n");
	action.accelerate = values[0];
	action.brake = 0.0;
	action.steering = values[2];
	System.out.print(action.accelerate+ " ");
	System.out.print(values[1]+ " " );
	System.out.print(action.steering+" ");

    }
    
    public void loadGenome(IGenome genome) {
        if (genome instanceof DefaultDriverGenome) {
            DefaultDriverGenome MyGenome = (DefaultDriverGenome) genome;
            MyNN = MyGenome.getMyNN();
        } else {
            System.err.println("Invalid Genome assigned");
        }
    }

    public double[] getValues(SensorModel sensors){     
    	return MyNN.useNN(sensors);
    }
    
    public String getDriverName() {
        return "simple example";
    }
    
    
    
    public void controlQualification(Action action, SensorModel sensors) {
        action.clutch = 1;
        action.steering =  Math.random() * (1 - -1)  -1;
        action.accelerate = 1;
        action.brake = 0;
        //super.controlQualification(action, sensors);
    }
    
    public void controlRace(Action action, SensorModel sensors) {
        action.clutch = 1;
        action.steering =  Math.random() * (1 - -1)  -1;
        action.accelerate = 1;
        action.brake = 0;
        //super.ControlRace(action, sensors);
    }
    
    public void defaultControl(Action action, SensorModel sensors){
        action.clutch = 1;
        action.steering =  Math.random() * (1 - -1)  -1;
        action.accelerate = 1;
        action.brake = 0;
        //super.defaultControl(action, sensors);
    }
    
    
	@Override
	public double getAcceleration(SensorModel arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getSteering(SensorModel arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
}