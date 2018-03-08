package basics;
import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.pd4ml.PD4ML;
import com.pd4ml.PageMargins;
import com.pd4ml.PageSize;

/**
 * Generates PDF from a simple HTML string. Overrides default page format, margins etc.
 */
public class E002SetPageFormat {
    public static void main( String[] args ) throws IOException {
    	
    	String html = "TEST<pd4ml:page.break><b>Hello, World!</b><pd4ml:page.break>Third Page";

    	PD4ML pd4ml = new PD4ML();

    	// define page format for the first page 
    	pd4ml.setPageSize(PageSize.A5, "1");

    	// define landscape page format for the second and following pages 
    	pd4ml.setPageSize(PageSize.A4.rotate(), "2+");

    	// reset page margins for the first two pages
    	pd4ml.setPageMargins(new PageMargins(0, 0, 0, 0), "1-2");
    	
    	// set page margins for the third and following (if any) pages
    	pd4ml.setPageMargins(new PageMargins(0, 0, 0, 0), "3+");
    	
    	ByteArrayInputStream bais = new ByteArrayInputStream(html.getBytes());
    	pd4ml.readHTML(bais);
    	
    	File pdf = File.createTempFile("result", ".pdf");
    	FileOutputStream fos = new FileOutputStream(pdf);
    	pd4ml.writePDF(fos);
    	
    	// open the just-generated PDF with a default PDF viewer
    	Desktop.getDesktop().open(pdf);
    }
}
