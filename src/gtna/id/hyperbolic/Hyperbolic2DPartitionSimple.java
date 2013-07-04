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
 * Hyperbolic2DPartitionSimple.java
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
package gtna.id.hyperbolic;

import java.util.Random;

import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;

import gtna.id.APFIdentifier;
import gtna.id.APFPartition;
import gtna.id.Identifier;
import gtna.id.Partition;
import gtna.id.md.MDIdentifier;

/**
 * @author Andreas Höfer
 *
 */
public class Hyperbolic2DPartitionSimple extends APFPartition {

	protected Hyperbolic2DIdentifier id;
	
	public Hyperbolic2DPartitionSimple(Hyperbolic2DIdentifier id){
		this.id = id;
	}
	
	public Hyperbolic2DPartitionSimple(String string) {
		this.id = new Hyperbolic2DIdentifier(string);
	}
		
	/* (non-Javadoc)
	 * @see gtna.id.APFPartition#distance(gtna.id.APFPartition)
	 */
	@Override
	public Apfloat distance(APFPartition p) {
		return this.id.distance((APFIdentifier) p.getRepresentativeIdentifier());
	}
	
	@Override
	public Apfloat distance(APFIdentifier id) {
		return this.id.distance(id);
	}
	

	public String toString() {
		return this.id.toString();
	}

	/* (non-Javadoc)
	 * @see gtna.id.Partition#asString()
	 */
	@Override
	public String asString() {
		return this.id.toString();
	}

	/* (non-Javadoc)
	 * @see gtna.id.Partition#contains(gtna.id.Identifier)
	 */
	@Override
	public boolean contains(Identifier id) {
		return this.id.equals((Hyperbolic2DIdentifier) id);
	}

	/* (non-Javadoc)
	 * @see gtna.id.Partition#getRepresentativeIdentifier()
	 */
	@Override
	public Identifier getRepresentativeIdentifier() {
		return this.id;
	}

	/* (non-Javadoc)
	 * @see gtna.id.Partition#getRandomIdentifier(java.util.Random)
	 */
	@Override
	public Identifier getRandomIdentifier(Random rand) {
		return new Hyperbolic2DIdentifier(id);
	}

	/* (non-Javadoc)
	 * @see gtna.id.Partition#equals(gtna.id.Partition)
	 */
	@Override
	public boolean equals(Partition p) {
		return this.id.equals((Hyperbolic2DIdentifier) id);
	}

	
	
}
