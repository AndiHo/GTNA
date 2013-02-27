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
 * KBEmbedding.java
 * ---------------------------------------
 * (C) Copyright 2009-2011, by Benjamin Schiller (P2P, TU Darmstadt)
 * and Contributors 
 *
 * Original Author: Andreas Höfer;
 * Contributors:    -;
 *
 * Changes since 2012-04-17
 * ---------------------------------------
 *
 */
package gtna.transformation.Embeddings;

import java.util.LinkedList;
import java.util.Queue;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;

import gtna.graph.Graph;
import gtna.graph.Node;
import gtna.id.hyperbolic.Hyperbolic2DIdentifier;
import gtna.id.hyperbolic.Hyperbolic2DIdentifierSpaceSimple;
import gtna.id.hyperbolic.Hyperbolic2DPartitionSimple;
import gtna.transformation.Transformation;
import gtna.util.Config;
import gtna.util.parameter.IntParameter;
import gtna.util.parameter.Parameter;

/**
 * @author Andreas Höfer
 *
 */
public class KBEmbedding extends Transformation{

	boolean debug = true;
	private int precision = Integer.parseInt(Config.get("KB_EMBEDDING_PRECISION"));
	private int root;
	
	public KBEmbedding(int root) {
		super("KB_EMBEDDING", new Parameter[] {new IntParameter("ROOT", root) });
		this.root = root;
	}

	/**
	 * Implements the Greedy Embedding into the hyperbolic plane (poincare model) by Robert Kleinberg
	 * Robert Kleinberg: "Geographic Routing Using Hyperbolic Space", INFOCOM 2007.
	 * @see gtna.transformation.Transformation#transform(gtna.graph.Graph)
	 */
	@Override
	public Graph transform(Graph g) {
		
		Node[] nodes = g.getNodes();
		// System.out.println("Nodes: " + g.getNodes().length);
		// System.out.println("Edges: " + g.computeNumberOfEdges());
		
		// array contains the coordinates of all nodes; is indexed with the node index from the graph 
		KBNodeCoords[] coordinates = new KBNodeCoords[nodes.length];
				
		// mark traversed nodes during the embedding
		boolean[] embedded = new boolean[nodes.length];
		
		// counter for the embedded nodes
		int embeddedNodes = 0;
			
		// compute a new embedding 
		int rootIndex;
		if (root < 0 || root > nodes.length -1)
			// choose a node randomly as start node for the graph traversal
			rootIndex = (int) Math.floor(nodes.length * Math.random());
		else
			rootIndex = root;
		
		// compute max degree of the spanning tree
		int degree = maxDegreeSpanningTree(g, rootIndex);
		System.out.println("maxDegree= " + degree);
		
		Apcomplex[] a = {Apfloat.ONE.negate(), Apfloat.ZERO, Apfloat.ZERO, Apfloat.ONE};
		Apcomplex[] sigma = computeSigma(degree);
		Apcomplex[] rho = computeRho(degree);
		Apcomplex[] b = multiply(sigma, multiply(rho, invert(sigma)));
		
		for (int i=0; i< 4; i++){
			System.out.println("sigma[" + i + "]=" + sigma[i]);
			System.out.println("rho[" + i + "]=" + rho[i]);
			System.out.println("a[" + i + "]=" + a[i]);
			System.out.println("b[" + i + "]=" + b[i]);
		}
		// u = sigma(0)
		Apcomplex u = map(Apcomplex.ZERO, sigma);
		Apcomplex sigma_one = map(Apcomplex.ONE, sigma);
		
		System.out.println("sigma(1): ("+ sigma_one.real() +"," + sigma_one.imag() + ")");
		
		// 
		// test start
		// 
		final Apfloat one = Apfloat.ONE;
		final Apfloat two =  one.add(one);
		final Apfloat minusTwo = two.negate();
		Apfloat angle = minusTwo.multiply(ApfloatMath.pi(precision).divide(new Apfloat(degree, precision)));
		// r_2 = cos(angle) + i sin(angle) 
		Apcomplex r_2 = new Apcomplex(ApfloatMath.cos(angle), ApfloatMath.sin(angle));
		Apfloat halfangle = angle.divide(two);
		Apcomplex m = new Apcomplex(ApfloatMath.cos(halfangle), ApfloatMath.sin(halfangle));
		System.out.println("r_2: " + r_2);
		System.out.println("m: " + m);
		Apcomplex sigma_r_2 = map(r_2, sigma);
		Apcomplex sigma_m = map(m, sigma);
		// sigma(r_2) should equal -1
		System.out.println("sigma(r_2): ("+ sigma_r_2.real() +"," + sigma_r_2.imag() + ")");
		// sigma(m) should equal -i
		System.out.println("sigma(m): ("+ sigma_m.real() +"," + sigma_m.imag() + ")");
		// b(u) should equal u
		Apcomplex b_u = map(u,b);
		System.out.println("b(u): ("+ b_u.real() +"," + b_u.imag() + ")");
		
		//
		// test end
		//
		
		// v = -u
		Apcomplex v = u.negate();
		
		Apcomplex inv_b_v = map(v,invert(b));
		System.out.println("invers(b(v)): ("+ inv_b_v.real() +"," + inv_b_v.imag() + ")");
		
		
		System.out.println("\nPrecision:" + precision);
		System.out.println("Index of root node: " + rootIndex);
		System.out.println("Coordinates of root node: (" + u.real() + "," + u.imag() + ")");
		System.out.println("Maximum degree of the spanning tree: " + degree);
		
		// root has u as coordinates and a as transform mu
		KBNodeCoords rootCoords = new KBNodeCoords(rootIndex, u);
		rootCoords.mu = a;
		rootCoords.treelevel = 0;
		
		coordinates[rootIndex] = rootCoords;
		embedded[rootCoords.index] = true;
		embeddedNodes++;
		
		Queue<Node> nodequeue = new LinkedList<Node>();
		nodequeue.add(nodes[rootIndex]);

		// while currentNode has some child node which has not been visited
		while(!nodequeue.isEmpty()){
			Node parentN = nodequeue.poll();
			KBNodeCoords parentC = coordinates[parentN.getIndex()]; 
			int[] outgoing = parentN.getOutgoingEdges();
			
			// child(i).mu = b^i * a * parent.mu, i=1, ..., degree(parent)-1 
			Apcomplex[] mu = multiply(a, parentC.mu);
			
			// int outdegree = 0;
			int i = 1;
			for (int n: outgoing){
				
				if (!embedded[n]){
					KBNodeCoords nC = new KBNodeCoords(n);
					mu = multiply(b, mu);
					nC.mu = mu;
					nC.pos = map(v, invert(mu));

					// set the tree level
					nC.treelevel = parentC.treelevel + 1;
					
					coordinates[n] = nC;
					embedded[n] = true;
					embeddedNodes++;
						
					nodequeue.add(nodes[n]);
					if (debug && n == 26234){
						System.out.println("parent node: " + parentN.getIndex());
						System.out.println("child index: " + i);
						System.out.println("embed node: " + n + "; coordinates:(" + nC.pos.real()+"," + nC.pos.imag() + ")");
						System.out.println("mu(parentC.pos) should equal u:" + map(parentC.pos, mu));
						System.out.println("Nr of embedded Nodes: " + embeddedNodes);
					}
					i++;
				}
				// if (outdegree >= degree) break;
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

	/* (non-Javadoc)
	 * @see gtna.transformation.Transformation#applicable(gtna.graph.Graph)
	 */
	@Override
	public boolean applicable(Graph g) {
		// always return true
		return true;
	}

	
	/*
	 * Helper function which multiplies two complex 2*2 matrices
	 * the matrix is given as an one-dimensional array 
	 */
	private Apcomplex[] multiply(Apcomplex[] m1, Apcomplex[] m2){
		Apcomplex[] result = new Apcomplex[4];
		
		// row1: m[0] m[1] 
		// row2: m[2] m[3]
		
		// row1 * column1
		result[0] = m1[0].multiply(m2[0]).add(m1[1].multiply(m2[2]));
		// row1 * column2
		result[1] = m1[0].multiply(m2[1]).add(m1[1].multiply(m2[3]));
		// row2 * column1
		result[2] = m1[2].multiply(m2[0]).add(m1[3].multiply(m2[2]));
		// row2 * column2
		result[3] = m1[2].multiply(m2[1]).add(m1[3].multiply(m2[3]));
		
		return result;
	}

	/*
	 * Helper function which inverts the given complex 2*2 matrix
	 * the matrix is given as an one-dimensional array
	 * the inverse is computed as inv = 1/det *[m[3] -m[1] -m[2] m[0]]
	 */
	private Apcomplex[] invert(Apcomplex[] m)
	{
		Apcomplex[] result = new Apcomplex[4];
		Apcomplex det = m[0].multiply(m[3]).subtract(m[1].multiply(m[2]));
		result[0] = m[3].divide(det);
		result[1] = m[1].negate().divide(det);
		result[2] = m[2].negate().divide(det);
		result[3] = m[0].divide(det);
		return result;
	}
	
	private Apcomplex[] computeSigma(int deg){
		Apcomplex[] result = new Apcomplex[4];
		
		// choose the side with the vertices r_1 = 1 and r_2 = e^{-2i\pi/deg} as side of the ideal polygon  
		// (r_2 is the neighbor-vertex of r_1 in the polygon in clockwise order)  
		// the midpoint m between r_1 and r_2 is m = e^{-i\pi/deg}
		
		// angle = -2 pi / deg
		final Apfloat one = Apfloat.ONE;
		final Apcomplex i = Apcomplex.I;
		final Apfloat two =  one.add(one);
		final Apfloat minusTwo = two.negate();
		Apfloat angle = minusTwo.multiply(ApfloatMath.pi(precision).divide(new Apfloat(deg, precision)));
		// r_2 = cos(angle) + i sin(angle) 
		Apcomplex r_2 = new Apcomplex(ApfloatMath.cos(angle), ApfloatMath.sin(angle));
		Apfloat halfangle = angle.divide(two);
		Apcomplex m = new Apcomplex(ApfloatMath.cos(halfangle), ApfloatMath.sin(halfangle));
		
		System.out.println("r_2: " + r_2);
		System.out.println("m: " + m);
		
		// Caution: a,b,c,d name here the entries of the 2*2 matrix which represents the hyperbolic isometry sigma
		// We set d=1 and compute a,b,c from the conditions sigma(r_1) = 1, sigma(r_2) = -1 and sigma(m) = -i

		// solve the equations a +b -c = 1, r_2 * a +b + r_2 *c = -1, m*a +b + i*m*c = -i for the variables a,b,c using
		// the CAS Axiom
		
		// solve([a + b -c = 1, r_2 * a +b + r_2 *c = -1, m*a +b + %i*m*c = -%i],[a, b, c])
		
		//	    	- %i r2 + (- 1 + %i)m + 1     (m - 1 + %i)r2 - %i m
		//	     [a= -------------------------, b= ---------------------,
		//	           (m - 1 - %i)r2 + %i m       (m - 1 - %i)r2 + %i m                                                                                                                                               
		//	         %i r2 + (- 1 - %i)m + 1                                                                                                                                                                           
		//	      c= -----------------------]                                                                                                                                                                          
		//	          (m - 1 - %i)r2 + %i m             
		//		
		
		Apcomplex denominator = i.negate().multiply(r_2).add(one.negate().add(i).multiply(m)).add(one); 
		Apcomplex nominator = m.subtract(one).subtract(i).multiply(r_2).add(i.multiply(m));
		Apcomplex a = denominator.divide(nominator);
		
		denominator = m.subtract(one).add(i).multiply(r_2).subtract(i.multiply(m)); 
		nominator = m.subtract(one).subtract(i).multiply(r_2).add(i.multiply(m));
		Apcomplex b = denominator.divide(nominator);
		
		denominator = i.multiply(r_2).add(one.negate().subtract(i).multiply(m)).add(one);
		nominator = m.subtract(one).subtract(i).multiply(r_2).add(i.multiply(m));
		Apcomplex c = denominator.divide(nominator);
		
		//        (1 - 2%i)r2 - 2m + 1             
		//  [a= ---------------------------------, 
		//      ((1 + %i)m - 2%i)r2 + (- 1 + %i)m     

//		Apcomplex denominator = one.subtract(two.multiply(i)).multiply(r_2).subtract(two.multiply(m)).add(one);
//		Apcomplex nominator = one.add(i).multiply(m).subtract(two.multiply(i)).multiply(r_2).add(one.negate().add(i).multiply(m));
//		Apcomplex a = denominator.divide(nominator);
//		
//		// 		(m + 2%i)r2 - %i m
		//  b= ---------------------
		// 		(m - 1 - %i)r2 + %i m
//		
//		denominator = m.add(two.multiply(i)).multiply(r_2).subtract(i.multiply(m));
//		nominator = m.subtract(one).subtract(i).multiply(r_2).add(i.multiply(m));
//		Apcomplex b = denominator.divide(nominator);
		
		//          (- 1 + 2%i)r2 - 2%i m + 1
		//   c= ---------------------------------]
		//      ((1 + %i)m - 2%i)r2 + (- 1 + %i)m

//		denominator = one.negate().add(two.multiply(i)).multiply(r_2).subtract(two.multiply(i.multiply(m))).add(one);
//		nominator = one.add(i).multiply(m).subtract(two.multiply(i)).multiply(r_2).add(one.negate().add(i).multiply(m));
//		Apcomplex c = denominator.divide(nominator);
//		
		result[0] = a;
		result[1] = b;
		result[2] = c;
		// d = 1
		result[3] = Apcomplex.ONE;
		return result;
	}
	
	private Apcomplex[] computeRho(int deg){
		Apcomplex[] result = new Apcomplex[4];
		// rho(z) = z*e^(2 \pi i / deg)
		final Apcomplex one = Apfloat.ONE;
		final Apcomplex two =  one.add(one);
		result[0] = ApcomplexMath.exp(two.multiply(Apcomplex.I).multiply(ApfloatMath.pi(precision)).divide(new Apfloat(deg, precision)));
		result[1] = Apcomplex.ZERO;
		result[2] = Apcomplex.ZERO;
		result[3] = one;
		return result;
	}
	
	/*
	 * Apply the hyperbolic isometry given by the supplied matrix m to the point p 
	 */
	private Apcomplex map(Apcomplex p, Apcomplex[] m){
		return m[0].multiply(p).add(m[1]).divide(m[2].multiply(p).add(m[3]));
	}

	
	/*
	 * Compute the maximum degree of the tree traversal in the transform method
	 * When the same rootIndex is used, both methods traverse the graph in lockstep
	 * this additional traversal is necessary as we need to know the max degree before the
	 * traversal for the Kleinberg embedding is done
	 */
	private int maxDegreeSpanningTree(Graph g, int rootIndex){

		Node[] nodes = g.getNodes();
		
		// mark traversed nodes during the computation of the max degree		
		boolean[] visited = new boolean[nodes.length];
				
		Queue<Node> nodequeue = new LinkedList<Node>();		
		nodequeue.add(nodes[rootIndex]);
		visited[rootIndex] = true;
		// at the beginning set maxDegree to the degree of the root node 
		int maxDegree = nodes[rootIndex].getOutDegree();
		// while currentNode has some child node which has not been visited
		while(!nodequeue.isEmpty()){
			Node parent = nodequeue.poll(); 
			int[] outgoing = parent.getOutgoingEdges();
			// initialize the spanning tree degree with 1 as each node is connected with its parent node // the degree in the full graph
			int degree = 1; 
			for (int n: outgoing){
				if (!visited[n]){
					degree++;
					nodequeue.add(nodes[n]);
					visited[n] = true;
				} //else {
//					// the node is already visited the corresponding edge is not part of spanning tree and
//					// does not count towards the node degree in the spanning tree
//					degree--;
//				}
			}
			if (degree > maxDegree){
				System.out.println("index: " + parent.getIndex());
				System.out.println("degree in the graph: " + parent.getOutDegree());
				System.out.println("degree in spanning tree: " + degree);
				maxDegree = degree;
			}
		}
		return maxDegree;
	}
}
