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
package org.grap.processing.operation;

import ij.ImagePlus;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import org.grap.io.GrapTest;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;
import org.grap.utilities.EnvelopeUtil;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CropTest extends GrapTest {
	private GeoRaster geoRasterSrc;

	private GeoRaster geoRasterDst;

        @Before
	public void setUp() throws Exception {
		geoRasterSrc = GeoRasterFactory.createGeoRaster(otherData
				+ "sample.asc");
		geoRasterSrc.open();
	}

        @Test
	public void testCropPolygon() throws Exception {
		Envelope rasterEnvelope = geoRasterSrc.getMetadata().getEnvelope();
		final int bufferSize = (int) (rasterEnvelope.getWidth() / 2.3);
		rasterEnvelope = new Envelope(new Coordinate(rasterEnvelope.getMinX()
				+ bufferSize, rasterEnvelope.getMinY() + bufferSize),
				new Coordinate(rasterEnvelope.getMaxX() - bufferSize,
						rasterEnvelope.getMaxY() - bufferSize));
		final LinearRing polygon = (LinearRing) EnvelopeUtil
				.toGeometry(rasterEnvelope);
		geoRasterSrc.save(tmpData + "1.tif");
		geoRasterDst = geoRasterSrc.doOperation(new Crop(polygon));
		geoRasterDst.save(tmpData + "2.png");

		assertTrue(geoRasterDst.getWidth() > 0);
		assertTrue(geoRasterDst.getHeight() > 0);

		final ImagePlus srcImagePlus = geoRasterSrc.getImagePlus();
		final ImagePlus dstImagePlus = geoRasterDst.getImagePlus();
		checkCrop(geoRasterDst.getMetadata().getEnvelope(), srcImagePlus,
				dstImagePlus);
	}

        @Test
	public void testCropPolygonOutside() throws Exception {
		final LinearRing polygon = new GeometryFactory()
				.createLinearRing(new Coordinate[] {
						new Coordinate(100.5, 100.5),
						new Coordinate(100.5, 101.5),
						new Coordinate(101.5, 101.5),
						new Coordinate(101.5, 100.5),
						new Coordinate(100.5, 100.5) });
		geoRasterSrc.save(tmpData + "1.tif");
		geoRasterDst = geoRasterSrc.doOperation(new Crop(polygon));

		assertTrue(geoRasterDst.isEmpty());
	}

        @Test
	public void testCropAll() throws Exception {
		final Envelope rasterEnvelope = geoRasterSrc.getMetadata()
				.getEnvelope();
		final Rectangle2D cropRectangle = new Rectangle2D.Double(rasterEnvelope
				.getMinX(), rasterEnvelope.getMinY(),
				rasterEnvelope.getWidth(), rasterEnvelope.getHeight());
		geoRasterDst = geoRasterSrc.doOperation(new Crop(cropRectangle));

		assertTrue(geoRasterDst.getWidth() > 0);
		assertTrue(geoRasterDst.getHeight() > 0);

		RasterMetadata dstMetadata = geoRasterDst.getMetadata();
		RasterMetadata srcMetadata = geoRasterSrc.getMetadata();
		assertTrue(dstMetadata.equals(srcMetadata));
	}

        @Test
	public void testCropRectangle() throws Exception {
		final Envelope rasterEnvelope = geoRasterSrc.getMetadata()
				.getEnvelope();
		geoRasterSrc.save(tmpData + "1.tif");
		final int buffer = (int) (rasterEnvelope.getWidth() / 2.3);
		final Rectangle2D cropRectangle = new Rectangle2D.Double(rasterEnvelope
				.getMinX()
				+ buffer, rasterEnvelope.getMinY() + buffer, rasterEnvelope
				.getWidth()
				- 2 * buffer, rasterEnvelope.getHeight() - 2 * buffer);
		geoRasterDst = geoRasterSrc.doOperation(new Crop(cropRectangle));
		geoRasterDst.save(tmpData + "2.tif");

		assertTrue(geoRasterDst.getWidth() > 0);
		assertTrue(geoRasterDst.getHeight() > 0);

		final ImagePlus srcImagePlus = geoRasterSrc.getImagePlus();
		final ImagePlus dstImagePlus = geoRasterDst.getImagePlus();
		RasterMetadata dstMetadata = geoRasterDst.getMetadata();
		RasterMetadata srcMetadata = geoRasterSrc.getMetadata();
		assertTrue(dstMetadata.getEnvelope().getMinX() < cropRectangle
				.getMinX());
		assertTrue(dstMetadata.getEnvelope().getMinY() < cropRectangle
				.getMinY());
		assertTrue(dstMetadata.getEnvelope().getMaxX() > cropRectangle
				.getMaxX());
		assertTrue(dstMetadata.getEnvelope().getMaxY() > cropRectangle
				.getMaxY());
		assertTrue(dstMetadata.getEnvelope().getWidth() < srcMetadata
				.getEnvelope().getWidth());
		checkCrop(geoRasterDst.getMetadata().getEnvelope(), srcImagePlus,
				dstImagePlus);
	}

	private void checkCrop(final Envelope envelope,
			final ImagePlus srcPixelProvider, final ImagePlus dstPixelProvider)
			throws IOException {
		// check metadata
		final RasterMetadata dstMetadata = geoRasterDst.getMetadata();
		final float pixelSize_X = dstMetadata.getPixelSize_X();
		final float pixelSize_Y = dstMetadata.getPixelSize_Y();
		final float halfPixelSize_X = dstMetadata.getPixelSize_X() / 2;
		final float halfPixelSize_Y = Math.abs(dstMetadata.getPixelSize_Y()) / 2;
		final int ncols = dstMetadata.getNCols();
		final int nrows = dstMetadata.getNRows();
		final double xulcorner = dstMetadata.getXulcorner();
		final double yulcorner = dstMetadata.getYulcorner();

		assertTrue(pixelSize_X == geoRasterDst.getMetadata().getPixelSize_X());
		assertTrue(ncols * pixelSize_X == dstMetadata.getEnvelope().getWidth());
		assertTrue(xulcorner - (pixelSize_X / 2) + ncols * pixelSize_X == dstMetadata
				.getEnvelope().getMaxX());

		assertTrue(pixelSize_Y == geoRasterDst.getMetadata().getPixelSize_Y());
		assertTrue(nrows * Math.abs(pixelSize_Y) == dstMetadata.getEnvelope()
				.getHeight());
		assertTrue(yulcorner - (pixelSize_Y / 2) - nrows
				* Math.abs(pixelSize_Y) == dstMetadata.getEnvelope().getMinY());

		// check raster values
		for (double y = envelope.getMinY() + halfPixelSize_Y; y < envelope
				.getMaxY(); y = y + 1) {
			for (double x = envelope.getMinX() + halfPixelSize_X; x < envelope
					.getMaxX(); x = x + 1) {
				final Point2D srcPixel = geoRasterSrc
						.fromRealWorldToPixel(x, y);
				final Point2D dstPixel = geoRasterDst
						.fromRealWorldToPixel(x, y);
				final float srcPixelValue = srcPixelProvider.getProcessor()
						.getPixelValue((int) srcPixel.getX(),
								(int) srcPixel.getY());
				final float dstPixelValue = dstPixelProvider.getProcessor()
						.getPixelValue((int) dstPixel.getX(),
								(int) dstPixel.getY());
				if (Float.isNaN(srcPixelValue)) {
					assertTrue(Float.isNaN(dstPixelValue));
				} else {
					assertTrue("pixel[" + x + ", " + y + "]",
							srcPixelValue == dstPixelValue);
				}
			}
		}
	}

        @Test
	public void testCropLeHavre() throws Exception {
		String src = internalData + "geotif/littlelehavre.tif";
		GeoRaster geoRaster = GeoRasterFactory.createGeoRaster(src);
		geoRaster.open();
		RasterMetadata metadata = geoRaster.getMetadata();
		Crop crop = new Crop(new Rectangle2D.Double(metadata.getXulcorner(),
				metadata.getYulcorner(), 10, -110));
		// Just test it doesn't throw any exception
		geoRaster.doOperation(crop);
	}
}