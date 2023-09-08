package com.tokio.Pagos.PendientesPortlet.portlet;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import javax.portlet.PortletSession;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.tokio.Pagos.PendientesPortlet.bean.SessionResponse;
import com.tokio.Pagos.PendientesPortlet.bean.SolicitarPagoRequest;
import com.tokio.Pagos.PendientesPortlet.constants.PagosPendientesPortletKeys;
import com.tokio.pagos.PagosServices;
import com.tokio.pagos.Been.PolizaPagar;
import com.tokio.pagos.Been.SolicitarPagoResponse;

@Component(
		immediate = true,
		 property = {
				 "javax.portlet.name="+  PagosPendientesPortletKeys.PagosPendientes,
				 "mvc.command.name=/pagos/getSessionId",
				 },
		 service = MVCResourceCommand.class
		 )
public class GetSessionId extends BaseMVCResourceCommand{
	@Reference
	PagosServices _PagosService;
	
	private static final Log _log = LogFactoryUtil.getLog(PagosPendientesPortlet.class);
	
	@Override
	protected void doServeResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws Exception {
		
		
		String json =  ParamUtil.getString(resourceRequest, "json");
		System.out.println(json);
		json = HtmlUtil.unescape( json );
		Gson gson = new Gson();
		PrintWriter writer = resourceResponse.getWriter();
		SolicitarPagoRequest solicitarPagoRequest = gson.fromJson(json, SolicitarPagoRequest.class);
		solicitarPagoRequest.setIdTransaccion(0);
		solicitarPagoRequest.setFolio("");
		solicitarPagoRequest.setIdTipoMovimiento(16);
		solicitarPagoRequest.setIdEstatus(13);
		solicitarPagoRequest.setTipoPago(16);
		
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();
		solicitarPagoRequest.setFechaTransaccion(dateFormat.format(date));
		
		SolicitarPagoResponse pagoResponse =
				_PagosService.wsSolicitarPago(
						solicitarPagoRequest.getIdTransaccion(), 
						solicitarPagoRequest.getFolio(), 
						solicitarPagoRequest.getIdTipoMovimiento(), 
						solicitarPagoRequest.getIdEstatus(), 
						solicitarPagoRequest.getFechaTransaccion(), 
						solicitarPagoRequest.getTipoPago(), 
						solicitarPagoRequest.getCorreo(), 
						solicitarPagoRequest.getListaPagoPolizas(), 
						"", "PagoPolizas", "PagoPolizas");
		
		if (pagoResponse.getCode() != 0){
			
			writer.write("{\"msjErrTMx\" : \"" +pagoResponse.getMsg()+"\" }");
			return;
		}
		
		solicitarPagoRequest.setFolio(pagoResponse.getFolio());
		float total = 0.0f;
		for (PolizaPagar item : solicitarPagoRequest.getListaPagoPolizas()) {
			total += item.getPrimaTotal();
		}
		
		total = Float.parseFloat( String.format("%.2f", total));
		/*System.out.println(total);					*/
		/*System.out.println(solicitarPagoRequest);		*/
		String apiKey = PropsUtil.get("ambiente.configuracion.key_evopayment");
		String apiPass = PropsUtil.get("ambiente.configuracion.pass_evopayment");
		String authKey = 
				"Basic " + Base64.getEncoder().encodeToString(("merchant."+apiKey+":"+apiPass).getBytes("UTF-8"));
		/*System.out.println(("merchant."+apiKey+":39641b7ca7da84654afe4eba74a73110"));			*/
		
		String returnUrl = getCurUrl(resourceRequest, solicitarPagoRequest);

		try {
			Client client = Client.create();

			WebResource webResource = client.resource("https://evopaymentsmexico.gateway.mastercard.com/api/rest/version/65/merchant/"+apiKey+"/session");

			JsonObject requestSessionId = new JsonObject();

			
			requestSessionId.addProperty("apiOperation", "INITIATE_CHECKOUT");
			

			JsonObject interaction = new JsonObject();
			interaction.addProperty("operation", "PURCHASE");
			/*Prueba redireccionamiento*/
			JsonObject displayControl = new JsonObject();
			displayControl.addProperty("billingAddress", "HIDE");
			interaction.add("displayControl", displayControl);
			
			/*interaction.addProperty("cancelUrl", "https://www.google.com.mx/");/*curUrl*/
			interaction.addProperty("returnUrl", returnUrl);/*urlPortal*/
			JsonObject merchant = new JsonObject();
			merchant.addProperty("name", "Tokio Marine - Polizas");
			interaction.add("merchant", merchant);
			
			requestSessionId.add("interaction", interaction);
			
			
			JsonObject order = new JsonObject();
			order.addProperty("amount", total);
			order.addProperty("reference", "Pago de Polizas");
			order.addProperty("currency", "MXN");
			order.addProperty("description", "Pago de Polizas");
			order.addProperty("id", solicitarPagoRequest.getFolio());
			
			
			requestSessionId.add("order", order);

			/*System.out.println(requestSessionId);
			System.out.println(authKey);*/
			
			
			ClientResponse response = 
					webResource.header(HttpHeaders.AUTHORIZATION,  authKey )
					.accept(MediaType.APPLICATION_JSON)
					.type(MediaType.APPLICATION_JSON)
					.post(ClientResponse.class, requestSessionId.toString());
			
			/*System.out.println(webResource);						*/
			//System.out.println(response.getEntity(String.class));
			String output = response.getEntity(String.class);
			/*System.out.println("Output from Server .... ");		*/
			/*System.out.println(output + "\n");					*/
			
			_log.info("https://evopaymentsmexico.gateway.mastercard.com/api/rest/version/65/merchant/"+apiKey+"/session");
			_log.info(requestSessionId.toString());
			_log.info(output);
			
			if (response.getStatus() != 201) {
				throw new RuntimeException("Failed : HTTP error code : "
				     + response.getStatus());
			}

	        SessionResponse sessionResponse = gson.fromJson(output, SessionResponse.class);
	        
	        JsonObject postResponse = new JsonObject();
	        postResponse.addProperty("sessionId", sessionResponse.getSession().getId());
	        solicitarPagoRequest.setRefPago( sessionResponse.getSession().getId() );
	        postResponse.addProperty("date", solicitarPagoRequest.getFechaTransaccion());
	        postResponse.addProperty("folio", solicitarPagoRequest.getFolio());
	        postResponse.addProperty("total", total); 
	        System.err.println("solicitarPagoRequest: " + gson.toJson(solicitarPagoRequest) );
	        
	        final PortletSession psession = resourceRequest.getPortletSession();
	        String auxNombre = "LIFERAY_SHARED_F=" + solicitarPagoRequest.getFolio();
	        psession.setAttribute(auxNombre, gson.toJson(solicitarPagoRequest), PortletSession.APPLICATION_SCOPE);
	        
	        writer.write(postResponse.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	String getCurUrl( ResourceRequest resourceRequest, SolicitarPagoRequest solicitarPagoRequest ){
		Gson gson = new Gson();
		ThemeDisplay themeDisplay = (ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);
		
		String encodedJson = Base64.getEncoder().encodeToString(gson.toJson(solicitarPagoRequest).getBytes());
		
		String newUrl = themeDisplay.getURLHome() + "/comprobante?inf="+encodedJson;
		return newUrl;
	}
}
