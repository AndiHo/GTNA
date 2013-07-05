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
 * Trails.java
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
package gtna.transformation.virtualoverlay;

import java.util.Vector;

/**
 * @author andi
 *
 */
public class Trails extends gtna.graph.GraphProperty {

	Vector<Vector<int[]>> trails;
	
	public Trails(Vector<Vector<int[]>> trails){
		this.trails = trails;
	}
	/* (non-Javadoc)
	 * @see gtna.graph.GraphProperty#write(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean write(String filename, String key) {
		return true;
	}

	/* (non-Javadoc)
	 * @see gtna.graph.GraphProperty#read(java.lang.String)
	 */
	@Override
	public String read(String filename) {
		return "";
	}

}
