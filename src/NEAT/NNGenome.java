package NEAT;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Vector;
import java.util.LinkedList;

public class NNGenome {
	
	private List<Node> inputNodes;
	private List<Node> hiddenNodes;
	private List<Node> outputNodes;
	private Vector<Connection> connections;
	
	private double fitness;	
	private Random randomGenerator;
	
	public NNGenome(int inputs, int outputs) {
		this.initializeNodeGenes(inputs, outputs);
		this.initializeConnectionGenes();
		this.fitness = 0;
		this.randomGenerator = new Random();
	}
	
	private void initializeNodeGenes(int inputs, int outputs) {
		this.inputNodes = new LinkedList<Node>();
		int nodeId = 0;
		for (int i = 0; i < inputs; ++i) {
			this.inputNodes.add(new Node(++nodeId, NodeType.INPUT));
		}
		this.hiddenNodes = new LinkedList<Node>();
		this.outputNodes = new LinkedList<Node>();
		for (int i = 0; i < outputs; ++i) {
			this.outputNodes.add(new Node(++nodeId, NodeType.OUTPUT));
		}
	}
	
	private void initializeConnectionGenes() {
		this.connections = new Vector<Connection>(this.inputNodes.size()*this.outputNodes.size());
		for (Node inputNode : this.inputNodes) {
			for (Node outputNode : this.outputNodes) {
				this.connections.add(new Connection(inputNode.getId(), outputNode.getId()));
			}
		}
	}
	
	public Vector<Connection> getSortedGenes() {
		this.connections.sort(null);
		return this.connections;
	}
	
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
	
	public double getFitness() {
		return this.fitness;
	}
	
	public void mutate(boolean smallSpecies) {
		this.mutateWeights();
		this.mutateStructure(smallSpecies);
	}
	
	private void mutateWeights() {
		for (Connection connection : this.connections) {
			if (this.randomGenerator.nextDouble() > 0.8) {
				//80% chance of mutation
				continue;
			}
			if (this.randomGenerator.nextDouble() > 0.9) {
				//10% chance of random reset
				connection.setWeight(NNGenome.scale(this.randomGenerator.nextDouble(), 0, 1, -1, 1));
			} else {//90% chance of perturbation
				double perturbation = NNGenome.scale(this.randomGenerator.nextDouble(), 0, 1, -0.01, 0.01);
				connection.setWeight(connection.getWeight() + perturbation);
			}
		}
	}
	
	private void mutateStructure(boolean smallSpecies) {
		if (this.randomGenerator.nextDouble() <= 0.03) {
			this.addNode();
		}
		if (smallSpecies) {
			if (this.randomGenerator.nextDouble() <= 0.05) {
				this.addConnection();
			}
		} else {
			if (this.randomGenerator.nextDouble() <= 0.3) {
				this.addConnection();
			}
		}
	}
	
	private void addConnection() {
		//find unconnectedNodes
		int toCount = this.hiddenNodes.size() + this.outputNodes.size();
		int fromCount = this.inputNodes.size() + toCount;
		//default boolean value is false
		boolean[][] connectivityMatrix = new boolean[fromCount][toCount];
		for (Connection connection : this.connections) {
			int from = this.translateNodeIdToCoordinate(connection.getFrom(), true);
			int to = this.translateNodeIdToCoordinate(connection.getTo(), false);
			connectivityMatrix[from][to] = true;
		}
		List<Connection> connections = new LinkedList<Connection>();
		for (int from = 0; from < connectivityMatrix.length; ++from) {
			for (int to = 0; to < connectivityMatrix[0].length; ++to) {
				if (!connectivityMatrix[from][to]) {
					int fromId = this.translateCoordinateToNodeId(from, true);
					int toId = this.translateCoordinateToNodeId(to, false);
					connections.add(new Connection(fromId, toId));
				}
			}
		}
		int connectionIndex = this.randomGenerator.nextInt(connections.size());
		this.connections.add(connections.get(connectionIndex));
	}
	
	private void addNode() {
		int connectionIndex = this.randomGenerator.nextInt(this.connections.size());
		Connection connection = this.connections.get(connectionIndex);
		connection.disable();
		int nodeId = (this.hiddenNodes.isEmpty() ?
					this.outputNodes.get(this.outputNodes.size()-1).getId() :
					this.hiddenNodes.get(this.hiddenNodes.size()-1).getId()) + 1;
		this.hiddenNodes.add(new Node(nodeId, NodeType.HIDDEN));
		this.connections.add(new Connection(connection.getFrom(), nodeId, 1));
		this.connections.add(new Connection(nodeId, connection.getTo(), connection.getWeight()));
	}
	
	private int translateNodeIdToCoordinate(int nodeId, boolean from) {
		int index = 0;
		if (from) {
			for (Node node : this.inputNodes) {
				if (nodeId == node.getId()) {
					return index;
				}
				++index;
			}
		}
		for (Node node : this.hiddenNodes) {
			if (nodeId == node.getId()) {
				return index;
			}
			++index;
		}
		for (Node node : this.outputNodes) {
			if (nodeId == node.getId()) {
				return index;
			}
			++index;
		}
		return -1;
	}
	
	private int translateCoordinateToNodeId(int coordinate, boolean from) {
		int inputNodesCount = 0;
		if (from) {
			inputNodesCount = this.inputNodes.size();
		}
		int hiddenNodesCount = inputNodesCount + this.hiddenNodes.size();
		int outputNodesCount = hiddenNodesCount + this.outputNodes.size();
		if (coordinate < inputNodesCount) {
			return this.inputNodes.get(coordinate).getId();
		}
		if (inputNodesCount + coordinate < hiddenNodesCount) {
			return this.hiddenNodes.get(coordinate).getId();
		}
		if (hiddenNodesCount + coordinate < outputNodesCount) {
			return this.outputNodes.get(coordinate).getId();
		}
		return -1;
	}
	
	public long getHighestInnovationNumber() {
		ListIterator<Connection> it = this.connections.listIterator(this.connections.size()-1);
		long highestInnovationNumber = 0;
		while (it.hasPrevious()) {
			Connection connection = it.previous();
			if (highestInnovationNumber < connection.getInnovationNumber()) {
				highestInnovationNumber = connection.getInnovationNumber();
			}
		}
		return highestInnovationNumber;
	}
	
	public static double scale(double number, double min, double max, double newMin, double newMax) {
		double t = (number - min) / (max - min);
		return (1-t)*newMin + t*newMax;
	}
	
	private class Node {
		private int id;
		private NodeType type;
		
		public Node(int id, NodeType type) {
			this.id = id;
			this.type = type;
		}
		
		public int getId() {
			return this.id;
		}
		
		public NodeType getType() {
			return this.type;
		}
	}
	
	enum NodeType {
		INPUT,
		HIDDEN,
		OUTPUT;
	}
}