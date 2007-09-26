package org.grap.processing.operation.manual;

import org.grap.model.GeoRaster;
import org.grap.processing.Operation;
import org.grap.processing.operation.FocalMean;

public class FocalMeanTest {
	public static void main(String[] args) {
		final String src = "../../datas2tests/grid/sample.asc";

		final GeoRaster geoRaster = new GeoRaster(src);
		geoRaster.open();
		final Operation focalMean = new FocalMean(7);
		final GeoRaster result = geoRaster.doOperation(focalMean);
		result.setLUT("fire");
		result.show();
	}
}