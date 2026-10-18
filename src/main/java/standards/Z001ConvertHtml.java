package standards;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.mustangproject.validator.ZUGFeRDValidator;

import com.pd4ml.Constants.ErrorPolicy;
import com.pd4ml.PD4ML;
import com.pd4ml.PdfSpec;

/**
 * Generates a ZUGFeRD / Factur-X invoice: a PDF/A-3b document with the machine-readable
 * invoice XML (EXTENDED profile) embedded as factur-x.xml, and validates it with Mustang.
 * <p>
 * Z001.htm is a template: the visible invoice, the page footer and the embedded XML
 * (the &lt;pd4ml:attachment&gt; block) contain $[...] placeholders only. All the real data
 * comes from one map passed to pd4ml.setDynamicData(), so the human-readable invoice and
 * the XML can't drift apart. Amounts are computed here once and put into the map twice:
 * as XML values (e.g. "410.10") and as German display text (e.g. "410,10").
 * <p>
 * See https://pd4ml.com/docs/features/zugferd-and-factur-x-pdf-invoices.html
 */
public class Z001ConvertHtml {

	private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	private static final DateTimeFormatter XML_DATE = DateTimeFormatter.ofPattern("yyyyMMdd"); // format="102"
	private static final DecimalFormatSymbols DE = DecimalFormatSymbols.getInstance(Locale.GERMANY);

	public static void main( String[] args ) throws IOException {
		PD4ML pd4ml = new PD4ML();

		pd4ml.setHtmlWidth(900); // render HTML in a virtual frame 900px wide
		pd4ml.addStyle(
				// map the "Arial" font face to the free FiraMono-Regular TTF font (only "plain" style, in the case).
				// PDF/A requires all fonts to be embedded.
				"@font-face {\n" +
				"  font-family: \"Arial\";\n" +
				"  src: url(\"https://pd4ml.com/f/FiraMono-Regular.ttf\") format(\"truetype\"),\n" +
				"}\n", false);

		pd4ml.setErrorPolicy(ErrorPolicy.NORMAL);

		// the invoice data; must be set before readHTML(), the placeholders are resolved while parsing
		pd4ml.setDynamicData(invoiceData());

		// read and parse the HTML template
		pd4ml.readHTML(new URL("file:src/main/java/standards/Z001.htm"));

		File pdf = File.createTempFile("result", ".pdf");
		try (FileOutputStream fos = new FileOutputStream(pdf)) {
			// render and write the result as PDF/A-3b with the embedded invoice XML
			pd4ml.writePDF(fos, PdfSpec.ZUGFeRD);
		}

		// validate the PDF/A container and the embedded invoice XML
		ZUGFeRDValidator validator = new ZUGFeRDValidator();
		String report = validator.validate(pdf.getAbsolutePath());
		if (validator.wasCompletelyValid()) {
			System.out.println("ZUGFeRD validation: valid");
		} else {
			System.out.println("ZUGFeRD validation failed:\n" + report);
		}

		// open the just-generated PDF with a default PDF viewer
		if ( args.length == 0 ) {
			Desktop.getDesktop().open(pdf);
		}
	}

	/**
	 * The invoice data. In a real application it comes from the billing system; values are
	 * inserted into the HTML and the XML as is, so they must not contain '&amp;', '&lt;' or '&gt;'.
	 */
	static Map<String, String> invoiceData() {
		Map<String, String> data = new HashMap<String, String>();

		data.put("curr", "EUR");
		data.put("inv.id", "RE-2026-00417");

		LocalDate invoiceDate = LocalDate.of(2026, 2, 11);
		putDate(data, "inv.date", invoiceDate);
		putDate(data, "delivery.date", LocalDate.of(2026, 2, 5));
		putDate(data, "due.date", invoiceDate.plusDays(14));
		data.put("delivery.note", "LS-2026-00388");

		// seller
		data.put("seller.id", "549910");
		data.put("seller.gln", "4333741000005");
		data.put("seller.name", "Hofmann Service GmbH");
		data.put("seller.tagline", "Energie \u00b7 Logistikdienstleistungen \u00b7 B\u00fcroservice");
		data.put("seller.street", "Bahnhofstra\u00dfe 27");
		data.put("seller.zip", "86807");
		data.put("seller.city", "Buchloe");
		data.put("seller.country", "DE");
		data.put("seller.countryname", "Deutschland");
		data.put("seller.phone", "+49 8241 96530-0");
		data.put("seller.email", "rechnung@hofmann-service.de");
		data.put("seller.web", "www.hofmann-service.de");
		data.put("seller.contact", "Sabine Keller");
		data.put("seller.ceo", "Thomas Hofmann");
		data.put("seller.court", "Amtsgericht Kempten (Allg\u00e4u)");
		data.put("seller.register", "HRB 14327");
		data.put("seller.vatid", "DE287415963");
		data.put("seller.taxno", "127/113/40209");
		data.put("seller.bank", "Commerzbank");
		data.put("seller.iban", "DE89370400440532013000");
		data.put("seller.iban.text", "DE89 3704 0044 0532 0130 00");
		data.put("seller.bic", "COBADEFFXXX");

		// buyer and delivery address
		data.put("buyer.id", "339420");
		data.put("buyer.gln", "4304171000002");
		data.put("buyer.name", "Lindner Logistik GmbH");
		data.put("buyer.contact", "Markus Weber");
		data.put("buyer.attention", "Herrn Markus Weber");
		data.put("buyer.greeting", "Sehr geehrter Herr Weber");
		data.put("buyer.street", "Kaiserswerther Stra\u00dfe 115");
		data.put("buyer.zip", "40474");
		data.put("buyer.city", "D\u00fcsseldorf");
		data.put("buyer.country", "DE");
		data.put("buyer.vatid", "DE812345679");
		data.put("buyer.order", "4500017763");

		data.put("shipto.gln", "4304171088093");
		data.put("shipto.name", "Lindner Logistik GmbH, Lager Sarstedt");
		data.put("shipto.costcenter", "7322");
		data.put("shipto.street", "Hauptstra\u00dfe 44");
		data.put("shipto.zip", "31157");
		data.put("shipto.city", "Sarstedt");
		data.put("shipto.country", "DE");

		// line items: number, article no., GTIN, name, quantity, UN/ECE unit code, unit text,
		// gross unit price, allowance per unit
		BigDecimal lines = BigDecimal.ZERO;
		lines = lines.add(putItem(data, 1, "WA997", "4123456000014", "Wirkarbeit Hochtarif (HT)",
				"1000", "KWH", "kWh", "0.0520", "0"));
		lines = lines.add(putItem(data, 2, "\u00d6ST250", "4123456000021", "Stromsteuer (\u00d6kosteuer), Lieferantenanteil",
				"1000", "KWH", "kWh", "0.0205", "0"));
		lines = lines.add(putItem(data, 3, "KOM-STD", "4260331811362", "Kommissionierung, Mitarbeiter-Nr. 1250032 (D. Brandt)",
				"27.5", "HUR", "Std.", "15.0000", "4.5000"));
		lines = lines.add(putItem(data, 4, "FB05", "2001015001325", "Faltenbeutel 16 x 6 x 28 cm",
				"3500", "H87", "Stk.", "0.0105", "0"));
		lines = lines.add(putItem(data, 5, "KOP05", "4123456000038", "Kopien A4 schwarz-wei\u00df",
				"1210", "H87", "Stk.", "0.0100", "0"));

		// item details
		data.put("item1.meterpoint", "DE0001346484600000000000000100038");
		data.put("item3.desc", "Besteller: Hr. Meier, Lager Sarstedt");
		data.put("item5.device", "MG-X79318");
		putCount(data, "item5.meter.from", 543210);
		putCount(data, "item5.meter.to", 544420);

		// document level allowance and charge, VAT, totals
		BigDecimal allowance = new BigDecimal("21.55");
		BigDecimal charge = new BigDecimal("15.00");
		BigDecimal vatRate = new BigDecimal("19.00");
		BigDecimal taxBasis = lines.subtract(allowance).add(charge);
		BigDecimal vat = percent(taxBasis, vatRate);
		BigDecimal grandTotal = taxBasis.add(vat);

		data.put("allowance.reason", "Sonderrabatt");
		data.put("charge.reason", "Frachtkosten");
		data.put("vat.rate", vatRate.toPlainString());
		data.put("vat.rate.text", text(vatRate, "#,##0.##"));
		putAmount(data, "sum.lines", lines);
		putAmount(data, "sum.allowance", allowance);
		putAmount(data, "sum.charge", charge);
		putAmount(data, "sum.allowancecharge", charge.subtract(allowance));
		putAmount(data, "sum.taxbasis", taxBasis);
		putAmount(data, "sum.vat", vat);
		putAmount(data, "sum.grand", grandTotal);

		// payment terms: early payment discount
		int skontoDays = 10;
		BigDecimal skontoPercent = new BigDecimal("2.00");
		BigDecimal skonto = percent(grandTotal, skontoPercent);
		data.put("skonto.days", String.valueOf(skontoDays));
		data.put("skonto.date", invoiceDate.plusDays(skontoDays).format(DISPLAY_DATE));
		data.put("skonto.percent", skontoPercent.toPlainString());
		data.put("skonto.percent.text", text(skontoPercent, "#,##0.##"));
		data.put("skonto.amount.text", text(skonto, "#,##0.00"));
		data.put("skonto.payable.text", text(grandTotal.subtract(skonto), "#,##0.00"));

		return data;
	}

	/** Puts one line item and returns its line total. */
	private static BigDecimal putItem(Map<String, String> data, int n, String id, String gtin, String name,
			String quantity, String unitCode, String unitText, String grossPrice, String allowancePerUnit) {
		String key = "item" + n + ".";
		BigDecimal qty = new BigDecimal(quantity);
		BigDecimal gross = new BigDecimal(grossPrice).setScale(4, RoundingMode.HALF_UP);
		BigDecimal allowance = new BigDecimal(allowancePerUnit).setScale(4, RoundingMode.HALF_UP);
		BigDecimal net = gross.subtract(allowance);
		BigDecimal total = qty.multiply(net).setScale(2, RoundingMode.HALF_UP);

		data.put(key + "id", id);
		data.put(key + "gtin", gtin);
		data.put(key + "name", name);
		data.put(key + "unit", unitCode);
		data.put(key + "qty", qty.setScale(4, RoundingMode.HALF_UP).toPlainString());
		data.put(key + "qty.text", text(qty, "#,##0.###") + " " + unitText);
		data.put(key + "gross", gross.toPlainString());
		data.put(key + "gross.text", text(gross, "#,##0.00"));
		data.put(key + "allowance", allowance.toPlainString());
		data.put(key + "allowance.text", text(allowance, "#,##0.00"));
		data.put(key + "price", net.toPlainString());
		data.put(key + "price.text", text(net, "#,##0.0000"));
		putAmount(data, key + "total", total);
		return total;
	}

	/** An amount as XML value (key) and as German display text (key.text). */
	private static void putAmount(Map<String, String> data, String key, BigDecimal amount) {
		BigDecimal a = amount.setScale(2, RoundingMode.HALF_UP);
		data.put(key, a.toPlainString());
		data.put(key + ".text", text(a, "#,##0.00"));
	}

	/** A date as display text (key) and in the XML date format 102 (key.code). */
	private static void putDate(Map<String, String> data, String key, LocalDate date) {
		data.put(key, date.format(DISPLAY_DATE));
		data.put(key + ".code", date.format(XML_DATE));
	}

	/** A counter reading as display text (key) and as plain number (key.code). */
	private static void putCount(Map<String, String> data, String key, long count) {
		data.put(key, text(BigDecimal.valueOf(count), "#,##0"));
		data.put(key + ".code", String.valueOf(count));
	}

	private static BigDecimal percent(BigDecimal base, BigDecimal percent) {
		return base.multiply(percent).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
	}

	private static String text(BigDecimal value, String pattern) {
		return new DecimalFormat(pattern, DE).format(value);
	}
}
