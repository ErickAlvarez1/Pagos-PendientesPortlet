<%@ include file="./init.jsp"%>

<link href="<%=request.getContextPath()%>/css/main.css?v=${ versionC }" media="all" rel="stylesheet" type="text/css" />

<div style="position: relative;">
	<liferay-ui:error key="errorConocido" message="${errorMsg}" />
</div>

<div class="container comprobante">
	<%-- <div class="row success-pay-header"> --%>
	<div class="row ${ classStatus }">
		<div class="col-md-12">
			<p class="header-icon-ok">
				<i class="fas fa-check-circle"></i>
			</p>
			<%-- <p>Operación Exitosa</p>  --%>
			<p>${ strTitulo }</p>
		</div>
		<div class="col-md-12 modal-pago-legend">
			<p>La operación ha sido ejecutada exitosamente</p>
			<p>
				Fecha Transacción: <span id="modal-pago-fecha"> ${objPago.fechaTransaccion} </span>
			</p>
			<h4>
				Folio: <span id="modal-pago-folio">${objPago.folio}</span>
			</h4>
			<h4>
				Referencia de Pago: <span id="modal-pago-refpago">${objPago.folio}</span>
			</h4>
		</div>
		<div class="col-md-12">
			<div class="card" style="width: 100%;">
				<div class="card-body">
					<h5 class="card-title">Detalle de la Transacción</h5>
					<table class="table table-hover table-sm table-bordered">
						<thead class="thead-light">
							<tr>
								<th>Póliza</th>
								<th>Endoso</th>
								<th>No. Recibo</th>
								<th>Estatus</th>
								<th>Prima Total</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${objPago.listaPagoPolizas}" var="poliza">
								<tr>
									<td>${poliza.poliza}</td>
									<td>${poliza.certif}</td>
									<td>${poliza.recibo}</td>
									<td>Pagado</td>
									<td>${poliza.primaTotal}</td>
								</tr>					
							</c:forEach>
								
						
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="col-md-12 mt-5">
			<button type="button" class="btn btn-secondary pull-right" onclick="goToHome();">Ok</button>
		</div>
	</div>
</div>

<script src="<%=request.getContextPath()%>/js/main.js?v=${ versionC }"></script>