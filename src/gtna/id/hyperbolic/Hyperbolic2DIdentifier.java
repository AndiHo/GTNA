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
 * Original Author: andi;
 * Contributors:    -;
 *
 * Changes since 2011-05-17
 * ---------------------------------------
 *
 */
package gtna.id.hyperbolic;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;

import gtna.id.APFIdentifier;
import gtna.id.Identifier;

/**
 * Hyperbolic 2D coordinates in the Poincare model 
 * @author Andreas Höfer
 *
 */
public class Hyperbolic2DIdentifier implements APFIdentifier{

	private Apcomplex pos;

	public Hyperbolic2DIdentifier(Apcomplex pos){
		this.pos = pos;
	}
	
	public Hyperbolic2DIdentifier(String pos){
		this.pos = new Apcomplex(pos);
	}
	

	/* 
	 * Implement hyperbolic distance function of the poincare model
	 * @see gtna.id.Identifier#distance(gtna.id.Identifier)
	 */
	@Override
	public Apfloat distance(Identifier<Apfloat> id) {
		
		Apcomplex otherpos =  ((Hyperbolic2DIdentifier) id).pos; 
		
		// Compute 2 *|z1 − z2 |^2 / ((1 − |z1 |^2 )(1 − |z2|^2 )) + 1.
		// z1 = this.pos, z2 = otherpos		
		
		Apfloat denominator = ApcomplexMath.abs(this.pos.subtract(otherpos));
		denominator = denominator.multiply(denominator);
		denominator = denominator.add(denominator); 
		
		Apfloat absThisPos = ApcomplexMath.abs(this.pos);
		Apfloat nominatorFirstFactor = Apfloat.ONE.subtract(absThisPos.multiply(absThisPos));
		Apfloat absOtherPos = ApcomplexMath.abs(otherpos);
		Apfloat nominatorSecondFactor = Apfloat.ONE.subtract(absOtherPos.multiply(absOtherPos));	
		Apfloat nominator = nominatorFirstFactor.multiply(nominatorSecondFactor);
		Apfloat x = null;
		try{
			x = denominator.divide(nominator).add(Apfloat.ONE);
			//throw new Exception();
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("Hyperpolic2DID.apDist debug info:");
			System.out.println("this.pos=" + "(" + this.pos.real() + "," + this.pos.imag() + ")");
			System.out.println("otherpos=" + "(" + otherpos.real() + "," + otherpos.imag() + ")");
			System.out.println("absThisPos=" + absThisPos);
			System.out.println("absThisPos.doubleValue()=" + absThisPos.doubleValue());
			System.out.println("nominatorFirstFactor=" + nominatorFirstFactor);
			System.out.println("nominatorFirstFacto.doubleValue()=" + nominatorFirstFactor.doubleValue());
			System.out.println("absOtherPos=" + absOtherPos);
			System.out.println("nominatorSecondFactor=" + nominatorSecondFactor);
			// System.out.println("apDistCalled=" + apDistCalled);
		}
//		apDistCalled++;
//		if (apDistCalled % 100000==0)
//			System.out.println("Nr of Hyperbolic2DID.apDist calls:" + apDistCalled);
		
		// compute arccosh(x)
		return ApfloatMath.acosh(x);
		
	}

	/* (non-Javadoc)
	 * @see gtna.id.Identifier#equals(gtna.id.Identifier)
	 */
	@Override
	public boolean equals(Identifier<Apfloat> id) {
		return pos.equals(((Hyperbolic2DIdentifier) id).pos);
	}

	public String toString(){
		return pos.toString();
	}
}
