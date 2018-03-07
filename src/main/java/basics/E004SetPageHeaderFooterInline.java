package basics;
import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.pd4ml.PD4ML;

/**
 * Generates PDF from a simple HTML string. Defines page-specific headers and footers not with API call, 
 * but in HTML document inline.
 */
public class E004SetPageHeaderFooterInline {
    public static void main( String[] args ) throws IOException {

    	String html = 
    			"<html>"
    			+ "<head>"
    			+ "<title>Header/Footer example</title>"
    			+ "<style>BODY {font-family: Arial}</style>"
    			+ "</head>"
    			+ "<body>"
    			// inline definition of the header/footer for the current and all following pages
    			+ "<pd4ml:page.header height=30>$[title]</pd4ml:page.header>"
    			+ "<pd4ml:page.footer height=30>Total pages: $[total]</pd4ml:page.footer>"
    			+ "First Page"
    			+ "<pd4ml:page.break>"
    			// here it overrides the header/footer set above starting from the current page
    			+ "<pd4ml:page.header height=30><b>$[title]</b> $[page]/$[total]</pd4ml:page.header>"
    			+ "<pd4ml:page.footer height=30><div style='width: 100%; text-align: right'>Page: $[page]</div></pd4ml:page.footer>"
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
