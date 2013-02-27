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
 * PrefixSIdentiferSpaceSimple.java
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
package gtna.id.prefix;

import gtna.graph.Graph;
import gtna.id.Identifier;
import gtna.id.IdentifierSpace;
import gtna.id.Partition;
import gtna.io.Filereader;
import gtna.io.Filewriter;
import gtna.util.Config;

import java.io.IOException;
import java.util.Random;

/**
 * @author Andreas Höfer
 *
 */
public class PrefixSIdentiferSpaceSimple implements IdentifierSpace<Integer> {
	
    private int size;
    private int bitsPerCoord;
	private PrefixSPartitionSimple[] partitions; 
	private boolean virtual;
	/* (non-Javadoc)
	 * @see gtna.id.IdentifierSpace#getPartitions()
	 */
	
	public PrefixSIdentiferSpaceSimple(){
	}
	
	public PrefixSIdentiferSpaceSimple(int bitsPerCoord, int size, boolean virtual){
		this.bitsPerCoord = bitsPerCoord;
		this.size = size;
		this.virtual = virtual;
	}
	public int getSize() {
		return this.size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getBitsPerCoord() {
		return this.bitsPerCoord;
	}
	public void setBitsPerCoord(int bitsPerCoord) {
		this.bitsPerCoord = bitsPerCoord;
	}
	@Override
	public PrefixSPartitionSimple[] getPartitions() {
		return partitions;
	}
	

	/* (non-Javadoc)
	 * @see gtna.id.IdentifierSpace#setPartitions(gtna.id.Partition<Type>[])
	 */
	@Override
	public void setPartitions(Partition<Integer>[] partitions) {
		this.partitions =(PrefixSPartitionSimple[]) partitions;  
	}

	/* (non-Javadoc)
	 * @see gtna.id.IdentifierSpace#randomID(java.util.Random)
	 */
	@Override
	public Identifier<Integer> randomID(Random rand) {
		return this.partitions[rand.nextInt(this.partitions.length)].getRepresentativeID();
	}

	/* (non-Javadoc)
	 * @see gtna.id.IdentifierSpace#getMaxDistance()
	 */
	@Override
	public Integer getMaxDistance() {
		return Short.MAX_VALUE;
	}

	/* (non-Javadoc)
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

		// SIZE
		fw.writeComment("SIZE");
		fw.writeln(size);
		
		// BITSPERCOORD
		fw.writeComment("BITSPERCOORD");
		fw.writeln(bitsPerCoord);

		// Type
		fw.writeComment("VIRTUAL");
		fw.writeln(""+this.virtual);
		
		// # PARTITIONS
		fw.writeComment("Partitions");
		fw.writeln(this.partitions.length);

		fw.writeln();

		System.out.println("Write Prefix ID space");
		// PARTITIONS
		int index = 0;
		for (PrefixSPartitionSimple p : this.partitions) {
			fw.writeln(index++ + ":" + p.toString());
		}

		return fw.close();
	}

	/* (non-Javadoc)
	 * @see gtna.graph.GraphProperty#read(java.lang.String, gtna.graph.Graph)
	 */
	@Override
	public void read(String filename, Graph graph) {
		Filereader fr = new Filereader(filename);

		// CLASS
		fr.readLine();

		// KEY
		String key = fr.readLine();
		
		// SIZE
		this.size = Integer.parseInt(fr.readLine());
		
		// BITSPERCOORD
		this.bitsPerCoord = Integer.parseInt(fr.readLine());
		
		// Type
        this.virtual = Boolean.parseBoolean(fr.readLine());
        
		// # PARTITIONS
		int partitions = Integer.parseInt(fr.readLine());
		this.partitions = new PrefixSPartitionSimple[partitions];

		// PARTITIONS
		String line = null;
		while ((line = fr.readLine()) != null) {
			String[] temp = line.split(":");
			int index = Integer.parseInt(temp[0]);
			this.partitions[index] = new PrefixSPartitionSimple(new PrefixSIdentifier(temp[1]));
		}

		fr.close();
		graph.addProperty(key, this);	
	}
	/**
	 * @return the virtual
	 */
	public boolean isVirtual() {
		return virtual;
	}
	/**
	 * @param virtual the virtual to set
	 */
	public void setVirtual(boolean virtual) {
		this.virtual = virtual;
	}


}
