package advanced;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import com.pd4ml.PD4ML;
import com.pd4ml.ProgressListener;

/**
 * Generates PDF from a simple HTML string. Page format, margins etc are default.
 * Dumps progress events to STDOUT
 */
public class A004AddProgressListener {

	public static class ProgressMeter implements ProgressListener {

		private long startTime = -1;
		
		/**
		 * callback method triggered by progress event. The implementation dumps the events to STDOUT.
		 * Alternatively it could control GUI progress bar etc. 
		 */
		public void progressUpdate(int messageID, int progress, String note, long msec) {
			
			if ( startTime < 0 ) {
				startTime = msec;
			}
			
			String tick = String.format( "%7d", msec - startTime );
			String progressString = String.format( "%3d", progress );

			String step = "";
			switch ( messageID ) {
				case CONVERSION_BEGIN:
					step = "conversion begin";
					break;
				case MAIN_DOC_READ:
					step = "doc read";
					break;
				case HTML_PARSED:
					step = "html parsed";
					break;
				case RENDERER_TREE_BUILT:
					step = "document tree structure built";
					break;
				case HTML_LAYOUT_IN_PROGRESS:
					step = "layouting...";
					break;
				case HTML_LAYOUT_DONE:
					step = "layout done";
					break;
				case PAGEBREAKS_ALIGNED:
					step = "pagebreaks aligned";
					break;
				case TOC_GENERATED:
					step = "TOC generated";
					break;
				case DOC_RENDER_IN_PROGRESS:
					step = "generating doc page";
					break;
				case RTF_PRE_RENDER_DONE:
					step = "RTF pre-render done";
					break;
				case DOC_WRITE_BEGIN:
					step = "writing doc...";
					break;
				case CONVERSION_END:
					step = "done.";
					break;
			}
			System.out.println( tick + " " + progressString + " " + step + " " + note );
		}
	}

    public static void main( String[] args ) throws IOException {

    	PD4ML pd4ml = new PD4ML();
    			
		pd4ml.monitorProgressWith(new ProgressMeter());

    	// read and parse HTML
    	pd4ml.readHTML(new URL("java:/advanced/A003.htm"));

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
