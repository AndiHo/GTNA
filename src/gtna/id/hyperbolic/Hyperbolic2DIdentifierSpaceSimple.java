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
 * Hyperbolic2DIdentifierSpaceSimple.java
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
package gtna.id.hyperbolic;

import gtna.graph.Graph;
import gtna.id.APFIdentifierSpace;
import gtna.id.Identifier;
import gtna.id.Partition;
import gtna.io.Filereader;
import gtna.io.Filewriter;
import gtna.util.Config;

import java.util.Random;

import org.apfloat.Apfloat;

/**
 * @author Andreas Höfer
 *
 */
public class Hyperbolic2DIdentifierSpaceSimple implements APFIdentifierSpace {

	private Hyperbolic2DPartitionSimple[] partitions; 
	
	/* 
	 * @see gtna.id.IdentifierSpace#getPartitions()
	 */
	@Override
	public Partition<Apfloat>[] getPartitions() {
		return partitions;
	}

	/* 
	 * @see gtna.id.IdentifierSpace#setPartitions(gtna.id.Partition<Type>[])
	 */
	@Override
	public void setPartitions(Partition<Apfloat>[] partitions) {
		this.partitions = (Hyperbolic2DPartitionSimple[]) partitions;
	}

	/* 
	 * @see gtna.id.IdentifierSpace#randomID(java.util.Random)
	 * Only return existing IDs as it is simple
	 */
	@Override
	public Identifier<Apfloat> randomID(Random rand) {
		return this.partitions[rand.nextInt(this.partitions.length)].getRepresentativeID();
	}

	/* 
	 * @see gtna.id.IdentifierSpace#getMaxDistance()
	 * As distance is not bounded there is no max distance
	 */
	@Override
	public Apfloat getMaxDistance() {
		throw new RuntimeException("Method not implemented as there is no meaningful max distance for Hyperbolic2DIdentifiers.");
	}

	/* 
	 * @see gtna.graph.GraphProperty#write(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean write(String filename, String key) {
		Filewriter fw = new Filewriter(filename);
		
		// CLASS
		fw.writeComment(Config.get("GRAPH_PROPERTY_CLASS"));
		fw.writeln(this.getClass().getCanonicalName().toString());

		// KEY
		fw.writeComment(Config.get("GRAPH_PROPERTY_KEY"));
		fw.writeln(key);
		
		// # PARTITIONS
		fw.writeComment("Partitions");
		fw.writeln(this.partitions.length);

		fw.writeln();

		// PARTITIONS
		int index = 0;
		for (Hyperbolic2DPartitionSimple p : this.partitions) {
			fw.writeln(index++ + ":" + p.toString());
		}

		return fw.close();
	}

	/* 
	 * @see gtna.graph.GraphProperty#read(java.lang.String, gtna.graph.Graph)
	 */
	@Override
	public void read(String filename, Graph graph) {
		Filereader fr = new Filereader(filename);

		// CLASS
		fr.readLine();

		// KEY
		String key = fr.readLine();

		// # PARTITIONS
		int partitions = Integer.parseInt(fr.readLine());
		this.partitions = new Hyperbolic2DPartitionSimple[partitions];

		// PARTITIONS
		String line = null;
		while ((line = fr.readLine()) != null) {
			String[] temp = line.split(":");
			int index = Integer.parseInt(temp[0]);
			this.partitions[index] = new Hyperbolic2DPartitionSimple(new Hyperbolic2DIdentifier(temp[1]));
		}

		fr.close();

		graph.addProperty(key, this);
		
	}

}
