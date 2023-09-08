package com.tokio.Pagos.PendientesPortlet.portlet;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Base64;
import java.util.Locale;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.gson.Gson;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
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
import com.tokio.pagos.Been.PagosCatalogoResponse;
import com.tokio.pagos.Been.ValidaResponse;
import com.tokio.pagos.Constants.PagosServicesKey;
import com.tokio.pagos.Exception.PagosException;

/**
 * @author martinfernandojimenezramirez
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=Pagos-PendientesPortlet Portlet",
		"javax.portlet.init-param.template-path=/",
		/*"javax.portlet.init-param.view-template=/view.jsp",*/
		"javax.portlet.init-param.view-template=/inicio.jsp",
		"javax.portlet.name=" + PagosPendientesPortletKeys.PagosPendientes,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.requires-namespaced-parameters=false",
		"com.liferay.portlet.private-request-attributes=false" 
	},
	service = Portlet.class
)
public class PagosPendientesPortlet extends MVCPortlet {
	
	@Reference
	PagosServices _PagosService;
	
	
	private static final Log _log = LogFactoryUtil.getLog(PagosPendientesPortlet.class);
	Gson gson = new Gson();
	ConsultaPagoResponse consultaPagoResponse = new ConsultaPagoResponse();
	
	@Override
	public void doView( RenderRequest renderRequest, RenderResponse renderResponse) 
			throws IOException, PortletException {
		
		try {
			
			/*** Pruebas pagos ****
			Gson gson = new Gson();
			String output1 = "{\"3dsAcsEci\":\"01\",\"amount\":8810.20,\"authentication\":{\"3ds\":{\"acsEci\":\"01\",\"authenticationToken\":\"kFMSL79mZP3Go81bSEEbgG+0hgGO\",\"transactionId\":\"6458a270-876a-4978-99d2-a1fca511a10b\"}},\"authenticationStatus\":\"AUTHENTICATION_ATTEMPTED\",\"authenticationVersion\":\"3DS2\",\"billing\":{\"address\":{\"city\":\"Ciudad México\",\"country\":\"MEX\",\"postcodeZip\":\"06500\",\"stateProvince\":\"MX\",\"street\":\"Av Reforma\"}},\"chargeback\":{\"amount\":0,\"currency\":\"MXN\"},\"creationTime\":\"2022-11-22T19:12:43.516Z\",\"currency\":\"MXN\",\"description\":\"Pago de Polizas\",\"device\":{\"browser\":\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36\",\"ipAddress\":\"189.236.70.98\"},\"id\":\"S2022112213104667\",\"lastUpdatedTime\":\"2022-11-22T19:14:51.782Z\",\"merchant\":\"1101943\",\"merchantAmount\":8810.20,\"merchantCategoryCode\":\"5960\",\"merchantCurrency\":\"MXN\",\"reference\":\"Pago de Polizas\",\"result\":\"SUCCESS\",\"sourceOfFunds\":{\"provided\":{\"card\":{\"brand\":\"MASTERCARD\",\"expiry\":{\"month\":\"2\",\"year\":\"25\"},\"fundingMethod\":\"CREDIT\",\"issuer\":\"BANCA MIFEL SA INSTITUCION DE BANCA MULTIPLE GRUPO FINANCIERO MIFEL\",\"nameOnCard\":\"RICARDO MENDOZA AVILA\",\"number\":\"526972xxxxxx4890\",\"scheme\":\"MASTERCARD\",\"storedOnFile\":\"NOT_STORED\"}},\"type\":\"CARD\"},\"status\":\"CAPTURED\",\"totalAuthorizedAmount\":8810.20,\"totalCapturedAmount\":8810.20,\"totalDisbursedAmount\":0.00,\"totalRefundedAmount\":0.00,\"transaction\":[{\"authentication\":{\"3ds2\":{\"directoryServerId\":\"A000000003\",\"methodCompleted\":false,\"methodSupported\":\"SUPPORTED\",\"protocolVersion\":\"2.1.0\",\"requestorId\":\"10065253*NA1101943_MPGS\",\"requestorName\":\"TOKIO MARINE CIB\"},\"acceptVersions\":\"3DS1,3DS2\",\"channel\":\"PAYER_BROWSER\",\"purpose\":\"PAYMENT_TRANSACTION\",\"redirect\":{\"domainName\":\"secure4.arcot.com\"},\"version\":\"3DS2\"},\"billing\":{\"address\":{\"city\":\"Ciudad México\",\"country\":\"MEX\",\"postcodeZip\":\"06500\",\"stateProvince\":\"MX\",\"street\":\"Av Reforma\"}},\"device\":{\"browser\":\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36\",\"ipAddress\":\"189.236.70.98\"},\"merchant\":\"1101943\",\"order\":{\"amount\":8810.20,\"authenticationStatus\":\"AUTHENTICATION_UNAVAILABLE\",\"chargeback\":{\"amount\":0,\"currency\":\"MXN\"},\"creationTime\":\"2022-11-22T19:12:43.516Z\",\"currency\":\"MXN\",\"id\":\"S2022112213104667\",\"lastUpdatedTime\":\"2022-11-22T19:14:51.782Z\",\"merchantAmount\":8810.20,\"merchantCategoryCode\":\"5960\",\"merchantCurrency\":\"MXN\",\"reference\":\"Pago de Polizas\",\"status\":\"CAPTURED\",\"totalAuthorizedAmount\":0,\"totalCapturedAmount\":0,\"totalDisbursedAmount\":0,\"totalRefundedAmount\":0,\"valueTransfer\":{\"accountType\":\"NOT_A_TRANSFER\"}},\"response\":{\"gatewayCode\":\"DECLINED\",\"gatewayRecommendation\":\"DO_NOT_PROCEED\"},\"result\":\"FAILURE\",\"sourceOfFunds\":{\"provided\":{\"card\":{\"brand\":\"VISA\",\"expiry\":{\"month\":\"6\",\"year\":\"25\"},\"fundingMethod\":\"CREDIT\",\"issuer\":\"BANCO NACIONAL DE MEXICO, S.A.\",\"nameOnCard\":\"RICARDO MENDOZA AVILA\",\"number\":\"403707xxxxxx3454\",\"scheme\":\"VISA\"}},\"type\":\"CARD\"},\"timeOfLastUpdate\":\"2022-11-22T19:12:44.191Z\",\"timeOfRecord\":\"2022-11-22T19:12:43.544Z\",\"transaction\":{\"acquirer\":{\"merchantId\":\"1101943\"},\"amount\":8810.20,\"authenticationStatus\":\"AUTHENTICATION_UNAVAILABLE\",\"currency\":\"MXN\",\"id\":\"trans-506\",\"stan\":\"0\",\"type\":\"AUTHENTICATION\"},\"version\":\"65\"},{\"authentication\":{\"3ds\":{\"acsEci\":\"01\",\"authenticationToken\":\"kFMSL79mZP3Go81bSEEbgG+0hgGO\",\"transactionId\":\"6458a270-876a-4978-99d2-a1fca511a10b\"},\"3ds2\":{\"acsTransactionId\":\"6458a270-876a-4978-99d2-a1fca511a10b\",\"directoryServerId\":\"A000000004\",\"dsTransactionId\":\"6458a270-876a-4978-99d2-a1fca511a10b\",\"methodSupported\":\"NOT_SUPPORTED\",\"protocolVersion\":\"2.1.0\",\"requestorId\":\"MAS00001_INT_MPGS_NA1101943\",\"requestorName\":\"TOKIO MARINE CIB\",\"transactionStatus\":\"A\"},\"acceptVersions\":\"3DS1,3DS2\",\"channel\":\"PAYER_BROWSER\",\"purpose\":\"PAYMENT_TRANSACTION\",\"version\":\"3DS2\"},\"billing\":{\"address\":{\"city\":\"Ciudad México\",\"country\":\"MEX\",\"postcodeZip\":\"06500\",\"stateProvince\":\"MX\",\"street\":\"Av Reforma\"}},\"device\":{\"browser\":\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36\",\"ipAddress\":\"189.236.70.98\"},\"merchant\":\"1101943\",\"order\":{\"amount\":8810.20,\"authenticationStatus\":\"AUTHENTICATION_ATTEMPTED\",\"chargeback\":{\"amount\":0,\"currency\":\"MXN\"},\"creationTime\":\"2022-11-22T19:12:43.516Z\",\"currency\":\"MXN\",\"id\":\"S2022112213104667\",\"lastUpdatedTime\":\"2022-11-22T19:14:51.782Z\",\"merchantAmount\":8810.20,\"merchantCategoryCode\":\"5960\",\"merchantCurrency\":\"MXN\",\"reference\":\"Pago de Polizas\",\"status\":\"CAPTURED\",\"totalAuthorizedAmount\":0,\"totalCapturedAmount\":0,\"totalDisbursedAmount\":0,\"totalRefundedAmount\":0,\"valueTransfer\":{\"accountType\":\"NOT_A_TRANSFER\"}},\"response\":{\"gatewayCode\":\"APPROVED\",\"gatewayRecommendation\":\"PROCEED\"},\"result\":\"SUCCESS\",\"sourceOfFunds\":{\"provided\":{\"card\":{\"brand\":\"MASTERCARD\",\"expiry\":{\"month\":\"2\",\"year\":\"25\"},\"fundingMethod\":\"CREDIT\",\"issuer\":\"BANCA MIFEL SA INSTITUCION DE BANCA MULTIPLE GRUPO FINANCIERO MIFEL\",\"nameOnCard\":\"RICARDO MENDOZA AVILA\",\"number\":\"526972xxxxxx4890\",\"scheme\":\"MASTERCARD\"}},\"type\":\"CARD\"},\"timeOfLastUpdate\":\"2022-11-22T19:14:46.891Z\",\"timeOfRecord\":\"2022-11-22T19:14:46.199Z\",\"transaction\":{\"acquirer\":{\"merchantId\":\"1101943\"},\"amount\":8810.20,\"authenticationStatus\":\"AUTHENTICATION_ATTEMPTED\",\"currency\":\"MXN\",\"id\":\"trans-113\",\"stan\":\"0\",\"type\":\"AUTHENTICATION\"},\"version\":\"65\"},{\"authentication\":{\"3ds\":{\"acsEci\":\"01\",\"authenticationToken\":\"kFMSL79mZP3Go81bSEEbgG+0hgGO\",\"transactionId\":\"6458a270-876a-4978-99d2-a1fca511a10b\"},\"3ds2\":{\"dsTransactionId\":\"6458a270-876a-4978-99d2-a1fca511a10b\",\"protocolVersion\":\"2.1.0\",\"transactionStatus\":\"A\"},\"transactionId\":\"trans-113\",\"version\":\"3DS2\"},\"billing\":{\"address\":{\"city\":\"Ciudad México\",\"country\":\"MEX\",\"postcodeZip\":\"06500\",\"stateProvince\":\"MX\",\"street\":\"Av Reforma\"}},\"device\":{\"browser\":\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36\",\"ipAddress\":\"189.236.70.98\"},\"gatewayEntryPoint\":\"CHECKOUT\",\"merchant\":\"1101943\",\"order\":{\"amount\":8810.20,\"authenticationStatus\":\"AUTHENTICATION_ATTEMPTED\",\"chargeback\":{\"amount\":0,\"currency\":\"MXN\"},\"creationTime\":\"2022-11-22T19:12:43.516Z\",\"currency\":\"MXN\",\"description\":\"Pago de Polizas\",\"id\":\"S2022112213104667\",\"lastUpdatedTime\":\"2022-11-22T19:14:51.782Z\",\"merchantAmount\":8810.20,\"merchantCategoryCode\":\"5960\",\"merchantCurrency\":\"MXN\",\"reference\":\"Pago de Polizas\",\"status\":\"CAPTURED\",\"totalAuthorizedAmount\":8810.20,\"totalCapturedAmount\":8810.20,\"totalDisbursedAmount\":0.00,\"totalRefundedAmount\":0.00},\"response\":{\"acquirerCode\":\"00\",\"cardSecurityCode\":{\"gatewayCode\":\"MATCH\"},\"gatewayCode\":\"APPROVED\"},\"result\":\"SUCCESS\",\"sourceOfFunds\":{\"provided\":{\"card\":{\"brand\":\"MASTERCARD\",\"expiry\":{\"month\":\"2\",\"year\":\"25\"},\"fundingMethod\":\"CREDIT\",\"issuer\":\"BANCA MIFEL SA INSTITUCION DE BANCA MULTIPLE GRUPO FINANCIERO MIFEL\",\"nameOnCard\":\"RICARDO MENDOZA AVILA\",\"number\":\"526972xxxxxx4890\",\"scheme\":\"MASTERCARD\",\"storedOnFile\":\"NOT_STORED\"}},\"type\":\"CARD\"},\"timeOfLastUpdate\":\"2022-11-22T19:14:51.782Z\",\"timeOfRecord\":\"2022-11-22T19:14:48.480Z\",\"transaction\":{\"acquirer\":{\"batch\":20221122,\"id\":\"EGLOBAL\",\"merchantId\":\"1101943\"},\"amount\":8810.20,\"authenticationStatus\":\"AUTHENTICATION_ATTEMPTED\",\"authorizationCode\":\"678444\",\"currency\":\"MXN\",\"id\":\"1\",\"receipt\":\"232619002214\",\"source\":\"INTERNET\",\"stan\":\"2214\",\"terminal\":\"1234\",\"type\":\"PAYMENT\"},\"version\":\"65\"}]}";
			String output2 = "{\"amount\":6840.11,\"billing\":{\"address\":{\"city\":\"Ciudad México\",\"country\":\"MEX\",\"postcodeZip\":\"06500\",\"stateProvince\":\"MX\",\"street\":\"Av Reforma\"}},\"chargeback\":{\"amount\":0,\"currency\":\"MXN\"},\"creationTime\":\"2022-11-22T19:55:49.196Z\",\"currency\":\"MXN\",\"description\":\"Pago de Polizas\",\"device\":{\"browser\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36\",\"ipAddress\":\"189.217.19.155\"},\"id\":\"S20221122135256932\",\"lastUpdatedTime\":\"2022-11-22T19:55:50.070Z\",\"merchant\":\"1101943\",\"merchantAmount\":6840.11,\"merchantCategoryCode\":\"5960\",\"merchantCurrency\":\"MXN\",\"reference\":\"Pago de Polizas\",\"result\":\"SUCCESS\",\"sourceOfFunds\":{\"provided\":{\"card\":{\"brand\":\"AMEX\",\"expiry\":{\"month\":\"3\",\"year\":\"27\"},\"fundingMethod\":\"CREDIT\",\"issuer\":\"AE MEXICO GLOBESTAR CONS REVOLVE\",\"nameOnCard\":\"LAURA E. NAVARRO CASTILLA\",\"number\":\"371775xxxxx1004\",\"scheme\":\"AMEX\",\"storedOnFile\":\"NOT_STORED\"}},\"type\":\"CARD\"},\"status\":\"CAPTURED\",\"totalAuthorizedAmount\":6840.11,\"totalCapturedAmount\":6840.11,\"totalDisbursedAmount\":0.00,\"totalRefundedAmount\":0.00,\"transaction\":[{\"authorizationResponse\":{\"posData\":\"1009S0S00010\",\"transactionIdentifier\":\"003226300216320\"},\"billing\":{\"address\":{\"city\":\"Ciudad México\",\"country\":\"MEX\",\"postcodeZip\":\"06500\",\"stateProvince\":\"MX\",\"street\":\"Av Reforma\"}},\"device\":{\"browser\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36\",\"ipAddress\":\"189.217.19.155\"},\"gatewayEntryPoint\":\"CHECKOUT\",\"merchant\":\"1101943\",\"order\":{\"amount\":6840.11,\"authenticationStatus\":\"AUTHENTICATION_NOT_IN_EFFECT\",\"chargeback\":{\"amount\":0,\"currency\":\"MXN\"},\"creationTime\":\"2022-11-22T19:55:49.196Z\",\"currency\":\"MXN\",\"description\":\"Pago de Polizas\",\"id\":\"S20221122135256932\",\"lastUpdatedTime\":\"2022-11-22T19:55:50.070Z\",\"merchantAmount\":6840.11,\"merchantCategoryCode\":\"5960\",\"merchantCurrency\":\"MXN\",\"reference\":\"Pago de Polizas\",\"status\":\"CAPTURED\",\"totalAuthorizedAmount\":6840.11,\"totalCapturedAmount\":6840.11,\"totalDisbursedAmount\":0.00,\"totalRefundedAmount\":0.00},\"response\":{\"acquirerCode\":\"000\",\"cardSecurityCode\":{\"acquirerCode\":\"Y\",\"gatewayCode\":\"MATCH\"},\"cardholderVerification\":{\"avs\":{\"acquirerCode\":\"K\",\"gatewayCode\":\"NAME_MATCH\"}},\"gatewayCode\":\"APPROVED\"},\"result\":\"SUCCESS\",\"sourceOfFunds\":{\"provided\":{\"card\":{\"brand\":\"AMEX\",\"expiry\":{\"month\":\"3\",\"year\":\"27\"},\"fundingMethod\":\"CREDIT\",\"issuer\":\"AE MEXICO GLOBESTAR CONS REVOLVE\",\"nameOnCard\":\"LAURA E. NAVARRO CASTILLA\",\"number\":\"371775xxxxx1004\",\"scheme\":\"AMEX\",\"storedOnFile\":\"NOT_STORED\"}},\"type\":\"CARD\"},\"timeOfLastUpdate\":\"2022-11-22T19:55:49.949Z\",\"timeOfRecord\":\"2022-11-22T19:55:49.273Z\",\"transaction\":{\"acquirer\":{\"batch\":1275,\"id\":\"AMEXGWS\",\"merchantId\":\"9359999555\"},\"amount\":6840.11,\"authenticationStatus\":\"AUTHENTICATION_NOT_IN_EFFECT\",\"authorizationCode\":\"270291\",\"currency\":\"MXN\",\"id\":\"1\",\"receipt\":\"002211226650\",\"source\":\"INTERNET\",\"stan\":\"6650\",\"terminal\":\"5678\",\"type\":\"AUTHORIZATION\"},\"version\":\"65\"},{\"authorizationResponse\":{\"posData\":\"1009S0S00010\",\"transactionIdentifier\":\"003226300216320\"},\"billing\":{\"address\":{\"city\":\"Ciudad México\",\"country\":\"MEX\",\"postcodeZip\":\"06500\",\"stateProvince\":\"MX\",\"street\":\"Av Reforma\"}},\"device\":{\"browser\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36\",\"ipAddress\":\"189.217.19.155\"},\"gatewayEntryPoint\":\"AUTO\",\"merchant\":\"1101943\",\"order\":{\"amount\":6840.11,\"authenticationStatus\":\"AUTHENTICATION_NOT_IN_EFFECT\",\"chargeback\":{\"amount\":0,\"currency\":\"MXN\"},\"creationTime\":\"2022-11-22T19:55:49.196Z\",\"currency\":\"MXN\",\"description\":\"Pago de Polizas\",\"id\":\"S20221122135256932\",\"lastUpdatedTime\":\"2022-11-22T19:55:50.070Z\",\"merchantAmount\":6840.11,\"merchantCategoryCode\":\"5960\",\"merchantCurrency\":\"MXN\",\"reference\":\"Pago de Polizas\",\"status\":\"CAPTURED\",\"totalAuthorizedAmount\":6840.11,\"totalCapturedAmount\":6840.11,\"totalDisbursedAmount\":0.00,\"totalRefundedAmount\":0.00},\"response\":{\"acquirerCode\":\"000\",\"acquirerMessage\":\"000 DataCaptureRequest request successful.\",\"cardSecurityCode\":{\"acquirerCode\":\"Y\",\"gatewayCode\":\"MATCH\"},\"cardholderVerification\":{\"avs\":{\"acquirerCode\":\"K\",\"gatewayCode\":\"NAME_MATCH\"}},\"gatewayCode\":\"APPROVED\"},\"result\":\"SUCCESS\",\"sourceOfFunds\":{\"provided\":{\"card\":{\"brand\":\"AMEX\",\"expiry\":{\"month\":\"3\",\"year\":\"27\"},\"fundingMethod\":\"CREDIT\",\"issuer\":\"AE MEXICO GLOBESTAR CONS REVOLVE\",\"nameOnCard\":\"LAURA E. NAVARRO CASTILLA\",\"number\":\"371775xxxxx1004\",\"scheme\":\"AMEX\",\"storedOnFile\":\"NOT_STORED\"}},\"type\":\"CARD\"},\"timeOfLastUpdate\":\"2022-11-22T19:55:50.070Z\",\"timeOfRecord\":\"2022-11-22T19:55:49.954Z\",\"transaction\":{\"acquirer\":{\"batch\":1275,\"id\":\"AMEXGWS\",\"merchantId\":\"9359999555\"},\"amount\":6840.11,\"authenticationStatus\":\"AUTHENTICATION_NOT_IN_EFFECT\",\"authorizationCode\":\"270291\",\"currency\":\"MXN\",\"id\":\"1\",\"receipt\":\"2211226651\",\"source\":\"INTERNET\",\"stan\":\"6651\",\"terminal\":\"5678\",\"type\":\"CAPTURE\"},\"version\":\"65\"}]}";
			String output3 = "{\"3dsAcsEci\":\"02\",\"amount\":21800.99,\"authentication\":{\"3ds\":{\"acsEci\":\"02\",\"authenticationToken\":\"jHyn+7YFi1EUAREAAAAvNUe6Hv8=\",\"transactionId\":\"rv9o22xmRwSM0nwwoUole6DvWCI=\"},\"3ds1\":{\"paResStatus\":\"Y\",\"veResEnrolled\":\"Y\"}},\"authenticationStatus\":\"AUTHENTICATION_SUCCESSFUL\",\"authenticationVersion\":\"3DS1\",\"billing\":{\"address\":{\"city\":\"Ciudad México\",\"country\":\"MEX\",\"postcodeZip\":\"06500\",\"stateProvince\":\"MX\",\"street\":\"Av Reforma\"}},\"chargeback\":{\"amount\":0,\"currency\":\"MXN\"},\"creationTime\":\"2023-04-21T22:32:00.321Z\",\"currency\":\"MXN\",\"description\":\"Pago de Polizas\",\"device\":{\"browser\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36 Edg/112.0.1722.54\",\"ipAddress\":\"201.137.98.141\"},\"id\":\"S2023421173131781\",\"lastUpdatedTime\":\"2023-04-21T22:32:08.029Z\",\"merchant\":\"TEST1101943\",\"merchantAmount\":21800.99,\"merchantCategoryCode\":\"5960\",\"merchantCurrency\":\"MXN\",\"reference\":\"Pago de Polizas\",\"result\":\"SUCCESS\",\"sourceOfFunds\":{\"provided\":{\"card\":{\"brand\":\"MASTERCARD\",\"expiry\":{\"month\":\"1\",\"year\":\"39\"},\"fundingMethod\":\"CREDIT\",\"issuer\":\"CITIBANK N.A.\",\"nameOnCard\":\"prueba\",\"number\":\"542418xxxxxx1732\",\"scheme\":\"MASTERCARD\",\"storedOnFile\":\"NOT_STORED\"}},\"type\":\"CARD\"},\"status\":\"CAPTURED\",\"totalAuthorizedAmount\":21800.99,\"totalCapturedAmount\":21800.99,\"totalDisbursedAmount\":0.00,\"totalRefundedAmount\":0.00,\"transaction\":[{\"authentication\":{\"3ds\":{\"acsEci\":\"02\",\"authenticationToken\":\"jHyn+7YFi1EUAREAAAAvNUe6Hv8=\",\"transactionId\":\"rv9o22xmRwSM0nwwoUole6DvWCI=\"},\"3ds1\":{\"paResStatus\":\"Y\",\"veResEnrolled\":\"Y\"},\"acceptVersions\":\"3DS1,3DS2\",\"channel\":\"PAYER_BROWSER\",\"payerInteraction\":\"REQUIRED\",\"purpose\":\"PAYMENT_TRANSACTION\",\"redirect\":{\"domainName\":\"ap.gateway.mastercard.com\"},\"version\":\"3DS1\"},\"billing\":{\"address\":{\"city\":\"Ciudad México\",\"country\":\"MEX\",\"postcodeZip\":\"06500\",\"stateProvince\":\"MX\",\"street\":\"Av Reforma\"}},\"device\":{\"browser\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36 Edg/112.0.1722.54\",\"ipAddress\":\"201.137.98.141\"},\"merchant\":\"TEST1101943\",\"order\":{\"amount\":21800.99,\"authenticationStatus\":\"AUTHENTICATION_SUCCESSFUL\",\"chargeback\":{\"amount\":0,\"currency\":\"MXN\"},\"creationTime\":\"2023-04-21T22:32:00.321Z\",\"currency\":\"MXN\",\"id\":\"S2023421173131781\",\"lastUpdatedTime\":\"2023-04-21T22:32:08.029Z\",\"merchantAmount\":21800.99,\"merchantCategoryCode\":\"5960\",\"merchantCurrency\":\"MXN\",\"reference\":\"Pago de Polizas\",\"status\":\"CAPTURED\",\"totalAuthorizedAmount\":0,\"totalCapturedAmount\":0,\"totalDisbursedAmount\":0,\"totalRefundedAmount\":0,\"valueTransfer\":{\"accountType\":\"NOT_A_TRANSFER\"}},\"response\":{\"gatewayCode\":\"APPROVED\",\"gatewayRecommendation\":\"PROCEED\"},\"result\":\"SUCCESS\",\"sourceOfFunds\":{\"provided\":{\"card\":{\"brand\":\"MASTERCARD\",\"expiry\":{\"month\":\"1\",\"year\":\"39\"},\"fundingMethod\":\"CREDIT\",\"issuer\":\"CITIBANK N.A.\",\"nameOnCard\":\"prueba\",\"number\":\"542418xxxxxx1732\",\"scheme\":\"MASTERCARD\"}},\"type\":\"CARD\"},\"timeOfLastUpdate\":\"2023-04-21T22:32:05.937Z\",\"timeOfRecord\":\"2023-04-21T22:32:00.349Z\",\"transaction\":{\"acquirer\":{\"merchantId\":\"1101943\"},\"amount\":21800.99,\"authenticationStatus\":\"AUTHENTICATION_SUCCESSFUL\",\"currency\":\"MXN\",\"id\":\"trans-723\",\"stan\":\"0\",\"type\":\"AUTHENTICATION\"},\"version\":\"65\"},{\"authentication\":{\"3ds\":{\"acsEci\":\"02\",\"authenticationToken\":\"jHyn+7YFi1EUAREAAAAvNUe6Hv8=\",\"transactionId\":\"rv9o22xmRwSM0nwwoUole6DvWCI=\"},\"3ds1\":{\"paResStatus\":\"Y\",\"veResEnrolled\":\"Y\"},\"transactionId\":\"trans-723\",\"version\":\"3DS1\"},\"billing\":{\"address\":{\"city\":\"Ciudad México\",\"country\":\"MEX\",\"postcodeZip\":\"06500\",\"stateProvince\":\"MX\",\"street\":\"Av Reforma\"}},\"device\":{\"browser\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36 Edg/112.0.1722.54\",\"ipAddress\":\"201.137.98.141\"},\"gatewayEntryPoint\":\"CHECKOUT\",\"merchant\":\"TEST1101943\",\"order\":{\"amount\":21800.99,\"authenticationStatus\":\"AUTHENTICATION_SUCCESSFUL\",\"chargeback\":{\"amount\":0,\"currency\":\"MXN\"},\"creationTime\":\"2023-04-21T22:32:00.321Z\",\"currency\":\"MXN\",\"description\":\"Pago de Polizas\",\"id\":\"S2023421173131781\",\"lastUpdatedTime\":\"2023-04-21T22:32:08.029Z\",\"merchantAmount\":21800.99,\"merchantCategoryCode\":\"5960\",\"merchantCurrency\":\"MXN\",\"reference\":\"Pago de Polizas\",\"status\":\"CAPTURED\",\"totalAuthorizedAmount\":21800.99,\"totalCapturedAmount\":21800.99,\"totalDisbursedAmount\":0.00,\"totalRefundedAmount\":0.00},\"response\":{\"acquirerCode\":\"00\",\"acquirerMessage\":\"APPROVED OR COMPLETED SUCCESSFULLY\",\"cardSecurityCode\":{\"acquirerCode\":\"0\",\"gatewayCode\":\"NOT_PROCESSED\"},\"cardholderVerification\":{\"avs\":{\"acquirerCode\":\"A\",\"gatewayCode\":\"ADDRESS_MATCH\"}},\"gatewayCode\":\"APPROVED\"},\"result\":\"SUCCESS\",\"sourceOfFunds\":{\"provided\":{\"card\":{\"brand\":\"MASTERCARD\",\"expiry\":{\"month\":\"1\",\"year\":\"39\"},\"fundingMethod\":\"CREDIT\",\"issuer\":\"CITIBANK N.A.\",\"nameOnCard\":\"prueba\",\"number\":\"542418xxxxxx1732\",\"scheme\":\"MASTERCARD\",\"storedOnFile\":\"NOT_STORED\"}},\"type\":\"CARD\"},\"timeOfLastUpdate\":\"2023-04-21T22:32:08.029Z\",\"timeOfRecord\":\"2023-04-21T22:32:07.919Z\",\"transaction\":{\"acquirer\":{\"batch\":1,\"id\":\"EGLOBAL\",\"merchantId\":\"1101943\"},\"amount\":21800.99,\"authenticationStatus\":\"AUTHENTICATION_SUCCESSFUL\",\"authorizationCode\":\"026787\",\"currency\":\"MXN\",\"id\":\"1\",\"receipt\":\"311122000215\",\"source\":\"INTERNET\",\"stan\":\"215\",\"terminal\":\"5678\",\"type\":\"PAYMENT\"},\"version\":\"65\"}]}";
			ConsultaPagoResponse consultaPagoResponse = new ConsultaPagoResponse();
			consultaPagoResponse = gson.fromJson(output3, ConsultaPagoResponse.class);
			
			String folioFinal = getFolioResponse(  consultaPagoResponse );
			System.err.println("folioFinal:" + folioFinal );
			System.err.println("consultaPagoResponse.getResult(): " + consultaPagoResponse.getResult());
			System.err.println("bool: " + !consultaPagoResponse.getResult().equals("SUCCESS") );
			
			/*** Pruebas pagos ****/
			getCurUrl( renderRequest );
			
			String usuario = "";// user.getScreenName();
			String pantalla = "pagosPendientes";
			String modulo = "pagosPendientes";
			int active = 1;
			Locale locale = Locale.forLanguageTag("es-MX");
			NumberFormat format = NumberFormat.getCurrencyInstance(locale);
			
			String msjFin = "El monto máximo de cobro es de ";
			double tpoCambio = _PagosService.getTipoCambio().getTipoCambio();
			PagosCatalogoResponse montMax = _PagosService.wsPagosCatalogo(PagosServicesKey.CAT_MON_MAXT, active, usuario, pantalla, modulo);
			String fistMontMax = montMax.getCatalogo().get(0).getValor();
			if(Validator.isNumber(fistMontMax)){
				double auxMonto = Double.parseDouble(fistMontMax)/tpoCambio;
				msjFin += format.format(Double.parseDouble(fistMontMax)) + " M.N. / " + format.format(  auxMonto)  + " USD";
			}
			renderRequest.setAttribute("msjFin", msjFin);
		} catch (PagosException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		super.doView(renderRequest, renderResponse);
	}
	
	String getFolioResponse( ConsultaPagoResponse consultaPagoResponse ){
		String auxFolio = "";
		String auxType = "";
		for (int i = consultaPagoResponse.getTransaction().size(); i > 0; i--) {
			auxFolio = consultaPagoResponse.getTransaction().get(i-1).getTransaction().getReceipt();
			auxType = consultaPagoResponse.getTransaction().get(i-1).getTransaction().getType();
			System.err.println("auxType: " + auxType);
			if ( auxType.equals("PAYMENT") || auxType.equals("CAPTURE") ){
				if ( Validator.isNotNull(auxFolio) ){					
					System.err.println("auxFolio: " + auxFolio);
					return auxFolio;
				}				
			}
			else{
				return null;
			}
		}
		return null;
	}
	
	void getCurUrl( RenderRequest renderRequest ){
		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
		if ( themeDisplay.getURLCurrent().contains( "comprobante?inf" ) ){
			System.err.println("comprobar pago");
			getInfoPago( renderRequest );
		}
		renderRequest.setAttribute("urlCurrent", themeDisplay.getURLCurrent() );
	}
	
	void getInfoPago( RenderRequest renderRequest ){
		
		/*final PortletSession psession = renderRequest.getPortletSession();*/
		HttpServletRequest originalRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(renderRequest));
        /*String folio = originalRequest.getParameter("folio");*/
        String infoEncode = originalRequest.getParameter("inf");
		/*String auxNombre = "LIFERAY_SHARED_F=" + folio ;
		String infoPago = (String) psession.getAttribute(auxNombre, PortletSession.APPLICATION_SCOPE);
		System.err.println("auxNombre: " + auxNombre);*/
		if ( Validator.isNull(infoEncode) ) {
			renderRequest.setAttribute("strTitulo", "Sin Información" );
			SessionErrors.add(renderRequest, "errorConocido");
			renderRequest.setAttribute("errorMsg", "Sin Información de Folio");
		}else{
			byte[] decodedJsonBytes = Base64.getDecoder().decode(infoEncode);
			String decodedStringJson = new String(decodedJsonBytes);
			System.err.println("infoPago: " + decodedStringJson);
			SolicitarPagoRequest solicitarPagoRequest = gson.fromJson(decodedStringJson, SolicitarPagoRequest.class);
			/*SolicitarPagoRequest solicitarPagoRequest = gson.fromJson(infoPago, SolicitarPagoRequest.class);*/
			verificaPagoEvopayments( renderRequest, solicitarPagoRequest );
			/*renderRequest.setAttribute("strTitulo", "Operación Exitosa" );*/
			renderRequest.setAttribute("objPago", solicitarPagoRequest );
		}
	}
	
	void verificaPagoEvopayments (RenderRequest renderRequest, SolicitarPagoRequest solicitarPagoRequest ) {
		
		try {
			String apiKey = PropsUtil.get("ambiente.configuracion.key_evopayment");
			String apiPass = PropsUtil.get("ambiente.configuracion.pass_evopayment");
			String authKey = "Basic " + Base64.getEncoder().encodeToString(("merchant."+apiKey+":"+apiPass).getBytes("UTF-8"));
			Client client = Client.create();
			WebResource webResource = client.resource("https://evopaymentsmexico.gateway.mastercard.com/api/rest/version/62/merchant/"+apiKey+"/order/"+solicitarPagoRequest.getFolio());
			ClientResponse response = 
					webResource.header(HttpHeaders.AUTHORIZATION,  authKey )
					.accept(MediaType.APPLICATION_JSON)
					.type(MediaType.APPLICATION_JSON)
					.get(ClientResponse.class);
			
			String output = response.getEntity(String.class);
			
			_log.info("https://evopaymentsmexico.gateway.mastercard.com/api/rest/version/62/merchant/"+apiKey+"/order/"+solicitarPagoRequest.getFolio() );
			_log.info(output);
			
			if (response.getStatus() == 200) {
		        System.err.println("Confirma pago Output from Server .... ");
		        System.out.println(output + "\n");
		        consultaPagoResponse = gson.fromJson(output, ConsultaPagoResponse.class);
		        boolean fPagoExito = verificaEstatusEvopayments ( solicitarPagoRequest, renderRequest );
		        if ( fPagoExito ) {
		        	renderRequest.setAttribute("strTitulo", "Operación Exitosa" );
		        	renderRequest.setAttribute("classStatus", "success-pay-header" );		        	
		        }else{
		        	renderRequest.setAttribute("strTitulo", "Pago No Realizado" );
		        	renderRequest.setAttribute("classStatus", "error-pay-header" );		        	
		        }
			}else{
				renderRequest.setAttribute("strTitulo", "Pago No Realizado" );
				renderRequest.setAttribute("classStatus", "error-pay-header" );
				System.err.println("Error Output from Server .... ");
				System.err.println(response.getStatus());
				return;
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	boolean verificaEstatusEvopayments ( SolicitarPagoRequest objData, RenderRequest renderRequest ){
		try {
			String folioFinal = getFolioResponse(  consultaPagoResponse );
			_log.info("idtransaccion: " +folioFinal + "---Result: " + consultaPagoResponse.getResult() + "---Bool: " + !consultaPagoResponse.getResult().equals("SUCCESS"));
			System.err.println("consultaPagoResponse");
			System.err.println(consultaPagoResponse);
			if ( Validator.isNull(folioFinal)  || !consultaPagoResponse.getResult().equals("SUCCESS")  ) {  /*consultaPagoResponse.getTransaction().get(1).getTransaction().getReceipt()*/
				System.out.println("Entre error conssulta pago");
				return false;
				
			} else {
				System.err.println("Pago exitoso TERMINADO");
				
				System.err.println("consultaPagoResponse");
				System.err.println(consultaPagoResponse);
				
				ValidaResponse respuesta = _PagosService.wsActualizarEstatus(generaEstatus(objData, consultaPagoResponse));
				System.out.println(respuesta);
				objData.setRefPago(folioFinal);
				File documentosReq = new CreatePdfPagosRealizados().createFile(objData, renderRequest);
				String mails = objData.getCorreo();
				String[] listMails = mails.split(",");
				System.err.println("Envia correo comprobante");
				boolean envioMail = new SendMailPagos().sendMail(listMails, documentosReq, objData);
				System.err.println("Envia correo comprobante 2");
				
				if(envioMail){
					_log.info( "Se envió correo comprobante de pago");
				}else{
					_log.info( "NO Se envió correo comprobante de pago");					
				}
				return true;
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
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
	
}