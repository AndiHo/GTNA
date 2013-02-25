/* ===========================================================
 * GTNA : Graph-Theoretic Network Analyzer
 * ===========================================================
 *
 * (C) Copyright 2009-2011, by Benjamin Schiller (P2P, TU Darmstadt)
 * and Contributors
 *
 * Project Info:  http://www.p2p.tu-darmstadt.de/research/gtna/
 *
 * GTNA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GTNA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * ---------------------------------------
 * Greedy.java
 * ---------------------------------------
 * (C) Copyright 2009-2011, by Benjamin Schiller (P2P, TU Darmstadt)
 * and Contributors 
 *
 * Original Author: benni;
 * Contributors:    -;
 *
 * Changes since 2011-05-17
 * ---------------------------------------
 *
 */
package gtna.routing.greedy;

import gtna.graph.Graph;
import gtna.graph.GraphProperty;
import gtna.graph.Node;
import gtna.graph.spanningTree.SpanningTree;
import gtna.id.APFIdentifier;
import gtna.id.APFIdentifierSpace;
import gtna.id.APFPartition;
import gtna.id.BIIdentifier;
import gtna.id.BIIdentifierSpace;
import gtna.id.BIPartition;
import gtna.id.DIdentifier;
import gtna.id.DIdentifierSpace;
import gtna.id.DPartition;
import gtna.id.SIdentifier;
import gtna.id.SIdentifierSpace;
import gtna.id.SPartition;
import gtna.id.prefix.PrefixSIdentiferSpaceSimple;
import gtna.id.prefix.PrefixSIdentifier;
import gtna.id.prefix.PrefixSPartitionSimple;
import gtna.routing.Route;
import gtna.routing.RouteImpl;
import gtna.routing.RoutingAlgorithm;
import gtna.util.parameter.IntParameter;
import gtna.util.parameter.Parameter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

import org.apfloat.Apfloat;

/**
 * @author Andreas Höfer, Stefanie Roos
 * Simple Extension of Greedy Routing for Virtula Tree Prefix Embedding:
 * In case of a local minimum forward the message to the parent in the tree  
 */

public class GreedyTree extends RoutingAlgorithm {

	boolean debug = true;

	private PrefixSIdentiferSpaceSimple idSpacePE;
	private PrefixSPartitionSimple[] pPE;
	private SpanningTree st;
	
	private int ttl;

	public GreedyTree() {
		super("GREEDYTREE");
		this.ttl = Integer.MAX_VALUE;
	}

	public GreedyTree(int ttl) {
		super("GREEDYTREE", new Parameter[] { new IntParameter("TTL", ttl) });
		this.ttl = ttl;
	}

	@Override
	public Route routeToRandomTarget(Graph graph, int start, Random rand) {
		if (this.idSpacePE != null) {
			return this.routeToRandomTargetPE(graph, start, rand);
		}else {			
			return null;
		}
	
	}

	private Route routeToRandomTargetPE(Graph graph, int start, Random rand) {
		if (!((PrefixSIdentifier)this.pPE[start].getRepresentativeID()).isSet()){
			return new RouteImpl(new ArrayList<Integer>(), false);
		}
		PrefixSIdentifier target = (PrefixSIdentifier) this.idSpacePE.randomID(rand);
		while (!target.isSet() || this.pPE[start].contains(target) ) {
			target = (PrefixSIdentifier) this.idSpacePE.randomID(rand);
		}
		return this.routePE(new ArrayList<Integer>(), start, target, rand,
				graph.getNodes());
	}
		
	private Route routePE(ArrayList<Integer> route, int current,
			PrefixSIdentifier target, Random rand, Node[] nodes) {
		route.add(current);
		if (this.idSpacePE.getPartitions()[current].contains(target)) {
			return new RouteImpl(route, true);
		}
		if (route.size() > this.ttl) {
			return new RouteImpl(route, false);
		}
		int currentDist = this.idSpacePE.getPartitions()[current]
				.distance(target);
		int minDist = currentDist;
		int minNode = -1;
		for (int out : nodes[current].getOutgoingEdges()) {
			int dist = this.pPE[out].distance(target);
			if (dist < minDist) {
				minDist = dist;
				minNode = out;
			}
		}
		if (minNode == -1) {
			// if the routing gets stuck, go the parent
			minNode = st.getParent(current);
			// if there is no parent the routing fails
			if (minNode == -1)
				return new RouteImpl(route, false);
		}
		return this.routePE(route, minNode, target, rand, nodes);
	}
	
	
	@Override
	public boolean applicable(Graph graph) {
		return graph.hasProperty("ID_SPACE_0") && (graph.getProperty("ID_SPACE_0") instanceof PrefixSIdentiferSpaceSimple) && graph.hasProperty("SPANNINGTREE"); 
	}

	@Override
	public void preprocess(Graph graph) {
		GraphProperty p = graph.getProperty("ID_SPACE_0");
		this.idSpacePE = (PrefixSIdentiferSpaceSimple) p;
		this.pPE = (PrefixSPartitionSimple[]) this.idSpacePE.getPartitions();
		this.st = (SpanningTree) graph.getProperty("SPANNINGTREE"); 
	}
}
