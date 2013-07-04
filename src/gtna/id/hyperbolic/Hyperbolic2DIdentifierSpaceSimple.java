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
 * Original Author: Andreas Höfer;
 * Contributors:    -;
 *
 * Changes since 2011-05-17
 * ---------------------------------------
 *
 */
package gtna.id.hyperbolic;

import gtna.id.APFPartition;
import gtna.id.Identifier;
import gtna.id.IdentifierSpace;
import gtna.id.Partition;
import gtna.io.Filereader;
import gtna.io.Filewriter;

import java.util.Random;

/**
 * @author Andreas Höfer
 *
 */
public class Hyperbolic2DIdentifierSpaceSimple extends IdentifierSpace {

	/**
	 * @param partitions
	 */
	public Hyperbolic2DIdentifierSpaceSimple(Partition[] partitions) {
		super(partitions);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see gtna.id.IdentifierSpace#writeParameters(gtna.io.Filewriter)
	 */
	@Override
	protected void writeParameters(Filewriter fw) {
	}

	/* (non-Javadoc)
	 * @see gtna.id.IdentifierSpace#readParameters(gtna.io.Filereader)
	 */
	@Override
	protected void readParameters(Filereader fr) {
	}

	/* (non-Javadoc)
	 * @see gtna.id.IdentifierSpace#getRandomIdentifier(java.util.Random)
	 */
	@Override
	public Identifier getRandomIdentifier(Random rand) {
		return this.partitions[rand.nextInt(this.partitions.length)].getRepresentativeIdentifier();
	}

}
