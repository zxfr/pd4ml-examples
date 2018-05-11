package html;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import com.pd4ml.PD4ML;

/**
 * Generates PDF from a simple HTML string. Page format, margins etc are default.
 * 
 * See /html/H006.htm for footnote definitions
 */
public class H007WriteDocumentPageInfo {
    public static void main( String[] args ) throws IOException {
    	PD4ML pd4ml = new PD4ML();

    	// read and parse HTML
    	pd4ml.readHTML(new URL("java:/html/H007.htm"));
    	
    	File pdf = File.createTempFile("result", ".pdf");
    	FileOutputStream fos = new FileOutputStream(pdf);
    	
    	// render and write the result as PDF
    	pd4ml.writePDF(fos);
    	// alternatively or additionally: 
    	// BufferedImage[] images = pd4ml.renderAsImages();
    	
    	// open the just-generated PDF with a default PDF viewer
    	Desktop.getDesktop().open(pdf);
    }
}
