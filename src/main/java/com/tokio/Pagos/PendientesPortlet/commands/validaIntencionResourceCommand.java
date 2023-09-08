package com.tokio.Pagos.PendientesPortlet.commands;

import java.io.PrintWriter;
import java.util.Base64;

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
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.tokio.Pagos.PendientesPortlet.bean.ConsultaPagoResponse;
import com.tokio.Pagos.PendientesPortlet.bean.ConsultaPagoResponseTransaction;
import com.tokio.Pagos.PendientesPortlet.bean.ConsultaPagoResponseTransactionTransaction;
import com.tokio.Pagos.PendientesPortlet.constants.PagosPendientesPortletKeys;
import com.tokio.Pagos.PendientesPortlet.portlet.PagosPendientesPortlet;
import com.tokio.pagos.PagosServices;
import com.tokio.pagos.Been.Estatus;
import com.tokio.pagos.Been.ResponseError;
import com.tokio.pagos.Been.ValidaResponse;
import com.tokio.pagos.Exception.PagosException;

@Component(immediate = true, property = { "javax.portlet.name=" + PagosPendientesPortletKeys.PagosPendientes,
		"mvc.command.name=/validaIntencion", }, service = MVCResourceCommand.class)

public class validaIntencionResourceCommand extends BaseMVCResourceCommand{
	private static final Log _log = LogFactoryUtil.getLog(PagosPendientesPortlet.class);
	
	@Reference
	PagosServices _PagosService;
	
	@Override
	protected void doServeResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws Exception {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		PrintWriter writer = resourceResponse.getWriter();
		
		String intencion = ParamUtil.getString(resourceRequest, "intencion");
		String apiKey = PropsUtil.get("ambiente.configuracion.key_evopayment");
		String apiPass = PropsUtil.get("ambiente.configuracion.pass_evopayment");
		String authKey = "Basic " + Base64.getEncoder().encodeToString(("merchant."+apiKey+":"+apiPass).getBytes("UTF-8"));
		
		JsonObject resp = new JsonObject();
		
		resp.addProperty("intencion", intencion);
		
		System.err.println("intencion----> " + intencion);
		
		ConsultaPagoResponse consultaPagoResponse = new ConsultaPagoResponse();
		try {
			Client client = Client.create();

			WebResource webResource = client.resource( "https://evopaymentsmexico.gateway.mastercard.com/api/rest/version/62/merchant/"+apiKey+"/order/"+intencion );

			ClientResponse response = 
					webResource.header(HttpHeaders.AUTHORIZATION,  authKey )
					.accept(MediaType.APPLICATION_JSON)
					.type(MediaType.APPLICATION_JSON)
					.get(ClientResponse.class);
			System.out.println(webResource);
			String output = response.getEntity(String.class);

			_log.info("https://evopaymentsmexico.gateway.mastercard.com/api/rest/version/62/merchant/"+apiKey+"/order/"+intencion );
			_log.info(output);
			_log.info("estatus: " + response.getStatus());
			
			if (response.getStatus() == 200) {
		        System.err.println("Confirma pago Output from Server .... ");
		        System.out.println(output + "\n");
		        consultaPagoResponse = gson.fromJson(output, ConsultaPagoResponse.class);
//		        System.err.println("consultaPagoResponse");
		        
		        ConsultaPagoResponseTransaction auxObj = consultaPagoResponse.getTransaction().get( consultaPagoResponse.getTransaction().size()-1) ;
		        
//		        System.err.println(auxObj.getResponse().getGatewayCode() );
//		        System.err.println(consultaPagoResponse.getResult() );
		        
		        if( auxObj.getResponse().getGatewayCode().equals("APPROVED") ){
		        	resp.addProperty("code", 200);
		        	resp.addProperty("msg", "Pago Realizado Anteriormente");
		        	resp.addProperty("date", auxObj.getTimeOfLastUpdate());
		        	resp.addProperty("folio", auxObj.getTransaction().getReceipt() );
		        	resp.addProperty("amount", auxObj.getTransaction().getAmount() );
		        	resp.addProperty("refpago", intencion);	
		        	
		        	/**Cambiar estatus a pagado**/
		        	Estatus estatus = new Estatus();
					estatus.setIdMovimiento("000");;
					estatus.setIdTransaccion(auxObj.getTransaction().getReceipt());
					estatus.setFolio(intencion);
					estatus.setCodigoEstatus(14);/*Estatus pagado*/
					
					
					try {
						ValidaResponse respuesta = _PagosService.wsActualizarEstatus(estatus);
					} catch (PagosException e) {
						e.printStackTrace();
					}
		        	/******************************/
		        	
		        }else{
		        	resp.addProperty("code", 1);
		        	resp.addProperty("msg", "Pago Pendiente");
		        	resp.addProperty("date", "");
		        	resp.addProperty("folio", "" );
		        	resp.addProperty("refpago", intencion);	   
		        	
		        	_log.info("cancelar pago por pago pendiente");
					Estatus estatus = new Estatus();
					estatus.setIdMovimiento("000");;
					estatus.setIdTransaccion("0");
					estatus.setFolio(intencion);
					estatus.setCodigoEstatus(15);
					ValidaResponse respuesta = _PagosService.wsActualizarEstatus(estatus);
					_log.info(respuesta);
		        }
		        
		        
			}else if(response.getStatus() == 400){
				ResponseError error = gson.fromJson(output, ResponseError.class);
				
				if ( error.getResult().equals("ERROR") && error.getError().getExplanation().contains("Unable to find order") ){
					_log.info("cancelar pago");
					Estatus estatus = new Estatus();
					estatus.setIdMovimiento("000");;
					estatus.setIdTransaccion("0");
					estatus.setFolio(intencion);
					estatus.setCodigoEstatus(15);
					
					
					try {
						ValidaResponse respuesta = _PagosService.wsActualizarEstatus(estatus);
						resp.addProperty("code", 300);
						resp.addProperty("msg", respuesta.getMsg());
					} catch (PagosException e) {
						e.printStackTrace();
					}
				}
				
			}
			else{
				return;
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		System.out.println();
		writer.write(resp.toString());
	}
}
