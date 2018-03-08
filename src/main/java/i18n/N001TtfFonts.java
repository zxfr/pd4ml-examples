package i18n;
import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.pd4ml.PD4ML;

/**
 * Generates PDF from a simple HTML string. Page format, margins etc are default.
 */
public class N001TtfFonts {
	
	public final static String FONTS_DIR = "c:/windows/fonts";
	
    public static void main( String[] args ) throws IOException {
    	
    	PD4ML pd4ml = new PD4ML();
    	pd4ml.useTTF(FONTS_DIR, true); // The second parameter forces to index fonts in FONTS_DIR. 
    	// As the indexing of a font directory with a big number of fonts is time/resource consuming, 
    	// it is a good idea to prepare the font mapping file in advance.
    	// See the next example how to index.
    	
    	String html = "TEST<pd4ml:page.break><b>Hello, World!</b>";
    	ByteArrayInputStream bais = new ByteArrayInputStream(html.getBytes());
    	// read and parse HTML
    	pd4ml.readHTML(bais);
    	
    	File pdf = File.createTempFile("result", ".pdf");
    	FileOutputStream fos = new FileOutputStream(pdf);
    	// render and write the result as PDF
    	pd4ml.writePDF(fos);
    	
    	// open the just-generated PDF with a default PDF viewer
    	Desktop.getDesktop().open(pdf);
    }
}
