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
 * VirtualOverlay.java
 * ---------------------------------------
 * (C) Copyright 2009-2011, by Benjamin Schiller (P2P, TU Darmstadt)
 * and Contributors 
 *
 * Original Author: andi;
 * Contributors:    -;
 *
 * Changes since 2011-05-17
 * ---------------------------------------
 *
 */
package gtna.transformation.virtualoverlay;

import gtna.graph.Graph;
import gtna.graph.Node;
import gtna.networks.p2p.chord.ChordIdentifier;
import gtna.networks.p2p.chord.ChordIdentifierSpace;
import gtna.networks.p2p.chord.ChordPartition;
import gtna.transformation.Transformation;
import gtna.transformation.id.RandomChordIDSpace;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

/**
 * @author andi
 *
 */
public class VirtualOverlay extends Transformation{

	private int bits = 160;
	
	/**
	 * @param key
	 * @param nodes
	 * @param parameters
	 * @param transformations
	 */
	public VirtualOverlay() {
		super("VIRTUALOVERLAY");
	}

	/* (non-Javadoc)
	 * @see gtna.networks.Network#generate()
	 */
	@Override
	public Graph transform(Graph graph) {
		Node[] nodes = graph.getNodes();
		//generate graph and ID space, trail object
		RandomChordIDSpace ids = new RandomChordIDSpace(bits, false);
		graph = ids.transform(graph);
		Vector<Vector<int[]>> trails = new Vector<Vector<int[]>>(nodes.length);
		for (int i = 0; i < nodes.length; i++){
			trails.add(new Vector<int[]>());
		}
		
		ChordIdentifierSpace idSpace = (ChordIdentifierSpace) graph
				.getProperty("ID_SPACE_0");
		ChordPartition[] partitions = (ChordPartition[]) idSpace
				.getPartitions();

		for (Node node:nodes){
			ChordPartition p = partitions[node.getIndex()];
			BigInteger id = ((ChordIdentifier) p.getRepresentativeIdentifier()).getPosition();
			// BigInteger predID = p.getPred().getId();
			
			BigInteger add = BigInteger.ONE;
			int[] fingerIndex = new int[this.bits];
			BigInteger[] fingerID = new BigInteger[this.bits];
			for (int i = 0; i < this.bits; i++) {
				fingerID[i] = id.add(add).mod(idSpace.getModulus());
				fingerIndex[i] = this.find(partitions, new ChordIdentifier(
						fingerID[i], this.bits), node.getIndex());
				add = add.shiftLeft(1);
			}
			int[] pred = new int[nodes.length];
			for (int i=0; i < nodes.length ; i++){
				pred[i] = -2;
			}
			pred[node.getIndex()] = -1;
			Queue<Integer> nodeQueue = new LinkedList<Integer>();
			nodeQueue.add(node.getIndex());
			while (!nodeQueue.isEmpty()){
				// count++;
				int s = nodeQueue.poll();
				int[] out = nodes[s].getOutgoingEdges();
				for (int i=0; i<out.length; i++){
					if (pred[out[i]] == -2){
						pred[out[i]] = s;
						nodeQueue.add(out[i]);
					}
				}
			}
			// System.out.println("count=" + count);
			int old = -1;
			for (int i=0; i < fingerIndex.length ; i++){
				if (fingerIndex[i] == old) 
					continue;
				else 
					old = fingerIndex[i];
			        int cur = old;
			        
                while (pred[cur] != -1){
                	int[] trail = {node.getIndex(),pred[cur]};
                	if (!trailExists(trails.get(cur), trail))
                		trails.get(cur).add(trail);
                	int[] backTrail = {old,cur};
                	if (!trailExists(trails.get(pred[cur]), backTrail))
                		trails.get(pred[cur]).add(backTrail);
                	cur = pred[cur];
       
                }
			}
		}
	    // save in GraphProperty Trails
		Trails trailsObj = new Trails(trails);
		graph.addProperty(graph.getNextKey("TRAILS"), trailsObj);
		return graph;
	}
	
	private boolean trailExists(Vector<int[]> trails, int[] trail){
		for (int i=0; i < trails.size(); i++){
			if (trails.get(i)[0] == trail[0] && trails.get(i)[1] == trail[1])
				return true;
		}
		return false;
	}
	
	private int find(ChordPartition[] partitions, ChordIdentifier id, int start) {
		int index = start;
		while (!partitions[index].contains(id)) {
			index = (index + 1) % partitions.length;
		}
		return index;
	}

	/* (non-Javadoc)
	 * @see gtna.transformation.Transformation#applicable(gtna.graph.Graph)
	 */
	@Override
	public boolean applicable(Graph g) {
		return true;
	}

}
