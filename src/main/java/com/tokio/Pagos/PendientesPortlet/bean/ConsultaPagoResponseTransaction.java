package com.tokio.Pagos.PendientesPortlet.bean;

public class ConsultaPagoResponseTransaction {
	ConsultaPagoResponseTransactionResponse response;
	ConsultaPagoResponseTransactionTransaction transaction;
	String timeOfLastUpdate;
	public ConsultaPagoResponseTransactionResponse getResponse() {
		return response;
	}
	public void setResponse(ConsultaPagoResponseTransactionResponse response) {
		this.response = response;
	}
	public ConsultaPagoResponseTransactionTransaction getTransaction() {
		return transaction;
	}
	public void setTransaction(ConsultaPagoResponseTransactionTransaction transaction) {
		this.transaction = transaction;
	}
	public String getTimeOfLastUpdate() {
		return timeOfLastUpdate;
	}
	public void setTimeOfLastUpdate(String timeOfLastUpdate) {
		this.timeOfLastUpdate = timeOfLastUpdate;
	}
	
	@Override
	public String toString() {
		return "ConsultaPagoResponseTransaction [response=" + response + ", transaction=" + transaction
				+ ", timeOfLastUpdate=" + timeOfLastUpdate + "]";
	}
	
}
