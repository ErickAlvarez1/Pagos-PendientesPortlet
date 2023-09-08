package com.tokio.Pagos.PendientesPortlet.util;

import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.mail.kernel.service.MailServiceUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.tokio.Pagos.PendientesPortlet.bean.SolicitarPagoRequest;
import com.tokio.pagos.Been.PolizaPagar;

import java.io.File;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.mail.internet.InternetAddress;

public class SendMailPagos {
	public boolean sendMail(String[] mails, File documento, SolicitarPagoRequest spr) {
		try {
			String fromMail = "portal_agentes@tokiomarine.com.mx";
			MailMessage ms = new MailMessage();
			String body = "<!DOCTYPE HTML><html><head>    <meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />    <title>pago ${folio}</title></head><body>    <header>        <h3> Estimado cliente: </h3>    </header>    <section>        <p> Por este conducto hacemos llegar su comprobante de pago. </p>   <p>Agradecemos la confianza depositada en nuestra Compañ&iacute;a y la oportunidad que nos brindan para ofrecerles nuestros servicios de seguros.</p>        <p>Le enviamos un cordial saludo</p>    </section>    <footer> <img src='https://preview.ibb.co/i3vFWp/Firma_Correo_Tokio_Marine.png' alt='Firma Correo Tokio Marine' border='0' width='30%' /> </footer></body></html>";
			body = StringUtil.replace(body,
					new String[] { "${folio}", "${fechaTransaccion}", "${referencia}", "${total}" },
					new String[] { spr.getFolio(), spr.getFechaTransaccion(), ""+spr.getRefPago(), montoTotal(spr.getListaPagoPolizas()) });
			
			InternetAddress fromAddress = new InternetAddress(fromMail);
			InternetAddress toAddress = null;
			InternetAddress[] bulkAddresses = new InternetAddress[mails.length];

			ms.setFrom(fromAddress);
			ms.setSubject("Comprobante " + spr.getFolio() + ",  Tokio Marine");
			ms.setBody(body);
			ms.setHTMLFormat(true);

			for (int i = 0; i < mails.length; i++) {
				toAddress = new InternetAddress(mails[i]);
				bulkAddresses[i] = toAddress;
			}
			ms.setBulkAddresses(bulkAddresses);

	
			ms.addFileAttachment(documento);

			MailServiceUtil.sendEmail(ms);
			System.out.println("--------fin mail-------");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}	
	}
	
	private String montoTotal(List<PolizaPagar> lpp){
		String montotot = null;
		float tot = 0;
		for (PolizaPagar listaPendientes : lpp) {
			tot += listaPendientes.getPrimaTotal();
		}
		NumberFormat format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-MX"));
		montotot = format.format(tot);
		return montotot;
	}
}
