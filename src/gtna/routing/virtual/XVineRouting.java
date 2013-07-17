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
 * XVineRouting.java
 * ---------------------------------------
 * (C) Copyright 2009-2011, by Benjamin Schiller (P2P, TU Darmstadt)
 * and Contributors 
 *
 * Original Author: stef;
 * Contributors:    -;
 *
 * Changes since 2011-05-17
 * ---------------------------------------
 *
 */
package gtna.routing.virtual;

import gtna.graph.Graph;
import gtna.graph.Node;
import gtna.id.BigIntegerIdentifier;
import gtna.id.Identifier;
import gtna.id.Partition;
import gtna.networks.p2p.chord.ChordIdentifier;
import gtna.routing.Route;
import gtna.routing.RoutingAlgorithm;
import gtna.transformation.virtualoverlay.Trails;
import gtna.util.parameter.IntParameter;
import gtna.util.parameter.Parameter;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

/**
 * @author stef
 *
 */
public class XVineRouting extends RoutingAlgorithm {
	private Vector<Vector<int[]>> trails; 
    private int ttl;

	public XVineRouting() {
		super("XVINE_ROUTING");
		this.ttl = Integer.MAX_VALUE;
	}

	public XVineRouting(int ttl) {
		super("XVINE_ROUTING", new Parameter[] { new IntParameter("TTL", ttl) });
		this.ttl = ttl;
	}

	@Override
	public Route routeToTarget(Graph graph, int start, Identifier target,
			Random rand) {
		return this.route(new ArrayList<Integer>(), start, target, rand,
				graph.getNodes());
	}

	private Route route(ArrayList<Integer> route, int current,
			Identifier target, Random rand, Node[] nodes) {
		route.add(current);
		if (this.isEndPoint(current, target)) {
			return new Route(route, true);
		}
		if (route.size() > this.ttl) {
			return new Route(route, false);
		}

		//look at overlay neighbors
		int closest = target.getClosestNode(nodes[current].getOutgoingEdges(),
				this.identifierSpace.getPartitions());
		if (!this.identifierSpace.getPartition(closest).contains(target)){
		Identifier closestID = this.identifierSpace.getPartition(closest).getRepresentativeIdentifier();
		//consider trails
		Vector<int[]> curTrails = this.trails.get(current);
		for (int i = 0; i < curTrails.size(); i++){
			if (closestID == null){
				Partition idEndpoint = this.identifierSpace.getPartition(curTrails.get(i)[0]);
				closestID = idEndpoint.getRepresentativeIdentifier();
				closest = curTrails.get(i)[1];
				if (idEndpoint.contains(target)){
					break;
				}
			}else {
			Partition idEndpoint = this.identifierSpace.getPartition(curTrails.get(i)[0]);
			if (idEndpoint.isCloser(target, closestID)){
				closestID = idEndpoint.getRepresentativeIdentifier();
				closest = curTrails.get(i)[1];
				if (idEndpoint.contains(target)){
					break;
				}
			}
			}
		}
		}
		//System.out.println("Closest " + ((ChordIdentifier)closestID).distance((BigIntegerIdentifier) target));
//		if (!target.isCloser(this.identifierSpace.getPartition(closest),
//				this.identifierSpace.getPartition(current))) {
//			return new Route(route, false);
//		}

		return this.route(route, closest, target, rand, nodes);
	}

	@Override
	public boolean applicable(Graph graph) {
		return graph.hasProperty("TRAILS_0");
	}
	
	@Override
	public void preprocess(Graph graph) {
		super.preprocess(graph);
		Trails gp = (Trails) graph.getProperty("TRAILS_0");
		trails = gp.getTrails();
	}

}
