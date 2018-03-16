package html;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import com.pd4ml.PD4ML;

/**
 * Generates PDF from a simple HTML string. Page format, margins etc are default.
 */
public class H004ApplyPageBreaks {
    public static void main( String[] args ) throws IOException {
    	PD4ML pd4ml = new PD4ML();

       	pd4ml.setHtmlWidth(900); // render HTML in a virtual frame 900px wide
    	pd4ml.addStyle( 
    			"H3 {\n" + 
    			"  page-break-before: always;\n" + 
    			"}\n" + 
    			"H3:first-of-type {\n" + 
    			"  page-break-before: auto;\n" + 
    			"}", true);
    										   
    	// read and parse HTML
    	pd4ml.readHTML(new URL("java:/html/H001.htm"));
    	
    	File pdf = File.createTempFile("result", ".pdf");
    	FileOutputStream fos = new FileOutputStream(pdf);
    	
    	// render and write the result as PDF
    	pd4ml.writePDF(fos);
    	// alternatively or additionally: 
    	// pd4ml.writeRTF(rtfos, false);
    	// BufferedImage[] images = pd4ml.renderAsImages();
    	
    	// open the just-generated PDF with a default PDF viewer
    	Desktop.getDesktop().open(pdf);
    }
}
