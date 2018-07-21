package pdftools;
import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import com.pd4ml.PD4ML;
import com.pd4ml.PdfDocument;

/**
 * Generates PDF from a simple HTML string. Page format, margins etc are default.
 * Appends selected pages from another PDF file 
 */
public class P001ConvertAndMergeWithPDF {
    public static void main( String[] args ) throws IOException {
		PD4ML pd4ml = new PD4ML(); // constructor implicitly registers "java:" protocol

		String html = "TEST<pd4ml:page.break><b>Hello, World!</b>";

		URL pdfUrl = new URL("java:/pdftools/PDFOpenParameters.pdf");
		PdfDocument pdf = new PdfDocument(pdfUrl, null);

		File f = File.createTempFile("result", ".pdf");

		pd4ml.setPageHeader("HEADER $[page] of $[total]", 40, "1+");
		
		// merge only with pages from 2 to 4. The pages will be appended to the converted PDF
		pd4ml.merge(pdf, 2, 4, true);
		
		pd4ml.readHTML(new ByteArrayInputStream(html.getBytes()));
		pd4ml.writePDF(new FileOutputStream(f));

		// now we can read the resulting PDF and count page number
		PdfDocument doc = new PdfDocument(new FileInputStream(f), null);
		System.out.println(doc.getNumberOfPages());

    	// open the just-generated PDF with a default PDF viewer
		if ( args.length == 0 ) {
			Desktop.getDesktop().open(f);
		}
    }
}
