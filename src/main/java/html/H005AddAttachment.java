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
public class H005AddAttachment {
    public static void main( String[] args ) throws IOException {
    	PD4ML pd4ml = new PD4ML();

       	pd4ml.setHtmlWidth(900); // render HTML in a virtual frame 900px wide
       	
       	// with the below code we embed the document source as an attachment to the resulting PDF
       	// The attachment icon will appear on the top (right side) of the document layout
    	pd4ml.injectHtml("<div style=\"text-align: right; width: 100%\">"
    			+ "<pd4ml:attachment style=\"align: right\" type=\"paperclip\" src=\"H001.htm\"/>"
    			+ "</div>", true);
    										   
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
