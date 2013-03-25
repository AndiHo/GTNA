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
 * MaxNormBIdentifier.java
 * ---------------------------------------
 * (C) Copyright 2009-2012, by Benjamin Schiller (P2P, TU Darmstadt)
 * and Contributors 
 *
 * Original Author: Andreas Höfer;
 * Contributors:    -;
 *
 * Changes since 2011-05-17
 * ---------------------------------------
 *
 */
package gtna.id.maxnorm;

import gtna.id.SIdentifier;
import gtna.id.Identifier;

/**
 * @author Andreas Höfer
 *
 */
public class MaxNormSIdentifier implements SIdentifier {

	private short[] pos;
	
	/**
	 * @param string
	 */
	public MaxNormSIdentifier(String string) {
		string = string.substring(1, string.length()-1);
		String[] substrings = string.split(",");
		pos = new short[substrings.length]; 
		for (int i=0; i< substrings.length; i++){
			pos[i] = Short.parseShort(substrings[i].trim());
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
	

	public MaxNormSIdentifier(short[] pos) {
		this.pos = pos;
	}
	
	/* (non-Javadoc)
	 * @see gtna.id.Identifier#distance(gtna.id.Identifier)
	 */
	@Override
	public Short distance(Identifier<Short> id) {
		
		short[] otherpos = ((MaxNormSIdentifier) id).pos;
		int common = (pos.length < otherpos.length) ? pos.length : otherpos.length; 
		
		// find the maximum distance
		int dist = 0;
		int diff;
		for (int i=0; i < common; i++){
			diff = pos[i] - otherpos[i];
			if (diff < 0) 
				diff = -1 * diff;
			if (dist < diff)
				dist = diff;
		}		
		return (short) dist;
	}

	/* (non-Javadoc)
	 * @see gtna.id.Identifier#equals(gtna.id.Identifier)
	 */
	@Override
	public boolean equals(Identifier<Short> id) {
		short[] otherpos = ((MaxNormSIdentifier) id).pos;
		if (pos.length != otherpos.length)
			return false;
		for (int i=0; i < pos.length; i++)
			if (pos[i] != otherpos[i])
				return false;
		return true;
	}
	
}
