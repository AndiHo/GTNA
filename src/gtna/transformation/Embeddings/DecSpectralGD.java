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
 * Spectral.java
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

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ParameterList;

import gtna.graph.Graph;
import gtna.graph.Node;
import gtna.id.euclidean.EuclideanIdentifier;
import gtna.id.euclidean.EuclideanIdentifierSpaceSimple;
import gtna.id.euclidean.EuclideanPartitionSimple;
import gtna.id.hyperbolic.Hyperbolic2DIdentifier;
import gtna.id.hyperbolic.Hyperbolic2DIdentifierSpaceSimple;
import gtna.id.hyperbolic.Hyperbolic2DPartitionSimple;
import gtna.id.md.MDIdentifier;
import gtna.id.md.MDIdentifierSpaceSimple;
import gtna.id.md.MDPartitionSimple;
import gtna.transformation.Transformation;
import gtna.util.parameter.IntParameter;
import gtna.util.parameter.Parameter;

/**
 * Embedding algorithm by Dell'Amico:
 * M. Dell'Amico: "Mapping Small Worlds". P2P, 2007.
 * Dell'Amico's algorithm is a distributed decentralized variant of Koren's spectral graph drawing algorithm, see also
 * O. Koren: "On Spectral Graph Drawing", COCOON, 2003. 
 * 
 * @author Andreas Höfer
 *
 */
public class DecSpectralGD extends Transformation {

	private int dims;
	private int rounds;
	
	/**
	 * @param dims: dimension of the id space the graph is embedded into
	 * @param rounds: nr of round used in the spectral embedding procedure
	 * 
	 */
	public DecSpectralGD(int dims, int rounds) {
		super("DEC_SPECTRAL_GD", new Parameter[] {new IntParameter("DIMS", dims), new IntParameter("ROUNDS", rounds) });
		this.dims = dims;
		this.rounds = rounds;
	}

	/* 
	 * 
	 */
	@Override
	public Graph transform(Graph g) {
		
		Node[] nodes = g.getNodes();
		// create an array of double arrays, each double array representing the coordinates of one node
		double[][] coordinates = new double[nodes.length][dims];
		Random rand = new Random();

		// D-Orthogonalization against the all-one vector (the first trivial Eigenvector solution)		
		// pairs of nodes are determined using random walks
		// each node is paired with another node for each dimension
		
		// store in this array which node is paired with which node for each dimension in the distributed D-orthogonalization
		boolean paired[][] = new boolean[nodes.length][dims];
		for (int n=0; n < nodes.length; n++)
			for (int d=0; d < dims; d++)
				paired[n][d] = false;
		
		for (int d=0; d<dims; d++){
			// the indices array contains a random permutation of the node indices 
			// this permutation defines the order of the start node of the random walks
			Integer[] indices = new Integer[nodes.length];
			for (int ind=0; ind < indices.length; ind++ ){
				indices[ind] = new Integer(ind);
			}
			Collections.shuffle(Arrays.asList(indices));
			
			// length of the random walk is O(log n) where n is the network size (fast mixing in social networks)
			int rw_len = (int) Math.ceil(Math.log(nodes.length));
			
			// for each node do a random walk to find an unpaired node for pairing
			// the random walk doesn't remember which nodes have already been visited before
			for (int n=0; n <nodes.length; n++){
				
				// use indices[n] as start node of the random walk				
				int curNodeIndex = indices[n];
				
				// skip nodes which are already paired
				if (paired[indices[n]][d] == true)
					continue;
				
				Node curNode = nodes[curNodeIndex]; 
				int steps = 0;
				int neighbors[];
				while (steps < rw_len){
					neighbors = curNode.getOutgoingEdges();
					curNodeIndex = neighbors[rand.nextInt(curNode.getOutDegree())];
					curNode = nodes[curNodeIndex];					
					steps++;
				}
				// the node we found is already paired or the random walk ended at the node it started from
				if (paired[curNodeIndex][d] == true || indices[n] == curNodeIndex){
					coordinates[indices[n]][d] = 0.0d;
				}
				else {
					// set the coordinates of the nodes with indices indices[n] and curNodeIndex
					paired[indices[n]][d] = true;
					paired[curNodeIndex][d] = true;
					double r = rand.nextDouble();
					coordinates[indices[n]][d] = r / nodes[indices[n]].getOutDegree();
					coordinates[curNodeIndex][d] = -1 * r / nodes[curNodeIndex].getOutDegree();							
				}
			}
		}
		
		// Multiplication
		// we compute in each round new coordinates, store them temporarily and then set coordinates at the end of the round 
		for (int r=0; r < rounds; r++){
			for (int d=0; d<dims; d++){
				double[] newCoordinates= new double[nodes.length];
				for (int n=0; n< nodes.length; n++){
					newCoordinates[n] = 0.5d * (coordinates[n][d] + sum(coordinates, nodes[n].getOutgoingEdges(), d)/nodes[n].getOutDegree());
				}
				for (int n=0; n< nodes.length; n++){
					coordinates[n][d] = newCoordinates[n];
				}
			}
		}
		
		// Normalization
		double[][] m =  new double[nodes.length][dims];
		for (int n=0; n < nodes.length; n++){
			for (int d=0; d < dims; d++)
				m[n][d] = coordinates[n][d] * coordinates[n][d];
		}
		for (int r=0; r < rounds; r++){
			for (int d=0; d < dims; d++){
				double[] new_m = new double[nodes.length];	
				for (int n=0; n < nodes.length; n++){
					new_m[n] = 0.5d * (m[n][d] + sum(m, nodes[n].getOutgoingEdges(), d)/nodes[n].getOutDegree());
				}
				for (int n=0; n< nodes.length; n++){
					m[n][d] = new_m[n];
				}		
			}
		}
		for (int n=0; n < nodes.length; n++){
			for (int d=0; d < dims; d++){
				coordinates[n][d] = coordinates[n][d] / Math.sqrt(m[n][d]);
			}
		}
				
		// create the id space, the partitions and assign each partition its identifier
		EuclideanIdentifierSpaceSimple idSpace = new EuclideanIdentifierSpaceSimple();
				
		EuclideanPartitionSimple[] partitions = new EuclideanPartitionSimple[g.getNodes().length];
		for (int i=0; i < nodes.length; i++){
			partitions[i] = new EuclideanPartitionSimple(new EuclideanIdentifier(coordinates[i]));
		}
		idSpace.setPartitions(partitions);
		
		g.addProperty(g.getNextKey("ID_SPACE"), idSpace);
		return g;
	}

	private double sum(double[][] coordinates, int[] neighboring, int currentDim){
		
		double sum = 0.0d;
		for (int neighbor:neighboring){
			sum += coordinates[neighbor][currentDim];
		}
		return sum;
	}
	
	/* 
	 * Dummy implementation
	 */
	@Override
	public boolean applicable(Graph g) {
		return true;
	}

}
