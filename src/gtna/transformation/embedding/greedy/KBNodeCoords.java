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
 * KBNodeCoords.java
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
package gtna.transformation.embedding.greedy;

import org.apfloat.Apcomplex;

/**
 * @author Andreas Höfer
 * Helper class for the KBEmbedding, stores all the relevant information for a node while the embedding procedure is executed
 */
class KBNodeCoords {
	// index of the node in the accompanying graph object
	int index;
	Apcomplex pos;
	// hyperbolic isometry mu_r as matrix in array form
	Apcomplex[] mu;
	// level of the node in the spanning tree
	int treelevel; 
	
	KBNodeCoords(int index, Apcomplex pos) {
		this.index = index;
		this.pos = pos;
	}
	/**
	 * @param index
	 */
	KBNodeCoords(int index) {
		this.index = index;
	}
}
