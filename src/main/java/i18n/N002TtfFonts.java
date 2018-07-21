package i18n;
import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.pd4ml.PD4ML;
import com.pd4ml.fonts.FontCache;

/**
 * Generates PDF from a simple HTML string. Page format, margins etc are default.
 */
public class N002TtfFonts {
	
	public final static String FONTS_DIR = "c:/windows/fonts";
	
    public static void main( String[] args ) throws IOException {
    	
    	// Index available fonts. As the indexing time/resource consuming, 
    	// it is a good idea to prepare the font mapping file in advance.
    	File index = File.createTempFile("pd4fonts", ".properties");
    	index.deleteOnExit();
		FontCache.generateFontPropertiesFile(FONTS_DIR, index.getAbsolutePath(), (short)0);
    	
		System.out.println("font indexing is done.");
		// The same can be done with a command line call: 
		// java -jar pd4ml.jar -configure.fonts <font.dir> [index.file.location] 
		
    	PD4ML pd4ml = new PD4ML();
    	pd4ml.useTTF(index.getAbsolutePath());
    	
    	String html = "TEST<pd4ml:page.break><b>Hello, World!</b>";
    	ByteArrayInputStream bais = new ByteArrayInputStream(html.getBytes());
    	// read and parse HTML
    	pd4ml.readHTML(bais);
    	
    	File pdf = File.createTempFile("result", ".pdf");
    	FileOutputStream fos = new FileOutputStream(pdf);
    	// render and write the result as PDF
    	pd4ml.writePDF(fos);
    	
    	// open the just-generated PDF with a default PDF viewer
		if ( args.length == 0 ) {
			Desktop.getDesktop().open(pdf);
		}
    }
}
