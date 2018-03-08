package advanced;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import com.pd4ml.PD4ML;

/**
 * Generates PDF from a simple HTML string. Page format, margins etc are default.
 */
public class A003ReadHtmlFromClasspath {
	
    public static void main( String[] args ) throws IOException {

    	PD4ML pd4ml = new PD4ML();
    			
    	// read and parse HTML
    	pd4ml.readHTML(new URL("java:/advanced/A003.htm"));

    	/* If you need to handle "java:" URLs in your application, run once the following code
    	 * e.g. in "static { }" section
    	 
    	URL.setURLStreamHandlerFactory(new URLStreamHandlerFactory() {
    	    public URLStreamHandler createURLStreamHandler(String protocol) {
    	        return "java".equals(protocol) ? new URLStreamHandler() {
    	            protected URLConnection openConnection(URL url) throws IOException {
    	                return new URLConnection(url) {
    	                    public void connect() throws IOException {
    	                    }
    	                };
    	            }
    	        } : null;
    	    }
    	});
    	 */
    	
    	
    	File pdf = File.createTempFile("result", ".pdf");
    	FileOutputStream fos = new FileOutputStream(pdf);
    	// render and write the result as PDF
    	pd4ml.writePDF(fos);
    	
    	// open the just-generated PDF with a default PDF viewer
    	Desktop.getDesktop().open(pdf);
    }
}
