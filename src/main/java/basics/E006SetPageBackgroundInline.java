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
public class E006SetPageBackgroundInline {
    public static void main( String[] args ) throws IOException {

    	String html = 
    			"<html>"
    			+ "<head>"
    			+ "<title>Page background example</title>"
    			+ "<style>BODY {font-family: Arial}</style>"
    			+ "</head>"
    			+ "<body>"
    			+ "<pd4ml:page.background><div style='width: 100%; height: 100%; background-color: rgb(228,255,228);'></div></pd4ml:page.background>"
    			+ "First Page"
    			+ "<pd4ml:page.break>"
    			// override the previously defined background with a new one starting from the current page
    			// A similar can be achieved if you place the page background definition to the top of the 
    			// doc and set scope='2+' attribute
    			//
    			// Note: the style is applied to <pd4ml:page.background> tag in the case 
    			+ "<pd4ml:page.background style='width: 100%; height: 100%; background-color: rgb(255,228,228);'></pd4ml:page.background>"
    			+ "Second Page";
    	
    	PD4ML pd4ml = new PD4ML(null);

    	ByteArrayInputStream bais = new ByteArrayInputStream(html.getBytes());
    	pd4ml.readHTML(bais);
    	
    	File pdf = File.createTempFile("result", ".pdf");
    	FileOutputStream fos = new FileOutputStream(pdf);
    	pd4ml.writePDF(fos);
    	
    	// open the just-generated PDF with a default PDF viewer
    	Desktop.getDesktop().open(pdf);
    }
}
