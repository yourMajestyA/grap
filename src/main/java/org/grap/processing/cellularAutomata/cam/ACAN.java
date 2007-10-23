/*
 * The GRAP library (GeoRAster Processing) is a middleware dedicated
 * to the processing of various kinds of geographic raster data. It
 * provides a complete and robust API to manipulate ASCII Grid or
 * tiff, png, bmp, jpg (with the corresponding world file) geographic
 * images. GRAP is produced  by the geomatic team of the IRSTV Institute
 * <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
 *
 * This file is part of GRAP.
 *
 * GRAP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GRAP. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.grap.processing.cellularAutomata.cam;

public abstract class ACAN implements ICAN {
	private ICA ca;

	private Object rac0;

	private Object rac1;

	private int nbCells;

	/* constructor */
	public ACAN(final ICA ca) {
		this.ca = ca;
		nbCells = ca.getNRows() * ca.getNCols();

		if (ca instanceof ICAShort) {
			rac0 = new short[nbCells];
			rac1 = new short[nbCells];
		} else if (ca instanceof ICAFloat) {
			rac0 = new float[nbCells];
			rac1 = new float[nbCells];
		}
	}

	/* getters */
	public ICA getCa() {
		return ca;
	}

	public Object getRac0() {
		return rac0;
	}

	public Object getRac1() {
		return rac1;
	}

	public Object getCANValues() {
		return rac0;
	}

	public int getNbCells() {
		return nbCells;
	}

	/* public methods */
	public abstract int getStableState();

	public void print(final String title) {
		System.out.println(title);
		if (ca instanceof ICAShort) {
			short[] _rac0 = (short[]) rac0;
			for (int r = 0; r < ca.getNRows(); r++) {
				for (int c = 0; c < ca.getNCols(); c++) {
					System.out.printf("%3d\t", _rac0[r * ca.getNCols() + c]);
				}
				System.out.println();
			}
		} else if (ca instanceof ICAFloat) {
			float[] _rac0 = (float[]) rac0;
			for (int r = 0; r < ca.getNRows(); r++) {
				for (int c = 0; c < ca.getNCols(); c++) {
					System.out.printf("%.1f\t", _rac0[r * ca.getNCols() + c]);
				}
				System.out.println();
			}
		}
	}
}