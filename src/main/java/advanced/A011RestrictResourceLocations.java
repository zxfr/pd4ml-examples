package advanced;

import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.pd4ml.PD4ML;

/**
 * Restricts where PD4ML loads stylesheets, images, web fonts and attachments from
 * (addAllowedResourceLocation(), PD4ML v4.2.0) - a safeguard for HTML from untrusted sources
 * against reading local files or reaching internal services.
 * <p>
 * The locations are URL prefixes; "*" matches within one path segment or host label, a local path
 * stands for its file: URL. A resource outside them is not loaded, nor is one whose path contains
 * "..". Inline data: URIs and classpath (java:) resources are always allowed.
 */
public class A011RestrictResourceLocations {

	public static void main(String[] args) throws IOException {
		PD4ML pd4ml = new PD4ML();
		pd4ml.addAllowedResourceLocation("https://pd4ml.com/");
		pd4ml.addAllowedResourceLocation("https://*.example.com/assets/, /opt/app/templates/");

		// as if submitted by a user: an allowed image, a local file and an internal address
		String userHtml = "<h1>User-supplied HTML</h1>"
				+ "<p>Allowed (pd4ml.com): <img src='https://pd4ml.com/i/logo.png' height='30'></p>"
				+ "<p>Blocked (local file): <img src='file:/etc/passwd'></p>"
				+ "<p>Blocked (internal service): <img src='http://169.254.169.254/latest/meta-data/'></p>"
				+ "<p>Blocked (path traversal): <img src='https://pd4ml.com/../secret.png'></p>"
				+ "<p>Always allowed (inline): <img src='data:image/svg+xml;base64," + Base64.getEncoder().encodeToString(
						"<svg xmlns='http://www.w3.org/2000/svg' width='20' height='20'><circle cx='10' cy='10' r='9' fill='green'/></svg>"
						.getBytes(StandardCharsets.UTF_8)) + "'></p>";
		pd4ml.readHTML(new ByteArrayInputStream(userHtml.getBytes(StandardCharsets.UTF_8)));

		File pdf = File.createTempFile("result", ".pdf");
		try (FileOutputStream fos = new FileOutputStream(pdf)) {
			pd4ml.writePDF(fos);
		}

		// open the just-generated PDF with a default PDF viewer
		if ( args.length == 0 ) {
			Desktop.getDesktop().open(pdf);
		}
	}
}
