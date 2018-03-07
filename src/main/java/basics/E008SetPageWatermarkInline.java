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
public class E008SetPageWatermarkInline {
    public static void main( String[] args ) throws IOException {

    	String html = 
    			"<html>"
    			+ "<head>"
    			+ "<title>Watermarking example</title>"
    			+ "<style>BODY {font-family: Arial}</style>"
    			+ "</head>"
    			+ "<body>"
    			+ "<pd4ml:watermark style=\"opacity: 30%; left: 20px; top: 0; scale: 900%; angle: 30deg; media: screen, print;\" scope=\"1\">"
    			+ "<b>WATERMARK</b></pd4ml:watermark>"
    			+ "<pd4ml:watermark style=\"opacity: 30%; left: 20px; top: 0; scale: 900%; angle: 30deg; media: screen, print;\" scope=\"2+\">"
    			+ "<b style='color: tomato'>WATERMARK</b></pd4ml:watermark>"
    			+ "First Page"
    			+ "<pd4ml:page.break>"
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
