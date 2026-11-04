package html;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import com.pd4ml.Constants;
import com.pd4ml.PD4ML;

/**
 * Takes all page setup from CSS @page rules in the document (H011.htm): page size and margins,
 * a different first page, margin boxes with running page numbers, and a landscape named page for
 * a wide table. No page setup in code. The v42 layout engine (PD4ML v4.2.0) is needed for named
 * pages and margin boxes.
 */
public class H011PageRulesAndMarginBoxes {

	public static void main(String[] args) throws IOException {
		PD4ML pd4ml = new PD4ML();
		pd4ml.useHtmlRenderer(Constants.LayoutEngine.v42);

		pd4ml.readHTML(new URL("java:/html/H011.htm"));

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
