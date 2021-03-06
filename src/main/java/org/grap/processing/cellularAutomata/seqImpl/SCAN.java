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
import org.grap.processing.cellularAutomata.cam.ICA;
import org.grap.processing.cellularAutomata.cam.ICAFloat;
import org.grap.processing.cellularAutomata.cam.ICAShort;

public class SCAN extends ACAN {
	private int iterationsCount;

	/* constructor */
	public SCAN(final ICA ca) {
		super(ca);
	}

	/* getters */
	public int getIterationsCount() {
		return iterationsCount;
	}

	/* public methods */
	public int getStableState() {
		final long startTime = System.currentTimeMillis();
		long startT = System.currentTimeMillis();
		ISCAN scan = null;

		// initialize
		if (getCa() instanceof ICAShort) {
			scan = new SCANShort(this);
		} else if (getCa() instanceof ICAFloat) {
			scan = new SCANFloat(this);
		}
		System.err.printf("end of initialization\n");

		// get stable state
		iterationsCount = 0;
		boolean goOn;

		do {
			goOn = scan.globalTransition(iterationsCount);
			System.err.printf("Seq. Step %d : %d ms\n", iterationsCount, System
					.currentTimeMillis()
					- startT);
			iterationsCount++;
			startT = System.currentTimeMillis();
		} while (goOn);

		System.err.printf("Total duration : %d ms\n", System
				.currentTimeMillis()
				- startTime);
		return iterationsCount;
	}
}