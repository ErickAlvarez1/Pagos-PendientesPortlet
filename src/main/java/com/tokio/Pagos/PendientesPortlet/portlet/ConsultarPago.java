package com.tokio.Pagos.PendientesPortlet.portlet;

import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.tokio.Pagos.PendientesPortlet.constants.PagosPendientesPortletKeys;
import com.tokio.pagos.PagosServices;

import java.util.Base64;

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
				 "mvc.command.name=/pagos/consultarpago",
				 },
		 service = MVCResourceCommand.class
		 )
public class ConsultarPago extends BaseMVCResourceCommand{
	@Reference
	PagosServices _PagosService;

	@Override
	protected void doServeResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws Exception {
		String folio =  ParamUtil.getString(resourceRequest, "folio");
		String apiKey = PropsUtil.get("ambiente.configuracion.key_evopayment");
		String apiPass = PropsUtil.get("ambiente.configuracion.pass_evopayment");
		String authKey = 
				"Basic " + Base64.getEncoder().encodeToString(("merchant."+apiKey+":"+apiPass).getBytes("UTF-8"));
		
		
		try {
			Client client = Client.create();

			WebResource webResource = client.resource("https://banamex.dialectpayments.com/api/rest/version/51/merchant/"+apiKey+"/order/"+folio);

			
			ClientResponse response = 
					webResource.header(HttpHeaders.AUTHORIZATION,  authKey )
					.accept(MediaType.APPLICATION_JSON)
					.type(MediaType.APPLICATION_JSON)
					.get(ClientResponse.class);
			System.out.println(webResource);
			//System.out.println(response.getEntity(String.class));
			if (response.getStatus() != 201) {
				throw new RuntimeException("Failed : HTTP error code : "
				     + response.getStatus());
			}

			String output = response.getEntity(String.class);
	        System.out.println("Output from Server .... ");
	        System.out.println(output + "\n");
	        resourceResponse.getWriter().write(output);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
