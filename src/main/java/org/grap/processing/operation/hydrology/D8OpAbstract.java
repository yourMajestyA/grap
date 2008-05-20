package org.grap.processing.operation.hydrology;

import ij.ImagePlus;

import java.io.IOException;

import org.grap.model.GeoRaster;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.orbisgis.progress.IProgressMonitor;

public abstract class D8OpAbstract implements Operation {
	public final GeoRaster execute(final GeoRaster geoRaster, IProgressMonitor pm)
			throws OperationException {
		final long startTime = System.currentTimeMillis();
		try {
			if (ImagePlus.COLOR_RGB == geoRaster.getType()) {
				throw new OperationException(
						"D8Operation only handle a GRAY{8, 16 or 32} or a COLOR_256 GeoRaster image !");
			}
			geoRaster.open();
		} catch (IOException e) {
			throw new OperationException(e);
		}
		GeoRaster result = evaluateResult(geoRaster);

		System.out.printf("D8Operation in %d ms\n", System.currentTimeMillis()
				- startTime);
		return result;
	}

	public abstract GeoRaster evaluateResult(GeoRaster geoRaster)
			throws OperationException;
}