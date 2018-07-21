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
 * Merges two PDF files. In the example it merges with itself and protects with password. 
 */
public class P005UnderlayOverlay {
    public static void main( String[] args ) throws IOException {

    	new PD4ML(); // we instantiate it here only to register "java:" protocol
    	
		URL pdfUrl = new URL("java:/pdftools/PDFOpenParameters.pdf");
		PdfDocument doc1 = new PdfDocument(pdfUrl, null);
		PdfDocument doc2 = new PdfDocument(pdfUrl, null);
				              
		// overlay request to place doc2 content over doc1   
		// "1" limits to use only the first page of doc2 as an overlay content  
		// "2+" specifies to apply the overlay to the second and all subsequent pages  
		// "128" is opacity of overlay (doc2) content, which corresponds ~50%  
		doc1.overlay(doc2, "1", "2+", 128);  
		// doc1.underlay(doc2, "1", "2+", 128);  

		File f = File.createTempFile("pdf", ".pdf");

		// writing the overlay result as a new PDF document   
		FileOutputStream fos = new FileOutputStream(f);  
		doc1.write(fos);  
		
    	// open the just-generated PDF with a default PDF viewer
		if ( args.length == 0 ) {
			Desktop.getDesktop().open(f);
		}
    }
}
