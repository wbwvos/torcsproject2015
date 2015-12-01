import cicontest.algorithm.abstracts.AbstractDriver;
import cicontest.torcs.client.Action;
import cicontest.torcs.client.SensorModel;
import cicontest.torcs.genome.IGenome;
//import trainNeuralNetwork.NeuralNetwork;
import trainNeuralNetwork.NeuralNetwork;

public class DefaultDriver extends AbstractDriver {
	
//	trainingdatawriter datawriter = new trainingdatawriter("Self generated training data/trainingdata.csv");

    private NeuralNetwork MyNN;
    private NeuralNetwork speedNN;
    private NeuralNetwork positionNN;
    private NeuralNetwork NeatNN;
    private double targetSpeed;
    private double targetPosition;

    @Override
    public void control(Action action, SensorModel sensors) {
    	this.directControl(action, sensors);
//    	this.indirectControl(action, sensors);
//    	this.NEATControl(action, sensors);
    }
    
    @Override
	public void loadGenome(IGenome genome) {
    	if (genome instanceof DefaultDriverGenome) {
    		DefaultDriverGenome MyGenome = (DefaultDriverGenome) genome;
    		MyNN = MyGenome.getMyNN();
    		this.speedNN = MyGenome.getSpeedNN();
    		this.positionNN = MyGenome.getPositionNN();
    		this.NeatNN = MyGenome.getNeatNN();
    	} else {
    		System.err.println("Invalid Genome assigned");
    	}
    }

    private double[] getValues(SensorModel sensors){     
    	return MyNN.useNN(sensors);
    }
    
    private double[] getNEATValues(SensorModel sensors){     
    	return NeatNN.useNN(sensors);
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
	public double getAcceleration(SensorModel sensors) {
		double acceleration = 0;
		double speedDiff = this.targetSpeed - sensors.getSpeed();
		if (speedDiff > 20) {
			acceleration = 1;
		} else if (speedDiff > 0) {
			acceleration = speedDiff / 20;
		}
		return acceleration;
	}
	
	public double getBrake(SensorModel sensors) {
		double brake = 0;
		double speedDiff = this.targetSpeed - sensors.getSpeed();
		if (speedDiff < -20) {
			brake = 1;
		} else if (speedDiff < 0) {
			brake = - speedDiff / 20;
		}
		return brake;
	}

	@Override
	public double getSteering(SensorModel sensors) {
		double steeringReactiveness = 0.1;
		double steerLock = 10;
	    double angle = sensors.getAngleToTrackAxis();
		double positionDiff = this.targetPosition - sensors.getTrackPosition();
		angle += positionDiff * steeringReactiveness;
		return angle / steerLock;
	}
	
	private void directControl(Action action, SensorModel sensors) {
		double[] values = getValues(sensors);

    	action.accelerate = values[0];
    	action.brake = 0;
    	action.steering = values[2];
    	System.out.print(action.accelerate + " ");
    	System.out.print(action.brake + " " );
    	System.out.print(action.steering +"\n");
	}
	
	private void NEATControl(Action action, SensorModel sensors) {
		double[] values = getNEATValues(sensors);

    	if(values[1] > values[0]){
			action.accelerate = values[0];
    	}else{
        	action.brake = values[1];
		}
    	action.steering = values[2];
    	System.out.print(action.accelerate + " ");
    	System.out.print(action.brake + " " );
    	System.out.print(action.steering +"\n");
	}
	
	private void indirectControl(Action action, SensorModel sensors) {
		this.targetSpeed = Math.abs(this.speedNN.predict(sensors));
		this.targetPosition = this.positionNN.predict(sensors);

		System.out.println("Current Speed: " + sensors.getSpeed());
		System.out.println("Target Speed: " + this.targetSpeed);
		System.out.println("Current Pos: " + sensors.getTrackPosition());
		System.out.println("Target Pos: " + this.targetPosition);
		
    	action.accelerate = this.getAcceleration(sensors);
    	action.brake = this.getBrake(sensors);
    	action.steering = this.getSteering(sensors);
    	System.out.print(action.accelerate + " ");
    	System.out.print(action.brake + " " );
    	System.out.print(action.steering +"\n");
	}
}