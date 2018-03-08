package basics;
import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.pd4ml.Constants;
import com.pd4ml.PD4ML;

/**
 * Generates PDF from a simple HTML string. Page format, margins etc are default. 
 * Protects the resulting document with a password
 */
public class E009SetDocumentPassword {
    public static void main( String[] args ) throws IOException {

    	String html = "TEST<pd4ml:page.break><b>Hello, World!</b>";
    	PD4ML pd4ml = new PD4ML();
    	
    	ByteArrayInputStream bais = new ByteArrayInputStream(html.getBytes());
    	// read and parse HTML
    	pd4ml.readHTML(bais);
    	
    	// protect the document with "test" password. No permission restrictions applied
    	pd4ml.setPermissions("test", Constants.DefaultPermissions);
    	
    	File pdf = File.createTempFile("result", ".pdf");
    	FileOutputStream fos = new FileOutputStream(pdf);
    	// render and write the result as PDF
    	pd4ml.writePDF(fos);
    	
    	// open the just-generated PDF with a default PDF viewer
    	Desktop.getDesktop().open(pdf);
    }
}
