package advanced;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;

import com.pd4ml.Constants;
import com.pd4ml.PD4ML;
import com.pd4ml.dom.Document;
import com.pd4ml.dom.Element;

/**
 * DOM API (PD4ML v4.2.0, v42 engine): reads and changes the parsed HTML between readHTML() and
 * the output - CSS selector queries, text, attributes and classes, inner HTML, new nodes and an
 * added style sheet. The template A009.htm is filled in and cleaned up from code.
 */
public class A009ModifyDocumentWithDom {

	public static void main(String[] args) throws IOException {
		PD4ML pd4ml = new PD4ML();
		pd4ml.useHtmlRenderer(Constants.LayoutEngine.v42);
		pd4ml.readHTML(new URL("java:/advanced/A009.htm"));

		Document doc = pd4ml.getDocument();
		System.out.println("Title: " + doc.getTitle());

		// text
		doc.getElementById("number").setTextContent("2026-0117");
		doc.getElementById("date").setTextContent(LocalDate.now().toString());
		doc.getElementById("customer").setTextContent("Example Customer GmbH");

		// replace the sample rows: setInnerHTML() parses table rows in the context of <tbody>
		Element rows = doc.querySelector("table.items tbody");
		rows.setInnerHTML("");
		String[][] items = { { "Consulting", "8 h", "960.00" }, { "Travel", "1", "120.00" }, { "Report", "1", "340.00" } };
		for (String[] item : items) {
			Element tr = doc.createElement("tr");
			for (String value : item) {
				Element td = doc.createElement("td");
				td.setTextContent(value);   // text, not HTML: "<" stays a character
				tr.appendChild(td);
			}
			rows.appendChild(tr);
		}
		doc.getElementById("total").setTextContent("1 420.00");

		// attributes and classes, selected with CSS selectors
		for (Element td : doc.querySelectorAll("table.items td:last-child")) {
			td.addClass("amount");
		}
		for (Element img : doc.querySelectorAll("img:not([alt])")) {
			img.setAttribute("alt", "");
		}

		// remove what the final document must not show
		for (Element e : doc.querySelectorAll(".draft-only")) {
			e.remove();
		}

		// a new paragraph before the table, and a style sheet with an @page rule
		Element note = doc.createElement("p");
		note.setClassName("note");
		note.setTextContent("Prices include VAT.");
		doc.getBody().insertBefore(note, doc.querySelector("table.items"));

		Element style = doc.createElement("style");
		style.setTextContent(".amount { text-align: right } .note { color: #667; font-style: italic }"
				+ "@page { margin: 20mm; @bottom-right { content: 'Page ' counter(page) ' of ' counter(pages) } }");
		doc.getHead().appendChild(style);

		// the document as it is converted, e.g. to archive or log it
		System.out.println(doc.querySelector("table.items").getOuterHTML());

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
