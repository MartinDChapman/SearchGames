package HideAndSeek.hider.singleshot.preference;

import java.util.HashSet;

import HideAndSeek.graph.GraphController;
import HideAndSeek.graph.StringEdge;
import HideAndSeek.graph.StringVertex;
import HideAndSeek.hider.HidingAgent;

/**
 * @author Martin
 *
 */
public class NodeTypeRestricted extends HidingAgent {

	/**
	 * @param graph
	 * @param numberOfHideLocations
	 */
	public NodeTypeRestricted(
			GraphController <StringVertex, StringEdge> graphController,
			int numberOfHideLocations) {
		super(graphController, numberOfHideLocations);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see HideAndSeek.hider.Hider#hideHere(HideAndSeek.graph.StringVertex)
	 */
	@Override
	public boolean hideHere(StringVertex vertex) {
		
		HashSet<Character> types = new HashSet<Character>();
		
		/* Permit hide if node is connected to other nodes of all types
		  (mimics connectivity constraints) */
		for ( StringEdge edge : graphController.edgesOf(vertex)) {
			
			if ( !types.contains(graphController.getNodeType(edgeToTarget(edge, vertex))) ) {
				
				types.add(graphController.getNodeType(edgeToTarget(edge, vertex)));
				
			}
			
		}
		
		if (types.size() == graphController.getNumberOfNodeTypes()) return true;
		
		return false;
		
	}

	/* (non-Javadoc)
	 * @see HideAndSeek.GraphTraverser#nextNode(HideAndSeek.graph.StringVertex)
	 */
	@Override
	public StringVertex nextNode(StringVertex currentNode) {
		// TODO Auto-generated method stub
		return connectedNode(currentNode);
	}

	/* (non-Javadoc)
	 * @see HideAndSeek.GraphTraverser#startNode()
	 */
	@Override
	public StringVertex startNode() {
		
		StringVertex[] vertices = new StringVertex[graphController.vertexSet().size()];
		
		return graphController.vertexSet().toArray(vertices)[0];
		
	}

}
