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
public class E003SetPageHeaderFooter {
    public static void main( String[] args ) throws IOException {

    	String html = 
    			"<html>"
    			+ "<head>"
    			+ "<title>Header/Footer example</title>"
    			+ "<style>BODY {font-family: Arial}</style>"
    			+ "</head>"
    			+ "<body>First Page<pd4ml:page.break>Second Page";
    	
    	PD4ML pd4ml = new PD4ML();

    	// define page header for the first page 
    	pd4ml.setPageHeader("$[title]", 30, "1");
    	// define page footer for the first page 
    	pd4ml.setPageFooter("Total pages: $[total]", 30, "1");

    	// define page header for the second page 
    	pd4ml.setPageHeader("<b>$[title]</b> $[page]/$[total]", 30, "2+");
    	// define page footer for the second page 
    	pd4ml.setPageFooter("<div style='width: 100%; text-align: right'>Page: $[page]</div>", 30, "2+");
    	
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
