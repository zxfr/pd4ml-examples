package advanced;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

import com.pd4ml.PD4ML;

/**
 * Generates PDF from a simple HTML string. Page format, margins etc are
 * default.
 */
public class A005AddCustomResourceLoader {

	public static void main(String[] args) throws IOException {

		// register new URL protocol to avoid MalformedURLException
		URL.setURLStreamHandlerFactory(new URLStreamHandlerFactory() {
			public URLStreamHandler createURLStreamHandler(String protocol) {
				return DummyProvider.PROTOCOL.equals(protocol) ? new URLStreamHandler() {
					protected URLConnection openConnection(URL url) throws IOException {
						return new URLConnection(url) {
							public void connect() throws IOException {
							}
						};
					}
				} : null;
			}
		});

		PD4ML pd4ml = new PD4ML();

		pd4ml.addCustomResourceProvider("advanced.DummyProvider");

		// read and parse HTML, read by the custom protocol
		pd4ml.readHTML(new URL("dummy:my.exotic.protocol"));

		File pdf = File.createTempFile("result", ".pdf");
		FileOutputStream fos = new FileOutputStream(pdf);
		// render and write the result as PDF
		pd4ml.writePDF(fos);

		// open the just-generated PDF with a default PDF viewer
		Desktop.getDesktop().open(pdf);
	}
}
