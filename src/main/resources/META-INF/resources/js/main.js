const col_filtro = 6;
var auxInicializacion = 0; 
var auxInicializacion2 = 0; 


var optionsXlsButton = {
	dom : 'fBrltip',
	buttons : [ {
		extend : 'excelHtml5',
		text : '<a class="btn-floating btn-sm teal waves-effect waves-light py-2 my-0">XLS</a>',
		titleAttr : 'Exportar XLS',
		className : "btn-unstyled",
		exportOptions : {
			format : {
				body : function(data, row, column, node) {
					return data.replace( /[$,%]/g, '' );
				}
			}
		}
	}, {
		extend : 'pdf',
		text : 'Save current page',
		exportOptions : {
			modifier : {
				page : 'current'
			}
		}
	} ],
	initComplete : function(settings) {
		if (settings.oInstance.selector == "#table1") {
			if (auxInicializacion == 0) {
				this.api().column( col_filtro ).data().unique().sort().each( function(d, j) {
					$( "#sel_moneda" ).append( '<option selected value="' + d + '">' + d + '</option>' )
				} );
				$( "#sel_moneda" ).trigger( "change" );
				auxInicializacion ++;
			}
		}
		if (settings.oInstance.selector == "#table2") {
			if(auxInicializacion2 > 0){
				var totDatos = this.fnGetData().length;
				if (totDatos > 0) {
					selectDestroy("#sel_moneda", true);
				} 				
			}else{
				auxInicializacion2 ++;
			}
		}

	},
	drawCallback : function(settings){
		if (settings.oInstance.selector == "#table2") {
			if(auxInicializacion2 > 0){
				var totDatos = this.fnGetData().length;
				if (totDatos > 0) {
					selectDestroy("#sel_moneda", true);
				}else{
					selectDestroy("#sel_moneda", false);					
				} 				
			}else{
				auxInicializacion2 ++;
			}
		}
	}
};
$( document ).ready(
		function() {
			auxInicializacion = 0;
			auxInicializacion2 = 0;
			if ($( "#menu-bonos-comisiones li.active" ).length <= 0)
				$( "#menu-bonos-comisiones li:first a" ).click();
			$( ".nav.nav-tabs li a" ).click( function() {
				$( "#filterMoneda input#dataType" ).val( $( this ).attr( "data" ) );
			} );

			$.extend( $.fn.dataTable.defaults, {
				responsive : true,
				searching : true,
				lengthChange : false,
				language : jsonLanguaje,
			} );
			var stockTable = $( '#table1' ).dataTable( optionsXlsButton );
			var catalogTable = $( '#table2' ).dataTable( optionsXlsButton );
			var stockTable_b = $( '#table5' ).dataTable();
			var catalogTable_b = $( '#table6' ).dataTable();
			var totals = $( '#table3' ).dataTable( {
				paging : false,
				info : false,
				searching : false
			} );
			var totals2_pre = $( '#table4' ).dataTable( {
				paging : false,
				info : false,
				searching : false
			} );

			stockTable.on( 'click', 'tbody tr', function() {
				if( valIsNullOrEmpty( $(this).attr("intencion") ) ){
					$( this ).toggleClass( 'selected' );										
				}else{
					validaIntencion( $(this).attr("intencion"), $(this).attr("poliza"), $(this).attr("certif"), $(this).attr("recibo") );
					$( this ).toggleClass( 'selected' );					
									
				}
			} );

			catalogTable.on( 'click', 'tbody tr', function() {
				$( this ).toggleClass( 'selected' );
			} );

			stockTable_b.on( 'click', 'tbody tr', function() {
				$( this ).toggleClass( 'selected' );
			} );

			catalogTable_b.on( 'click', 'tbody tr', function() {
				$( this ).toggleClass( 'selected' );
			} );
			/** **** Table comisiones ******** */
			$( '#LeftMove' ).on( 'click', function() {
				moveRows_tipo( catalogTable, stockTable, 'selected', totals );
			} );

			$( '#RightMove' ).on( 'click', function() {
				moveRows_tipo( stockTable, catalogTable, 'selected', totals );
			} );

			$( '#TodosVida' ).on( 'click', function() {
				moveRows_tipo( stockTable, catalogTable, 'vida', totals, todos = true );
			} );

			$( '#TodosDano' ).on( 'click', function() {
				moveRows_tipo( stockTable, catalogTable, 'danos', totals, todos = true );
			} );

			$( "#RightTodos" ).on( 'click', function() {
				stockTable.fnDestroy();
				$( "#table1 tbody tr" ).toggleClass( 'selected' );
				moveRows_tipo( stockTable, catalogTable, 'selected', totals, todos = true );
			} );

			$( "#LeftTodos" ).on( 'click', function() {
				catalogTable.fnDestroy();
				$( "#table2 tbody tr" ).toggleClass( 'selected' );
				moveRows_tipo( catalogTable, stockTable, 'selected', totals, todos = true );
			} );

			/** **** Table bonos ******** */
			$( '#LeftMove2' ).on( 'click', function() {
				moveRows_tipo( catalogTable_b, stockTable_b, 'selected', totals2_pre );
			} );

			$( '#RightMove2' ).on( 'click', function() {
				moveRows_tipo( stockTable_b, catalogTable_b, 'selected', totals2_pre );
			} );

			$( '#TodosVida2' ).on( 'click', function() {
				moveRows_tipo( stockTable_b, catalogTable_b, 'vida', totals2_pre, todos = true );
			} );

			$( '#TodosDano2' ).on( 'click', function() {
				moveRows_tipo( stockTable_b, catalogTable_b, 'danos', totals2_pre, todos = true );
			} );

			$( "#RightTodos2" ).on( 'click', function() {
				stockTable_b.fnDestroy();
				$( "#table5 tbody tr" ).toggleClass( 'selected' );
				moveRows_tipo( stockTable_b, catalogTable_b, 'selected', totals2_pre, todos = true );
			} );

			$( "#LeftTodos2" ).on( 'click', function() {
				catalogTable_b.fnDestroy();
				$( "#table6 tbody tr" ).toggleClass( 'selected' );
				moveRows_tipo( catalogTable_b, stockTable_b, 'selected', totals2_pre, todos = true );
			} );

			$( "#saveFileModal" ).click(
					function() {
						console.log( comisionFile );

						var newFile = comisionFile.fileinput( 'getFileStack' );
						$( "#fileListPdf" ).hide();
						$( "#fileListXml" ).hide();
						$( "#sendSolicitudComision" ).attr( "disabled", true );
						$( "#addFacts" ).text( "AGREGAR FACTURA" );
						newFile.forEach( function(item, index) {
							switch (item.type) {
								case "text/xml":
									var reader = new FileReader();
									reader.onload = function(e) {

										$( '#xmlViewer' ).removeClass( "prettyprint prettyprinted" ).text(
												vkbeautify.xml( e.target.result ) ).addClass( "prettyprint" );
										PR.prettyPrint();
									}
									reader.readAsText( item );
									$( "#fileListXml" ).show();
									$( "#addFacts" ).text( "REEMPLAZAR FACTURA" );
									$( "#sendSolicitudComision" ).attr( "disabled", true );
									break;
								case "application/pdf":
									var reader = new FileReader();
									reader.onload = function(e) {
										$( '#pdfViewer' ).attr( 'data', e.target.result );
									}
									reader.readAsDataURL( item );
									$( "#fileListPdf" ).show();
									$( "#addFacts" ).text( "REEMPLAZAR FACTURA" );
									$( "#sendSolicitudComision" ).attr( "disabled", true );
									break;
							}
						} );
						$( "#fileModal" ).modal( "hide" );
						if (newFile.length == 2) {
							$( "#sendSolicitudComision" ).attr( "disabled", false );
						}
					} );
			function hideFiles() {
				$( "#fileListPdf" ).hide();
				$( "#fileListXml" ).hide();
				$( "#fileListPdf2" ).hide();
				$( "#fileListXml2" ).hide();
				$( "#addFacts" ).text( "AGREGAR FACTURA" );
				$( "#addFacts2" ).text( "AGREGAR FACTURA" );
				$( "#sendSolicitudComision" ).attr( "disabled", true );
				$( "#sendSolicitudBono" ).attr( "disabled", true );
			}
			$( "#saveFileModal2" ).click( function() {

				/*
				 * var newFile = new
				 * Blob([comisionFile.fileinput('getFileStack')[0]], {type:
				 * 'application/pdf'});
				 * console.log(URL.createObjectURL(newFile));
				 */
				var newFile = bonoFile.fileinput( 'getFileStack' );
				$( "#fileListPdf2" ).hide();
				$( "#fileListXml2" ).hide();
				$( "#addFacts2" ).text( "AGREGAR FACTURA" );
				$( "#sendSolicitudBono" ).attr( "disabled", true );
				newFile.forEach( function(item, index) {
					switch (item.type) {
						case "text/xml":
							var reader = new FileReader();
							reader.onload = function(e) {
								$( '#xmlViewer' ).text( e.target.result );
							}
							reader.readAsText( item );
							$( "#fileListXml2" ).show();
							$( "#addFacts2" ).text( "REEMPLAZAR FACTURA" );
							$( "#sendSolicitudBono" ).attr( "disabled", true );
							break;
						case "application/pdf":
							var reader = new FileReader();
							reader.onload = function(e) {
								$( '#pdfViewer' ).attr( 'data', e.target.result );
							}
							reader.readAsDataURL( item );
							$( "#fileListPdf2" ).show();
							$( "#addFacts2" ).text( "REEMPLAZAR FACTURA" );
							$( "#sendSolicitudBono" ).attr( "disabled", true );
							break;
					}
				} );
				$( "#file2Modal" ).modal( "hide" );
				if (newFile.length == 2) {
					$( "#sendSolicitudBono" ).attr( "disabled", false );
				}
			} );
			$( "#sendSolicitudComision" ).click( function() {
				showLoader();
				comisionFile.fileinput( 'upload' );
				console.log( JSON.stringify( catalogTable.fnGetData() ) );
			} );
			$( "#sendSolicitudBono" ).click( function() {
				showLoader();
				bonoFile.fileinput( 'upload' );
				console.log( JSON.stringify( catalogTable.fnGetData() ) );
			} );
		} );

function moveRows_tipo(fromTable, toTable, tipo, totals, todos) {
	todos = typeof todos !== 'undefined' ? todos : false;
	showLoader();
	if (todos) {
		fromTable.fnDestroy();
		if (tipo != "vida" && tipo != "danos") {
			if ($( "#sel_moneda" ).val() != "") {
				fromTable.find( "tr:not([moneda='" + $( "#sel_moneda" ).val() + "'])" ).removeClass( 'selected' );
			} else {
				fromTable.toggleClass( 'selected' );
			}
		}
	}
	if (tipo == "vida")
		var $row = fromTable.find( '.t_vida' );
	else if (tipo == "danos")
		var $row = fromTable.find( '.t_dano' );
	else
		var $row = fromTable.find( ".selected" );
	if (todos) {
		var options = isExportTable( fromTable, {} );
		fromTable.dataTable( options );
	}
	$.each( $row, function(k, v) {
		if (this !== null) {
			toTable.fnAddData( $( this ).clone().removeClass( 'selected' ).removeClass( 'py-2' ) );
			fromTable.fnDeleteRow( this );
		}
	} );
	changeData();
	changeData2();
	redrawTable( toTable );
	redrawTable( totals, noPaginator = true );
	/* redrawTable(fromTable); */
	hideLoader();
}

function getFormData() {
	var subtotal = parseFloat( $( '#totalComision-pos' ).attr( 'data' ) );
	var iva = parseFloat( $( '#iva-pos' ).attr( 'data' ) );
	var retISR = parseFloat( $( '#retIsr-pos' ).attr( 'data' ) );
	var retIVA = parseFloat( $( '#retIva-pos' ).attr( 'data' ) );
	/* var total = Math.round((subtotal + iva + retISR + retIVA)*100)/100; */
	var total = parseFloat( $( '#total-pos' ).attr( 'data' ) );
	return [ {
		subtotal : subtotal,
		iva : iva,
		retISR : retISR,
		retIVA : retIVA,
		total : total,
	} ]
}

function getPrimaNeta() {
	return $( "#primaNeta-pos" ).attr( 'data' );
}
function getFormDataRows() {

	var listDataRow = [];
	var rows = $( "#table2" ).DataTable().rows().nodes();
	$( rows ).each( function() {
		var p_primaNeta = parseFloat( $( this ).find( 'td.primaNeta' ).attr( 'data' ) );
		var p_comision = parseFloat( $( this ).find( 'td.comision' ).attr( 'data' ) );
		var p_iva = parseFloat( $( this ).find( 'td.iva' ).attr( 'data' ) );
		var p_retIsr = parseFloat( $( this ).find( 'td.retIsr' ).attr( 'data' ) );
		var p_retIva = parseFloat( $( this ).find( 'td.retIva' ).attr( 'data' ) );
		var p_porcComision = parseFloat( $( this ).find( 'td.porcComision' ).attr( 'data' ) );
		listDataRow.push( {
			p_poliza : $( this ).find( 'td.poliza' ).text(),
			p_endoso : $( this ).find( 'td.endoso' ).text(),
			p_numdocto : $( this ).find( 'td.numDocto' ).text(),
			p_asegurado : $( this ).find( 'td.asegurado' ).text(),
			p_producto : $( this ).find( 'td.producto' ).text(),
			p_moneda : $( '#selector_moneda' ).val(),
			p_primaNeta : p_primaNeta,
			p_comision : p_comision,
			p_iva : p_iva,
			p_retIsr : p_retIsr,
			p_retIva : p_retIva,
			p_comisionTotal : Math.round( (p_comision + p_iva + p_retIsr + p_retIva) * 100 ) / 100,
			p_recibo : $( this ).find( 'td.recibo' ).text(),
			p_porcComision : p_porcComision
		} );
	} );
	return {
		p_edoCuenta : null,
		p_moneda : $( '#selector_moneda' ).val(),
		p_agente : '',
		listIdBono : JSON.stringify( listDataRow ),
	}
}

function getFormDataBono() {
	var listIdBono = [];
	var rows = $( "#table6" ).DataTable().rows().nodes();
	$( rows ).each( function() {
		listIdBono.push( parseInt( $( this ).find( "td.idBono" ).text() ) );
	} );
	return {
		p_edoCuenta : null,
		p_moneda : $( '#selector_moneda' ).val(),
		p_agente : '',
		listIdBono : listIdBono,
	}
}
function getFormDataBonoTotales() {
	var subtotal = parseFloat( $( '#table8 #bono-pos' ).attr( 'data' ) );
	var iva = parseFloat( $( '#table8 #iva2-pos' ).attr( 'data' ) );
	var retISR = parseFloat( $( '#table8 #retIsr2-pos' ).attr( 'data' ) );
	var retIVA = parseFloat( $( '#table8 #retIva2-pos' ).attr( 'data' ) );
	var tot = parseFloat( $( '#table8 #total2-pos' ).attr( 'data' ) );
	/* total: Math.round((subtotal + iva + retISR + retIVA)*100)/100, */
	return [ {
		subtotal : subtotal,
		iva : iva,
		retISR : retISR,
		retIVA : retIVA,
		total : tot,
	} ]
}

function changeData2() {
	var primaPagada_pre = 0.0;
	var bono_pre = 0.0;
	var iva2_pre = 0.0;
	var retIsr2_pre = 0.0;
	var retIva2_pre = 0.0;

	var bono_pos = 0.0;
	var iva2_pos = 0.0;
	var retIsr2_pos = 0.0;
	var retIva2_pos = 0.0;
	var rows = $( "#table5" ).DataTable().rows().nodes();
	var rows2 = $( "#table6" ).DataTable().rows().nodes();

	$( 'td.primaPagada[data]', rows ).each( function() {
		primaPagada_pre += parseFloat( $( this ).attr( "data" ) );
	} );
	$( 'td.bono[data]', rows ).each( function() {
		bono_pre += parseFloat( $( this ).attr( "data" ) );
	} );
	$( 'td.iva[data]', rows ).each( function() {
		iva2_pre += parseFloat( $( this ).attr( "data" ) );
	} );
	$( 'td.retIsr[data]', rows ).each( function() {
		retIsr2_pre += parseFloat( $( this ).attr( "data" ) );
	} );
	$( 'td.retIva[data]', rows ).each( function() {
		retIva2_pre += parseFloat( $( this ).attr( "data" ) );
	} );

	$( 'td.bono[data]', rows2 ).each( function() {
		bono_pos += parseFloat( $( this ).attr( "data" ) );
	} );
	$( 'td.iva[data]', rows2 ).each( function() {
		iva2_pos += parseFloat( $( this ).attr( "data" ) );
	} );
	$( 'td.retIsr[data]', rows2 ).each( function() {
		retIsr2_pos += parseFloat( $( this ).attr( "data" ) );
	} );
	$( 'td.retIva[data]', rows2 ).each( function() {
		retIva2_pos += parseFloat( $( this ).attr( "data" ) );
	} );

	$( "#primaPagada-pre" ).text( format( primaPagada_pre ) );
	$( "#bono-pre" ).text( format( bono_pre ) );
	$( "#iva2-pre" ).text( format( iva2_pre ) );
	$( "#retIsr2-pre" ).text( format( retIsr2_pre ) );
	$( "#retIva2-pre" ).text( format( retIva2_pre ) );
	$( "#total2-pre" ).text( format( primaPagada_pre + bono_pre + iva2_pre + retIsr2_pre + retIva2_pre ) );

	$( "#bono-pos" ).text( format( bono_pos ) ).attr( 'data', bono_pos.toFixed( 2 ) );
	$( "#iva2-pos" ).text( format( iva2_pos ) ).attr( 'data', iva2_pos.toFixed( 2 ) );
	$( "#retIsr2-pos" ).text( format( retIsr2_pos ) ).attr( 'data', retIsr2_pos.toFixed( 2 ) );
	$( "#retIva2-pos" ).text( format( retIva2_pos ) ).attr( 'data', retIva2_pos.toFixed( 2 ) );
	var totalTemp = bono_pos + iva2_pos + retIsr2_pos + retIva2_pos;
	$( "#total2-pos" ).text( format( totalTemp ) ).attr( 'data', totalTemp.toFixed( 2 ) );
}

function changeData() {
	var totalPrima_pre = 0.0;

	var totalPrima_pos = 0.0;

	var rows = $( "#table1" ).DataTable().rows().nodes();
	var rows2 = $( "#table2" ).DataTable().rows().nodes();

	$( 'td.primaTotal[data]', rows ).each( function() {
		totalPrima_pre += parseFloat( $( this ).attr( "data" ) );
	} );

	$( 'td.primaTotal[data]', rows2 ).each( function() {
		totalPrima_pos += parseFloat( $( this ).attr( "data" ) );
	} );

	$( "#primaTotal-pre" ).text( format( totalPrima_pre ) );

	$( "#primaTotal-pos" ).text( format( totalPrima_pos ) ).attr( 'data', totalPrima_pos.toFixed( 2 ) );
}

function format(number) {
	return '$' + parseFloat( number, 10 ).toFixed( 2 ).replace( /\d(?=(\d{3})+\.)/g, '$&,' ).toString();
}

function redrawTable(table, noPagination) {
	noPagination = typeof noPagination !== 'undefined' ? noPagination : false;
	var options = {};
	if (noPagination)
		options = {
			paging : false,
			info : false
		};
	options = isExportTable( table, options );
	table.fnDestroy();
	table.dataTable( options );
	$( "#sel_moneda" ).trigger( "change" );
}

function showPopupSuccess(msg) {
	$( '#ok-modal' ).find( '.modal-body' ).text( msg );
	/* $('.bgd-full').show(); */
	$( '#ok-modal' ).modal( 'show' );
	$( '#ok-modal' ).on( 'hidden.bs.modal', function(e) {
		showLoader();
		$( '#filterMoneda' ).submit();
	} );
}

function namesValidation(event, files, erroPopup, btnSave) {
	$( '#' + btnSave ).prop( "disabled", true );
	var customValidationFailed = false;
	console.log( event );
	console.log( files );
	var name = "";
	if (files.length < 2)
		return;
	files.forEach( function(file) {
		var tempName = file.name.split( "." );
		if (name == "") {
			name = tempName[0];
		} else if (tempName[0] != name) {
			customValidationFailed = "El nombre de los archivos no coincide";
		}
	} );
	if (customValidationFailed) {
		showMessageError( "." + erroPopup, customValidationFailed, 0 );
		return;
	}
	$( '#' + btnSave ).prop( "disabled", false );
}

function isExportTable(table, options) {
	if (table.selector == "#table2" || table.selector == "#table1") {
		Object.assign( options, optionsXlsButton );
	}
	return options;
}
var jsonLanguaje = {
	"sProcessing" : "Procesando...",
	"sLengthMenu" : "Mostrar _MENU_ registros",
	"sZeroRecords" : "No se encontraron resultados",
	"sEmptyTable" : "Ningún dato disponible en esta tabla",
	"sInfo" : "Mostrando registros del _START_ al _END_ de un total de _TOTAL_ registros",
	"sInfoEmpty" : "Mostrando registros del 0 al 0 de un total de 0 registros",
	"sInfoFiltered" : "(filtrado de un total de _MAX_ registros)",
	"sInfoPostFix" : "",
	"sSearch" : "Buscar:",
	"sUrl" : "",
	"sInfoThousands" : ",",
	"sLoadingRecords" : "Cargando...",
	"oPaginate" : {
		"sFirst" : "Primero",
		"sLast" : "Último",
		"sNext" : "Siguiente",
		"sPrevious" : "Anterior"
	},
	"oAria" : {
		"sSortAscending" : ": Activar para ordenar la columna de manera ascendente",
		"sSortDescending" : ": Activar para ordenar la columna de manera descendente"
	}
};

$( "#sel_moneda" ).change( function() {
	console.log( $( this ).val() );
	if(tipoPago != 1){
		var table = $( '#table1' ).DataTable();
		table.column( col_filtro ).search( $( this ).val() ).draw();		
	}
} );


function selectDestroy(objeto, enabled) {
    $(objeto).prop("disabled", enabled);
   /* $(objeto).material_select('destroy');
    */
    $(objeto).material_select({destroy:true});
    
}

function goToHome(){
	showLoader();
	var url = window.location.href;
	/*var newUrl = url.replace('/comprobante', '/pago-con-tarjeta');*/
	var newUrl = url.split('/comprobante')[0] + '/pago-con-tarjeta';
	window.location.replace( newUrl );
}