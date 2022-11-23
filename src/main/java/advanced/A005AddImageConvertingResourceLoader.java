package advanced;

import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.pd4ml.PD4ML;

/**
 * Generates PDF from a simple HTML string. Page format, margins etc are
 * default.
 */
public class A005AddImageConvertingResourceLoader {

	public static void main(String[] args) throws IOException {

		PD4ML pd4ml = new PD4ML();

		pd4ml.addCustomResourceProvider("advanced.ConvertedImageProvider");

		// read and parse HTML, which refers PNG resource via HTTPS
		pd4ml.readHTML(new ByteArrayInputStream(
				"<img src=\"https://pd4ml.com/images/illustration2.png\">".getBytes()));

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
