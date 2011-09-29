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
 * FruchtermanReingold.java
 * ---------------------------------------
 * (C) Copyright 2009-2011, by Benjamin Schiller (P2P, TU Darmstadt)
 * and Contributors 
 *
 * Original Author: Nico;
 * Contributors:    -;
 *
 * Changes since 2011-05-17
 * ---------------------------------------
 *
 */
package gtna.transformation.gd;

import java.util.ArrayList;
import java.util.Random;

import gtna.graph.Graph;
import gtna.graph.GraphProperty;
import gtna.graph.Node;
import gtna.id.md.MDIdentifier;
import gtna.id.md.MDIdentifierSpaceSimple;
import gtna.id.md.MDPartitionSimple;
import gtna.plot.Gephi;
import gtna.util.Config;
import gtna.util.MDVector;

/**
 * @author Nico
 *
 */
public class FruchtermanReingold extends AbstractGD {
		/*
		 * How many iterations should the algorithm run?
		 */
	private int iterations;
	
		/*
		 * Maximal area for the drawing, as defined per the moduli
		 */
	private double area;
	
		/*
		 * Optimal distance, named k by FR
		 */
	private double k;
	
		/*
		 * Global cooling factor
		 */
	private double t;
	
		/*
		 * Displacement array which will hold the displacement
		 * throughout one single iteration
		 */
	private MDVector[] disp;
	
		/*
		 * Handled edges - save this as GTNA might deliver corrupt
		 * values (in an undirected graph, all edges are returned
		 * when asking for incoming edges - so they are handled two
		 * times each!)
		 */
	private ArrayList<String> handledEdges;

	private Gephi gephi;
	
	public FruchtermanReingold() {
		this("GDA_FRUCHTERMAN_REINGOLD", new String[]{}, new String[]{});
	}
	
	public FruchtermanReingold(String key, String[] configKeys,
			String[] configValues) {
		super(key, configKeys, configValues);
		this.iterations = Config.getInt("GDA_FRUCHTERMAN_REINGOLD_ITERATIONS");
	}
		
	/**
	 * @param plotter
	 */
	public FruchtermanReingold(Gephi plotter) {
		this("GDA_FRUCHTERMAN_REINGOLD", new String[]{}, new String[]{});
		this.gephi = plotter;
	}

	@Override
	public boolean applicable(Graph g) {
		Random rand = new Random();
		this.idSpace = null;
		for (GraphProperty p : g.getProperties("ID_SPACE")) {
			if (p instanceof MDIdentifierSpaceSimple) {
				MDIdentifier id = (MDIdentifier) ((MDIdentifierSpaceSimple) p)
						.randomID(rand);
				if (!(id instanceof MDIdentifier)) {
					return false;
				}
				// as we are going through: remember some values we can use!
				if ( this.idSpace != null ) {
					System.err.println("Cannot apply FruchtermanReingold if multiple IDSpaces are set");
					return false;
				}
				this.idSpace = (MDIdentifierSpaceSimple) p;
				this.partitions = (MDPartitionSimple[]) this.idSpace.getPartitions();
				double[] moduli = this.idSpace.getModuli();
				this.area = 1;
				for ( double singleModulus: moduli ) this.area = this.area * singleModulus;
				k = Math.sqrt( this.area / this.partitions.length );
				System.out.println("Best distance: " + k);
				this.disp = new MDVector[this.partitions.length];
				
				double maxModulus = 0;
				for ( int i = 0; i < moduli.length; i++ ) {
					maxModulus = Math.max(maxModulus, moduli[i]);
				}
				this.t = maxModulus;
			} else {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public Graph transform(Graph g) {
		if ( !this.applicable(g) ) {
			System.err.println("Cannot apply FR");
			return g;
		}
		
		extractNodePositions(g);
		for ( int i = 0; i < this.iterations; i++ ) {
			System.out.println("\n\n   >>> in iteration " + i + " <<<");
			this.doIteration ( g );
			if ( gephi != null && i % 10 == 0 ) {
				gephi.Plot ( g, "test" + i + ".svg" );
			}
		}
		return g;
	}

	private void doIteration(Graph g) {
		MDVector delta, currDisp;
		String identifier;
		
		this.handledEdges = new ArrayList<String>();		
				
			// First step: repulsive forces
		for ( Node v: g.getNodes() ) {
				// Reset displacement
			this.disp[v.getIndex()] = new MDVector(idSpace.getDimensions(), 0d);
			
				// Calculate repulsive forces to *all* other nodes
			for ( Node u: g.getNodes() ) {
				if ( u.getIndex() == v.getIndex() ) continue;
				delta = new MDVector( nodePositions[v.getIndex()].getDimension(), nodePositions[v.getIndex()].getCoordinates() );
				delta.subtract ( nodePositions[u.getIndex()] );
				currDisp = new MDVector(delta.getDimension(), delta.getCoordinates());
				double currDispNorm = currDisp.getNorm(); 
				if ( Double.isNaN(currDispNorm) ) throw new RuntimeException("You broke it");
				currDisp.divideBy(currDispNorm);
				currDisp.multiplyWith(fr ( currDispNorm ) );
				this.disp[v.getIndex()].add(currDisp);
			}
		}

			// Second step: attractive forces
		for ( Node v: g.getNodes() ) {
			for ( int uIndex: v.getOutgoingEdges() ) {
				identifier = Math.min ( v.getIndex(), uIndex) + "-" + Math.max ( v.getIndex(), uIndex);
				if ( handledEdges.contains(identifier)) {
						// Do not handle edges twice
					continue;
				}
				
				delta = new MDVector( nodePositions[v.getIndex()].getDimension(), nodePositions[v.getIndex()].getCoordinates() );
				delta.subtract ( nodePositions[uIndex] );
				currDisp = new MDVector(delta.getDimension(), delta.getCoordinates());
				double currDispNorm = currDisp.getNorm(); 
				if ( Double.isNaN(currDispNorm) ) throw new RuntimeException("You broke it");
				currDisp.divideBy(currDispNorm);
				currDisp.multiplyWith(fa ( currDispNorm ) );

				this.disp[v.getIndex()].subtract(currDisp);
				this.disp[uIndex].add(currDisp);
				
				handledEdges.add(identifier);
			}
		}
		
			// Last but not least: assign new coordinates
		for ( Node v: g.getNodes() ) {
			currDisp = new MDVector(disp[v.getIndex()].getDimension(), disp[v.getIndex()].getCoordinates());
			double currDispNorm = currDisp.getNorm(); 
			currDisp.divideBy(currDispNorm);
			currDisp.multiplyWith( Math.min(currDispNorm, t) );
					
			System.out.println("Move " + nodePositions[v.getIndex()] + " by " + currDisp + " (calculated disp: " + disp[v.getIndex()] + ", t: " + t + ")" );
			System.out.println("Old pos: " + nodePositions[v.getIndex()] );

			nodePositions[v.getIndex()].add(currDisp);
			nodePositions[v.getIndex()] = setNormalized ( nodePositions[v.getIndex()] );
			
			System.out.println("New pos: " + nodePositions[v.getIndex()] + " for " + v.getIndex());
		}
		
		t = cool ( t );		
	}
	
	private MDVector setNormalized ( MDVector v ) {
		for ( int i = 0; i < v.getDimension(); i++ ) {
			double coordinate = Math.min(idSpace.getModulus(i), Math.max(-idSpace.getModulus(i), v.getCoordinate(i)));
			v.setCoordinate(i, coordinate);
		}
		return v;
	}
		
	private double fr ( Double x ) {
		return ( ( k * k ) / x );
	}
	
	private double fa ( Double x ) {
		return ( ( x * x ) / k );
	}
	
	private double cool ( Double t ) {
		return 0.95 * t;
	}

}
