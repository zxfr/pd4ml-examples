package advanced;
import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.pd4ml.Constants;
import com.pd4ml.PD4ML;
import com.pd4ml.StatusMessage;

/**
 * Generates PDF from a simple HTML string. Page format, margins etc are default.
 * 
 */
public class A007RenderingStatusInfo {
	public static void main( String[] args ) throws IOException {
    	PD4ML pd4ml = new PD4ML();
    	
    	String html = "TEST<pd4ml:page.break><b>Hello, World!</b>";
    	ByteArrayInputStream bais = new ByteArrayInputStream(html.getBytes());
    	// read and parse HTML
    	pd4ml.readHTML(bais);
    	
    	File pdf = File.createTempFile("result", ".pdf");
    	FileOutputStream fos = new FileOutputStream(pdf);
    	// render and write the result as PDF/A
    	pd4ml.writePDF(fos, Constants.PDFA);

    	System.out.println("pages: " + (Long)pd4ml.getLastRenderInfo(Constants.PD4ML_TOTAL_PAGES));  
    	  
    	// reports actual HTML document layout height in pixels   
    	// (as a rule the value depends on htmlWidth conversion parameter)  
    	System.out.println("height: " + (Long)pd4ml.getLastRenderInfo(Constants.PD4ML_DOCUMENT_HEIGHT_PX));  
    	  
    	// reports default width of the HTML document layout in pixels.   
    	// If the document has root-level elements with width="100%",   
    	// the returned value is almost always going to be equal htmlWidth parameter.  
    	// If the returned value is smaller htmlWidth, probably it is optimal htmlWidth for the given document.  
    	System.out.println("right edge: " + (Long)pd4ml.getLastRenderInfo(Constants.PD4ML_RIGHT_EDGE_PX));  

    	StatusMessage[] msgs =   
    	        (StatusMessage[])pd4ml.getLastRenderInfo(Constants.PD4ML_PDFA_STATUS);  
    	  
    	for ( int i = 0; i < msgs.length; i++ ) {  
    	    System.out.println( (msgs[i].isError() ? "ERROR: " : "WARNING: ") + msgs[i].getMessage());  
    	}      	
    	
    	// open the just-generated PDF with a default PDF viewer
    	Desktop.getDesktop().open(pdf);
    }
}
