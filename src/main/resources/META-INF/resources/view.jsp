<%@ include file="./init.jsp"%>
<%@ include file="./modales.jsp"%>
<liferay-portlet:actionURL name="/pagarPolizas" var="pagarPolizas">
</liferay-portlet:actionURL>



<liferay-ui:error key="errorNotData" message="pago.errorNotData" />

<link href="<%=request.getContextPath()%>/css/main.css?v=${ versionC }" media="all" rel="stylesheet" type="text/css" />
<style>
.nav.nav-tabs {
	margin-bottom: 15px;
}

.nav.nav-tabs .nav-item .nav-link {
	font-size: 15px;
	color: aliceblue;
}

.site-wrapper a:not ([href] ):not ([tabindex] ):hover {
	color: greenyellow;
}
</style>

<section class="site-wrapper">
	<header></header>

	<section id="acceso-pagos" >
		<form action="${ pagarPolizas }" method="POST">
			<div class="container-fluid">
				<div class="section-heading">
					<div class="container-fluid">
						<h3 class="title text-center">
							<liferay-ui:message key="Pagos-AccesoPorlet.titulo2Acceso" />
						</h3>
						<div class="row justify-content-md-center">
							<div class="section-nav-wrapper col-md-6">
								<ul class="nav nav-tabs md-tabs nav-justified light-blue darken-4" role="tablist">

									<li class="nav-item"> <%-- active --%>
										<a class="nav-link active" data-toggle="tab" id="1" role="tab"> CON TARJETA </a>
									</li>
									<li class="nav-item" hidden="true">
										<a class="nav-link" data-toggle="tab" id="2" role="tab"> CON REFERENCIA BANCARIA </a>
									</li>
								</ul>
							</div>
						</div>

						<h4 id="titulo2Pagos" class="title text-center ">
							<liferay-ui:message key="Pagos-AccesoPorlet.tituloAcceso" />
						</h4>

					</div>
				</div>

				<div class="row">
					<div class="col-md-12 infVer">
						<p class="text-right" style="margin-bottom: 0px;">${ msjFin }</p>
					</div>
					<div class="col-md-12">
						<div class="md-form form-group">
							<input id="txtNoPolis" type="text" name="txtNoPolis" class="form-control" style="text-transform: uppercase;"  required>
							<label id="lbltxtNoPolis" for="txtNoPolis">
								<liferay-ui:message key="Pagos-AccesoPorlet.noPoliza" />
							</label>
						</div>
					</div>
					<div class="col-md-12">
						<div class="md-form form-group">
							<input id="txtCodClient" type="text" name="txtCodClient" class="form-control" style="text-transform: uppercase;" required>
							<label id="lbltxtCodClient" for="txtCodClient">
								<liferay-ui:message key="Pagos-AccesoPorlet.noCliente" />
							</label>
						</div>
					</div>
				</div>

				<div class="row">
					<div class="col-md-6">
						<button class="btn btn-pink" id="buscarAcceso" type="submit" disabled>Buscar</button>
					</div>
					<div class="col-md-6 infVer">
						<p class="text-right">
							<liferay-ui:message key="Pagos-AccesoPorlet.leyenda" />
						</p>
					</div>
				</div>
			</div>
			<input type="hidden" id="tipoPago" name="tipoPago" value="1" hidden>
		</form>
	</section>
	<%--
	<button id="pushale" url="${referenciaBancaria}">pushaleeee</button>
 --%>
</section>


<script>
	var tipoPago = ${showModal};
	$( document ).ready(function() {
		if (tipoPago==1){
			$( "#modal-pago" ).modal( 'show' );
		}
	});
</script>

<script type="text/javascript">

	$( '#acceso-pagos #txtNoPolis' ).on( 'keyup', function() {
		$( event.target ).val( function(index, value) {
			var aux = value.toUpperCase();
			hayInformacion();
			return aux;
		} );
	} );

	$( '#acceso-pagos #txtCodClient' ).on( 'keyup', function() {
		$( event.target ).val( function(index, value) {
			var aux = value.toUpperCase();
			hayInformacion();
			return aux;
		} );
	} );
	
	

	function hayInformacion() {
		if (($( '#acceso-pagos #txtNoPolis' ).val().length == 0)
				|| ($( '#acceso-pagos #txtCodClient' ).val().length == 0)) {
			$( '#buscarAcceso' ).prop( "disabled", true );
		} else {
			$( '#buscarAcceso' ).prop( "disabled", false );
		}
	}

	$( "#buscarAcceso" ).click( function(e) {
		showLoader();
		/*grecaptcha.execute();*/
		hideLoader();
	} );

	/*
	-------------- Cambios para pagos referenciados
	 */

	 

	$( ".nav-link" ).click( function() {
		var idSelect = $( this ).prop( 'id' );
		console.log( "id -  " + idSelect );
		if (idSelect == '1') {
			$( '#titulo2Pagos' ).html( 'Pago de Pólizas con Tarjetas de Crédito o Débito para clientes' );
			$( '.infVer' ).show();
			$( '#tipoPago' ).val( '1' );
			$( '#lbltxtNoPolis' ).text("Número de Póliza (Póliza pendiente de pago)");
			$( '#lbltxtCodClient' ).text('Código de Cliente (Todas las Pólizas pendientes de pago de un Cliente)');	
			
		} else {
			$( '#titulo2Pagos' ).html( 'Pago de Pólizas con Referencia Bancaria para clientes' );
			$( '.infVer' ).hide();
			$( '#tipoPago' ).val( '2' );
			$( '#lbltxtNoPolis' ).text("Número de Póliza ");
			$( '#lbltxtCodClient' ).text('Código de Cliente ');	
			
		}

	} );

	$( '#pushale' ).click( function() {
		var a = $( this ).attr( 'url' );
		console.log( a );
		$.post( a ).done( function(data) {
			console.log( 'termine post' );
		} );

	} );
</script>