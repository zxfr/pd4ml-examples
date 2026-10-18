package html;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import com.pd4ml.PD4ML;

/**
 * Generates a two-column PDF (a newsletter) with generateMulticolumn().
 * <p>
 * The document is laid out to the width of a column; every page break moves on to the next column,
 * after the last column a new page starts. &lt;pd4ml:page.break multicolumn="true"/&gt; starts a new
 * page right away. Page headers and footers span the page, page numbers count pages, footnotes go to
 * the bottom of their column.
 * <p>
 * Available in PD4ML v4.0.24 and v4.1.2 and newer (PDF output).
 */
public class H008MulticolumnNewsletter {
	public static void main( String[] args ) throws IOException {
		PD4ML pd4ml = new PD4ML();

		// two columns per page, 8 mm gap between them
		pd4ml.generateMulticolumn(2, 8, true);

		// read and parse HTML
		pd4ml.readHTML(new URL("java:/html/H008.htm"));

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
