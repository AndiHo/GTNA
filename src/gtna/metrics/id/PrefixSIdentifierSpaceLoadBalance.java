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
 * PrefixSIdentifierSpaceLoadBalance.java
 * ---------------------------------------
 * (C) Copyright 2009-2011, by Benjamin Schiller (P2P, TU Darmstadt)
 * and Contributors 
 *
 * Original Author: Andreas Höfer;
 * Contributors:    -;
 *
 * Changes since 2011-05-17
 * ---------------------------------------
 *
 */
package gtna.metrics.id;

import gtna.data.Single;
import gtna.graph.Graph;
import gtna.graph.Node;
import gtna.graph.spanningTree.SpanningTree;
import gtna.id.prefix.PrefixSIdentiferSpaceSimple;
import gtna.id.prefix.PrefixSIdentifier;
import gtna.id.prefix.PrefixSPartitionSimple;
import gtna.io.DataWriter;
import gtna.metrics.Metric;
import gtna.networks.Network;
import gtna.util.Statistics;
import gtna.util.parameter.IntParameter;
import gtna.util.parameter.Parameter;

import java.util.Arrays;
import java.util.HashMap;

/**
 * @author Andreas Höfer
 *
 */
public class PrefixSIdentifierSpaceLoadBalance extends Metric {

	private double[][] addressSliceDistribution;
	private double[][] addressSliceDistributionCdf;
	private double max;
	private double avg;
	private double median;
	private double minGreaterZero;
	private double fractionOfZeroes;
	
	private int bins;
	/**
	 * @param key
	 * @param parameters
	 */
	public PrefixSIdentifierSpaceLoadBalance(int bins) {
		super("PREFIX_S_IDENTIFIER_SPACE_LOAD_BALANCE", new Parameter[]{new IntParameter("BINS", bins),
				});
		this.bins = bins;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gtna.metrics.Metric#computeData(gtna.graph.Graph,
	 * gtna.networks.Network, java.util.HashMap)
	 */
	@Override
	public void computeData(Graph g, Network n, HashMap<String, Metric> m) {
		// use the spanning tree generated during the embedding / before the
		// embedding
		SpanningTree st = (SpanningTree) g.getProperty("SPANNINGTREE");
		PrefixSIdentiferSpaceSimple idSpace = (PrefixSIdentiferSpaceSimple) g
				.getProperty("ID_SPACE_0");
		PrefixSPartitionSimple[] partitions = idSpace.getPartitions();

		int addressSpaceSize = idSpace.getSize();
		int bitsPerLevel;
		Node[] nodes = g.getNodes();
		// sizes of address space slices (in bit)
		double[] sizesOfAddressSlices = new double[nodes.length];
		if (idSpace.isVirtual()) {
			for (int i = 0; i < nodes.length; i++) {
				// check whether the node has an ID assigned
				if (!((PrefixSIdentifier) partitions[i].getRepresentativeIdentifier())
						.isSet())
					continue;
				// for each node determine whether it is a leave
				int nrOfChildren = st.getChildren(i).length;
				if (nrOfChildren == 0) {
					// get address of the leaf and compute from the address the
					// size of the ID space slice
					PrefixSIdentifier id = (PrefixSIdentifier) partitions[i]
							.getRepresentativeIdentifier();
					// compute distance to the root node
					int level = id.distance(new PrefixSIdentifier(
							new short[] {}));
					sizesOfAddressSlices[i] = addressSpaceSize - level;
				}
				if (nrOfChildren == 1) {
					// get address of the leaf and compute from the address the
					// size of the ID space slice
					PrefixSIdentifier id = (PrefixSIdentifier) partitions[i]
							.getRepresentativeIdentifier();
					// compute distance to the root node
					int level = id.distance(new PrefixSIdentifier(
							new short[] {}));
					sizesOfAddressSlices[i] = addressSpaceSize - level-1;
				}
				// internal nodes have no part in the ID space
			}

		} else {
			bitsPerLevel = idSpace.getBitsPerCoord();
			int maxNrOfChildren = (int) Math.pow(2, bitsPerLevel);
			for (int i = 0; i < nodes.length; i++) {
				// check whether the node has an ID assigned
				if (!((PrefixSIdentifier) partitions[i].getRepresentativeIdentifier())
						.isSet())
					continue;
				// for each node determine whether it is a leave
				int nrOfChildren = st.getChildren(i).length;
				if (nrOfChildren == 0) {
					// get address of the leaf and compute from the address the
					// size of the ID space slice
					PrefixSIdentifier id = (PrefixSIdentifier) partitions[i]
							.getRepresentativeIdentifier();
					// compute distance to the root node
					int level = id.distance(new PrefixSIdentifier(
							new short[] {}));
					sizesOfAddressSlices[i] = addressSpaceSize - bitsPerLevel
							* level;
				} else {
					// in case it is not a leave determine whether it is a inner
					// node with full degree
					if (nrOfChildren == maxNrOfChildren) {
						// if the node is an inner node with full degree its
						// address space slice has size 0
						sizesOfAddressSlices[i] = 0;
					} else {
						// in case it is not an inner node with full degree, it
						// is responsible for the ID space slices which are not
						// assigned to any child
						int missingChildren = maxNrOfChildren - nrOfChildren;
						PrefixSIdentifier id = (PrefixSIdentifier) partitions[i]
								.getRepresentativeIdentifier();
						// compute distance to the root node
						int level = id.distance(new PrefixSIdentifier(
								new short[] {}));
						// the id space slice of the node is the id space slice
						// of one child times the nr of missing children
						// as we measure the address space slices in bit,
						// instead of multiplying with the nr of missing
						// children we add the logarithm of this number
						sizesOfAddressSlices[i] = addressSpaceSize
								- bitsPerLevel * (level + 1)
								+ Math.log(missingChildren) / Math.log(2.0d);
					}
				}
			}
		}	
		
		addressSliceDistribution = Statistics.binnedDistribution(
				sizesOfAddressSlices, 0, addressSpaceSize, this.bins);
		addressSliceDistributionCdf = Statistics
				.binnedCdf(addressSliceDistribution);

		//
		// Compute Singles
		// 
		double sizesSorted[] = sizesOfAddressSlices.clone();
		Arrays.sort(sizesSorted);
		this.max = sizesSorted[sizesSorted.length-1];
		this.median = sizesSorted[sizesSorted.length / 2] ;
		
		double sum = 0.0d;
		for (double sl:sizesOfAddressSlices){
			sum = sum + sl;
		}
		this.avg = sum / nodes.length;
		
		// count the zero entries and find the minimum slice value greater zero in the sorted array
		int zeroes = 0;
		for (double sl: sizesSorted){
			if (sl == 0.0d) 
				zeroes++;
			else {
				this.minGreaterZero = sl;
				break;
			}
		}
		this.fractionOfZeroes = zeroes / (double) nodes.length; 
		
	}

	/* (non-Javadoc)
	 * @see gtna.metrics.Metric#writeData(java.lang.String)
	 */
	@Override
	public boolean writeData(String folder) {
		boolean success = true;
		success &= DataWriter.writeWithoutIndex(this.addressSliceDistribution,
				"PREFIX_S_IDENTIFIER_SPACE_LOAD_BALANCE_ADDRESS_SLICE_DISTRIBUTION",
				folder);
		success &= DataWriter.writeWithoutIndex(
				this.addressSliceDistributionCdf,
				"PREFIX_S_IDENTIFIER_SPACE_LOAD_BALANCE_ADDRESS_SLICE_DISTRIBUTION_CDF",
				folder);
		return success;
	}

	/* (non-Javadoc)
	 * @see gtna.metrics.Metric#getSingles()
	 */
	@Override
	public Single[] getSingles() {
		Single max = new Single("PREFIX_S_IDENTIFIER_SPACE_LOAD_BALANCE_MAX",
				this.max);
		Single avg = new Single("PREFIX_S_IDENTIFIER_SPACE_LOAD_BALANCE_AVG",
				this.avg);
		Single median = new Single("PREFIX_S_IDENTIFIER_SPACE_LOAD_BALANCE_MEDIAN",
				this.median);
		Single min = new Single("PREFIX_S_IDENTIFIER_SPACE_LOAD_BALANCE_MINGREATERZERO",
				this.minGreaterZero);
		Single fraction = new Single("PREFIX_S_IDENTIFIER_SPACE_LOAD_BALANCE_FRACTIONOFZEROES",
				this.fractionOfZeroes);
		return new Single[]{max, avg, median, min, fraction};
	}

	/* (non-Javadoc)
	 * @see gtna.metrics.Metric#applicable(gtna.graph.Graph, gtna.networks.Network, java.util.HashMap)
	 */
	@Override
	public boolean applicable(Graph g, Network n, HashMap<String, Metric> m) {
		return g.hasProperty("ID_SPACE_0") && g.getProperty("ID_SPACE_0") instanceof PrefixSIdentiferSpaceSimple && g.hasProperty("SPANNINGTREE");
	}

}
