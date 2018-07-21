package basics;
import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.pd4ml.PD4ML;

/**
 * Generates PDF from a simple HTML string. Defines page-specific headers and footers.
 */
public class E005SetPageBackground {
    public static void main( String[] args ) throws IOException {

    	String html = 
    			"<html>"
    			+ "<head>"
    			+ "<title>Page background example</title>"
    			+ "<style>BODY {font-family: Arial}</style>"
    			+ "</head>"
    			+ "<body>First Page<pd4ml:page.break>Second Page";
    	
    	PD4ML pd4ml = new PD4ML();

    	// define page header for the first page 
    	pd4ml.setPageBackground("<div style='width: 100%; height: 100%; background-color: rgb(228,255,228);'></div>", "1");
    	// define page footer for the first page 
    	pd4ml.setPageBackground("<div style='width: 100%; height: 100%; background-color: rgb(255,228,228);'></div>", "2+");
    	
    	ByteArrayInputStream bais = new ByteArrayInputStream(html.getBytes());
    	pd4ml.readHTML(bais);
    	
    	File pdf = File.createTempFile("result", ".pdf");
    	FileOutputStream fos = new FileOutputStream(pdf);
    	pd4ml.writePDF(fos);
    	
    	// open the just-generated PDF with a default PDF viewer
		if ( args.length == 0 ) {
			Desktop.getDesktop().open(pdf);
		}
    }
}
