package advanced;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.pd4ml.Constants;
import com.pd4ml.CustomTag;
import com.pd4ml.PD4ML;
import com.pd4ml.fonts.FontCache;

/**
 * Generates PDF from a HTML string, contains a custom tag. 
 * The tag renderer is defined with PD4ML API.
 * 
 */
public class A008AddingCustomTagRenderer {
	
	/**
	 * Derive custom tag renderer implementation from a dummy custom tag.
	 */
	public static class StarTag extends CustomTag {
		
		@Override
		public CustomTag getInstance(String code, FontCache fontCache) {
			return new StarTag(code, fontCache);
		}
		
		public StarTag() {
		}
		
		public StarTag(String code, FontCache fontCache) {
			super(code, fontCache);
		}
		
		@Override
		public void paint(float x, float y, float containerWidth, float containerHeight, Graphics2D g) {
		    g.setColor(Color.red);
		    float min = Math.min(width, height); // width and height are implicitly set based on 
		    									 // the corresponding tag attribute values
	        float base = min * .5f;
	        y += min * .07f;
			GeneralPath path = new GeneralPath();
	        path.moveTo( x + base * 0.1f,  y + base * 0.65f);
	        path.lineTo( x + base * 1.9f,  y + base * 0.65f);
	        path.lineTo( x + base * 0.40,  y + base * 1.65f);
	        path.lineTo( x + base,         y + 0           );
	        path.lineTo( x + base * 1.60f, y + base * 1.65f);
	        path.closePath(); 
		    g.fill(path);
		}

		@Override
		public boolean isEmptyTag() {
			return true;
		}
	}
	
	public static void main( String[] args ) throws IOException {
    	PD4ML pd4ml = new PD4ML();
    	
    	String html = "TEST STAR [<star height=20 width=20 style='border: 1 solid blue'>]";
    	ByteArrayInputStream bais = new ByteArrayInputStream(html.getBytes());
    	pd4ml.addCustomTagHandler("star", new StarTag());
    	
    	// read and parse HTML
    	pd4ml.readHTML(bais);
    	
    	File pdf = File.createTempFile("result", ".pdf");
    	FileOutputStream fos = new FileOutputStream(pdf);
    	// render and write the result as PDF/A
    	pd4ml.writePDF(fos, Constants.PDFA);
    	
    	// open the just-generated PDF with a default PDF viewer
    	Desktop.getDesktop().open(pdf);
    }
}
