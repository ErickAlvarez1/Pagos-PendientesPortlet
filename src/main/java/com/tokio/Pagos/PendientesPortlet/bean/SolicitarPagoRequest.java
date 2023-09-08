package com.tokio.Pagos.PendientesPortlet.bean;

import com.tokio.pagos.Been.PolizaPagar;

import java.util.List;

public class SolicitarPagoRequest {
	int idTransaccion;
	String folio;
	int idTipoMovimiento;
	int idEstatus;
	String fechaTransaccion;
	int tipoPago;
	String correo;
//	String RefPago;
	List<PolizaPagar> listaPagoPolizas;
	
	String refPago;
	String p_agente;
	
	public int getIdTransaccion() {
		return idTransaccion;
	}
	public void setIdTransaccion(int idTransaccion) {
		this.idTransaccion = idTransaccion;
	}
	public String getFolio() {
		return folio;
	}
	public void setFolio(String folio) {
		this.folio = folio;
	}
	public int getIdTipoMovimiento() {
		return idTipoMovimiento;
	}
	public void setIdTipoMovimiento(int idTipoMovimiento) {
		this.idTipoMovimiento = idTipoMovimiento;
	}
	public int getIdEstatus() {
		return idEstatus;
	}
	public void setIdEstatus(int idEstatus) {
		this.idEstatus = idEstatus;
	}
	public String getFechaTransaccion() {
		return fechaTransaccion;
	}
	public void setFechaTransaccion(String fechaTransaccion) {
		this.fechaTransaccion = fechaTransaccion;
	}
	public int getTipoPago() {
		return tipoPago;
	}
	public void setTipoPago(int tipoPago) {
		this.tipoPago = tipoPago;
	}
	public String getCorreo() {
		return correo;
	}
	public void setCorreo(String correo) {
		this.correo = correo;
	}
	public List<PolizaPagar> getListaPagoPolizas() {
		return listaPagoPolizas;
	}
	public void setListaPagoPolizas(List<PolizaPagar> listaPagoPolizas) {
		this.listaPagoPolizas = listaPagoPolizas;
	}
	public String getRefPago() {
		return refPago;
	}
	public void setRefPago(String refPago) {
		this.refPago = refPago;
	}
	public String getP_agente() {
		return p_agente;
	}
	public void setP_agente(String p_agente) {
		this.p_agente = p_agente;
	}
	
	@Override
	public String toString() {
		return "SolicitarPagoRequest [idTransaccion=" + idTransaccion + ", folio=" + folio + ", idTipoMovimiento="
				+ idTipoMovimiento + ", idEstatus=" + idEstatus + ", fechaTransaccion=" + fechaTransaccion
				+ ", tipoPago=" + tipoPago + ", correo=" + correo + ", listaPagoPolizas=" + listaPagoPolizas
				+ ", refPago=" + refPago + ", p_agente=" + p_agente + "]";
	}
	
}
