package advanced;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import com.pd4ml.PD4ML;

/**
 * Generates PDF from a simple HTML string. Page format, margins etc are
 * default.
 */
public class A006SubstitutePlaceholders {

	public static void main(String[] args) throws IOException {

		PD4ML pd4ml = new PD4ML();

    	HashMap<String, String> map = new HashMap<>();
    	map.put("var1", "value 1");
    	map.put("var2", "[value 2]");
    	map.put("var3", "* value 3 *");
    	map.put("my.variable", "Dynamically inserted text");
    	pd4ml.setDynamicData(map);

		// read and parse HTML, read by the custom protocol
    	pd4ml.readHTML(new URL("java:/advanced/A006.htm"));

		File pdf = File.createTempFile("result", ".pdf");
		FileOutputStream fos = new FileOutputStream(pdf);
		// render and write the result as PDF
		pd4ml.writePDF(fos);

		// open the just-generated PDF with a default PDF viewer
		Desktop.getDesktop().open(pdf);
	}
}
