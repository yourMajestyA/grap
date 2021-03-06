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
package org.grap.processing.operation.manual.hydrology;

import org.grap.lut.LutGenerator;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.processing.Operation;
import org.grap.processing.operation.hydrology.D8OpAccumulation;
import org.grap.processing.operation.hydrology.D8OpAllOutlets;
import org.grap.processing.operation.hydrology.D8OpAllWatersheds;
import org.grap.processing.operation.hydrology.D8OpDirection;
import org.grap.processing.operation.hydrology.D8OpWatershedsWithThreshold;

public class ManualWatershedWithThreshold {
	public static void main(String[] args) throws Exception {
		final String src = "../../datas2tests/grid/sample.asc";
		// final String src = "../../datas2tests/grid/mntzee_500.asc";
		// final String src = "../../datas2tests/grid/saipan-5.asc";

		// load the DEM
		final GeoRaster grDEM = GeoRasterFactory.createGeoRaster(src);
		grDEM.open();

		// compute the slopes directions
		final Operation slopesDirections = new D8OpDirection();
		final GeoRaster grSlopesDirections = grDEM
				.doOperation(slopesDirections);
		grSlopesDirections.save("../../datas2tests/tmp/1.tif");

		// compute the slopes accumulations
		final Operation slopesAccumulations = new D8OpAccumulation();
		final GeoRaster grSlopesAccumulations = grSlopesDirections
				.doOperation(slopesAccumulations);
		grSlopesAccumulations.save("../../datas2tests/tmp/11.tif");

		// find all the outlets
		final Operation allOutlets = new D8OpAllOutlets();
		final GeoRaster grAllOutlets = grSlopesDirections
				.doOperation(allOutlets);
		grAllOutlets.save("../../datas2tests/tmp/111.tif");

		// compute all the watersheds
		final Operation allWatersheds = new D8OpAllWatersheds();
		final GeoRaster grAllWatersheds = grSlopesDirections
				.doOperation(allWatersheds);
		grAllWatersheds.save("../../datas2tests/tmp/1111.tif");

		// extract some "big" watersheds
		final int threshold = 100;
		final Operation watershedsWithThreshold = new D8OpWatershedsWithThreshold(
				grAllWatersheds, grAllOutlets, threshold);
		final GeoRaster grWatershedsWithThreshold = grSlopesAccumulations
				.doOperation(watershedsWithThreshold);

		grWatershedsWithThreshold.getImagePlus().getProcessor()
				.setColorModel(LutGenerator.colorModel("fire"));
		grWatershedsWithThreshold.show();
		grWatershedsWithThreshold.save("../../datas2tests/tmp/2.tif");
	}
}