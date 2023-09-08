/**
 * 
 */
package com.tokio.Pagos.PendientesPortlet.util;

import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.mail.kernel.service.MailServiceUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.File;

import javax.mail.internet.InternetAddress;

/**
 * @author jonathanfviverosmoreno
 *
 */
public class SendMailReferencias {

	public void sendMail(String[] mails,  String folio, File referencia){
		
		try {
			String fromMail = "portal_agentes@tokiomarine.com.mx";
			MailMessage ms = new MailMessage();
			String body = "<!DOCTYPE html> <html>   <head>     <meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />     <title>Referencia de pago ${folio}</title>   </head>   <body>     <header><h2>Estimado cliente:</h2></header>     <section> <p>         Por este conducto hacemos llegar su solicitud de Pago por referencia Bancaria.       </p>       <p>         Agradecemos la confianza depositada en nuestra Compañía y la oportunidad que nos brindan para ofrecerles nuestros servicios de seguros.       </p>       <p>         Le enviamos un cordial saludo       </p>     </section>     <footer>       <img         src='https://preview.ibb.co/i3vFWp/Firma_Correo_Tokio_Marine.png'         alt='Firma Correo Tokio Marine'         width='35%'       />     </footer>   </body> </html> ";
			body = StringUtil.replace(body,
					new String[] { "${folio}" },
					new String[] { folio});
			
			InternetAddress fromAddress = new InternetAddress(fromMail);
			InternetAddress toAddress = null;
			InternetAddress[] bulkAddresses = new InternetAddress[mails.length];

			ms.setFrom(fromAddress);
			ms.setSubject("Referencia bancaria  - " + folio );
			ms.setBody(body);
			ms.setHTMLFormat(true);
			ms.addFileAttachment(referencia);

			for (int i = 0; i < mails.length; i++) {
				toAddress = new InternetAddress(mails[i]);
				bulkAddresses[i] = toAddress;
			}
			ms.setBulkAddresses(bulkAddresses);

			MailServiceUtil.sendEmail(ms);
			System.out.println("--------fin mail-------");
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
	}
}
