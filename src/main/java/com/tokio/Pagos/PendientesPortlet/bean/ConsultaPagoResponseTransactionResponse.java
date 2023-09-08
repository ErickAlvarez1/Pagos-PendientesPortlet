package com.tokio.Pagos.PendientesPortlet.bean;

public class ConsultaPagoResponseTransactionResponse {
	String acquirerCode;
	String acquirerMessage;
	String gatewayCode;
	public String getAcquirerCode() {
		return acquirerCode;
	}
	public void setAcquirerCode(String acquirerCode) {
		this.acquirerCode = acquirerCode;
	}
	public String getAcquirerMessage() {
		return acquirerMessage;
	}
	public void setAcquirerMessage(String acquirerMessage) {
		this.acquirerMessage = acquirerMessage;
	}
	public String getGatewayCode() {
		return gatewayCode;
	}
	public void setGatewayCode(String gatewayCode) {
		this.gatewayCode = gatewayCode;
	}
	@Override
	public String toString() {
		return "ConsultaPagoResponseTransactionResponse [acquirerCode=" + acquirerCode + ", acquirerMessage="
				+ acquirerMessage + ", gatewayCode=" + gatewayCode + "]";
	}
}
