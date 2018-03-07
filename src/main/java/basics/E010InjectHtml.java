package basics;
import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.pd4ml.PD4ML;

/**
 * Generates PDF from a simple HTML string. Page format, margins etc are default. 
 * Inject HTML content to the source document
 */
public class E010InjectHtml {
    public static void main( String[] args ) throws IOException {

    	String html = "TEST<pd4ml:page.break><b>Hello, World!</b>";
    	PD4ML pd4ml = new PD4ML(null);

    	// insert some content just after the opening <body> tag:
    	pd4ml.injectHtml("Some new content to the top of the document", true);
    	
    	// insert some content before the closing </body> tag:
    	pd4ml.injectHtml("<p>Content to append", false);

    	// pd4ml.injectHtml() API calls must be before pd4ml.readHTML()
    	
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
