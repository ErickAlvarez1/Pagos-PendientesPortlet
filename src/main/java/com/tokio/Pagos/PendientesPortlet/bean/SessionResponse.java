package com.tokio.Pagos.PendientesPortlet.bean;

public class SessionResponse {
	String checkoutMode;
	String merchant;
	String result;
	Session session;
	String successIndicator;
	
	public String getCheckoutMode() {
		return checkoutMode;
	}
	public void setCheckoutMode(String checkoutMode) {
		this.checkoutMode = checkoutMode;
	}
	public String getMerchant() {
		return merchant;
	}
	public void setMerchant(String merchant) {
		this.merchant = merchant;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public Session getSession() {
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}
	public String getSuccessIndicator() {
		return successIndicator;
	}
	public void setSuccessIndicator(String successIndicator) {
		this.successIndicator = successIndicator;
	}
	
	@Override
	public String toString() {
		return "SessionResponse [checkoutMode=" + checkoutMode + ", merchant=" + merchant + ", result=" + result
				+ ", session=" + session + ", successIndicator=" + successIndicator + "]";
	}
	
}
