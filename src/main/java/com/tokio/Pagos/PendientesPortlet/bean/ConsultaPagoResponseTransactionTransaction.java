package com.tokio.Pagos.PendientesPortlet.bean;

public class ConsultaPagoResponseTransactionTransaction {
	String receipt;
	String authorizationCode;
	String terminal;
	String amount;
	String type;
	String authenticationStatus;
	
	public String getReceipt() {
		return receipt;
	}
	public void setReceipt(String receipt) {
		this.receipt = receipt;
	}
	public String getAuthorizationCode() {
		return authorizationCode;
	}
	public void setAuthorizationCode(String authorizationCode) {
		this.authorizationCode = authorizationCode;
	}
	public String getTerminal() {
		return terminal;
	}
	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAuthenticationStatus() {
		return authenticationStatus;
	}
	public void setAuthenticationStatus(String authenticationStatus) {
		this.authenticationStatus = authenticationStatus;
	}
	
	@Override
	public String toString() {
		return "ConsultaPagoResponseTransactionTransaction [receipt=" + receipt + ", authorizationCode="
				+ authorizationCode + ", terminal=" + terminal + ", amount=" + amount + ", type=" + type
				+ ", authenticationStatus=" + authenticationStatus + "]";
	}
	
}
