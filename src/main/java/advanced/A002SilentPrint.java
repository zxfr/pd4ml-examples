package advanced;
import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.pd4ml.PD4ML;

/**
 * Generates PDF from a simple HTML string. Page format, margins etc are default.
 * After the resulting PDF is loaded to a viewer, it should start printing on a default device
 */
public class A002SilentPrint {
    public static void main( String[] args ) throws IOException {
    	PD4ML pd4ml = new PD4ML();
    			
    	pd4ml.addDocumentActionHandler("silentprint", null);
		// similarly:
		// pd4ml.addDocumentActionHandler("OpenAction", "this.print({bUI: false, bSilent: true});");

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
