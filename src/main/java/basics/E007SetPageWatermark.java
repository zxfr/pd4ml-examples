package basics;
import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.pd4ml.PD4ML;

/**
 * Generates PDF from a simple HTML string. Defines page-specific watermarks.
 */
public class E007SetPageWatermark {
    public static void main( String[] args ) throws IOException {

    	String html = 
    			"<html>"
    			+ "<head>"
    			+ "<title>Watermarking example</title>"
    			+ "<style>BODY {font-family: Arial}</style>"
    			+ "</head>"
    			+ "<body>First Page<pd4ml:page.break>Second Page";
    	
    	PD4ML pd4ml = new PD4ML();

    	// define watermark for the first page 
    	pd4ml.setWatermark("<b>WATERMARK</b>", 
    			20,   // offset X 
    			0,    // offset Y
    			.3f,  // opacity
    			30,   // angle
    			9,    // scale (1 = 100%)
    			true, // should the watermark be visible in PDF viewers?
    			true, // should the watermark be printed?
    			"1"); // page range to apply

    	// define watermark for the second and following pages 
    	pd4ml.setWatermark("<b style='color: tomato'>WATERMARK</b>", 20, 0, .3f, 30, 9, true, true, "2+");
    	
    	// the similar watermarking can be also achieved with <pd4ml:watermark> tag

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
