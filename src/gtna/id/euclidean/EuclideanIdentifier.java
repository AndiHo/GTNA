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
 * Hyperbolic2DIdentifier.java
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
package gtna.id.euclidean;

import gtna.id.DoubleIdentifier;
import gtna.id.Identifier;

/**
 * Euclidean coordinates with arbitrary dimensions 
 * 
 * @author Andreas Höfer
 *
 */
public class EuclideanIdentifier extends DoubleIdentifier{

	protected double[] pos;

	public EuclideanIdentifier(double[] pos){
		this.pos = pos;
	}
	
	/**
	 * @param string
	 */
	public EuclideanIdentifier(String string) {
		string = string.substring(1, string.length()-1);
		String[] substrings = string.split(",");
		pos = new double[substrings.length]; 
		for (int i=0; i< substrings.length; i++){
			pos[i] = Double.parseDouble(substrings[i].trim());
		}
	}

	public String toString(){
		StringBuilder strb = new StringBuilder("(");
		for (int i=0; i<pos.length -1; i++){
			strb.append(pos[i] + ", ");
		}
		strb.append(pos[pos.length-1] + ")");
		return strb.toString();
	}

	public double[] getPos(){
		return pos;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(DoubleIdentifier o) {
		EuclideanIdentifier other = (EuclideanIdentifier) o;
		for (int i = 0; i < this.pos.length; i++) {
			if (this.pos[i] < other.pos[i]) {
				return -1;
			} else if (this.pos[i] > other.pos[i]) {
				return 1;
			}
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see gtna.id.DoubleIdentifier#distance(gtna.id.DoubleIdentifier)
	 */
	@Override
	public double distance(DoubleIdentifier id) {
		// TODO check dimensions
		double[] otherpos = ((EuclideanIdentifier) id).pos;
		double sumOfSquares = 0;
		for (int i=0; i <pos.length; i++)
			sumOfSquares += (this.pos[i] - otherpos[i]) * (this.pos[i] - otherpos[i]);		
		return Math.sqrt(sumOfSquares);
	}

	/* (non-Javadoc)
	 * @see gtna.id.Identifier#asString()
	 */
	@Override
	public String asString() {
		return this.toString();
	}

	/* (non-Javadoc)
	 * @see gtna.id.Identifier#equals(gtna.id.Identifier)
	 */
	@Override
	public boolean equals(Identifier id) {
		// TODO check dimensions
		double[] otherpos = ((EuclideanIdentifier) id).pos;
		boolean eq = true;
		for (int i=0; i <pos.length; i++)
			eq &= (this.pos[i] == otherpos[i]);
		return eq;
	} 
}
