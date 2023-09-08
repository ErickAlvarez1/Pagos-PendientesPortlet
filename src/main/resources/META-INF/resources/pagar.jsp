<%@ include file="./init.jsp"%>
<%@ include file="./modales.jsp"%>

<portlet:resourceURL id="/pagos/getSessionId" var="getSessionId" cacheability="FULL" />
<portlet:resourceURL id="/solicitudPago" var="solicitudPago" cacheability="FULL" />
<portlet:resourceURL id="/pagos/pagoestatus" var="cancelarPago" cacheability="FULL">

<portlet:param name="status" value="15" /></portlet:resourceURL>
<portlet:resourceURL id="/pagos/pagoestatus" var="errorPago" cacheability="FULL">
<portlet:param name="status" value="18" /></portlet:resourceURL>

<portlet:resourceURL id="/pagos/referenciaBancaria" var="referenciaBancaria" cacheability="FULL" />

<portlet:resourceURL id="/validaIntencion" var="validaIntencionURL" cacheability="FULL" />

<fmt:setLocale value="es_MX" />
<link href="<%=request.getContextPath()%>/css/main.css?v=${ version }" media="all" rel="stylesheet" type="text/css" />

<div class="container-fluid text-uppercase">
	<h3 class="tit-reporte">Pólizas por pagar</h3>

	<!-----------------------------segunda tabla prueba--------------------------------------------------------->
	<div class="container-fluid mt-5">
		<div class="row tab-pane">
			<a class="btn btn-default" href="pago-con-tarjeta">Volver</a>

			<div class="table-wrapper col-sm-12">
				<p ${ tipoPago == 1 ? "" : "hidden" }>
					${ containsDollar }
				</p>
				<div class="row justify-content-md-center">
					<div class="col.md-5">
						<div class="md-form form-group" ${ tipoPago == 1 ? "hidden" : "" }>
							<select name="sel_moneda" id="sel_moneda" class="mdb-select form-control-sel">
								<!-- option value="" selected>Seleccione una opci&oacute;n</option --> 
								
							</select>
							<label for="dc_movimientos"> Filtro moneda </label>
						</div>
					</div>
				</div>
				

				<table class="table table-striped table-bordered" style="width: 100%;" id="table1">
					<thead>
						<tr>
							<th>Recibo</th>
							<th>Asegurado</th>
							<th class="hidden">Codigo Asegurado</th>
							<th>Póliza</th>
							<th>Endoso</th>
							<th>Documento</th>
							<th>Moneda</th>
							<th>Prima Neta</th>
							<th>IVA</th>
							<th>Recargos</th>
							<th>Derechos</th>
							<th>Red. de Prima</th>
							<th>Prima Total</th>
						</tr>
					</thead>
					<tbody>
						<%
							int totalPrima = 0;
							pageContext.setAttribute("totalPrima", totalPrima);
						%>
						<c:forEach items="${ lstPendientes }" var="pendientes_item">
							<tr class="campos_moneda" moneda="${ pendientes_item.listaMoneda.descripcion }" intencion="${ pendientes_item.intencion }" poliza="${pendientes_item.poliza}" certif="${ pendientes_item.certif }" recibo="${ pendientes_item.recibo }">
								<c:set var="totalPrima" value="${totalPrima + pendientes_item.primaTotal}" />
								<td class="recibo">${ pendientes_item.recibo }</td>
								<td class="asegurado" style="max-width: 145px;">${ pendientes_item.asegurado }</td>
								<td class="codigoAsegurado hidden">${ pendientes_item.codigoAsegurado }</td>
								<td class="poliza" style="max-width: 110px;">${ pendientes_item.poliza }</td>
								<td class="certif">${ pendientes_item.certif }</td>
								<td class="numdoc">${ pendientes_item.numdoc }</td>
								<td class="moneda">${ pendientes_item.listaMoneda.descripcion }</td>
								<td data="${ pendientes_item.primaNeta }" class="primaNeta">
									<fmt:formatNumber value="${ pendientes_item.primaNeta }" type="currency" />
								</td>
								<td data="${ pendientes_item.iva }" class="iva">
									<fmt:formatNumber value="${ pendientes_item.iva }" type="currency" />
								</td>
								<td data="${ pendientes_item.recargos }" class="recargos">
									<fmt:formatNumber value="${ pendientes_item.recargos }" type="currency" />
								</td>
								<td data="${ pendientes_item.derechos }" class="derechos">
									<fmt:formatNumber value="${ pendientes_item.derechos }" type="currency" />
								</td>
								<td data="${ pendientes_item.reduccionPrima }" class="reduccionPrima">
									<fmt:formatNumber value="${ pendientes_item.reduccionPrima }" type="currency" />
								</td>
								<td data="${ pendientes_item.primaTotal }" class="primaTotal">
									<fmt:formatNumber value="${ pendientes_item.primaTotal }" type="currency" />
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
			<div class="table-wrapper offset-sm-9 col-sm-3">
				<table class="table table-bordered" style="width: 100%;" id="table3">
					<thead>
						<tr>
							<th></th>
							<th>
								<liferay-ui:message key="PagosPendientesPortlet.primaTotal" />
							</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td>
								<liferay-ui:message key="ModuloComisionesPortlet.totales" />
							</td>
							<td data="${totalPrima}" id="primaTotal-pre">
								<fmt:formatNumber value="${totalPrima}" type="currency" />
							</td>
						</tr>
					</tbody>
				</table>
			</div>
			<div class="col-sm-12 text-center grid-options">
				<button type="button" class="btn btn-blue btn-sm waves-effect waves-light" id="LeftTodos">&lt;&lt;</button>
				<button type="button" class="btn btn-blue btn-sm waves-effect waves-light" id="LeftMove">&lt;</button>
				<button type="button" class="btn btn-blue btn-sm waves-effect waves-light" id="RightMove">&gt;</button>
				<button type="button" class="btn btn-blue btn-sm waves-effect waves-light" id="RightTodos">&gt;&gt;</button>
			</div>
			<div class="table-wrapper col-sm-12 float-right">
				<!--<table class="table data-table table-striped table-bordered" style="width:100%;" id="table2">-->
				<table class="table table-striped table-bordered" style="width: 100%;" id="table2">
					<thead>
						<tr>
							<th>Recibo</th>
							<th>Asegurado</th>
							<th class="hidden">Codigo Asegurado</th>
							<th>Póliza</th>
							<th>Endoso</th>
							<th>Documento</th>
							<th>Moneda</th>
							<th>Prima Neta</th>
							<th>IVA</th>
							<th>Recargos</th>
							<th>Derechos</th>
							<th>Red. de Prima</th>
							<th>Prima Total</th>
						</tr>
					</thead>
					<tbody>

					</tbody>
				</table>
			</div>
			<div class="table-wrapper offset-sm-9 col-sm-3">
				<table class="table table-bordered" style="width: 100%;" id="table4">
					<thead>
						<tr>
							<th></th>
							<th>
								<liferay-ui:message key="PagosPendientesPortlet.primaTotal" />
							</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td>
								<liferay-ui:message key="ModuloComisionesPortlet.totales" />
							</td>
							<td id="primaTotal-pos">
								<fmt:formatNumber value="${0}" type="currency" />
							</td>
						</tr>
					</tbody>
				</table>
			</div>
			<div class="offset-sm-9 col-sm-3">
				<div class="form-group">
					<label for="email">Correo Electrónico</label>
					<input type="email" class="form-control" id="email" aria-describedby="emailHelp" placeholder="Ingrese su correo electronico" required>
					<small id="emailHelp" class="form-text text-muted">Ingrese un correo electrónico valido a donde será enviado su comprobante de pago.</small>
				</div>
				<form id="pay-form" ${(tipoPago == 1 ) ? '' : 'hidden'}>
					<button class="btn btn-pink" style="width: 100%; margin: 0.375rem 0;" type="submit" id="btn-pagar">PAGAR</button>
				</form>
				<form id="referencia-form" ${(tipoPago == 2 ) ? '' : 'hidden'}>
					<button class="btn btn-pink" style="width: 100%; margin: 0.375rem 0;" type="submit" id="btn-pagar2">Generar Referencia Bancaria</button>
				</form>
			</div>

		</div>
	</div>

	
</div>


<!-------------------------------------------------------------------------------------->

       
	<%-- <div id="embed-target"> </div> --%> 
<%--<input type="button" value="Pay with Embedded Page" onclick="Checkout.showEmbeddedPage('#embed-target');" />
<input type="button" value="Pay with Payment Page" onclick="Checkout.showPaymentPage();" />  --%>


<script>
	var getSessionId = "${getSessionId} ";
	var solicitudPago = "${solicitudPago}";
	var referenciaBancaria = "${referenciaBancaria}";
	var tipoPago = ${tipoPago};
	var key_evopayment = "${key_evopayment}";
	var errorPago = "${errorPago}";
	var cancelarPago = "${cancelarPago}";
	var validaIntencionURL = "${validaIntencionURL}";
</script>

<script src="https://evopaymentsmexico.gateway.mastercard.com/static/checkout/checkout.min.js" data-complete="completeCallback" data-error="errorCallback" data-cancel="cancelCallback"></script>
<script src="<%=request.getContextPath()%>/js/main.js?v=${ versionC }"></script>

<script src="<%=request.getContextPath()%>/js/pagar.js?v=${ versionC }"></script>