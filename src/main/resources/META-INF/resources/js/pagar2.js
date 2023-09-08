	var requestPolizasPagar = {};

	$( function() {
		$( "#pay-form" )
				.submit(
						function(event) {
							event.preventDefault();
							requestPolizasPagar = getFormDataRows();

							if (requestPolizasPagar.listaPagoPolizas.length == 0) {
								$( '#modalEjemploSeleccionar' ).modal( 'show' );
							} else {
								$
										.post( getSessionId, {
											json : JSON.stringify( requestPolizasPagar )
										} )
										.done(
												function(data) {
													data = JSON.parse( data );
													if ("msjErrTMx" in data) {
														showMessageError( '.navbar', data.msjErrTMx, 0 );
													} else {

														requestPolizasPagar.fechaTransaccion = data.date;
														requestPolizasPagar.folio = data.folio;
														requestPolizasPagar.RefPago = 0; /*data.sessionId;*/
														Checkout
																.configure( {
																	merchant : '${key_evopayment}',
																	order : {
																		description : 'Pago de Polizas',
																		currency : 'MXN',
																		id : data.folio,
																		amount : data.total
																	},
																	session : {
																		id : data.sessionid
																	},
																	customer : {
																		email : requestPolizasPagar.email
																	},
																	interaction : {
																		merchant : {
																			name : 'Tokio Marine - Polizas',
																			address : {
																				line1 : 'Av Reforma #200 ',
																				line2 : '1234 Torre Mayor'
																			},
																			email : 'contacto@tokiomarine.com.mx',
																			phone : '5278 2100',
																			logo : 'https://tokiomarine.com.mx/o/tokio-marine-dxp-theme/images/tokio_marine/logo/main_logo.png'
																		},
																		displayControl : {
																			billingAddress : 'HIDE',
																			orderSummary : 'SHOW',
																			shipping : 'HIDE'
																		}
																	}
																} );
														Checkout.showLightbox();
													}
												} ).fail( function(data) {
													showMessageError( '.navbar', 'Ocurrió un error', 0 );
										} );
							}
						} );

		$( '.mdlsucces' ).on( 'hidden.bs.modal', function(e) {
			//window.location.replace("pago-con-tarjeta");
			var url = new URL( window.location.href );
			window.location.href = url.origin + url.pathname;
		} );

		$( "#referencia-form" ).submit( function(event) {
			event.preventDefault();
			requestPolizasPagar = getFormDataRows();

			if (requestPolizasPagar.listaPagoPolizas.length == 0) {
				$( '#modalEjemploSeleccionar' ).modal( 'show' );
			} else {
				$.post( referenciaBancaria, {
					json : JSON.stringify( requestPolizasPagar )
				} ).done( function(data) {
					var respuestaJson = JSON.parse( data );
					if (respuestaJson.code == 0) {
						fileAux = 'data:application/octet-stream;base64,' + respuestaJson.documento
						var dlnk = document.getElementById( 'aPdf' );
						dlnk.href = fileAux;
						$( '#btnDescargarReferencia' ).attr( 'titulo', respuestaJson.titulo );
						console.log( "termine" );
						$( '#modalokReferencia' ).modal( 'show' );
					} else {
						showMessageError( '.navbar', respuestaJson.msj, 0 );
					}
				} ).fail( function(xhr, status, error) {
					// error handling
					showMessageError( '.navbar', "Error al consumir la informacion", 0 );
				} );;
			}
		} );

	} );

	function errorCallback(error) {
		$.post( "${errorPago}", {
			folio : requestPolizasPagar.folio
		} );
		console.log( JSON.stringify( error ) );
	}
	function cancelCallback() {
		console.log( 'Payment cancelled' );
		$.post( "${cancelarPago}", {
			folio : requestPolizasPagar.folio
		} );
	}
	function completeCallback(resultIndicator, sessionVersion) {
		$.post( solicitudPago, {
			json : JSON.stringify( requestPolizasPagar )
		} ).done( function(data) {
			data = JSON.parse( data );
			$( "#modal-pago-fecha" ).text( data.date );
			$( "#modal-pago-folio" ).text( data.folio );
			$( "#modal-pago-refpago" ).text( data.refpago );
			requestPolizasPagar.listaPagoPolizas.forEach( function(poliza) {
				console.log( poliza );
				var cols = '';
				cols = cols + '<td>' + poliza.poliza + '</td>';
				cols = cols + '<td>' + poliza.certif + '</td>';
				cols = cols + '<td>' + poliza.recibo + '</td>';
				cols = cols + '<td>Pagado</td>';
				cols = cols + '<td>' + poliza.primaTotal + '</td>';
				$( "#modal-pago table tbody" ).append( '<tr>' + cols + '</tr>' );
			} );
		} );

		$( "#modal-pago" ).modal( 'show' );
	}

	function getFormDataRows() {

		var listDataRow = [];
		var rows = $( "#table2" ).DataTable().rows().nodes();
		$( rows ).each( function() {
			listDataRow.push( {
				asegurado : $( this ).find( 'td.asegurado' ).text(),
				codigoAsegurado : $( this ).find( 'td.codigoAsegurado' ).text(),
				poliza : $( this ).find( 'td.poliza' ).text(),
				certif : $( this ).find( 'td.certif' ).text(),
				numdoc : $( this ).find( 'td.numdoc' ).text(),
				recibo : $( this ).find( 'td.recibo' ).text(),
				primaNeta : parseFloat( $( this ).find( 'td.primaNeta' ).attr( 'data' ) ),
				recargos : parseFloat( $( this ).find( 'td.recargos' ).attr( 'data' ) ),
				iva : parseFloat( $( this ).find( 'td.iva' ).attr( 'data' ) ),
				derechos : parseFloat( $( this ).find( 'td.derechos' ).attr( 'data' ) ),
				reduccionPrima : parseFloat( $( this ).find( 'td.reduccionPrima' ).attr( 'data' ) ),
				primaTotal : parseFloat( $( this ).find( 'td.primaTotal' ).attr( 'data' ) ),
				moneda : 1
			/*$('#id_selMoneda').val(),*/
			} );
		} );
		return {
			folio : "",
			p_agente : '',
			listaPagoPolizas : listDataRow,
			correo : $( '#email' ).val()
		}
	}

	$( '#btnDescargarReferencia' ).click( function() {
		var link = document.getElementById( "aPdf" );
		link.download = $( this ).attr( 'titulo' );
		link.click();
	} );