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
 * Original Author: andi;
 * Contributors:    -;
 *
 * Changes since 2011-05-17
 * ---------------------------------------
 *
 */
package gtna.id.euclidean;

import gtna.id.DoubleIdentifier;
import gtna.id.DoublePartition;
import gtna.id.Identifier;
import gtna.id.Partition;

import java.util.Random;

/**
 * @author andi
 *
 */
public class EuclideanPartitionSimple extends DoublePartition {

	protected EuclideanIdentifier id;
	
	public EuclideanPartitionSimple(EuclideanIdentifier id){
		this.id = id;
	}
	
	public EuclideanPartitionSimple(String string) {
		this.id = new EuclideanIdentifier(string);
	}
	
	
	@Override
	public double distance(DoubleIdentifier id) {
		return this.id.distance(id);
	}

	@Override
	public double distance(DoublePartition p) {
		return this.id.distance((DoubleIdentifier) p.getRepresentativeIdentifier());
	}

	@Override
	public String asString() {
		return this.id.asString();
	}

	@Override
	public boolean contains(Identifier id) {
		return this.id.equals(id);
	}
	

	@Override
	public boolean equals(Partition p) {
		return this.id.equals(((EuclideanPartitionSimple) p).id);
	}
	

	public String toString() {
		return this.id.toString();
	}
	
	
	@Override
	public Identifier getRepresentativeIdentifier() {
		return this.id;
	}

	@Override
	public Identifier getRandomIdentifier(Random rand) {
		return new EuclideanIdentifier(this.id.pos.clone());
	}

	

	/**
	 * @return the identifier
	 */
	public EuclideanIdentifier getIdentifier() {
		return this.id;
	}

	/**
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(EuclideanIdentifier identifier) {
		this.id = identifier;
	}	
}
