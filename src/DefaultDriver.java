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
//    	this.directControl(action, sensors);
//    	this.indirectControl(action, sensors);
//    	this.NEATControl(action, sensors);
    	this.swarmControl(action, sensors);
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
		return acceleration > 1 ? 1 : acceleration;
	}
	
	public double getBrake(SensorModel sensors) {
		double brake = 0;
		double speedDiff = this.targetSpeed - sensors.getSpeed();
		if (speedDiff < -20) {
			brake = 1;
		} else if (speedDiff < 0) {
			brake = - speedDiff / 20;
		}
		return brake < -1 ? -1 : brake;
	}

	@Override
	public double getSteering(SensorModel sensors) {
		double steeringReactiveness = 20;
		double steerLock = 2;
	    double angle = sensors.getAngleToTrackAxis();
		double positionDiff = this.targetPosition - sensors.getTrackPosition();
		angle += positionDiff * steeringReactiveness;
		double steering = angle/steerLock;
		if (steering < -1) { 
			return -1;
		}
		if (steering > 1) {
			return 1;
		}
		return steering;
	}
	
	private void directControl(Action action, SensorModel sensors) {
		double[] values = getValues(sensors);

		boolean smooth = Math.abs(values[2]) < 0.15;
    	action.accelerate = smooth && (0.4 > values[0]) ? 0.4 : values[0];
    	action.brake = 0;
    	action.steering = smooth ? 0 : values[2];
    	
    	System.out.print(action.accelerate + " ");
//    	System.out.print(action.brake + " " );
    	System.out.print(values[1] + " " );
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
		System.out.println(this.targetSpeed);
		double minSpeed = this.targetSpeed; //50;
		if (this.targetSpeed < minSpeed) {
			this.targetSpeed = minSpeed;
		}

		System.out.println("Current Speed: " + sensors.getSpeed());
		System.out.println("Target Speed: " + this.targetSpeed);
		System.out.println("Current Pos: " + sensors.getTrackPosition());
		System.out.println("Target Pos: " + this.targetPosition);
		
		double steering = this.getSteering(sensors);
		boolean smooth = Math.abs(steering) < 0.15;
		action.accelerate = this.getAcceleration(sensors);
    	action.brake = this.getBrake(sensors);
    	action.steering = smooth ? 0 : steering;
    	System.out.print(action.accelerate + " ");
    	System.out.print(action.brake + " " );
    	System.out.print(action.steering +"\n");
	}
	
	private void swarmControl(Action action, SensorModel sensors) {
		double[] frontDistances = this.getFrontDistances(sensors);
		double targetSpeed = 1.7*this.max(frontDistances);
		if (targetSpeed < 0) {
			double trackPos = sensors.getTrackPosition();
			action.accelerate = 1;
			action.brake = 0;
			action.steering = - Math.signum(trackPos);
		}action.accelerate = this.getSwarmAcceleration(sensors, targetSpeed);
		action.brake = this.getSwarmBrake(sensors, targetSpeed);
		action.steering = this.getSwarmSteering(sensors, frontDistances);
		System.out.print(action.accelerate + " ");
    	System.out.print(action.brake + " " );
    	System.out.print(action.steering +"\n");
//    	this.printOpponents(sensors);
	}
	
	private double[] getFrontDistances(SensorModel sensors) {
		double[] trackEdges = sensors.getTrackEdgeSensors();
		double[] opponents = sensors.getOpponentSensors();
		int firstOpponent = 9;
		double[] dists = new double[trackEdges.length];
		for (int i = 0; i < trackEdges.length; ++i) {
			dists[i] = Math.min(trackEdges[i], opponents[firstOpponent + i]);
		}
		this.printArray(dists);
		return dists;
	}
	
	private double getSwarmAcceleration(SensorModel sensors, double tSpeed) {
		double curSpeed = sensors.getSpeed();
		double speedDiff = tSpeed - curSpeed;
		if (speedDiff < 0) {
			return 0;
		} else if (speedDiff > 100) {
			return 1;
		} else {
			return Math.min(1, speedDiff / 50);
		}
	}
	
	private double getSwarmBrake(SensorModel sensors, double tSpeed) {
		double curSpeed = sensors.getSpeed();
		double speedDiff = tSpeed - curSpeed;
		if (speedDiff > 0) {
			return 0;
		} else if (speedDiff < -100) {
			return 1;
		} else {
			return Math.abs(speedDiff / 200);
		}
	}
	
	private double getSwarmSteering(SensorModel sensors, double[] dists) {
		int maxDir = this.argmax(dists);
		System.out.println(maxDir);
		double maxAngle = (double) (maxDir - 9) / (double) 10;
		return maxAngle;
	}
	
	private double max(double[] dists) {
		double max = Double.NEGATIVE_INFINITY;
		for (double dist : dists) {
			if (max < dist) {
				max = dist;
			}
		}
		return max;
	}
	
	private int argmax(double[] dists) {
		double max = Double.NEGATIVE_INFINITY;
		int arg = -1;
		for (int i = 0; i < dists.length; ++i) {
			if (max < dists[i]) {
				max = dists[i];
				arg = i;
			}
		}
		return arg;
	}
	
	private void printArray(double[] array) {
		for (double number : array) {
			System.out.print(number + ",");
		}
		System.out.println();
		System.out.flush();
	}
}