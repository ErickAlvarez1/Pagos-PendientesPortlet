package com.tokio.Pagos.PendientesPortlet.portlet;

import com.google.gson.JsonObject;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.tokio.Pagos.PendientesPortlet.constants.PagosPendientesPortletKeys;
import com.tokio.pagos.PagosServices;
import com.tokio.pagos.Been.Estatus;
import com.tokio.pagos.Been.ValidaResponse;
import com.tokio.pagos.Exception.PagosException;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;


@Component(
		immediate = true,
		 property = {
				 "javax.portlet.name="+  PagosPendientesPortletKeys.PagosPendientes,
				 "mvc.command.name=/pagos/pagoestatus",
				 },
		 service = MVCResourceCommand.class
		 )
public class CancelarPago extends BaseMVCResourceCommand{
	@Reference
	PagosServices _PagosService;
	
	@Override
	protected void doServeResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws Exception {
				String folio =  ParamUtil.getString(resourceRequest, "folio");
				int status =  ParamUtil.getInteger(resourceRequest, "status");
				System.out.println("folio: "+folio);
				Estatus estatus = new Estatus();
				estatus.setFolio(folio);
				estatus.setCodigoEstatus(status);
				
				String json = "{}";
				try {
					ValidaResponse respuesta = _PagosService.wsActualizarEstatus(estatus);
					JsonObject jsonObj = new JsonObject();
					jsonObj.addProperty("code", respuesta.getCode());
					jsonObj.addProperty("msg", respuesta.getMsg());
					json = jsonObj.toString();
				} catch (PagosException e) {
					e.printStackTrace();
				}
				resourceResponse.getWriter().write(json);
	}

}
