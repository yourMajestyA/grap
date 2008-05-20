/*
 * The GRAP library (GeoRAster Processing) is a middleware dedicated
 * to the processing of various kinds of geographic raster data. It
 * provides a complete and robust API to manipulate ASCII Grid or
 * tiff, png, bmp, jpg (with the corresponding world file) geographic
 * images. GRAP is produced  by the geomatic team of the IRSTV Institute
 * <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
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
package org.grap.processing.operation.hydrology;

import ij.process.ImageProcessor;

import java.io.IOException;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.orbisgis.progress.IProgressMonitor;

public class FillSinks implements Operation {

	private double dEpsilon[] = new double[8];
	private int R, C;
	private int[] R0 = new int[8];
	private int[] C0 = new int[8];
	private int[] dR = new int[8];
	private int[] dC = new int[8];
	private int[] fR = new int[8];
	private int[] fC = new int[8];
	private int depth;
	private int ncols;
	private int nrows;
	private ImageProcessor m_DEM;
	private ImageProcessor m_Border;
	private ImageProcessor m_PreprocessedDEM;
	private Double minSlope = 0.01;

	private final static int m_iOffsetX[] = { 0, 1, 1, 1, 0, -1, -1, -1 };
	private final static int m_iOffsetY[] = { 1, 1, 0, -1, -1, -1, 0, 1 };
	private final static double INIT_ELEVATION = 50000D;

	public FillSinks(Double minSlope) {
		this.minSlope = minSlope;
	}

	public GeoRaster execute(final GeoRaster geoRaster, IProgressMonitor pm)
			throws OperationException {

		return processAlgorithm(geoRaster, minSlope);
	}

	/**
	 *
	 * @param geoRaster
	 *            the DEM to be processed.
	 * @param dMinSlope
	 *            is a slope parameters used to fill the sink, to find an
	 *            outlet.
	 */

	public GeoRaster processAlgorithm(final GeoRaster geoRaster,
			final double minSlope) {

		try {

			m_DEM = geoRaster.getGrapImagePlus().getProcessor();

			int i;
			double iValue;
			int x, y;
			int scan;
			int it;
			int ix, iy;
			boolean something_done = false;
			float z, z2, wz, wzn;

			double dMinSlope = Math.tan(Math.toRadians(minSlope));

			float cellSize = geoRaster.getMetadata().getPixelSize_X();
			nrows = geoRaster.getMetadata().getNRows();
			ncols = geoRaster.getMetadata().getNCols();
			depth = 0;

			for (i = 0; i < 8; i++) {
				dEpsilon[i] = dMinSlope * getDistToNeighborInDir(i, cellSize);
			}

			R0[0] = 0;
			R0[1] = nrows - 1;
			R0[2] = 0;
			R0[3] = nrows - 1;
			R0[4] = 0;
			R0[5] = nrows - 1;
			R0[6] = 0;
			R0[7] = nrows - 1;
			C0[0] = 0;
			C0[1] = ncols - 1;
			C0[2] = ncols - 1;
			C0[3] = 0;
			C0[4] = ncols - 1;
			C0[5] = 0;
			C0[6] = 0;
			C0[7] = ncols - 1;
			dR[0] = 0;
			dR[1] = 0;
			dR[2] = 1;
			dR[3] = -1;
			dR[4] = 0;
			dR[5] = 0;
			dR[6] = 1;
			dR[7] = -1;
			dC[0] = 1;
			dC[1] = -1;
			dC[2] = 0;
			dC[3] = 0;
			dC[4] = -1;
			dC[5] = 1;
			dC[6] = 0;
			dC[7] = 0;
			fR[0] = 1;
			fR[1] = -1;
			fR[2] = -nrows + 1;
			fR[3] = nrows - 1;
			fR[4] = 1;
			fR[5] = -1;
			fR[6] = -nrows + 1;
			fR[7] = nrows - 1;
			fC[0] = -ncols + 1;
			fC[1] = ncols - 1;
			fC[2] = -1;
			fC[3] = 1;
			fC[4] = ncols - 1;
			fC[5] = -ncols + 1;
			fC[6] = 1;
			fC[7] = -1;

			initAltitude();

			// TODO : Add progress listenner setProgressText("Fase 1");
			for (x = 0; x < ncols; x++) {
				for (y = 0; y < nrows; y++) {

					iValue = m_Border.getPixelValue(x, y);

					if (iValue == 1) {
						dryUpwardCell(x, y);
					}
				}

			}

			for (it = 0; it < 1000; it++) {
				// TODO : relate to progress monitor setProgressText("fase 2.
				// Iteracion " + Integer.toString(it));
				for (scan = 0; scan < 8; scan++) {
					R = R0[scan];
					C = C0[scan];
					something_done = false;

					do {
						z = m_DEM.getPixelValue(C, R);
						wz = m_PreprocessedDEM.getPixelValue(C, R);
						if (!Float.isNaN(z) && (wz > z)) {
							for (i = 0; i < 8; i++) {
								ix = C + m_iOffsetX[i];
								iy = R + m_iOffsetY[i];
								z2 = m_DEM.getPixelValue(ix, iy);
								if (!Float.isNaN(z2)) {
									wzn = m_PreprocessedDEM.getPixelValue(ix,
											iy)
											+ (float) dEpsilon[i];
									if (z >= wzn) {
										m_PreprocessedDEM
												.putPixelValue(C, R, z);
										something_done = true;
										dryUpwardCell(C, R);
										break;
									}
									if (wz > wzn) {
										m_PreprocessedDEM.putPixelValue(C, R,
												wzn);
										something_done = true;
									}
								}
							}
						}
					} while (nextCell(scan));

					if (!something_done) {
						break;
					}
				}
				if (!something_done) {
					break;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return GeoRasterFactory.createGeoRaster(m_PreprocessedDEM, geoRaster
				.getMetadata());

	}

	private void initAltitude() {
		boolean border;
		int x, y, i, ix, iy;
		float dValue;

		m_PreprocessedDEM = m_DEM.duplicate();
		m_Border = m_DEM.duplicate();

		m_PreprocessedDEM.multiply(Double.NaN);
		m_Border.multiply(Double.NaN);

		for (x = 0; x < ncols; x++) {
			for (y = 0; y < nrows; y++) {
				border = false;
				dValue = m_DEM.getPixelValue(x, y);

				if (!Float.isNaN(dValue)) {
					for (i = 0; i < 8; i++) {
						ix = x + m_iOffsetX[i];
						iy = y + m_iOffsetY[i];
						dValue = m_DEM.getPixelValue(ix, iy);
						if (Float.isNaN(dValue)) {
							border = true;
							break;
						}
					}
					if (border) {
						m_Border.putPixelValue(x, y, 1);
						m_PreprocessedDEM.putPixelValue(x, y, m_DEM
								.getPixelValue(x, y));
					}

					else {
						m_PreprocessedDEM.putPixelValue(x, y, INIT_ELEVATION);

					}
				}
			}
		}

	}

	private void dryUpwardCell(int x, int y) {
		final int MAX_DEPTH = 32000;
		int ix, iy, i;
		float zn, zw;

		depth += 1;

		if (depth <= MAX_DEPTH) {
			for (i = 0; i < 8; i++) {
				ix = x + m_iOffsetX[i];
				iy = y + m_iOffsetY[i];

				zw = m_PreprocessedDEM.getPixelValue(ix, iy);

				zn = m_DEM.getPixelValue(ix, iy);
				if (!Float.isNaN(zn) && zw == INIT_ELEVATION) {
					zw = m_PreprocessedDEM.getPixelValue(x, y)
							+ (float) dEpsilon[i];
					if (zn >= zw) {
						m_PreprocessedDEM.putPixelValue(ix, iy, zn);
						dryUpwardCell(ix, iy);
					}
				}
			}
		}
		depth -= 1;

	}

	private boolean nextCell(int i) {

		R = R + dR[i];
		C = C + dC[i];

		if (R < 0 || C < 0 || R >= nrows || C >= ncols) {
			R = R + fR[i];
			C = C + fC[i];

			if (R < 0 || C < 0 || R >= nrows || C >= ncols) {
				return false;
			}
		}

		return true;
	}

	/**
	 * TODO : This method must be extracted into a global class. It is used to
	 * calculate the distance for each pixels around a 3X3 matrix.
	 *
	 * @param iDir
	 * @return
	 */

	public static double getDistToNeighborInDir(int iDir, float cellSize) {
		final int m_iOffsetX[] = { 0, 1, 1, 1, 0, -1, -1, -1 };
		final int m_iOffsetY[] = { 1, 1, 0, -1, -1, -1, 0, 1 };
		final double m_dDist[] = new double[8];

		for (int i = 0; i < 8; i++) {
			m_dDist[i] = Math.sqrt(m_iOffsetX[i] * cellSize * m_iOffsetX[i]
					* cellSize + m_iOffsetY[i] * cellSize * m_iOffsetY[i]
					* cellSize);
		}
		return m_dDist[iDir];
	}
}