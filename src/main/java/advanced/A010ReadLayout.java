package advanced;

import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.pd4ml.Constants;
import com.pd4ml.PD4ML;
import com.pd4ml.dom.Document;
import com.pd4ml.dom.Element;
import com.pd4ml.dom.Layout;
import com.pd4ml.dom.LayoutBox;

/**
 * Layout API (PD4ML v4.2.0, v42 engine): layout() lays the document out into pages as writePDF()
 * would, without writing it, and tells where each element is. Used here to
 * <ul>
 * <li>print the page count, a box position and computed styles,</li>
 * <li>keep a block from being split over two pages,</li>
 * <li>find where a signature field would go.</li>
 * </ul>
 * Positions are in PDF points from the top left corner of the page.
 */
public class A010ReadLayout {

	public static void main(String[] args) throws IOException {
		StringBuilder html = new StringBuilder("<!DOCTYPE html><html><body style='font-family: serif'>"
				+ "<h1>Terms of service</h1>");
		for (int i = 1; i <= 26; i++) {
			html.append("<p>").append(i).append(". The parties agree on this clause, which is long enough to take two lines")
					.append(" of the page at the default width.</p>");
		}
		html.append("<div id='payment' style='border: 1px solid #889; padding: 8px'>"
				+ "<h2>Payment details</h2><p>IBAN DE00 0000 0000 0000 0000 00</p><p>BIC EXAMPLEXXX</p>"
				+ "<p>Due within 14 days.</p></div>"
				+ "<div id='sign-here' style='height: 60px; width: 250px; margin-top: 20px; border-bottom: 1px solid black'></div>"
				+ "</body></html>");

		PD4ML pd4ml = new PD4ML();
		pd4ml.useHtmlRenderer(Constants.LayoutEngine.v42);
		pd4ml.readHTML(new ByteArrayInputStream(html.toString().getBytes(StandardCharsets.UTF_8)));
		Document doc = pd4ml.getDocument();

		Layout layout = pd4ml.layout();
		System.out.println(layout.getPageCount() + " pages, page 1 is "
				+ layout.getPageWidth(1) + " x " + layout.getPageHeight(1) + " pt");
		for (Element h : doc.querySelectorAll("h1, h2")) {
			System.out.println(h.getTextContent() + ": font-size " + layout.getComputedStyle(h, "font-size")
					+ ", on page(s) " + layout.getPages(h));
		}

		// keep the payment details on one page
		Element payment = doc.getElementById("payment");
		System.out.println("Payment details on page(s) " + layout.getPages(payment));
		if (layout.getPages(payment).size() > 1) {
			payment.setAttribute("style", payment.getAttribute("style") + "; page-break-before: always");
			layout = pd4ml.layout();   // a layout is a snapshot: lay out again after a change
			System.out.println("moved to page(s) " + layout.getPages(payment));
		}

		// where a PDF tool should place a signature field after writing
		LayoutBox box = layout.getBoxes(doc.getElementById("sign-here")).get(0);
		System.out.println("Signature field: " + box);

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
