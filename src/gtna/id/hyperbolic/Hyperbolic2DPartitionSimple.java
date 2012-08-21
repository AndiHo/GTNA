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

import org.apfloat.Apfloat;

import gtna.id.APFPartition;
import gtna.id.Identifier;
import gtna.id.Partition;

/**
 * @author Andreas Höfer
 *
 */
public class Hyperbolic2DPartitionSimple implements APFPartition {

	private Hyperbolic2DIdentifier id;
	
	public Hyperbolic2DPartitionSimple(Hyperbolic2DIdentifier id){
		this.id = id;
	}
	
	/* 
	 * @see gtna.id.Partition#distance(gtna.id.Identifier)
	 */
	@Override
	public Apfloat distance(Identifier<Apfloat> id) {
		return this.id.distance(id);
	}

	/* 
	 * @see gtna.id.Partition#contains(gtna.id.Identifier)
	 */
	@Override
	public boolean contains(Identifier<Apfloat> id) {
		return this.id.equals((Hyperbolic2DIdentifier) id);
	}

	/* 
	 * @see gtna.id.Partition#equals(gtna.id.Partition)
	 */
	@Override
	public boolean equals(Partition<Apfloat> p) {
		return this.id.equals((Hyperbolic2DIdentifier) p.getRepresentativeID());
	}

	/* 
	 * @see gtna.id.Partition#getRepresentativeID()
	 */
	@Override
	public Identifier<Apfloat> getRepresentativeID() {
		return this.id;
	}

	public String toString() {
		return this.id.toString();
	}
}
