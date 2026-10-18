package html;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import com.pd4ml.PD4ML;
import com.pd4ml.PageSize;

/**
 * Generates a three-column PDF on landscape pages (a glossary) with generateMulticolumn().
 * <p>
 * Available in PD4ML v4.0.24 and v4.1.2 and newer (PDF output).
 */
public class H009MulticolumnGlossary {
	public static void main( String[] args ) throws IOException {
		PD4ML pd4ml = new PD4ML();

		// landscape A4 pages
		pd4ml.setPageSize(PageSize.A4.rotate());

		// three columns per page, 6 mm gap between them
		pd4ml.generateMulticolumn(3, 6, true);

		// read and parse HTML
		pd4ml.readHTML(new URL("java:/html/H009.htm"));

		File pdf = File.createTempFile("result", ".pdf");
		try (FileOutputStream fos = new FileOutputStream(pdf)) {
			// render and write the result as PDF
			pd4ml.writePDF(fos);
		}

		// open the just-generated PDF with a default PDF viewer
		if ( args.length == 0 ) {
			Desktop.getDesktop().open(pdf);
		}
	}
}
