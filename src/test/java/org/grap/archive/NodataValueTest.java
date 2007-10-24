/*
 * The GRAP library (GeoRAster Processing) is a middleware dedicated
 * to the processing of various kinds of geographic raster data. It
 * provides a complete and robust API to manipulate ASCII Grid or
 * tiff, png, bmp, jpg (with the corresponding world file) geographic
 * images. GRAP is produced  by the geomatic team of the IRSTV Institute
 * <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
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
package org.grap.archive;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.plugin.TextReader;
import ij.process.ImageProcessor;

public class NodataValueTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// Opener opener = new Opener();
		// ImagePlus imp = opener.openImage(src );

		String src = "..//datas2tests//grid//ijsample.asc";

		TextReader textReader = new TextReader();
		ImageProcessor ip = textReader.open(src);

		System.out.println(ip.getMin());
		System.out.println(ip.getMax());

		// Cette option permet d'ajuster les valeurs affichées.

		// ip.setMinAndMax(0, 500);

		ip.setThreshold(0.0d, 500.0d, ImageProcessor.NO_LUT_UPDATE);

		// ip.setBackgroundValue(550d);
		ImagePlus imp = new ImagePlus("", ip);

		WindowManager.setTempCurrentImage(imp);
		IJ.run("NaN Background");

		imp.show();

		System.out.println(imp.getProcessor().getf(0, 0));

		int pixelx = 10;
		int pixely = 10;

		float v = ip.getPixelValue(pixelx, pixely);

		System.out.println(v);

	}

}