package com.tokio.Pagos.PendientesPortlet.portlet;

import com.google.gson.Gson;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.ParamUtil;
import com.tokio.Pagos.PendientesPortlet.bean.SolicitarPagoRequest;
import com.tokio.Pagos.PendientesPortlet.constants.PagosPendientesPortletKeys;
import com.tokio.Pagos.PendientesPortlet.util.CreatePdfReferenciaBancaria;
import com.tokio.Pagos.PendientesPortlet.util.SendMailReferencias;
import com.tokio.pagos.PagosServices;
import com.tokio.pagos.Been.PagoReferencia;
import com.tokio.pagos.Been.SolicitarPagoResponse;
import com.tokio.pagos.Exception.PagosException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.commons.io.FileUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true, property = { "javax.portlet.name=" + PagosPendientesPortletKeys.PagosPendientes,
		"mvc.command.name=/pagos/referenciaBancaria", }, service = MVCResourceCommand.class)

public class GetSolicitudReferencia extends BaseMVCResourceCommand {

	@Reference
	PagosServices _PagosService;

	@Override
	protected void doServeResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse) throws IOException
			 {
		// TODO Auto-generated method stub
		

		String pantalla = PagosPendientesPortletKeys.PANTALLA;

		String pagos = ParamUtil.getString(resourceRequest, "json");

		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		String dateString = format.format(new Date());

		System.out.println("json : " + pagos);
		Gson gson = new Gson();
		SolicitarPagoRequest objData = gson.fromJson(pagos, SolicitarPagoRequest.class);
		PagoReferencia referencia = gson.fromJson(pagos, PagoReferencia.class);
		referencia.setIdTipoMovimiento(17);
		referencia.setIdEstatus(13);
		referencia.setFechaTransaccion(dateString);
		referencia.setTipoPago(17);

		try {
			SolicitarPagoResponse solPago = _PagosService.wsSolicitarPagoReferencia(referencia, "", pantalla);
			if (solPago.getCode() == 0){
				objData.setFolio(solPago.getFolio());
				objData.setRefPago(solPago.getReferencia());
				objData.setIdEstatus(13);
				objData.setIdTipoMovimiento(17);
				objData.setTipoPago(17);
				objData.setFechaTransaccion(dateString);

				File file = new CreatePdfReferenciaBancaria().createFile(objData, resourceRequest);
				System.out.println(file.getAbsolutePath());
				new SendMailReferencias().sendMail(objData.getCorreo().split(","), solPago.getFolio(), file);

				String doc = "{\"documento\" : \" " + Base64.encode(FileUtils.readFileToByteArray(file))
						+ "\",\"titulo\" : \"" + file.getName() + "\", \"code\":0}";
				System.out.println(doc);
				PrintWriter writer = resourceResponse.getWriter();
				writer.write(doc);
			}else{
				String respuesta = "{\"code\":1,\"msj\": \"" + solPago.getMsg() + "\"}";
				PrintWriter writer = resourceResponse.getWriter();
				writer.write(respuesta);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			String respuesta = "{\"code\":1,\"msj\": \" Error al consultar la información \"}";
			PrintWriter writer = resourceResponse.getWriter();
			writer.write(respuesta);
		}
		
		
	}

	

}
