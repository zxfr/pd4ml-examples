package standards;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import com.pd4ml.Constants.ErrorPolicy;
import com.pd4ml.PD4ML;
import com.pd4ml.PdfSpec;

/**
 * Generates PDF from a simple HTML string. Page format, margins etc are default.
 */
public class Z001ConvertHtml {
    public static void main( String[] args ) throws IOException {
    	PD4ML pd4ml = new PD4ML();

       	pd4ml.setHtmlWidth(900); // render HTML in a virtual frame 900px wide
    	pd4ml.addStyle( 
    					// specify TTF font file for "Consolas" font face (only "plain" style, in the case). 
    			
    					// Here we use free FiraMono-Regular instead of the original Consolas.
    					// Other font faces to be mapped to PDF viewer standard built-in fonts.
    			
    					// In the resulting PDF you can see '?' symbols instead of some character glyphs.
    					// That means the missing glyphs are not defined by any of the available fonts.
    			
    					// As a workaround create a font dir, place a set of fonts there to cover the 
    					// desired language or character range, index fonts and refer to the dir 
    					// with pd4ml.useTTF() API call. Optionally the font dir can be packed to
    					// a fonts.jar
    			"@font-face {\n" + 
    			"  font-family: \"Arial\";\n" + 
    			"  src: url(\"https://pd4ml.com/f/FiraMono-Regular.ttf\") format(\"truetype\"),\n" + 
    			"}\n", false);

    	pd4ml.setErrorPolicy(ErrorPolicy.NORMAL);
    	
    	// read and parse HTML
    	pd4ml.readHTML(new URL("file:src/main/java/standards/Z001.htm"));
    	
    	File pdf = File.createTempFile("result", ".pdf");
    	FileOutputStream fos = new FileOutputStream(pdf);
    	
    	// render and write the result as PDF
    	pd4ml.writePDF(fos, PdfSpec.ZUGFeRD);
    	
    	// open the just-generated PDF with a default PDF viewer
		if ( args.length == 0 ) {
			Desktop.getDesktop().open(pdf);
		}
    }
}
