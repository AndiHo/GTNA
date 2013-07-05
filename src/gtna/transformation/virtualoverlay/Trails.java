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

import gtna.io.Filereader;
import gtna.io.Filewriter;

import java.util.Vector;

/**
 * @author andi
 *
 */
public class Trails extends gtna.graph.GraphProperty {

	Vector<Vector<int[]>> trails;
	
	
	public Trails(){
	}
	public Trails(Vector<Vector<int[]>> trails){
		this.trails = trails;
	}
	/* (non-Javadoc)
	 * @see gtna.graph.GraphProperty#write(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean write(String filename, String key) {
		Filewriter fw = new Filewriter(filename);
		this.writeHeader(fw, this.getClass(), key);
		// iterate over the outer vector
		for (int i=0; i < trails.size(); i++){
			Vector<int[]> curTrail = trails.get(i);
			String line = "";
			// iterate over the inner vector
			for (int j=0; j < curTrail.size(); j++){
				String elem = "(" + curTrail.get(j)[0] + ":" + curTrail.get(j)[1] + ")";  
				if (j==0)
					line = line + elem;
				else
					line = line + "," + elem;
			}
			line = line + "\n";
			fw.write(line);
		}
		return fw.close();
	}

	/* (non-Javadoc)
	 * @see gtna.graph.GraphProperty#read(java.lang.String)
	 */
	@Override
	public String read(String filename) {
		Filereader fr = new Filereader(filename);
		String key = this.readHeader(fr);
		
		trails = new Vector<Vector<int[]>>();
		
		String line = null;
		while ((line = fr.readLine()) != null) {
			String[] elements = line.split(",");
			Vector<int[]> trailsPerNode = new Vector<int[]>(elements.length);
			// iterate over the trails
			for (int i=0; i < elements.length; i++){
				int[] trail = new int[2];
				// first char is '(', second char the start point of the trail 
				String[] parts = elements[i].split(":");
				trail[0] = Integer.parseInt(parts[0].substring(1));
				// third char is end point of the trail
				trail[1] = Integer.parseInt(parts[1].substring(0,parts[1].length()-1));
				trailsPerNode.add(trail);
			}	
			trails.add(trailsPerNode);
		}
		
		
		fr.close();
		return key;
	}

	public Vector<Vector<int[]>> getTrails(){
		return trails;
	}	
}