package i18n;

import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.pd4ml.PD4ML;

/**
 * Color emoji (PD4ML v4.2.0, both layout engines). Emoji the text font lacks are drawn in color
 * from a color emoji font of the font set: COLR/CPAL (Twemoji Mozilla, Segoe UI Emoji) or CBDT
 * (Noto Color Emoji). Zero-width-joiner sequences, skin tones and flags stay single glyphs, and the
 * text stays selectable and searchable.
 * <p>
 * Where the emoji font comes from - either:
 * <ul>
 * <li>the optional pd4ml-emoji add-on (Twemoji Mozilla) on the class path - no setup; or</li>
 * <li>a directory holding a color emoji font, given with useTTF(), as below.</li>
 * </ul>
 * PD4ML never uses the host's fonts, so without either the emoji are not drawn.
 */
public class N003ColorEmoji {

	public static void main(String[] args) throws IOException {
		PD4ML pd4ml = new PD4ML();

		// a directory that holds, for example, NotoColorEmoji.ttf (not needed with pd4ml-emoji)
		String emojiFonts = System.getProperty("emoji.fonts.dir");
		if (emojiFonts != null) {
			pd4ml.useTTF(emojiFonts);
		}

		String html = "<!DOCTYPE html><html><body style='font-family: sans-serif; font-size: 20px'>"
				+ "<p>Weather: ☀️ ⛅ 🌧️ ❄️</p>"
				+ "<p>Skin tones: 👋 👋🏽 👋🏿</p>"
				+ "<p>A family (one ZWJ sequence): 👩‍👩‍👧</p>"
				+ "<p>Flags: 🇩🇪 🇫🇷</p>"
				+ "<p>Status: ✅ done ❌ failed ⚠️ warning</p>"
				+ "</body></html>";
		pd4ml.readHTML(new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8)));

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
