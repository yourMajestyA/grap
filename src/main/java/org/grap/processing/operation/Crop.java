package org.grap.processing.operation;

import ij.ImagePlus;
import ij.gui.PolygonRoi;

import java.awt.Rectangle;

import org.grap.model.GeoRaster;
import org.grap.model.RasterMetadata;
import org.grap.processing.BasicOperation;
import org.grap.processing.Operation;
import org.grap.utilities.EnvelopeUtil;
import org.grap.utilities.JTSConverter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

public class Crop extends BasicOperation implements Operation {

	private Geometry geom;

	private Rectangle rectangle;

	public Crop() {
	}

	public Crop(GeoRaster geoRaster, Geometry geom) {
		this.geoRaster = geoRaster;
		this.geom = geom;
	}

	public Crop(GeoRaster geoRaster, Rectangle rectangle) {
		this.geoRaster = geoRaster;
		this.rectangle = rectangle;
	}

	public GeoRaster execute() {
		ImagePlus imp = geoRaster.getImagePlus();
		RasterMetadata rasterMetadata = geoRaster.getMetadata();
		ImagePlus impResult = null;
		RasterMetadata metadata = new RasterMetadata(0, 0, 0, 0, 0);

		if (geom != null) {
			if (geom instanceof Polygon) {
				Geometry geomEnvelope = EnvelopeUtil.toGeometry(rasterMetadata
						.getEnvelope());
				if (geomEnvelope.intersects(geom)) {
					PolygonRoi roi = JTSConverter.toPolygonRoi(geom);
					imp.setRoi(roi);
					impResult = new ImagePlus("", imp.getProcessor().crop());
					Envelope newEnvelope = JTSConverter.RoitoJTS(roi)
							.getEnvelopeInternal();
					metadata.setXOrigin(newEnvelope.getMinX());
					metadata.setYOrigin(newEnvelope.getMaxY());
					metadata.setPixelSize_X(rasterMetadata.getPixelSize_X());
					metadata.setPixelSize_Y(rasterMetadata.getPixelSize_Y());
					metadata.setXRotation(rasterMetadata.getRotation_X());
					metadata.setYRotation(rasterMetadata.getRotation_Y());

					metadata.setNCols(impResult.getWidth());
					metadata.setNRows(imp.getHeight());
				} else {
				}
			}
		}
		if (rectangle != null) {
			imp.setRoi(rectangle);
			impResult = new ImagePlus("", imp.getProcessor().crop());
			Envelope newEnvelope = new Envelope(rectangle.getMinX(), rectangle
					.getMaxX(), rectangle.getMinY(), rectangle.getMaxY());
			Coordinate coordinates = GeoRaster.pixelToWorldCoord(
					(int) newEnvelope.getMinX(), (int) newEnvelope.getMaxY());
			metadata.setXOrigin(coordinates.x);
			metadata.setYOrigin(coordinates.y);
			metadata.setPixelSize_X(rasterMetadata.getPixelSize_X());
			metadata.setPixelSize_Y(rasterMetadata.getPixelSize_Y());
			metadata.setXRotation(rasterMetadata.getRotation_X());
			metadata.setYRotation(rasterMetadata.getRotation_Y());
			metadata.setNCols(impResult.getWidth());
			metadata.setNRows(imp.getHeight());
		}
		return new GeoRaster(impResult, metadata);
	}
}