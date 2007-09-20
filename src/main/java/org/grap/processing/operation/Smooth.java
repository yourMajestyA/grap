package org.grap.processing.operation;

import ij.ImagePlus;

import org.grap.model.GeoRaster;
import org.grap.model.RasterMetadata;
import org.grap.processing.Operation;

public class Smooth implements Operation {
	private GeoRaster geoRaster;

	public Smooth(final GeoRaster geoRaster) {
		this.geoRaster = geoRaster;
	}

	public GeoRaster execute() {
		final ImagePlus imp = geoRaster.getImagePlus();
		final RasterMetadata rasterMetadata = geoRaster.getMetadata();
		imp.getProcessor().smooth();
		return new GeoRaster(imp, rasterMetadata);
	}

	public GeoRaster execute(final GeoRaster raster) {
		final ImagePlus imp = geoRaster.getImagePlus();
		final RasterMetadata rasterMetadata = geoRaster.getMetadata();
		imp.getProcessor().smooth();
		return new GeoRaster(imp, rasterMetadata);
	}
}