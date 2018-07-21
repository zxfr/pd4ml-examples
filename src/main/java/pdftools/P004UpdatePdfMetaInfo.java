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
public class P004UpdatePdfMetaInfo {
    public static void main( String[] args ) throws IOException {

    	new PD4ML(); // we instantiate it here only to register "java:" protocol
    	
		URL pdfUrl = new URL("java:/pdftools/PDFOpenParameters.pdf");
		PdfDocument doc = new PdfDocument(pdfUrl, null);
		
        System.out.println("document author: " + doc.getAuthor());  
          
        doc.setTitle("Document Modification Test");  
        doc.setSubject("PdfDocument API test");  
        doc.setKeywords("key1, key2");  
        doc.setModDate(); // set modification date to NOW  
  
		File f = File.createTempFile("pdf", ".pdf");

		doc.write(new FileOutputStream(f), null, -1); // no password, default permissions  

    	// open the just-generated PDF with a default PDF viewer
		if ( args.length == 0 ) {
			Desktop.getDesktop().open(f);
		}
    }
}
