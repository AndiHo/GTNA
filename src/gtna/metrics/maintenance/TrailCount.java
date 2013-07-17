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
 * TrailCount.java
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
package gtna.metrics.maintenance;

import gtna.data.Single;
import gtna.graph.Graph;
import gtna.io.DataWriter;
import gtna.metrics.Metric;
import gtna.networks.Network;
import gtna.transformation.virtualoverlay.Trails;
import gtna.util.Distribution;

import java.util.HashMap;
import java.util.Vector;

/**
 * @author stef
 *
 */
public class TrailCount extends Metric {
     private Distribution counts;
	
	/**
	 * @param key
	 */
	public TrailCount() {
		super("TRAIL_COUNT");
	}

	/* (non-Javadoc)
	 * @see gtna.metrics.Metric#computeData(gtna.graph.Graph, gtna.networks.Network, java.util.HashMap)
	 */
	@Override
	public void computeData(Graph g, Network n, HashMap<String, Metric> m) {
		Trails gp = (Trails) g.getProperty("TRAILS_0");
		Vector<Vector<int[]>> trails = gp.getTrails();
		int max = 0;
		for (int i = 0; i < trails.size(); i++){
			if (trails.get(i).size() > max){
				max = trails.get(i).size();
			}
		}
		double[] count = new double[max+1];
       for (int i = 0; i < trails.size(); i++){
	      count[trails.get(i).size()]++;
       }
       for (int c = 0; c < count.length; c++){
    	   count[c] = count[c]/(double)trails.size();
       }
       this.counts = new Distribution(count);
	}

	/* (non-Javadoc)
	 * @see gtna.metrics.Metric#writeData(java.lang.String)
	 */
	@Override
	public boolean writeData(String folder) {
		boolean success = true;
		success &= DataWriter.writeWithIndex(
				this.counts.getDistribution(),
				"TRAIL_COUNT_DISTRIBUTION", folder);
		success &= DataWriter.writeWithIndex(this.counts.getCdf(),
				"TRAIL_COUNT_DISTRIBUTION_CDF", folder);
		return success;
	}

	/* (non-Javadoc)
	 * @see gtna.metrics.Metric#getSingles()
	 */
	@Override
	public Single[] getSingles() {
		Single min = new Single("TRAIL_COUNT_MIN",
				this.counts.getMin());
		Single max = new Single("TRAIL_COUNT_MAX",
				this.counts.getMax());
		Single mean = new Single("TRAIL_COUNT_MEAN",
				this.counts.getAverage());
		Single med = new Single("TRAIL_COUNT_MED",
				this.counts.getMedian());
		return new Single[]{min,max,mean,med};
	}

	/* (non-Javadoc)
	 * @see gtna.metrics.Metric#applicable(gtna.graph.Graph, gtna.networks.Network, java.util.HashMap)
	 */
	@Override
	public boolean applicable(Graph g, Network n, HashMap<String, Metric> m) {
		return g.hasProperty("TRAILS_0");
	}

	

}
