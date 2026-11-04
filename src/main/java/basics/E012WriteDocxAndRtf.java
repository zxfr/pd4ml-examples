package basics;
import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.pd4ml.Constants;
import com.pd4ml.PD4ML;

/**
 * Writes one parsed document as PDF, DOCX and RTF. With the v42 engine (PD4ML v4.2.0) the Word
 * outputs are written from the same layout as the PDF - same lines, page breaks and spacing - as
 * editable paragraphs and tables, with positioned boxes as text boxes.
 */
public class E012WriteDocxAndRtf {
    public static void main( String[] args ) throws IOException {
    	PD4ML pd4ml = new PD4ML();
    	pd4ml.useHtmlRenderer(Constants.LayoutEngine.v42);

    	String html = "<!DOCTYPE html><html><body style='font-family: serif'>"
    			+ "<h1>Quarterly report</h1>"
    			+ "<p>One HTML source, three output formats.</p>"
    			+ "<table border='1' style='border-collapse: collapse; width: 100%'>"
    			+ "<tr style='background: linear-gradient(#cde, #fff)'><th>Quarter</th><th>Revenue</th></tr>"
    			+ "<tr><td>Q1</td><td style='text-align: right'>1 200</td></tr>"
    			+ "<tr><td>Q2</td><td style='text-align: right'>1 450</td></tr>"
    			+ "</table></body></html>";
    	// parse once
    	pd4ml.readHTML(new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8)));

    	// the Word files go to their own directory (RunAllExamples collects new PDFs of the temp directory)
    	File dir = new File(System.getProperty("java.io.tmpdir"), "pd4ml-examples");
    	dir.mkdirs();
    	File docx = new File(dir, "E012.docx");
    	try (FileOutputStream fos = new FileOutputStream(docx)) {
    		pd4ml.writeDOCX(fos);
    	}
    	File rtf = new File(dir, "E012.rtf");
    	try (FileOutputStream fos = new FileOutputStream(rtf)) {
    		pd4ml.writeRTF(fos, false);
    	}
    	File pdf = File.createTempFile("result", ".pdf");
    	try (FileOutputStream fos = new FileOutputStream(pdf)) {
    		pd4ml.writePDF(fos);
    	}
    	System.out.println("DOCX: " + docx.getAbsolutePath() + "\nRTF:  " + rtf.getAbsolutePath());

    	// open the just-generated documents with their default viewers
		if ( args.length == 0 ) {
			Desktop.getDesktop().open(pdf);
			Desktop.getDesktop().open(docx);
		}
    }
}
