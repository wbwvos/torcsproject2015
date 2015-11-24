package NEAT;
import java.util.List;
import java.util.Vector;
import java.util.Comparator;
import java.util.LinkedList;

public class NNPopulation {

	private List<Species> species;
	private int populationSize;
	
	private static long innovationNumber = 0;
	private static final double sameSpeciesThreshold = 3.0;
	
	public NNPopulation() {
		this.species = new LinkedList<Species>();
	}
	
	public void randomInitialize(int populationSize, int inputs, int outputs) {
		this.populationSize = populationSize;
		NNGenome genome = new NNGenome(inputs, outputs);
		innovationNumber = genome.getHighestInnovationNumber();
		Species firstSpecies = new Species();
		firstSpecies.setRepresentative(genome);
		for (int i = 0; i < populationSize; ++i) {
			firstSpecies.addGenome(genome);
		}
	}
	
	public void epoch() {
		//create new species
		List<Species> newSpecies = new LinkedList<Species>();
		for (Species species : this.species) {
			Species newSpecie = new Species();
			newSpecie.setRepresentative(species.getRepresentative());
			newSpecies.add(newSpecie);
		}
		//calculate number of offsprings and number of carried on individuals
		int carryOnIndividuals = this.calculateOffspringsPerSpecies();
		//carry on in the new species the best individuals from the previous population
		List<NNGenome> individuals = this.getBestIndividuals(carryOnIndividuals);
		for (NNGenome individual : individuals) {
			this.speciate(individual, newSpecies);
		}
		//produce offsprings from old species and add to new species
		for (Species species : this.species) {
			List<NNGenome> offsprings = species.produceOffsprings();
			for (NNGenome offspring : offsprings) {
				this.speciate(offspring, newSpecies);
			}
		}
		//mutate new species
		for (Species species : newSpecies) {
			species.mutateSpecies();
		}
		//set new species to be the species
		this.species = newSpecies;
	}
	
	private int calculateOffspringsPerSpecies() {
		double[] speciesFitness = new double[this.species.size()];
		double fitnessSum = 0;
		int index = 0;
		for (Species species : this.species) {
			speciesFitness[index] = species.getFitness();
			fitnessSum += speciesFitness[index];
			++index;
		}
		
		//crossover produces only 75% of offsprings
		int totalOffsprings = 0;
		double precompute = 0.75 * (double)(this.populationSize) / fitnessSum;
		for (int i = 0; i < speciesFitness.length; ++i) {
			int offsprings = (int) Math.round(precompute*speciesFitness[i]);
			this.species.get(i).allowOffsprings(offsprings);
			totalOffsprings += offsprings;
		}
		return this.populationSize - totalOffsprings;
	}
	
	private List<NNGenome> getBestIndividuals(int count) {
		Vector<NNGenome> individuals = new Vector<NNGenome>(this.populationSize);
		for (Species species : this.species) {
			List<NNGenome> genomes = species.getGenomes();
			for (NNGenome genome : genomes) {
				individuals.add(genome);
			}
		}
		individuals.sort(new Comparator<NNGenome>() {
			@Override
			public int compare(NNGenome left, NNGenome right) {
				return (int) (right.getFitness() - left.getFitness());
			}
		});
		return individuals.subList(0, count);
	}
	
	private void speciate(NNGenome network, List<Species> allSpecies) {
		boolean added = false;
		for (Species species : allSpecies) {
			double compatibility = species.getCompatibility(network);
			if (compatibility < NNPopulation.sameSpeciesThreshold) {
				species.addGenome(network);
				added = false;
				break;
			}
		}
		if (!added) {
			Species newSpecies = new Species();
			newSpecies.setRepresentative(network);
			newSpecies.addGenome(network);
			allSpecies.add(newSpecies);
		}
	}
}
