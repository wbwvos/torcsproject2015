import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.encog.neural.networks.BasicNetwork;

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
    public double[] evo = new double[10]; 
    int counter = 0;
    
    @Override
    public void control(Action action, SensorModel sensors) {
//    	this.directControl(action, sensors);
//    	this.indirectControl(action, sensors);
//    	this.NEATControl(action, sensors);
//    	this.swarmControl(action, sensors);
    	this.swarmControlVectorized(action, sensors);
    }
    
    @Override
	public void loadGenome(IGenome genome) {
    	if (genome instanceof DefaultDriverGenome) {
    		DefaultDriverGenome MyGenome = (DefaultDriverGenome) genome;
    		MyNN = MyGenome.getMyNN();
    		this.speedNN = MyGenome.getSpeedNN();
    		this.positionNN = MyGenome.getPositionNN();
    		this.evolutionaryValuesInit();
//    		this.getBestEvo();
//    		this.evolutionaryValuesGauss();
    		//this.NeatNN = MyGenome.getNeatNN();
    	} else {
    		System.err.println("Invalid Genome assigned");
    	}
    }
    
    public double randomGaussian(double value){
    	Random randomGenerator = new Random();
    	
    	double random = randomGenerator.nextGaussian();
    	int weight = Math.max(1, (counter % 15)*2);
    	System.out.print("SIGMA: " + weight);
    	return value * ((random/weight) + 1);
    }

    
    
    public double[] evolutionaryValuesInit(){
//    	System.out.println("EVINIT");
//    	evo[0] = 2.0; //targetSpeedMultiplier
//    	evo[1] = 2.0; //smoothedTargetSpeedMultiplier
//    	evo[2] = 2.0; //opponentSensorMultiplier
//    	evo[3] = 3.0; //smootedWindowsSize
//    	evo[4] = 25.0; //accelerationDiff
//    	evo[5] = 50.0; //brakeDiffOrig
//    	evo[6] = 2.0; //brakeDiffMultiplier
//    	evo[7] = 0.2; //switchSteering
//    	evo[8] = 0.6; //combiSteeringNormalWeight
//    	evo[9] = 0.4; //CombiSteeringSmoothedWeight
    	evo[0] = 0.0;
    	evo[1] = 2.6084724126;
    	evo[2] = 2;
    	evo[3] = 3;
    	evo[4] = 15;
    	evo[5] = 27.16666667;
    	evo[6] = 0.0;
    	evo[7] = 0.0;
    	evo[8] = 0.0;
    	evo[9] = 0.0;
    	return evo;
    }
    
    public double[] evolutionaryValuesGauss(){
    	System.out.println("EVGAUSS");
    	evo[0] = Math.max(2, Math.min(3, randomGaussian(evo[0]))); //targetSpeedMultiplier
    	evo[1] = Math.max(2, Math.min(4, randomGaussian(evo[1]))); //smoothedTargetSpeedMultiplier
    	evo[2] = Math.max(1, Math.min(4, randomGaussian(evo[2]))); //opponentSensorMultiplier
//    	evo[3] = Math.round(Math.max(1, Math.min(9, randomGaussian(evo[3])))); //smootedWindowsSize
    	evo[4] = Math.round(Math.max(1, Math.min(50, randomGaussian(evo[4])))); //accelerationDiff
    	evo[5] = Math.round(Math.max(1, Math.min(100, randomGaussian(evo[5])))); //brakeDiffOrig
    	evo[6] = Math.round(Math.max(1, Math.min(10, randomGaussian(evo[6])))); //brakeDiffMultiplier
    	evo[7] = Math.max(0, Math.min(1, randomGaussian(evo[7]))); //switchSteering
    	evo[8] = Math.max(0, Math.min(10, randomGaussian(evo[8]))); //combiSteeringNormalWeight
    	evo[9] = Math.max(0, Math.min(10, randomGaussian(evo[9]))); //CombiSteeringSmoothedWeight
    	printEvo();
    	return evo;
    }
    
    public void writeEvo(SensorModel sensors){
    	try {
    		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("evoresults/results.csv", true)));
    		for(double value : evo){
    			out.print(value + ";");
    		}
    		out.println(sensors.getLastLapTime());
    		out.close();
    	}catch (IOException e) {
    	    //exception handling left as an exercise for the reader
    	}
    }
    
    public void getBestEvo(){
    	BufferedReader br = null;
    	try {
			br = new BufferedReader(new FileReader("evoresults/results.csv"));
			//skip header
			String line = "";
			counter = 0;
			int best = 0;
			double bestTime = Double.POSITIVE_INFINITY;
			while ((line = br.readLine()) != null) {
				if (line.equals("")) {
					continue;
				}
				++counter;
				// use comma as separator
				String[] values = line.split(";");
				
				double currentTime = Double.parseDouble(values[values.length-1]);
				System.out.println("current time: " + currentTime + " bestTime: " + bestTime);
				if(currentTime < bestTime && currentTime != 0.0){
					bestTime = currentTime;
					best = counter;
					for(int i = 0; i < evo.length-2; i++){
						evo[i] = Double.parseDouble(values[i]);
					}
				}
				System.out.println("current time: " + currentTime + " bestTime: " + bestTime);
			}
			System.out.println("BEST PARENT: " + best);
			printEvo();
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
    	finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
    	}
    }
    
    public void printEvo(){
    	System.out.println("targetSpeedMultiplier: " + evo[0]);
    	System.out.println("smoothedTargetSpeedMultiplier: " + evo[1]);
    	System.out.println("opponentSensorMultiplier: " + evo[2]);
    	System.out.println("smootedWindowsSize: " + evo[3]);
    	System.out.println("accelerationDiff: " + evo[4]);
    	System.out.println("brakeDiffOrig: " + evo[5]);
    	System.out.println("brakeDiffMultiplier: " + evo[6]);
    	System.out.println("switchSteering: " + evo[7]);
    	System.out.println("combiSteeringNormalWeight: " + evo[8]);
    	System.out.println("CombiSteeringSmoothedWeight: " + evo[9]);
    }

    private double[] getValues(SensorModel sensors){     
    	return MyNN.useNN(sensors);
    }
    
    private double[] getNEATValues(SensorModel sensors){     
    	return NeatNN.useNN(sensors);
    }
    
    private double id = Math.random();
    
    public String getDriverName() {
        return "simple example: " + this.id;
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
		//System.out.println(sensors.getRacePosition() + ": "+sensors.getLastLapTime());
		//if(sensors.getRacePosition() == 1){
		//	System.out.println(sensors.getDistanceFromStartLine());
		//}
		if(sensors.getRacePosition() == 1 && sensors.getLastLapTime() != 0.0){ //
			writeEvo(sensors);
			action.abandonRace = true;
		}
		if(sensors.getDamage() > 0){
			writeEvo(sensors);
			action.abandonRace = true;
		}
		double[] frontDistances = this.getFrontDistances(sensors);
		double targetSpeedMultiplier = evo[0];
		double targetSpeed = targetSpeedMultiplier*this.max(frontDistances); //possible optimization
		if (targetSpeed < 0) {
			double trackPos = sensors.getTrackPosition();
			action.accelerate = 1;
			action.brake = 0;
			action.steering = - Math.signum(trackPos);
			return;
		}
		action.accelerate = this.getSwarmAcceleration(sensors, targetSpeed);
		action.brake = this.getSwarmBrake(sensors, targetSpeed);
		action.steering = this.getSwarmSteering(sensors, frontDistances);
		//System.out.print(action.accelerate + " ");
    	//System.out.print(action.brake + " " );
    	//System.out.print(action.steering +"\n");
	}
	
	private void swarmControlVectorized(Action action, SensorModel sensors) {
		//System.out.println(sensors.getRacePosition() + ": "+sensors.getLastLapTime());
//		if(sensors.getRacePosition() == 1 && sensors.getLastLapTime() != 0.0){
//			writeEvo(sensors);
//			action.abandonRace = true;
//		}
//		if(sensors.getDamage() > 0){
//			writeEvo(sensors);
//			action.abandonRace = true;
//		}
		double[] frontDistances = this.getFrontDistances(sensors);
		if (frontDistances[0] < 0) {
			double trackPos = sensors.getTrackPosition();
			action.accelerate = 1;
			action.brake = 0;
			action.steering = - Math.signum(trackPos);
			return;
		}
		int bestAngle = this.argmax(frontDistances);
		double smoothedTargetSpeedMultiplier = evo[1];
		double[] smoothedDistances = this.smooth(frontDistances, bestAngle);
		double targetSpeed = smoothedTargetSpeedMultiplier * smoothedDistances[0];
		action.accelerate = this.getSwarmAcceleration(sensors, targetSpeed);
		action.brake = this.getSwarmBrake(sensors, targetSpeed);
		action.steering = this.getSwarmSteeringVectorized(sensors, smoothedDistances[1]);
//		System.out.print(action.accelerate + " ");
//    	System.out.print(action.brake + " " );
//    	System.out.print(action.steering +"\n");
	}
	
	private double[] getFrontDistances(SensorModel sensors) {
		double[] trackEdges = sensors.getTrackEdgeSensors();
		double[] opponents = sensors.getOpponentSensors();
		int firstOpponent = 9;
		double opponentSensorMultiplier = evo[2];
		double[] dists = new double[trackEdges.length];
		for (int i = 0; i < trackEdges.length; ++i) {
			dists[i] = Math.min(trackEdges[i], opponents[firstOpponent + i]*opponentSensorMultiplier); // optimize
		}
//		this.printArray(dists);
		return dists;
	}
 
	
	private double[] smooth(double[] dists, int best) {
		double windowSize = evo[3];
		int window = (int) windowSize; // optimize
		double[] xs = new double[2 * window - 1];
		double[] ys = new double[2 * window - 1];
		int start = best - (window - 1);
		int end = best + window;
		for (int i = start; i < end; ++i) {
			if (i < 0 || i >= dists.length) {
				continue;
			}
			xs[i - start] = dists[i] * Math.sin(- Math.PI/2 + i * Math.PI/18);
			ys[i - start] = dists[i] * Math.cos(- Math.PI/2 + i * Math.PI/18);
		}
		double[] smoothedX = new double[window];
		double[] smoothedY = new double[window];
		for (int i = 0; i < smoothedX.length; ++i) {
			for (int j = 0; j < window; ++j) {
				smoothedX[i] += xs[i + j];
				smoothedY[i] += ys[i + j];
			}
			smoothedX[i] /= window;
			smoothedY[i] /= window;
		}
		double bestMagnitude = Double.NEGATIVE_INFINITY;
		double sine = 0;
		for (int i = 0; i < smoothedX.length; ++i) {
			double magnitude = Math.sqrt(smoothedX[i] * smoothedX[i] + smoothedY[i] * smoothedY[i]);
			if (bestMagnitude < magnitude) {
				bestMagnitude = magnitude;
				sine = smoothedX[i] / magnitude; // Math.floor(10 *  (smoothedX[i] / magnitude)) / (double) 10; // 
			}
		}
		double[] result = {bestMagnitude, sine};
//		System.out.println(best);
//		this.printArray(result);
		return result;
	}
	private double getSwarmAcceleration(SensorModel sensors, double tSpeed) {
		double curSpeed = sensors.getSpeed();
		double speedDiff = tSpeed - curSpeed;
		double accelerationDiff = evo[4];
		if (speedDiff < 0) {
			return 0;
		} else if (speedDiff > accelerationDiff) { //possible optimization
			return 1;
		} else { 
			return Math.min(1, speedDiff / accelerationDiff); //possible optimization
		}
	}
	
	
	private double getSwarmBrake(SensorModel sensors, double tSpeed) {
		double curSpeed = sensors.getSpeed();
		double speedDiff = tSpeed - curSpeed;
		double brakeDiffOrig = evo[5];
		//double brakeDiffMultiplier = evo[6];
		double brakeDiff = brakeDiffOrig; //possible optimization
//		if (curSpeed > 150){
//			brakeDiff = brakeDiffOrig * 0.75;
//		}
//		if(curSpeed > 200){
//			brakeDiff = brakeDiffOrig * 0.25;
//		}
//		if(curSpeed > 225){
//			brakeDiff = brakeDiffOrig * 0.15;
//		}
//		if(curSpeed > 250){
//			brakeDiff = 1;
//		}
		//System.out.println("BRAKEDIFF:" + brakeDiff);
		if (speedDiff > 0) {
			return 0;
		} else if (speedDiff < - brakeDiff) {
			return 1;
		} else {
			return Math.min(1, Math.abs(speedDiff / brakeDiffOrig)); //possible optimization (was 200)(brakeDiff * brakeDiffMultiplier)
		}
	}
	
	private double getSwarmSteering(SensorModel sensors, double[] dists) {
		int maxDir = this.argmax(dists);
		double weightedDir = this.getWeightedDir(dists);
		//System.out.println(maxDir);
		//System.out.println(weightedDir);
		double maxAngle = (double) (maxDir - 9) / (double) 10;
		double maxAngleWeighted = (double) (weightedDir - 9) / (double) 10;
		//return (maxAngle + maxAngle +  maxAngleWeighted)/3;
		if(maxAngle <= evo[7] && maxAngle >= - evo[7]){
			//System.out.println("maxAngleWeighted activated");
			return maxAngleWeighted;
		}else{
			//System.out.println("maxAngle activated");
			return (evo[8]*maxAngle +  evo[9]*maxAngleWeighted)/(evo[8] + evo[9]); //Weighted
		}

		//return maxAngle;
	}
	
	private double getSwarmSteeringVectorized(SensorModel sensors, double cosine) {
		return cosine;
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
	
	private double getWeightedDir(double[] dists){
		double totalWeight = 0.0;
		double totalFreq = 0.0;
		for(int i = 0; i < dists.length ; i++){
			totalFreq = totalFreq + dists[i];
			totalWeight = totalWeight + (double) (i) * dists[i];
		}
		double weightedDir = totalWeight/totalFreq;
		//System.out.print(weightedDir);
		return weightedDir;
	}
	
	private void printArray(double[] array) {
		for (double number : array) {
			System.out.print(number + ",");
		}
		System.out.println();
		System.out.flush();
	}
	
	public static void serializeEvo(double[][] evolutionaryValues){
		try{
			FileOutputStream fileOut = new FileOutputStream("evo/evolutionaryValues.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(evolutionaryValues);
	        out.close();
	        fileOut.close();
	        System.out.printf("Serialized data is saved in evolutionaryValues.ser");
		}
		catch(IOException i){
			i.printStackTrace();
		}
	}
		
	public static double[][] deserializeEvo(){
		try{
			FileInputStream fileIn = new FileInputStream("evo/evolutionaryValues.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			double[][] evolutionaryValues = (double[][]) in.readObject();
			in.close();
			fileIn.close();
			System.out.println("Deserialized evolutionaryValues.ser");
			return evolutionaryValues;
		}
		catch(IOException i){
			i.printStackTrace();
			return null;
		}
		catch(ClassNotFoundException c){
			System.out.println("Employee class not found");
			c.printStackTrace();
			return null;
		}
	}
}