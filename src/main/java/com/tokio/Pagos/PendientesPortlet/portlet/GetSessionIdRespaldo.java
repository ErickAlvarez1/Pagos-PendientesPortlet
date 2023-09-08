package com.tokio.Pagos.PendientesPortlet.portlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.tokio.Pagos.PendientesPortlet.bean.SessionResponse;
import com.tokio.Pagos.PendientesPortlet.bean.SolicitarPagoRequest;
import com.tokio.Pagos.PendientesPortlet.constants.PagosPendientesPortletKeys;
import com.tokio.pagos.PagosServices;
import com.tokio.pagos.Been.PolizaPagar;
import com.tokio.pagos.Been.SolicitarPagoResponse;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
		immediate = true,
		 property = {
				 "javax.portlet.name="+  PagosPendientesPortletKeys.PagosPendientes,
				 "mvc.command.name=/pagos/getSessionIdRespaldo",
				 },
		 service = MVCResourceCommand.class
		 )
public class GetSessionIdRespaldo extends BaseMVCResourceCommand{
	@Reference
	PagosServices _PagosService;
	
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
		System.out.println(total);
		System.out.println(solicitarPagoRequest);
		String apiKey = PropsUtil.get("ambiente.configuracion.key_evopayment");
		String apiPass = PropsUtil.get("ambiente.configuracion.pass_evopayment");
		String authKey = 
				"Basic " + Base64.getEncoder().encodeToString(("merchant."+apiKey+":"+apiPass).getBytes("UTF-8"));
		System.out.println(("merchant."+apiKey+":39641b7ca7da84654afe4eba74a73110"));
		
		try {
			Client client = Client.create();

			WebResource webResource = client.resource("https://banamex.dialectpayments.com/api/rest/version/51/merchant/"+apiKey+"/session");

			JsonObject requestSessionId = new JsonObject();
			requestSessionId.addProperty("apiOperation", "CREATE_CHECKOUT_SESSION");

			JsonObject order = new JsonObject();
			order.addProperty("currency", "MXN");
			order.addProperty("amount", total);
			order.addProperty("id", solicitarPagoRequest.getFolio());
			
			requestSessionId.add("order", order);

			System.out.println(requestSessionId);
			System.out.println(authKey);
			
			ClientResponse response = 
					webResource.header(HttpHeaders.AUTHORIZATION,  authKey )
					.accept(MediaType.APPLICATION_JSON)
					.type(MediaType.APPLICATION_JSON)
					.post(ClientResponse.class, requestSessionId.toString());
			System.out.println(webResource);
			//System.out.println(response.getEntity(String.class));
			if (response.getStatus() != 201) {
				throw new RuntimeException("Failed : HTTP error code : "
				     + response.getStatus());
			}

			String output = response.getEntity(String.class);
	        System.out.println("Output from Server .... ");
	        System.out.println(output + "\n");
	        
	        SessionResponse sessionResponse = gson.fromJson(output, SessionResponse.class);
	        
	        JsonObject postResponse = new JsonObject();
	        postResponse.addProperty("sessionId", sessionResponse.getSession().getId());
	        postResponse.addProperty("date", solicitarPagoRequest.getFechaTransaccion());
	        postResponse.addProperty("folio", solicitarPagoRequest.getFolio());
	        postResponse.addProperty("total", total);
	        writer.write(postResponse.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
