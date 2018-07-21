package pdftools;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import com.pd4ml.PD4ML;
import com.pd4ml.PdfDocument;

/**
 * Generates PDF from a simple HTML string. Page format, margins etc are default.
 * Merges two PDF files. In the example it merges with itself. 
 */
public class P002MergeTwoPDFs {
    public static void main( String[] args ) throws IOException {

    	// The simplest way, but not convenient for current "java:"-protocol-addressed resources:
    	// PdfDocument.mergePDFs(new FileInputStream(args[0]), new FileInputStream(args[1]), new FileOutputStream(args[2]));  

    	new PD4ML(); // we instantiate it here only to register "java:" protocol
    	
		URL pdfUrl = new URL("java:/pdftools/PDFOpenParameters.pdf");
		PdfDocument pdf1 = new PdfDocument(pdfUrl, null);
		PdfDocument pdf2 = new PdfDocument(pdfUrl, null);
		
		File f = File.createTempFile("pdf", ".pdf");
		
		pdf1.append(pdf2);
		pdf1.write(new FileOutputStream(f));

    	// open the just-generated PDF with a default PDF viewer
		if ( args.length == 0 ) {
			Desktop.getDesktop().open(f);
		}
    }
}
