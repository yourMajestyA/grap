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
package org.grap.processing.hydrology.manual;

import org.grap.lut.LutGenerator;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.processing.Operation;
import org.grap.processing.operation.hydrology.GridAccumulation;
import org.grap.processing.operation.hydrology.GridDirection;

public class SlopesAccumulationsTest {
	public static void main(String[] args) throws Exception {
		final String src = "../../datas2tests/grid/sample.asc";
		// final String src = "../../datas2tests/grid/mntzee_500.asc";
		// final String src = "../../datas2tests/grid/saipan-5.asc";

		// load the DEM
		final GeoRaster grDEM = GeoRasterFactory.createGeoRaster(src);
		grDEM.open();

		// compute the slopes directions
		final Operation slopesDirections = new GridDirection();
		final GeoRaster grSlopesDirections = grDEM
				.doOperation(slopesDirections);
		grSlopesDirections.save("../../datas2tests/tmp/1.tif");

		// compute the slopes accumulations
		final Operation slopesAccumulations = new GridAccumulation();
		final GeoRaster grSlopesAccumulations = grSlopesDirections
				.doOperation(slopesAccumulations);

		grSlopesAccumulations.setLUT(LutGenerator.colorModel("fire"));
		grSlopesAccumulations.show();
		grSlopesAccumulations.save("../../datas2tests/tmp/2.tif");
	}
}