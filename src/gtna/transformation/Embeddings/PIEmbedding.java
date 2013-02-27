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
 * PIEmbedding.java
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
package gtna.transformation.Embeddings;

import gtna.graph.Graph;
import gtna.graph.Node;
import gtna.id.maxnorm.MaxNormSIdentiferSpaceSimple;
import gtna.id.maxnorm.MaxNormSIdentifier;
import gtna.id.maxnorm.MaxNormSPartitionSimple;
import gtna.transformation.Transformation;
import gtna.util.parameter.IntParameter;
import gtna.util.parameter.Parameter;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Andreas Höfer
 * 
 * Herzen et al.: "Scalable Routing Easy as PIE: a Practical Isometric Embedding Protocol", ICNP 2011.

 * Simplified version of the embedding algorithm, embeds only one spanning tree
 */
public class PIEmbedding extends Transformation {

	private boolean debug = true;
	private int root;
	/**
	 * @param root index of the root node
	 */
	public PIEmbedding(int root) {
		super("PIEMBEDDING", new Parameter[] {new IntParameter("ROOT", root) });
		this.root = root;
	}

	
	/* (non-Javadoc)
	 * @see gtna.transformation.Transformation#transform(gtna.graph.Graph)
	 */
	@Override
	public Graph transform(Graph g) {
		// create a spanning tree
		Node[] nodes = g.getNodes();
		
		// mark traversed nodes during the embedding
		boolean[] embedded = new boolean[nodes.length];
		
		// compute a new embedding 
		// array contains the coordinates of all nodes; is indexed with the node index from the graph 
		short[][] coordinates = new short[nodes.length][];  	
		
		// choose a node randomly as start node for the graph traversal
		// int rootIndex = 15148;
		// (int) Math.floor(nodes.length * Math.random());
		// int rootIndex = (int) Math.floor(nodes.length * Math.random());
		
		int rootIndex;
		if (root < 0 || root > nodes.length -1)
			// choose a node randomly as start node for the graph traversal
			rootIndex = (int) Math.floor(nodes.length * Math.random());
		else
			rootIndex = root;
		
		// root has coordinates 0
		coordinates[rootIndex] = new short[]{0};
		embedded[rootIndex] = true;
		
		Queue<Node> nodequeue = new LinkedList<Node>();
		nodequeue.add(nodes[rootIndex]);

		// while currentNode has some child node which has not been visited
		while(!nodequeue.isEmpty()){
			Node parentN = nodequeue.poll(); 
			short[] parentC = coordinates[parentN.getIndex()];
			
			int[] outgoing = parentN.getOutgoingEdges();
			// along the spanning tree assign the children indices
			
			// check which neighbours have already been embedded 
			int nr_of_children = 0;
			for (int n: outgoing){
				if (!embedded[n])
					nr_of_children++;
			}		
			// 4 children, indices 0 .. 3, ceil(log (4)) = 2, ceil(log(7)) = 3  
			int nr_of_additional_coordinates = (int) Math.ceil(Math.log(nr_of_children)/Math.log(2.0d)); // Integer.numberOfLeadingZeros(i)
			
			// index of the current child
			int child_index = 0;
			
			for (int n: outgoing){
				if (!embedded[n]){			
					// the new coordinates are based on the coords from the parent plus the additional coordinates
					coordinates[n] = new short[parentC.length + nr_of_additional_coordinates];
					System.arraycopy(parentC, 0, coordinates[n], 0, parentC.length);
					
					// increase the abs value of the coordinates passed down from the parent by 1
					for (int i=0; i < parentC.length; i++){
						if (coordinates[n][i] > 0)
							coordinates[n][i]++;
						else
							coordinates[n][i]--;
					}
					
					// compute binary representation of child index
					boolean[] binary_index = new boolean[nr_of_additional_coordinates];
					int temp = child_index;
					// the binary conversion computation is done from lowest digit to the highest digit
					for (int i=0; i < nr_of_additional_coordinates; i++){
						binary_index[nr_of_additional_coordinates - i -1] = (temp % 2 == 1) ? true : false;
						temp = temp / 2;
						if (temp == 0) break;
					}
					
					// depending on the binary index set the values of the new additional coordinates
					for (int i=0; i < nr_of_additional_coordinates; i++){
						if (!binary_index[i]) 
							coordinates[n][parentC.length +i] = -1;
						else 
							coordinates[n][parentC.length +i] = 1;
					}
					
					child_index++;
					
					embedded[n] = true;
					if (debug){
						System.out.print("node: " + n + " :");
						for (int i=0; i < coordinates[n].length; i++)
							System.out.print(" " + coordinates[n][i]);
						System.out.println();
					}
					nodequeue.add(nodes[n]);
				}					
			}
			// PIE coordinate representation of shorts
		}
		
		// create the id space, the partitions and assign each partition its identifier/coordinates
		MaxNormSIdentiferSpaceSimple idSpace = new MaxNormSIdentiferSpaceSimple();
		MaxNormSPartitionSimple[] partitions = new MaxNormSPartitionSimple[nodes.length];
		for (int n=0; n < nodes.length; n++){
			partitions[n] = new MaxNormSPartitionSimple(new MaxNormSIdentifier(coordinates[n]));
		}
		idSpace.setPartitions(partitions);

		g.addProperty(g.getNextKey("ID_SPACE"), idSpace);
		return g;
	}

	/* 
	 * Dummy method
	 */
	@Override
	public boolean applicable(Graph g) {
		return true;
	}

}
