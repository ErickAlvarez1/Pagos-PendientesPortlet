<!-- Modal EjemploSeleccionar -->
<div class="modal" id="modalEjemploSeleccionar" tabindex="-1" role="dialog"
	aria-labelledby="modalEjemploSeleccionarLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg modal-dialog-centered" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title" id="modalEjemploSeleccionarLabel">No haz seleccionado pólizas</h5>
			</div>
			<!--Body-->
			<div class="modal-body">

				<div class="row">
					<div class="col-12">
						<p class="text-center">Para continuar es necesario seleccionar al menos una póliza</p>
					</div>
					<div class="col-md-12 text-center">
						<img alt="ejemplo" src="<%=request.getContextPath()%>/img/ejemplo.gif" width="85%">
					</div>
				</div>
			</div>

			<!--Footer-->
			<div class="modal-footer justify-content">
				<div class="row">
					<div class="col-md-12">
						<button class="btn btn-pink waves-effect waves-light float-right" data-dismiss="modal">Entendido</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<!-- END Modal EjemploSeleccionar -->

<!--  Modal mensaje ok pago con tarjeta -->
<div class="modal mdlsucces" tabindex="-1" role="dialog" id="modal-pago">
	<div class="modal-dialog modal-lg" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
			</div>
			<div class="modal-body">
				<div class="row success-pay-header">
					<div class="col-md-12">
						<p class="header-icon-ok">
							<i class="fas fa-check-circle"></i>
						</p>
						<p>Operación Exitosa</p>
					</div>
					<div class="col-md-12 modal-pago-legend">
						<p>La operación ha sido ejecutada exitosamente</p>
						<p>
							Fecha Transacción: <span id="modal-pago-fecha"> ${auxModal.fechaTransaccion} </span>
						</p>
						<h4>
							Folio: <span id="modal-pago-folio">${auxModal.folio}</span>
						</h4>
						<h4>
							Referencia de Pago: <span id="modal-pago-refpago">${auxModal.refPago}</span>
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
										<tr>
											<c:forEach items="${auxModal.listaPagoPolizas}" var="poliza">
												<td>${poliza.poliza}</td>
												<td>${poliza.certif}</td>
												<td>${poliza.recibo}</td>
												<td>Pagado</td>
												<td>${poliza.primaTotal}</td>
											</c:forEach>
											
										</tr>					
									
									</tbody>
								</table>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-secondary" data-dismiss="modal">Ok</button>
			</div>
		</div>
	</div>
</div>

<!-- End Modal mensaje ok pago con tarjeta -->

<!--  Modal mensaje ok pago con Referenciado -->
<div class="modal mdlsucces" tabindex="-1" role="dialog" id="modalokReferencia">
	<div class="modal-dialog modal-lg" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
			</div>
			<div class="modal-body">
				<div class="row success-pay-header">
					<div class="col-md-12">
						<p class="header-icon-ok">
							<i class="fas fa-check-circle"></i>
						</p>
						<p>Generación Exitosa</p>
					</div>
					<div class="col-md-12 modal-pago-legend">
						<p class="h3">Se le ha enviado un correo con su referencia bancaria</p>

					</div>
					<div class="col-md-12">
						<div class="card" style="width: 100%;">
							<div class="card-body">
								<button type="button" class="btn btn-pink waves-effect waves-light" id="btnDescargarReferencia" titulo="">
									Descargar Referencia</button>
								<div id="divAuxPdf" hidden="true">
									<a id="aPdf" hidden="true"></a>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-secondary" data-dismiss="modal">Ok</button>
			</div>
		</div>
	</div>
</div>

<!-- End Modal mensaje ok pago con tarjeta -->



<!-- Modal Tipos moneda -->
<div class="modal" id="modaltipmoneda" tabindex="-1" role="dialog"
	aria-labelledby="modaltipmonedaLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg modal-dialog-centered" role="document">
		<div class="modal-content">
			<div class="modal-header orange darken-1 white-text">
				<h5 class="modal-title" id="modaltipmonedaLabel">
					<i class="fas fa-exclamation-triangle mr-2"></i>
					Multiples tipos de moneda
				</h5>
			</div>
			<!--Body-->
			<div class="modal-body">

				<div class="row">
					<div class="col-12">
						<span>
							Por favor seleccione un tipo de moneda
						 </span> <br>
					</div>
				</div>
			</div>

			<!--Footer-->
			<div class="modal-footer justify-content-center">
				<div class="row">
					<div class="col-md-6">
						<button class="btn btn-pink waves-effect waves-light" data-dismiss="modal">Entendido</button>
					</div>

				</div>
			</div>
		</div>
	</div>
</div>
<!-- END Modal Tipos moneda -->

<!-- Modal Pago -->
<div class="modal" id="embedded-modal" tabindex="-1" role="dialog"
	aria-labelledby="embedded-modalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg modal-dialog-centered" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title" id="embedded-modalLabel">Pagar</h5>
			</div>
			<!--Body-->
			<div class="modal-body">

				<div id="modal-host">
              <!-- Embedded content is injected here -->
            </div>
			</div>
		</div>
	</div>
</div>
<!-- END Modal Pago -->