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
 * CCEmbedding.java
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
package gtna.transformation.hyperbolicEmbeddings;

import gtna.graph.Graph;
import gtna.graph.Node;
import gtna.id.hyperbolic.Hyperbolic2DIdentifier;
import gtna.id.hyperbolic.Hyperbolic2DIdentifierSpaceSimple;
import gtna.id.hyperbolic.Hyperbolic2DPartitionSimple;
import gtna.transformation.Transformation;
import gtna.util.Config;

import java.util.LinkedList;
import java.util.Queue;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;

/**
 * @author andi
 * Hyperbolic Embedding algorithm by Cvetkovski and Crovella, INFOCOM 2009
 */
public class CCEmbedding extends Transformation{

	private int precision = Integer.parseInt(Config.get("CC_EMBEDDING_PRECISION"));
	
	public CCEmbedding(){	
		super("CC_EMBEDDING");
	}

	/* (non-Javadoc)
	 * @see gtna.transformation.Transformation#transform(gtna.graph.Graph)
	 */
	@Override
	public Graph transform(Graph g) {
	
		Node[] nodes = g.getNodes();
		// System.out.println("Nodes: " + g.getNodes().length);
		// System.out.println("Edges: " + g.computeNumberOfEdges());
		
		// array contains the coordinates of all nodes; is indexed with the node index from the graph 
		// Apcomplex[] coordinates = new Apcomplex[nodes.length];
		CCNodeCoords[] coordinates = new CCNodeCoords[nodes.length];
		
		// mark traversed nodes
		boolean[] embedded = new boolean[nodes.length];
		
		// counter for the embedded nodes
		int embeddedNodes = 0;
			
		// compute a new embedding 
		// choose a node randomly as start node for the graph traversal
		// int rootIndex = 15148;
		// (int) Math.floor(nodes.length * Math.random());
		int rootIndex = (int) Math.floor(nodes.length * Math.random());
				
		System.out.println("\nPrecision:" + precision);
		System.out.println("Index of root node: " + rootIndex);

		CCNodeCoords rootCoords = new CCNodeCoords(rootIndex);
		
		// initialise coordinates of rootNode
		// choose a point in the triangle OAB, p. 1650, figure 2 in the paper by Cvetkovski & Crovella
		// x = 1/2 \sqrt{1.5 - \sqrt{2}} y = \sqrt{1.5 - \sqrt{2}} 

		Apfloat two = Apfloat.ONE.add(Apfloat.ONE).precision(precision);

		Apfloat halve = Apfloat.ONE.divide(two);
		Apfloat threeHalves = Apfloat.ONE.add(halve);
		Apfloat sqrtOfTwo = ApfloatMath.sqrt(two);

		Apfloat rootRealPart = halve.negate().multiply(ApfloatMath.sqrt(threeHalves.subtract(sqrtOfTwo))); 
		Apfloat rootImagPart = Apfloat.ONE.negate().multiply(ApfloatMath.sqrt(threeHalves.subtract(sqrtOfTwo)));

		rootCoords.pos = new Apcomplex(rootRealPart, rootImagPart);

		// rootNode.id.pos = new Apcomplex(new Apfloat(-0.5 * Math.sqrt(1.5 - Math.sqrt(2)), precision), new Apfloat(-1 * Math.sqrt(1.5 - Math.sqrt(2)), precision));
		// root alpha = \pi
		rootCoords.alpha = ApfloatMath.pi(precision);
		// root beta = 2\pi 
		rootCoords.beta = rootCoords.alpha.add(rootCoords.alpha);

		rootCoords.treelevel = 0;
		
		// coordinates[rootIndex] = rootCoords.pos;
		coordinates[rootIndex] = rootCoords;
		embedded[rootCoords.index] = true;
		embeddedNodes++;
		
		// deprecated comment; use two queues which move in lockstep, one for the nodes and one for the coordinates
		Queue<Node> nodequeue = new LinkedList<Node>();
		// Queue<CCNodeCoords> coordqueue = new LinkedList<CCNodeCoords>();
		nodequeue.add(nodes[rootIndex]);

		// while currentNode has some child node which has not been visited
		while(!nodequeue.isEmpty()){
			Node parentN = nodequeue.poll();
			CCNodeCoords parentC = coordinates[parentN.getIndex()]; 
			// CCNodeCoords parentC =  coordqueue.poll();
			int[] outgoing = parentN.getOutgoingEdges();
			for (int n: outgoing){
				if (!embedded[n]){
					CCNodeCoords nC = new CCNodeCoords(n) ;
					nC.alpha = parentC.alpha; 
					nC.beta = parentC.alpha.add(parentC.beta).divide(two);
					// h.beta = parent.alpha.add(parent.beta).divide(new Apfloat(2, precision));
					// update alpha at the parent
					parentC.alpha = nC.beta;

					// compute n's hyperbolic coordinates 
					// a_n = e^{i \alpha}, b_n = e^{i \beta}
					Apcomplex a = ApcomplexMath.exp(Apcomplex.I.multiply(nC.alpha));
					Apcomplex b = ApcomplexMath.exp(Apcomplex.I.multiply(nC.beta));

					// m = (a_n + b_n)/2
					Apcomplex m = a.add(b).divide(two);

					// c = 1/m*
					Apcomplex c = Apcomplex.ONE.divide(m.conj());

					// R^2 = 1 / |m|² -1
					Apcomplex rsquared = Apcomplex.ONE.divide(ApcomplexMath.abs(m).multiply(ApcomplexMath.abs(m))).subtract(Apcomplex.ONE);
					// if (m.abs()*m.abs() >= 1)
					// System.out.format("%.48f, %.48f %n", h.alpha, h.beta);
					// System.out.println(h.alpha + "," + h.beta);

					// C(n) = R²/(C(p_n)* - c*) + c
					nC.pos = rsquared.divide(parentC.pos.conj().subtract(c.conj())).add(c);				

					//update alpha at n 
					nC.alpha = nC.alpha.add(nC.beta).divide(two);

					// if (nC.pos.isNaN())
					// 	System.out.println("embed node: " + n + "; parent coordinates:(" + parentC.pos.getReal()+"," + parentC.pos.getImaginary() + ")");

					// set the tree level
					nC.treelevel = parentC.treelevel + 1;

					// coordinates[n] = nC.pos;
					coordinates[n] = nC;
					embedded[n] = true;
					embeddedNodes++;
					
					nodequeue.add(nodes[n]);
					// coordqueue.add(nC);
					
					System.out.println("parent node:" + parentN.getIndex());
					System.out.println("embed node: " + n + "; coordinates:(" + nC.pos.real()+"," + nC.pos.imag() + ")");
					System.out.println("Nr of embedded Nodes: " + embeddedNodes);
				}
			}

		}

		System.out.println("Nr of embedded Nodes: " + embeddedNodes);
		
		// create the idspace, the partitions and assign each partition its identifier/coordinates
		Hyperbolic2DIdentifierSpaceSimple idSpace = new Hyperbolic2DIdentifierSpaceSimple();
		Hyperbolic2DPartitionSimple[] partitions = new Hyperbolic2DPartitionSimple[nodes.length];
		for (int i=0; i < nodes.length; i++){
			partitions[i] = new Hyperbolic2DPartitionSimple(new Hyperbolic2DIdentifier(coordinates[i].pos));
		}
		idSpace.setPartitions(partitions);

		g.addProperty(g.getNextKey("ID_SPACE"), idSpace);
		return g;
	}

	/* 
	 * @see gtna.transformation.Transformation#applicable(gtna.graph.Graph)
	 */
	@Override
	public boolean applicable(Graph g) {
		// always return true
		return true;
	}

}
