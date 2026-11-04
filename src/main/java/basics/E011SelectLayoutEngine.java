package basics;
import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.pd4ml.Constants;
import com.pd4ml.PD4ML;

/**
 * Selects the v42 layout engine (PD4ML v4.2.0) and its settings. v40 stays the default engine;
 * v42 adds Chrome-verified layout with Flexbox, CSS Grid, @page margin boxes and the DOM API.
 * The extra settings lay the document out for the "print" media type, in dark mode.
 */
public class E011SelectLayoutEngine {
    public static void main( String[] args ) throws IOException {
    	PD4ML pd4ml = new PD4ML();

    	Map<String, String> settings = new HashMap<>();
    	settings.put("media", "print");     // match @media print rules (default: "screen")
    	settings.put("dark", "true");       // match @media (prefers-color-scheme: dark)
    	// select the engine and the HTML width ("virtual browser window") in one call
    	pd4ml.useHtmlRenderer(Constants.LayoutEngine.v42, 800, settings);

    	String html = "<!DOCTYPE html><html><head><style>"
    			+ "body { font-family: sans-serif; }"
    			+ ".cards { display: flex; gap: 12px; }"
    			+ ".card { flex: 1; padding: 12px; border-radius: 8px; background: #eef; }"
    			+ "@media print { h1 { color: #036; } }"
    			+ "@media screen { h1 { color: red; } }"
    			+ "@media (prefers-color-scheme: dark) { .card { background: #224; color: white; } }"
    			+ "</style></head><body>"
    			+ "<h1>The v42 layout engine</h1>"
    			+ "<div class='cards'><div class='card'>Flexbox</div><div class='card'>CSS Grid</div>"
    			+ "<div class='card'>@page margin boxes</div></div>"
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
