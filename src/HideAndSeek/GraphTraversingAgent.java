package HideAndSeek;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import HideAndSeek.graph.GraphController;
import HideAndSeek.graph.StringEdge;
import HideAndSeek.graph.StringVertex;
import Utility.Utils;

/**
 * @author Martin
 *
 */
public abstract class GraphTraversingAgent implements GraphTraverser {

	/**
	 * 
	 */
	protected GraphController<StringVertex, StringEdge> graphController;
	
	/**
	 * @param graph
	 */
	public GraphTraversingAgent(GraphController<StringVertex, StringEdge> graphController) {
		
		this.graphController = graphController;
		
		graphController.registerTraversingAgent(this);
		
		if ( uniquelyVisitNodes == true ) {
		
			uniquelyVisitedNodes = new HashSet<StringVertex>();
		
			uniquelyVisitedEdges = new HashSet<StringEdge>();
			
		}
		
		name = this.getClass().toString().substring(this.getClass().toString().lastIndexOf('.') + 1, this.getClass().toString().length());
		
		ID = "" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()) + Math.random() * 100;

		
	}
	
	/**
	 * 
	 */
	protected String ID;
	
	/**
	 * 
	 */
	protected String name;
	
	/* (non-Javadoc)
	 * @see HideAndSeek.seeker.SeekerTemplate#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		
		this.name = name;
	
	}
	
	/**
	 * @return
	 */
	protected StringVertex firstNode() {
		
		StringVertex[] vertices = new StringVertex[graphController.vertexSet().size()];
		
		return graphController.vertexSet().toArray(vertices)[0];
				
	}
	
	/**
	 * @return
	 */
	protected StringVertex randomNode() {
		
		StringVertex[] vertices = new StringVertex[graphController.vertexSet().size()];
		
		graphController.vertexSet().toArray(vertices);
		
		return vertices[(int)(Math.random() * vertices.length)];
		
	}
	
	/**
	 * 
	 */
	protected boolean uniquelyVisitNodes = true;
	
	/**
	 * 
	 */
	private HashSet<StringVertex> uniquelyVisitedNodes; 
	
	/**
	 * @param node
	 */
	protected void addUniquelyVisitedNode(StringVertex node) {
		
		uniquelyVisitedNodes.add(node);
		
	}
	
	/* (non-Javadoc)
	 * @see HideAndSeek.GraphTraverserTemplate#uniquelyVisitedNodes()
	 */
	@Override
	final public HashSet<StringVertex> uniquelyVisitedNodes() {
		
		return uniquelyVisitedNodes;
		
	}
	
	/**
	 * 
	 */
	private HashSet<StringEdge> uniquelyVisitedEdges;
	
	/**
	 * @param node
	 */
	protected void addUniquelyVisitedEdge(StringEdge edge) {
		
		uniquelyVisitedEdges.add(edge);
		
	}
	
	/* (non-Javadoc)
	 * @see HideAndSeek.GraphTraverserTemplate#uniquelyVisitedEdges()
	 */
	@Override
	final public HashSet<StringEdge> uniquelyVisitedEdges() {
		
		return uniquelyVisitedEdges;
		
	}
	
	/**
	 * @param edge
	 * @return
	 */
	protected boolean visitedEdge(StringEdge edge) {
		
		return uniquelyVisitedEdges.contains(edge);
		
	}
	
	/**
	 * Working with connectedNode in order to determine how nodes
	 * from a set are selected. Default is random.
	 * 
	 * @param connectedEdges
	 * @return
	 */
	protected StringEdge getConnectedEdge(StringVertex currentNode, List<StringEdge> connectedEdges) {
	
		return connectedEdges.get((int)(Math.random() * connectedEdges.size()));
		
	}
	
	/**
	 * Return the edges linked to the specified node. Default is
	 * all connected nodes.
	 * 
	 * @param currentNode
	 * @return
	 */
	protected List<StringEdge> getConnectedEdges(StringVertex currentNode) {
		
		return new ArrayList<StringEdge>(graphController.edgesOf(currentNode));
		
	}
	
	/**
	 * Select a connected node according to how edges are sorted, and how
	 * edges are selected from this set.
	 * 
	 * A wrapper method, of sorts, to enforce additional constraints such a
	 * no backtracking.
	 * 
	 * @param currentNode
	 * @return
	 */
	protected StringVertex connectedNode(StringVertex currentNode) {
		
		List<StringEdge> connectedEdges = getConnectedEdges(currentNode);
		
		StringEdge connectedEdge = null;
		
		StringVertex target = null;
		
		HashSet<StringEdge> selectedInThisSession = new HashSet<StringEdge>();
		
		do {
			
			// If we have tried all outgoing edges available (more a programmatic choice than a strategic one), return random.
			if (selectedInThisSession.size() == connectedEdges.size()) {
				
				target = edgeToTarget(connectedEdges.get((int)(Math.random() * connectedEdges.size())), currentNode);
				
				break;
				
			}
			
			connectedEdge = getConnectedEdge(currentNode, connectedEdges);
			
			target = edgeToTarget(connectedEdge, currentNode);
			
			selectedInThisSession.add(connectedEdge);
			
					// Otherwise, loop while not allowed to repeat nodes
		} while (   uniquelyVisitNodes == true && uniquelyVisitedNodes.contains( target ) );
		
		addUniquelyVisitedNode(target);
		
		addUniquelyVisitedEdge(connectedEdge);
		
		return target;
		
	}
	
	/**
	 * @param connectedEdge
	 * @param currentNode
	 * @return
	 */
	protected StringVertex edgeToTarget(StringEdge connectedEdge, StringVertex currentNode) {
		
		if (connectedEdge.getSource().equals(currentNode)) {
			
			return connectedEdge.getTarget();
			
		} else if (connectedEdge.getTarget().equals(currentNode)) {
			
			return connectedEdge.getSource();
			
		}
		
		return null;
		
	}
	
	/**
	 * 
	 */
	protected StringVertex currentNode = null;
	
	/**
	 * @return
	 */
	protected StringVertex getCurrentNode() {
		
		if (currentNode == null) {
			
			return randomNode();
			
		} else {
			
			return currentNode;
			
		}
		
	}
	
	/**
	 * Record of the number of rounds passed 
	 */
	protected int roundsPassed = 0;
	
	/**
	 * @return
	 */
	public int getRoundsPassed() {
		
		return roundsPassed;
	
	}

	/**
	 * 
	 */
	public void endOfRound() { 
		
		if (uniquelyVisitedNodes != null) {
			
			uniquelyVisitedNodes.clear();
			
		}
		
		if (uniquelyVisitedEdges != null) {
			
			uniquelyVisitedEdges.clear();
			
		}
		
		roundsPassed++;
		
	}
	
	/**
	 * Flags whether this strategy develops over all rounds.
	 * Required to test correct number of times (i.e. a single
	 * set of rounds isn't sufficient if this is true).
	 */
	protected boolean strategyOverRounds = false;
	
	/* (non-Javadoc)
	 * @see HideAndSeek.GraphTraverserTemplate#getStrategyOverRounds()
	 */
	@Override
	public boolean getStrategyOverRounds() {
		
		return strategyOverRounds;
		
	}
	
	/* (non-Javadoc)
	 * @see HideAndSeek.GraphTraverserTemplate#setStrategyOverRounds(boolean)
	 */
	@Override
	public void setStrategyOverRounds(boolean strategyOverRounds) {
		
		this.strategyOverRounds = strategyOverRounds;
		
	}
	
	/**
	 * 
	 */
	private boolean playing;
	
	/**
	 * 
	 */
	public void startPlaying() {
		
		playing = true;
		
	}
	
	/* (non-Javadoc)
	 * @see HideAndSeek.GraphTraverserTemplate#isPlaying()
	 */
	@Override
	public boolean isPlaying() {
		
		return playing;
		
	}
	
	/**
	 * 
	 */
	public void endOfGame() {
		
		playing = false;
		
		roundsPassed = 0;
		
	}
	
	/* (non-Javadoc)
	 * @see HideAndSeek.GraphTraverserTemplate#nextNode(HideAndSeek.graph.StringVertex)
	 */
	@Override
	public abstract StringVertex nextNode(StringVertex currentNode);
	
	/* (non-Javadoc)
	 * @see HideAndSeek.GraphTraverserTemplate#startNode()
	 */
	@Override
	public abstract StringVertex startNode();

	/* (non-Javadoc)
	 * @see HideAndSeek.GraphTraverserTemplate#printGameStats()
	 */
	@Override
	public String printGameStats() {
		
		return "";
		
	}

	/* (non-Javadoc)
	 * @see HideAndSeek.GraphTraverserTemplate#printRoundStats()
	 */
	@Override
	public String printRoundStats() {
		
		return "";
		
	}
	
	/* (non-Javadoc)
	 * @see HideAndSeek.hider.HiderTemplate#toString()
	 */
	@Override
	public String toString() {
		
		return "t" + name;
		
	}

}
