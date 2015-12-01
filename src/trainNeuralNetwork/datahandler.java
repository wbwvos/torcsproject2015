package trainNeuralNetwork;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class datahandler {
	
	private double[] mins;
	private double[] maxes;
	
	public List<List<List<Double>>> readCSV(String csvFile, int inputsCount, int outputsCount, boolean normalizeInputs){
		BufferedReader br = null;
		String line = "";
		String separator = ",";
		
		List<List<Double>> input = new LinkedList<List<Double>>();
		List<List<Double>> output = new LinkedList<List<Double>>();
		this.mins = new double[inputsCount];
		this.maxes = new double[inputsCount];
		
		try {
						br = new BufferedReader(new FileReader(csvFile));
			//skip header
			line = br.readLine();
			while ((line = br.readLine()) != null) {
				// use comma as separator
				String[] values = line.split(separator);
				List<Double> currentInput = new ArrayList<Double>(inputsCount);
				List<Double> currentOutput = new ArrayList<Double>(outputsCount);

				for(int i = 0; i < values.length; ++i){
					if(i < inputsCount){
						double value = values[i].isEmpty() ? 0.0 : Double.parseDouble(values[i]);
						currentInput.add(value);
						if (this.mins[i] > value) {
							this.mins[i] = value;
						}
						if (this.maxes[i] < value) {
							this.maxes[i] = value;
						}
					}
					else{
						currentOutput.add(Double.parseDouble(values[i]));
					}
				}
				input.add(currentInput);
				output.add(currentOutput);
			}

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

		if (normalizeInputs) {
			this.normalizeInputs(input, this.mins, this.maxes);
		}
		
		System.out.println("Done");
		List<List<List<Double>>> data = new ArrayList<List<List<Double>>>(2);
		data.add(input);
		data.add(output);
		return data;
	}
	
	public double[] getMins() {
		return this.mins;
	}
	
	public double[] getMaxes() {
		return this.maxes;
	}
	
	private void normalizeInputs(List<List<Double>> inputs, double[] mins, double[] maxes) {
		for (List<Double> input : inputs) {
			for (int i = 0; i < input.size(); ++i) {
				double scaledInput = datahandler.scale(input.get(i), mins[i], maxes[i]);
				input.set(i, scaledInput);
			}
		}
	}
	
	public static void normalize(double[] inputs, double[] mins, double[] maxes) {
		for (int i = 0; i < inputs.length; ++i) {
			inputs[i] = datahandler.scale(inputs[i], mins[i], maxes[i]);
		}
	}
	
	private static double scale(double number, double min, double max) {
		double t = (number - min) / (max - min);
		return (1-t)*0 + t*1;
	}
}
