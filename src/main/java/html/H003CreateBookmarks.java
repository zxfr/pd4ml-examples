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
public class H003CreateBookmarks {
    public static void main( String[] args ) throws IOException {
    	PD4ML pd4ml = new PD4ML();

       	pd4ml.setHtmlWidth(900); // render HTML in a virtual frame 900px wide
    	pd4ml.addStyle( 
    			"@font-face {\r\n" + 
    			"  font-family: \"Arial\";\r\n" + 
    			"  src: url(\"java:/html/rc/LibreFranklin-Regular.ttf\") format(\"ttf\"),\r\n" + 
    			"}" +
    			"@font-face {\r\n" + 
    			"  font-family: \"Consolas\";\r\n" + 
    			"  src: url(\"java:/html/rc/FiraMono-Regular.ttf\") format(\"ttf\"),\r\n" + 
    			"}", false);

    	pd4ml.generateBookmarksFromHeadings(true); 
    										   
    	// read and parse HTML
    	pd4ml.readHTML(new URL("java:/html/H001.htm"));
    	
    	File pdf = File.createTempFile("result", ".pdf");
    	FileOutputStream fos = new FileOutputStream(pdf);
    	
    	// render and write the result as PDF
    	pd4ml.writePDF(fos);
    	
    	// open the just-generated PDF with a default PDF viewer
    	Desktop.getDesktop().open(pdf);
    }
}
