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
package org.grap.processing.operation.manual;

import java.awt.geom.Point2D;
import java.io.File;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;

import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.gui.Wand;



import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateList;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

public class ShapeExtractionTest {
	public static void main(String[] args) throws Exception {
		String src = "../../datas2tests/grid/sample.asc";

		final GeoRaster geoRaster = GeoRasterFactory.createGeoRaster(src);
		geoRaster.open();

		Wand w = new Wand(geoRaster.getGrapImagePlus().getProcessor());

		w.autoOutline(150, 150);

		System.out.println("Points:" + w.npoints);

		int x[] = w.xpoints;
		int y[] = w.ypoints;
		Roi roi = new PolygonRoi(w.xpoints, w.ypoints, w.npoints,
				Roi.TRACED_ROI);
		final Coordinate[] jtsCoords = new Coordinate[w.npoints];
		for (int i = 0; i < roi.getPolygon().npoints; i++) {
			final int xWand = roi.getPolygon().xpoints[i];
			final int yWand = roi.getPolygon().ypoints[i];
			final Point2D worldXY = geoRaster.pixelToWorldCoord(xWand,
					yWand);

			jtsCoords[i] = new Coordinate(worldXY.getX(), worldXY.getY());
		}
		
		final CoordinateList cl = new CoordinateList(jtsCoords);
		cl.closeRing();

		final LinearRing geomRing = new GeometryFactory()
				.createLinearRing(cl.toCoordinateArray());

		final Polygon geomResult = new GeometryFactory().createPolygon(
				geomRing, null);
		
		System.out.println(geomResult.toText());
			
		

		geoRaster.show();
	}
	
	
	
	
	
}