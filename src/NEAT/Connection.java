package NEAT;

import java.util.Random;

public class Connection implements Comparable<Connection> {
	private int from;
	private int to;
	private double weight;
	private boolean enabled;
	private long innovation;
	
	public Connection(int from, int to) {
		this.from = from;
		this.to = to;
		Random randomGenerator = new Random();
		this.weight = NNGenome.scale(randomGenerator.nextDouble(), 0, 1, -1, 1);
		this.enabled = true;
	}
	
	public Connection(int from, int to, double weight) {
		this.from = from;
		this.to = to;
		this.weight = weight;
		this.enabled = true;
	}
	
	public int getFrom() {
		return this.from;
	}
	
	public int getTo() {
		return this.to;
	}
	
	public double getWeight() {
		return this.weight;
	}
	
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	public void setInnovationNumber(long innovation) {
		this.innovation = innovation;
	}
	
	public long getInnovationNumber() {
		return this.innovation;
	}
	
	public void disable() {
		this.enabled = false;
	}
	
	public void enable() {
		this.enabled = true;
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}

	@Override
	public int compareTo(Connection other) {
		return (int) (this.innovation - other.innovation);
	}
}