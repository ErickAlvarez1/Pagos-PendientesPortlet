package com.tokio.Pagos.PendientesPortlet.util;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.liferay.portal.kernel.servlet.PortletResourcesUtil;
import com.liferay.portal.kernel.util.Validator;
import com.tokio.Pagos.PendientesPortlet.bean.SolicitarPagoRequest;
import com.tokio.pagos.Been.PolizaPagar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.portlet.ResourceRequest;

public class CreatePdfReferenciaBancaria {

	public File createFile(SolicitarPagoRequest objData, ResourceRequest request) {
		
		DateFormat dateFormatFull = new SimpleDateFormat("yyMMdd");
		Date date = new Date();
		
		File temp = null;
		String nomDoc = objData.getFolio() + dateFormatFull.format(date);
		Document document = new Document(PageSize.LETTER, 15, 15, 20, 90);
		try {

			
			temp = File.createTempFile(nomDoc, ".pdf");
			
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(temp.getAbsolutePath()));
			document.open();
			addImages(document, request);
			
			genTblEncabezado(objData,document, writer);
			
			document.add(createMeta("", ""));
			document.add(createMeta("", ""));
			document.add(createMeta("REFERENCIA DE PAGO", true));
			
			PdfPTable tblCliente = genTblCliente(objData);
			
			PdfPTable tblDetalle = genTblDetalle(objData);
			
			document.add(tblCliente);
			document.add(createMeta("", ""));
			document.add(createMeta("", ""));
			document.add(createMeta("", ""));
			document.add(createMeta("DETALLE DE LA REFERENCIA", true));
			document.add(tblDetalle);
			document.add(createMeta("", ""));
			document.add(createMeta("", ""));
			document.add(createMeta("", ""));
			document.add(createMeta("", ""));
			document.add(genTblCuentaConsenta(objData));
			
			PlaceChunck(writer, "*Este documento no representa un comprobante fiscal digital.", 30, 80);
			PlaceChunck(writer, "Para dudas y aclaraciones, 5278 2100, contacto@tokiomarine.com.mx", 30, 50);
			
			document.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return temp;
	}

	private void PlaceChunck(PdfWriter writer, String text, int x, int y) throws DocumentException, IOException {
		PdfContentByte cb = writer.getDirectContent();
		BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
		cb.saveState();
		cb.beginText();
		cb.moveText(x, y);
		cb.setFontAndSize(bf, 12);
		cb.showText(text);
		cb.endText();
		cb.restoreState();
	}

	private void addImages(Document document, ResourceRequest request)
			throws MalformedURLException, IOException, DocumentException {
		Image image = Image
				.getInstance(PortletResourcesUtil.getResource(request.getContextPath() + "/img/tokio-marine.png"));
		image.scaleAbsolute(160, 50);
		document.add(image);
		image = Image.getInstance(PortletResourcesUtil.getResource(request.getContextPath() + "/img/logo-blue.png"));
		image.setAbsolutePosition(document.right() - image.getWidth() + document.rightMargin(), 0);
		document.add(image);
		image = Image.getInstance(
				PortletResourcesUtil.getResource(request.getContextPath() + "/img/to-be-a-good-company.png"));
		image.setAbsolutePosition(20, 20);
		document.add(image);
		
		image = Image.getInstance(
				PortletResourcesUtil.getResource(request.getContextPath() + "/img/Redondo.png"));
		image.setAbsolutePosition(10, 615);
		document.add(image);
		
		image = Image.getInstance(
				PortletResourcesUtil.getResource(request.getContextPath() + "/img/RedondoTit.png"));
		image.setAbsolutePosition(363, 719);
		document.add(image);
		
	}

	private PdfPCell fTxt(double text, Locale locale) {
		NumberFormat format = NumberFormat.getCurrencyInstance(locale);
		String currency = format.format(text);
		return fTxt(currency);
	}

	private PdfPCell fTxt(String text, int font) {
		Phrase phrase = new Phrase();
		phrase.add(new Chunk(text, new Font(Font.FontFamily.COURIER, 10, font)));
		phrase.setLeading(0, 1);

		PdfPCell cell = new PdfPCell();
		
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.addElement(phrase);
		cell.setBorderColor(BaseColor.BLACK);
		

		return cell;
	}

	private PdfPCell fTxt(String text) {
		Phrase phrase = new Phrase();
		phrase.add(new Chunk(text, new Font(Font.FontFamily.COURIER, 9)));
		phrase.setLeading(0, 1);

		PdfPCell cell = new PdfPCell();
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.addElement(phrase);

		return cell;
	}

	private PdfPCell fTxtT1(String text, boolean subrayado) {
		Phrase phrase = new Phrase();
		if (subrayado) {
			phrase.add(new Chunk(text, new Font(Font.FontFamily.COURIER, 9, Font.BOLD)));			
		}else{
			phrase.add(new Chunk(text, new Font(Font.FontFamily.COURIER, 9)));
		}
		phrase.setLeading(0, 1);
		PdfPCell cell = new PdfPCell();
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.addElement(phrase);
		if (subrayado) {
			cell.setBorder(Rectangle.BOTTOM);
		} else {
			cell.setBorder(Rectangle.NO_BORDER);	
		}
		return cell;
	}

	private void createHeaderBono(PdfPTable table) {
		table.addCell(fTxt("Póliza", Font.BOLD));
		table.addCell(fTxt("Endoso", Font.BOLD));
		table.addCell(fTxt("No. Documento", Font.BOLD));
		table.addCell(fTxt("No. Recibo", Font.BOLD));
		table.addCell(fTxt("Prima Total", Font.BOLD));
	}

	private String validaNull(String s) {
		s = Validator.isNull(s) ? "" : s;
		return s;
	}

	private Paragraph createMeta(String title, String text) {
		Phrase phrase = new Phrase();
		Paragraph paragraph = new Paragraph();
		phrase.add(new Chunk(String.format("%-18s", title), new Font(Font.FontFamily.COURIER, 11)));
		paragraph.setAlignment(Element.ALIGN_CENTER);
		phrase.add(new Chunk(text, new Font(Font.FontFamily.COURIER, 12, Font.BOLD)));
		paragraph.add(phrase);
		return paragraph;
	}

	private Paragraph createMeta(String text, boolean negritas) {
		Phrase phrase = new Phrase();
		Paragraph paragraph = new Paragraph();
		if (negritas) {
			phrase.add(new Chunk(text, new Font(Font.FontFamily.COURIER, 12, Font.BOLD)));
		} else {
			phrase.add(new Chunk(text, new Font(Font.FontFamily.COURIER, 12)));

		}
		paragraph.setAlignment(Element.ALIGN_CENTER);
		paragraph.add(phrase);
		return paragraph;
	}

	private void addTotal(PdfPTable table, float total, Locale locale, int TableCols) {

		PdfPCell cell = fTxt("");
		cell.setBorder(Rectangle.NO_BORDER);
		for (int i = 0; i < TableCols; i++)
			table.addCell(cell);
		cell = fTxt("Prima Total", Font.BOLD);
		table.addCell(cell);
		cell = fTxt(total, locale);
		table.addCell(fTxt(total, locale));
	}
	
	private void genTblEncabezado(SolicitarPagoRequest objData, Document document, PdfWriter writer){
		Rectangle page = document.getPageSize();
		
		PdfPTable t1 = new PdfPTable(2);
		t1.setSpacingBefore(5);
		t1.addCell(fTxtT1("Fecha de Transacción", false));
		t1.addCell(fTxtT1(objData.getFechaTransaccion(), true));
		t1.addCell(fTxtT1("Folio", false));
		t1.addCell(fTxtT1(objData.getFolio(), true));
//		t1.addCell(fTxtT1("No. Transacción", false));
//		t1.addCell(fTxtT1("" + objData.getRefPago(), true));

		t1.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin() - 350);
		t1.writeSelectedRows(0, -1, document.right() - 231, document.top() - 10, writer.getDirectContent());

	}
	
	private PdfPTable genTblCliente(SolicitarPagoRequest objData){
		
		float[] columnWidths = { 10, 30 };
		PdfPTable table = new PdfPTable(columnWidths);
		table.setSpacingBefore(10);
		table.setHeaderRows(1);
		table.setSplitRows(false);
		table.setComplete(false);
		table.setWidthPercentage(100);
		

		System.out.println("Cliente : " + objData.getListaPagoPolizas().get(0).getAsegurado());
		
		table.addCell(fTxtT1(validaNull("Cliente: "), false));
		table.addCell(fTxtT1(validaNull(objData.getListaPagoPolizas().get(0).getAsegurado()), true));
		table.addCell(fTxtT1(validaNull("Referencia alfanumérica: "), false));
		table.addCell(fTxtT1(validaNull(objData.getRefPago()), true));
		table.addCell(fTxtT1(validaNull("Estatus:"), false));
		table.addCell(fTxtT1(validaNull("Pendiente"), true));
		return table;
	}
	
	
	private PdfPTable genTblDetalle(SolicitarPagoRequest objData){
		Locale locale = Locale.forLanguageTag("es-MX");
		float sumTotal = 0;
		float[] columnWidths = { 22, 22, 22, 10, 15 };
		PdfPTable table = new PdfPTable(columnWidths);
		table.setSpacingBefore(10);
		table.setHeaderRows(1);
		table.setSplitRows(false);
		table.setComplete(false);
		table.setWidthPercentage(100);
		createHeaderBono(table);

		for (PolizaPagar lp : objData.getListaPagoPolizas()) {
			table.addCell(fTxt(validaNull(lp.getPoliza())));
			table.addCell(fTxt(validaNull(lp.getCertif())));
			table.addCell(fTxt(validaNull(lp.getNumdoc())));
			table.addCell(fTxt(validaNull(lp.getRecibo())));
			table.addCell(fTxt(lp.getPrimaTotal(), locale));
			sumTotal += lp.getPrimaTotal();
		}
		addTotal(table, sumTotal, locale, 3);
		return table;
	}
	
	
	
	private PdfPTable genTblCuentaConsenta(SolicitarPagoRequest objData){
		float[] columnWidths = { 10, 6, 8, 12 };
		PdfPTable table = new PdfPTable(columnWidths);
		table.setSpacingBefore(10);
		table.setHeaderRows(1);
		table.setSplitRows(false);
		table.setComplete(false);
		table.setWidthPercentage(55);
		table.setHorizontalAlignment(Element.ALIGN_LEFT);
		
		
		table.addCell(fTxtConb("                   Cuentas Concentradoras", false, 4, false));
		table.addCell(fTxtConb("Banco:", false, 1, false));
		table.addCell(fTxtConb("Banco Nacional de México, S.A. (BANAMEX)", false, 3, false));
		table.addCell(fTxtConb("Moneda:", false, 1, false));
		table.addCell(fTxtConb("   Pesos",  true, 1, true));
		table.addCell(fTxtConb("Cuenta:", false, 1, false));
		table.addCell(fTxtConb("     575625",  true, 1, true));
		table.addCell(fTxtConb("Sucursal:", false, 1, false));
		table.addCell(fTxtConb("    870",  true, 1, true));
		table.addCell(fTxtConb("Referencia:", false, 1, false));
		table.addCell(fTxtConb(validaNull(objData.getRefPago()),  true, 1, true));
		table.addCell(fTxtConb(" Clabe interbancaria:", false, 2, false));
		table.addCell(fTxtConb("  02180087005756250", true, 2, true));
		table.addCell(fTxtConb("Moneda:", false, 1, false));
		table.addCell(fTxtConb("  Dólares",  true, 1, true));
		table.addCell(fTxtConb("Cuenta:", false, 1, false));
		table.addCell(fTxtConb("    9702352",  true, 1, true));
		table.addCell(fTxtConb("Sucursal:", false, 1, false));
		table.addCell(fTxtConb("    870",  true, 1, true));
		table.addCell(fTxtConb("Referencia:", false, 1, false));
		table.addCell(fTxtConb(validaNull(objData.getRefPago()),  true, 1, true));
		table.addCell(fTxtConb(" Clabe interbancaria:", false, 2, false));
		table.addCell(fTxtConb("  02180087097023520", true, 2, true));
		
		return table;
	}
	
	
	private PdfPCell fTxtConb(String text, Boolean isBold, int colspan, Boolean fondo) {
		Phrase phrase = new Phrase();
		if (isBold){
			phrase.add(new Chunk(text, new Font(Font.FontFamily.COURIER, 7,  Font.BOLD)));			
		}else{
			phrase.add(new Chunk(text, new Font(Font.FontFamily.COURIER, 7)));						
		}
		phrase.setLeading(0, 1);
		
		PdfPCell cell = new PdfPCell();
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.addElement(phrase);
		cell.setBorderColor(BaseColor.BLACK);
		if (fondo){
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		}
		cell.setColspan(colspan);

		return cell;
	}
	

	class Events extends PdfPageEventHelper {
		Font font;
		PdfTemplate t;
		Image total;
		ResourceRequest request;

		public Events() {
		}

		public Events(ResourceRequest request) {
			this.request = request;
		}
		
		

		@Override
		public void onOpenDocument(PdfWriter writer, Document document) {
			t = writer.getDirectContent().createTemplate(30, 16);
			try {
				total = Image.getInstance(t);
				total.setRole(PdfName.ARTIFACT);
				font = new Font(BaseFont.createFont(), 10);
			} catch (DocumentException de) {
				throw new ExceptionConverter(de);
			} catch (IOException ioe) {
				throw new ExceptionConverter(ioe);
			}
		}

		@Override
		public void onStartPage(PdfWriter writer, Document document) {
			try {
				addImages(document, this.request);
			} catch (IOException | DocumentException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onEndPage(PdfWriter writer, Document document) {
			PdfPTable table = new PdfPTable(2);
			try {
				table.setWidths(new int[] { 50, 50 });
				table.setTotalWidth(700);
				table.getDefaultCell().setFixedHeight(20);
				table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
				table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
				table.addCell(new Phrase(String.format("%d / ", writer.getPageNumber()), font));
				PdfPCell cell = new PdfPCell(total);
				cell.setBorder(Rectangle.NO_BORDER);
				table.addCell(cell);
				PdfContentByte canvas = writer.getDirectContent();
				canvas.beginMarkedContentSequence(PdfName.ARTIFACT);
				table.writeSelectedRows(0, -1, 36, 30, canvas);
				canvas.endMarkedContentSequence();
			} catch (DocumentException de) {
				de.printStackTrace();
				throw new ExceptionConverter(de);
			}
		}

		@Override
		public void onCloseDocument(PdfWriter writer, Document document) {
			ColumnText.showTextAligned(t, Element.ALIGN_CENTER,
					new Phrase(String.valueOf(writer.getPageNumber()), font), 2, 4, 0);
		}
	}
}
