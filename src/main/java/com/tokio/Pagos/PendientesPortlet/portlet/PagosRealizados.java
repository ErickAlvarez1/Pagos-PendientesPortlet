package com.tokio.Pagos.PendientesPortlet.portlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.tokio.Pagos.PendientesPortlet.bean.ConsultaPagoResponse;
import com.tokio.Pagos.PendientesPortlet.bean.SolicitarPagoRequest;
import com.tokio.Pagos.PendientesPortlet.constants.PagosPendientesPortletKeys;
import com.tokio.Pagos.PendientesPortlet.util.CreatePdfPagosRealizados;
import com.tokio.Pagos.PendientesPortlet.util.SendMailPagos;
import com.tokio.pagos.PagosServices;
import com.tokio.pagos.Been.Estatus;
import com.tokio.pagos.Been.ValidaResponse;

import java.io.File;
import java.io.PrintWriter;
import java.util.Base64;

import javax.portlet.PortletSession;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true, property = { "javax.portlet.name=" + PagosPendientesPortletKeys.PagosPendientes,
		"mvc.command.name=/solicitudPago", }, service = MVCResourceCommand.class)

public class PagosRealizados extends BaseMVCResourceCommand {

	@Reference
	PagosServices _PagosService;

	private static final Log _log = LogFactoryUtil.getLog(PagosPendientesPortlet.class);
	
	@Override
	protected void doServeResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws Exception {
		// TODO Auto-generated method stub

		String pagos = ParamUtil.getString(resourceRequest, "json");
		
		
		
		System.out.println("json : " + pagos);
		Gson gson = new Gson();
		PrintWriter writer = resourceResponse.getWriter();
		
		String apiKey = PropsUtil.get("ambiente.configuracion.key_evopayment");
		String apiPass = PropsUtil.get("ambiente.configuracion.pass_evopayment");
		String authKey = 
				"Basic " + Base64.getEncoder().encodeToString(("merchant."+apiKey+":"+apiPass).getBytes("UTF-8"));
		
		SolicitarPagoRequest objData = gson.fromJson(pagos, SolicitarPagoRequest.class);
		
		ConsultaPagoResponse consultaPagoResponse = new ConsultaPagoResponse();
		try {
			Client client = Client.create();

//			WebResource webResource = client.resource("https://banamex.dialectpayments.com/api/rest/version/51/merchant/"+apiKey+"/order/"+objData.getFolio());
			                                         //https://evopaymentsmexico.gateway.mastercard.com/api/rest/version/62/merchant/{merchantId}/order/{orderid}
			WebResource webResource = client.resource("https://evopaymentsmexico.gateway.mastercard.com/api/rest/version/62/merchant/"+apiKey+"/order/"+objData.getFolio());

			ClientResponse response = 
					webResource.header(HttpHeaders.AUTHORIZATION,  authKey )
					.accept(MediaType.APPLICATION_JSON)
					.type(MediaType.APPLICATION_JSON)
					.get(ClientResponse.class);
			System.out.println(webResource);
			//System.out.println(response.getEntity(String.class));
			String output = response.getEntity(String.class);

			_log.info("https://evopaymentsmexico.gateway.mastercard.com/api/rest/version/62/merchant/"+apiKey+"/order/"+objData.getFolio() );
			_log.info(output);
			
			if (response.getStatus() == 200) {
		        System.err.println("Confirma pago Output from Server .... ");
		        System.out.println(output + "\n");
		        consultaPagoResponse = gson.fromJson(output, ConsultaPagoResponse.class);
			}else{
				returnMsjError( resourceResponse );
				return;
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			String jsonString = "{\"code\" : \"4\", \"msg\" : \"Pago no realizado\" }";
			writer.write(jsonString);
		}
		
		try {
			
			String folioFinal = getFolioResponse(  consultaPagoResponse );
			_log.info("idtransaccion: " +folioFinal + "---Result: " + consultaPagoResponse.getResult() + "---Bool: " + !consultaPagoResponse.getResult().equals("SUCCESS"));
			if ( Validator.isNull(pagos) || Validator.isNull(folioFinal)  || !consultaPagoResponse.getResult().equals("SUCCESS")  ) {  /*consultaPagoResponse.getTransaction().get(1).getTransaction().getReceipt()*/
				System.out.println("Entre error conssulta pago");
				String jsonString = "{\"code\" : \"4\", \"msg\" : \"No se realizaron pagos\" }";
				writer.write(jsonString);
			} else {
				JsonObject response = new JsonObject();
				
				System.err.println("consultaPagoResponse");
				System.out.println(consultaPagoResponse);
				
				ValidaResponse respuesta = _PagosService.wsActualizarEstatus(generaEstatus(objData, consultaPagoResponse));
				System.out.println(respuesta);
				objData.setRefPago(folioFinal);
				File documentosReq = new CreatePdfPagosRealizados().createFile(objData, resourceRequest);
				String mails = objData.getCorreo();
				String[] listMails = mails.split(",");
				System.err.println("Envia correo");
				boolean envioMail = new SendMailPagos().sendMail(listMails, documentosReq, objData);
				System.err.println("Envia correo2");
				String jsonString = null;
				if(envioMail){
					jsonString = "Pago exitoso, su comprobante sera enviado";
				}else{
					jsonString = "Error al enviar el comprobante";					
				}
				response.addProperty("mail", jsonString);
				response.addProperty("code", 0);
				response.addProperty("date", objData.getFechaTransaccion());
				response.addProperty("folio", objData.getFolio());
				response.addProperty("refpago", objData.getRefPago());
				
				
				
				writer.write(response.toString());
			}
		} catch (Exception e) {
			// TODO: handle exception
			
			String jsonString = "{\"code\" : \"5\", \"msg\" : \" " + e.getMessage() +" \" }";
			writer.write(jsonString);
		}
					
	}

	Estatus generaEstatus(SolicitarPagoRequest objData, ConsultaPagoResponse consultaPagoResponse){
		Estatus estatus = new Estatus();
		estatus.setIdMovimiento(consultaPagoResponse.getTransaction().get(1).getResponse().getAcquirerCode());
		estatus.setIdTransaccion(getFolioResponse(  consultaPagoResponse ));
		estatus.setFolio(objData.getFolio());
		estatus.setCodigoEstatus(14);
		return estatus;
	}
	
	void returnMsjError( ResourceResponse resourceResponse )  throws Exception{
		PrintWriter writer = resourceResponse.getWriter();
		JsonObject responseErr = new JsonObject();
		
		responseErr.addProperty("msg", "Pago no generado");
		responseErr.addProperty("mail", "Pago no generado");
		responseErr.addProperty("code", 1);
		responseErr.addProperty("date", "");
		responseErr.addProperty("folio", "");
		responseErr.addProperty("refpago", "");
		
		writer.write(responseErr.toString());
	}
	
	String getFolioResponse( ConsultaPagoResponse consultaPagoResponse ){
		String auxFolio = "";
		for (int i = consultaPagoResponse.getTransaction().size(); i > 0; i--) {
			auxFolio = consultaPagoResponse.getTransaction().get(i-1).getTransaction().getReceipt();
			if ( Validator.isNotNull(auxFolio) ){
				return auxFolio;
			}
		}
		return null;
	}
}
