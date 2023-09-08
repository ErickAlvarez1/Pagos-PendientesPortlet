package com.tokio.Pagos.PendientesPortlet.portlet;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.tokio.Pagos.PendientesPortlet.constants.PagosPendientesPortletKeys;
import com.tokio.pagos.PagosServices;
import com.tokio.pagos.Been.ListaPolizasPendientesPagoResponse;
import com.tokio.pagos.Been.PagosCatalogoResponse;
import com.tokio.pagos.Been.PolizaPagar;
import com.tokio.pagos.Constants.PagosServicesKey;
import com.tokio.pagos.Exception.PagosException;

import java.text.NumberFormat;
import java.util.Locale;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
		 property = {
		 "javax.portlet.name="+ PagosPendientesPortletKeys.PagosPendientes,
		 "mvc.command.name=/pagarPolizas"
		 },
		 service = MVCActionCommand.class
		 )

public class PagarPolizas extends BaseMVCActionCommand{
	
	@Reference
	PagosServices _PagosService; 
	
	@Override
	protected void doProcessAction(ActionRequest actionRequest, ActionResponse actionResponse) throws PagosException {
		// TODO Auto-generated method stub
		try{
			
			String usuario = "";// user.getScreenName();
			String pantalla = "pagosPendientes";
			String modulo = "pagosPendientes";
			
			String cliente =  ParamUtil.getString(actionRequest, "txtCodClient");
			String poliza =  ParamUtil.getString(actionRequest, "txtNoPolis");
			int tipoPago = ParamUtil.getInteger(actionRequest, "tipoPago");
			
			System.out.println("tipoPago : " + tipoPago);
			
			
			int active = 1;
			HttpServletRequest originalRequest = PortalUtil
					.getOriginalServletRequest(PortalUtil.getHttpServletRequest(actionRequest));
			
			PagosCatalogoResponse montMax = _PagosService.wsPagosCatalogo(PagosServicesKey.CAT_MON_MAXT, active, usuario, pantalla, modulo);
			
			String fistMontMax = montMax.getCatalogo().get(0).getValor();
			//tipo de pago 1 tarjetas
			// 2 para referencias bancarias
			
			ListaPolizasPendientesPagoResponse pendientesPago = _PagosService.wsListaPolizasPendientesPago(poliza, cliente,fistMontMax, tipoPago, 1, usuario, pantalla, modulo);
			
			if (pendientesPago.getCatalogo().isEmpty()){
				SessionErrors.add(actionRequest , "errorNotData");
				actionResponse.setRenderParameter("jspPage", "/view.jsp");
				return;
			}
			
			String  containsDollar = "";
			
			Locale locale = Locale.forLanguageTag("es-MX");
			NumberFormat format = NumberFormat.getCurrencyInstance(locale);
			
			for (PolizaPagar pp : pendientesPago.getCatalogo()) {
				if(pp.getListaMoneda().getDescripcion().contains("USD")){
					double tpoCambio = _PagosService.getTipoCambio().getTipoCambio();
					containsDollar = "Tipo de Cambio " + format.format(tpoCambio) + " USD  /  MN = conversión de prima de USD a MN";
					break;
				}
			}
			
			
			actionRequest.setAttribute("version", "12022020.1.01");
			actionRequest.setAttribute("key_evopayment", PropsUtil.get("ambiente.configuracion.key_evopayment"));
			actionRequest.setAttribute("tipoPago", tipoPago);
			actionRequest.setAttribute("containsDollar", containsDollar);
			actionRequest.setAttribute("lstPendientes", pendientesPago.getCatalogo());
			actionResponse.setRenderParameter("jspPage", "/pagar.jsp");
			
		} catch (Exception e) {
			 SessionErrors.add(actionRequest , "errorComisiones");
			e.printStackTrace();
		}
	}

}
