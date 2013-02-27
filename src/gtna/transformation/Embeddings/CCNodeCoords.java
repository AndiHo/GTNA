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
 * CCNode.java
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
package gtna.transformation.Embeddings;

import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;

/**
 * @author Andreas Höfer
 * Helper class for the CCEMebdding, stores all the relevant information for a node while the embedding procedure is executed
 */
class CCNodeCoords {
	// index of the node in  the accompanying graph object
	int index;
	Apcomplex pos;
	Apfloat alpha;
	Apfloat beta;
	// CCNodeCoords parent;
	int treelevel; // level of the node in the spanning tree
	
	CCNodeCoords(int index, Apcomplex pos) {
		this.index = index;
		this.pos = pos;
	}
	/**
	 * @param n
	 */
	CCNodeCoords(int index) {
		this.index = index;
	}
}
