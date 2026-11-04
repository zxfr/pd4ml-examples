package html;

import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.pd4ml.PD4ML;

/**
 * CSS gradient backgrounds (PD4ML v4.2.0, both layout engines): linear, radial, conic and
 * repeating gradients, with rounded corners and transparency. They are written to the PDF as
 * vector shadings, so they stay sharp at any zoom.
 */
public class H012GradientBackgrounds {

	public static void main(String[] args) throws IOException {
		PD4ML pd4ml = new PD4ML();

		String html = "<!DOCTYPE html><html><head><style>"
				+ "body { font-family: sans-serif; }"
				+ ".box { width: 200px; height: 90px; margin: 10px; display: inline-block; color: white;"
				+ "  padding: 8px; border-radius: 12px; vertical-align: top; }"
				+ ".banner { height: 60px; border-radius: 8px; color: white; padding: 12px; font-size: 20px;"
				+ "  background: linear-gradient(to right, #0f2027, #2c5364); }"
				+ "</style></head><body>"
				+ "<div class='banner'>linear-gradient(to right, ...)</div>"
				+ "<div class='box' style='background: linear-gradient(135deg, #f093fb, #f5576c)'>linear, 135deg</div>"
				+ "<div class='box' style='color: #335; background: radial-gradient(circle at 30% 30%, #fddb92, #d1fdff 40%, #6a82fb)'>radial</div>"
				+ "<div class='box' style='background: conic-gradient(red, yellow, lime, aqua, blue, magenta, red)'>conic</div>"
				+ "<div class='box' style='background: repeating-linear-gradient(45deg, #345 0 10px, #567 10px 20px)'>repeating</div>"
				+ "<div class='box' style='color: black; border: 3px dashed #2a9d8f;"
				+ "  background: linear-gradient(rgba(42,157,143,0.6), rgba(42,157,143,0))'>transparent, dashed border</div>"
				+ "<div class='box' style='color: black; background-color: #fff3d6;"
				+ "  background-image: radial-gradient(#ffd166 20%, transparent 21%); background-size: 24px 24px'>"
				+ "tiled like an image</div>"
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
