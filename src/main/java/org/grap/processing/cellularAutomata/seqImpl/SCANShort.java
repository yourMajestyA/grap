/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.grap.processing.cellularAutomata.seqImpl;

import org.grap.processing.cellularAutomata.cam.ACAN;
import org.grap.processing.cellularAutomata.cam.ICAShort;

public class SCANShort implements ISCAN {
	private short[] rac1;
	private short[] rac0;
	private ICAShort ca;
	private int ncols;
	private int nrows;

	public SCANShort(final ACAN can) {
		rac0 = (short[]) can.getRac0();
		rac1 = (short[]) can.getRac1();
		ca = (ICAShort) can.getCa();
		ncols = ca.getNCols();
		nrows = ca.getNRows();

		int i = 0;
		for (int r = 0; r < nrows; r++) {
			for (int c = 0; c < ncols; c++) {
				rac0[i] = ca.init(r, c, i);
				i++;
			}
		}
	}

	public boolean globalTransition(final int iterationsCount) {
		boolean modified = false;
		int i = 0;

		if (0 == iterationsCount % 2) {
			for (int r = 0; r < nrows; r++) {
				for (int c = 0; c < ncols; c++) {
					rac1[i] = ca.localTransition(rac0, r, c, i);
					if (rac0[i] != rac1[i]) {
						modified = true;
					}
					i++;
				}
			}
		} else {
			for (int r = 0; r < nrows; r++) {
				for (int c = 0; c < ncols; c++) {
					rac0[i] = ca.localTransition(rac1, r, c, i);
					if (rac0[i] != rac1[i]) {
						modified = true;
					}
					i++;
				}
			}
		}
		return modified;
	}
}