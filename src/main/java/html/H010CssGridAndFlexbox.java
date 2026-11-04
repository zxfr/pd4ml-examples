package html;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import com.pd4ml.Constants;
import com.pd4ml.PD4ML;

/**
 * Converts a dashboard laid out with CSS Grid (named template areas) and Flexbox (H010.htm).
 * Both need the v42 layout engine (PD4ML v4.2.0).
 */
public class H010CssGridAndFlexbox {

	public static void main(String[] args) throws IOException {
		PD4ML pd4ml = new PD4ML();
		pd4ml.useHtmlRenderer(Constants.LayoutEngine.v42, 900);

		pd4ml.readHTML(new URL("java:/html/H010.htm"));

		File pdf = File.createTempFile("result", ".pdf");
		try (FileOutputStream fos = new FileOutputStream(pdf)) {
			pd4ml.writePDF(fos);
		}

		// open the just-generated PDF with a default PDF viewer
		if ( args.length == 0 ) {
			Desktop.getDesktop().open(pdf);
		}
	}
}
