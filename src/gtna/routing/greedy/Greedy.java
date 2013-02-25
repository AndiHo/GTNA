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
 * @author benni
 * 
 */

public class Greedy extends RoutingAlgorithm {

	boolean debug = true;
		
	private DIdentifierSpace idSpaceD;
	private DPartition[] pD;

	private BIIdentifierSpace idSpaceBI;
	private BIPartition[] pBI;
	
	private APFIdentifierSpace idSpaceAPF;
	private APFPartition[] pAPF;
	
	private SIdentifierSpace idSpaceS;
	private SPartition[] pS;

	private PrefixSIdentiferSpaceSimple idSpacePE;
	private PrefixSPartitionSimple[] pPE;
	
	private int ttl;

	public Greedy() {
		super("GREEDY");
		this.ttl = Integer.MAX_VALUE;
	}

	public Greedy(int ttl) {
		super("GREEDY", new Parameter[] { new IntParameter("TTL", ttl) });
		this.ttl = ttl;
	}

	@Override
	public Route routeToRandomTarget(Graph graph, int start, Random rand) {
		if (this.idSpaceBI != null) {
			return this.routeToRandomTargetBI(graph, start, rand);
		} else if (this.idSpaceD != null) {
			return this.routeToRandomTargetD(graph, start, rand);
		} else if (this.idSpaceAPF != null) {
			return this.routeToRandomTargetAPF(graph, start, rand);
		} else if (this.idSpaceS != null) {
			return this.routeToRandomTargetS(graph, start, rand);
		}else if (this.idSpacePE != null) {
			return this.routeToRandomTargetPE(graph, start, rand);
		}else {			
			return null;
		}
	
	}


	private Route routeToRandomTargetBI(Graph graph, int start, Random rand) {
		BIIdentifier target = (BIIdentifier) this.idSpaceBI.randomID(rand);
		while (this.pBI[start].contains(target)) {
			target = (BIIdentifier) this.idSpaceBI.randomID(rand);
		}
		return this.routeBI(new ArrayList<Integer>(), start, target, rand,
				graph.getNodes());
	}

	private Route routeBI(ArrayList<Integer> route, int current,
			BIIdentifier target, Random rand, Node[] nodes) {
		route.add(current);
		if (this.idSpaceBI.getPartitions()[current].contains(target)) {
			return new RouteImpl(route, true);
		}
		if (route.size() > this.ttl) {
			return new RouteImpl(route, false);
		}
		BigInteger currentDist = this.idSpaceBI.getPartitions()[current]
				.distance(target);
		BigInteger minDist = this.idSpaceBI.getMaxDistance();
		int minNode = -1;
		for (int out : nodes[current].getOutgoingEdges()) {
			BigInteger dist = this.pBI[out].distance(target);
			if (dist.compareTo(minDist) == -1
					&& dist.compareTo(currentDist) == -1) {
				minDist = dist; // System.err.println("Greedy routing failed at node: " + current);
				minNode = out;
			}
		}
		if (minNode == -1) {
			return new RouteImpl(route, false);
		}
		return this.routeBI(route, minNode, target, rand, nodes);
	}

	private Route routeToRandomTargetD(Graph graph, int start, Random rand) {
		DIdentifier target = (DIdentifier) this.idSpaceD.randomID(rand);
		while (this.pD[start].contains(target)) {
			target = (DIdentifier) this.idSpaceD.randomID(rand);
		}
		return this.routeD(new ArrayList<Integer>(), start, target, rand,
				graph.getNodes());
	}

	private Route routeD(ArrayList<Integer> route, int current,
			DIdentifier target, Random rand, Node[] nodes) {
		route.add(current);
		if (this.idSpaceD.getPartitions()[current].contains(target)) {
			return new RouteImpl(route, true);
		}
		if (route.size() > this.ttl) {
			return new RouteImpl(route, false);
		}
		double currentDist = this.idSpaceD.getPartitions()[current]
				.distance(target);
		double minDist = this.idSpaceD.getMaxDistance();
		int minNode = -1;
		for (int out : nodes[current].getOutgoingEdges()) {
			double dist = this.pD[out].distance(target);
			if (dist < minDist && dist < currentDist) {
				minDist = dist;
				minNode = out;
			}
		}
		if (minNode == -1) {
			return new RouteImpl(route, false);
		}
		return this.routeD(route, minNode, target, rand, nodes);
	}

	private Route routeToRandomTargetAPF(Graph graph, int start, Random rand) {
		APFIdentifier target = (APFIdentifier) this.idSpaceAPF.randomID(rand);
		while (this.pAPF[start].contains(target)) {
			target = (APFIdentifier) this.idSpaceAPF.randomID(rand);
		}
		return this.routeAPF(new ArrayList<Integer>(), start, target, rand,
				graph.getNodes());
	}

	private Route routeAPF(ArrayList<Integer> route, int current,
			APFIdentifier target, Random rand, Node[] nodes) {
		route.add(current);
		if (this.idSpaceAPF.getPartitions()[current].contains(target)) {
			return new RouteImpl(route, true);
		}
		if (route.size() > this.ttl) {
			return new RouteImpl(route, false);
		}
		Apfloat currentDist = this.idSpaceAPF.getPartitions()[current]
				.distance(target);
		Apfloat minDist = currentDist;
		int minNode = -1;
		for (int out : nodes[current].getOutgoingEdges()) {
			Apfloat dist = this.pAPF[out].distance(target);
			// lhs.compareTo(rhs) returns -1 iff lhs < rhs
			if (dist.compareTo(minDist) == -1) {
				minDist = dist;
				minNode = out;
			}
		}
		if (minNode == -1) {
			if (debug){
				System.err.println("Target:" + target);
				System.err.println("Greedy routing failed at node: " + current);
				System.err.println("Dist of node to target: " + currentDist);
				for (int out : nodes[current].getOutgoingEdges()) {
					Apfloat dist = this.pAPF[out].distance(target);
					System.err.println("Dist of node " + out + ": " + dist);
				}
			}
			return new RouteImpl(route, false);
		}
		return this.routeAPF(route, minNode, target, rand, nodes);
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
	
	private Route routeToRandomTargetS(Graph graph, int start, Random rand) {
		SIdentifier target = (SIdentifier) this.idSpaceS.randomID(rand);
		while (this.pS[start].contains(target) ) {
			target = (SIdentifier) this.idSpaceS.randomID(rand);
		}
		return this.routeS(new ArrayList<Integer>(), start, target, rand,
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
			return new RouteImpl(route, false);
		}
		return this.routePE(route, minNode, target, rand, nodes);
	}
	
	private Route routeS(ArrayList<Integer> route, int current,
			SIdentifier target, Random rand, Node[] nodes) {
		route.add(current);
		if (this.idSpaceS.getPartitions()[current].contains(target)) {
			return new RouteImpl(route, true);
		}
		if (route.size() > this.ttl) {
			return new RouteImpl(route, false);
		}
		short currentDist = this.idSpaceS.getPartitions()[current]
				.distance(target);
		short minDist = currentDist;
		int minNode = -1;
		for (int out : nodes[current].getOutgoingEdges()) {
			short dist = this.pS[out].distance(target);
			if (dist < minDist) {
				minDist = dist;
				minNode = out;
			}
		}
		if (minNode == -1) {
			return new RouteImpl(route, false);
		}
		return this.routeS(route, minNode, target, rand, nodes);
	}
	
	@Override
	public boolean applicable(Graph graph) {
		return graph.hasProperty("ID_SPACE_0")
				&& (graph.getProperty("ID_SPACE_0") instanceof DIdentifierSpace || graph
						.getProperty("ID_SPACE_0") instanceof BIIdentifierSpace ||
						graph.getProperty("ID_SPACE_0") instanceof APFIdentifierSpace || 
						graph.getProperty("ID_SPACE_0") instanceof SIdentifierSpace ||
						graph.getProperty("ID_SPACE_0") instanceof PrefixSIdentiferSpaceSimple );
	}

	@Override
	public void preprocess(Graph graph) {
		GraphProperty p = graph.getProperty("ID_SPACE_0");
		this.idSpaceD = null;
		this.pD = null;
		this.idSpaceBI = null;
		this.pBI = null;
		this.idSpaceAPF = null;
		this.pAPF = null;
		this.idSpaceS = null;
		this.pS = null;
		this.idSpacePE = null;
		this.pPE = null;
		if (p instanceof DIdentifierSpace) {
			this.idSpaceD = (DIdentifierSpace) p;
			this.pD = (DPartition[]) this.idSpaceD.getPartitions();
		} else if (p instanceof BIIdentifierSpace) {
			this.idSpaceBI = (BIIdentifierSpace) p;
			this.pBI = (BIPartition[]) this.idSpaceBI.getPartitions();
		} else if (p instanceof APFIdentifierSpace) {
			this.idSpaceAPF = (APFIdentifierSpace) p;
			this.pAPF = (APFPartition[]) this.idSpaceAPF.getPartitions();
		} else if (p instanceof SIdentifierSpace) {
			this.idSpaceS = (SIdentifierSpace) p;
			this.pS = (SPartition[]) this.idSpaceS.getPartitions();
		} else if (p instanceof PrefixSIdentiferSpaceSimple) {
			this.idSpacePE = (PrefixSIdentiferSpaceSimple) p;
			this.pPE = (PrefixSPartitionSimple[]) this.idSpacePE.getPartitions();
		}
	}
}
