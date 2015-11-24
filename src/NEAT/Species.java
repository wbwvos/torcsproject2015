package NEAT;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class Species {
	
	private List<NNGenome> networks;
	private NNGenome representative;
	private int allowedOffsprings;
	
	public Species() {
		this.networks = new LinkedList<NNGenome>();
		this.representative = null;
	}
	
	public void setRepresentative(NNGenome network) {
		this.representative = network;
	}
	
	public NNGenome getRepresentative() {
		return this.representative;
	}
	
	public void addGenome(NNGenome network) {
		this.networks.add(network);
	}
	
	public List<NNGenome> getGenomes() {
		return this.networks;
	}
	
	public double getFitness() {
		double fitness = 0;
		for (NNGenome network : this.networks) {
			fitness += network.getFitness();
		}
		return fitness / (double) this.networks.size();
	}
	
	public void allowOffsprings(int offsprings) {
		this.allowedOffsprings = offsprings;
	}
	
	public double getCompatibility(NNGenome network) {
		Vector<Connection> repConnections = this.representative.getSortedGenes();
		Vector<Connection> netConnections = network.getSortedGenes();
		
		int longerGenomeSize = repConnections.size() > netConnections.size() ? repConnections.size() : netConnections.size();
		int disjoint = 0;
		int excess = 0;
		double weightDiff = 0;
		int matches = 0;
		
		int repIndex = 0;
		int netIndex = 0;
		while (repIndex < repConnections.size() && netIndex < netConnections.size()) {
			Connection repConnection = repConnections.get(repIndex);
			Connection netConnection = netConnections.get(netIndex);
			long innovationComparison = repConnection.getInnovationNumber() - netConnection.getInnovationNumber();
			if (0 == innovationComparison) {
				//matching update weightDiff
				++matches;
				weightDiff += repConnection.getWeight() - netConnection.getWeight();
				//move to next elements
				++repIndex;
				++netIndex;
			} else { //disjoint
				++disjoint;
				//move to next element in the connections with smaller current number
				if (innovationComparison < 0) {
					++repIndex;
				} else {
					++netIndex;
				}
			}
		}
		
		//at most one of the following loops will work.
		//The other will not as it's condition ended the previous loop
		while (repIndex < repConnections.size()) {
			++excess;
			++repIndex;
		}
		
		while (netIndex < netConnections.size()) {
			++excess;
			++netIndex;
		}
		
		double averageWeightDiff = weightDiff / matches;
		
		return (double)excess/(double)longerGenomeSize + (double)disjoint/(double)longerGenomeSize + 0.4*averageWeightDiff;
	}
	
	public List<NNGenome> produceOffsprings() {
		//TODO add implementation
		throw new UnsupportedOperationException();
	}
	
	public void mutateSpecies() {
		//TODO add implementation
		throw new UnsupportedOperationException();
	}
}
