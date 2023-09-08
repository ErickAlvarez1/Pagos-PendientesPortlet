package com.tokio.Pagos.PendientesPortlet.bean;

import java.util.List;

public class ConsultaPagoResponse {
	String id;
	String merchantCategoryCode;
	String result;
	List<ConsultaPagoResponseTransaction> transaction;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMerchantCategoryCode() {
		return merchantCategoryCode;
	}
	public void setMerchantCategoryCode(String merchantCategoryCode) {
		this.merchantCategoryCode = merchantCategoryCode;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public List<ConsultaPagoResponseTransaction> getTransaction() {
		return transaction;
	}
	public void setTransaction(List<ConsultaPagoResponseTransaction> transaction) {
		this.transaction = transaction;
	}
	@Override
	public String toString() {
		return "ConsultaPagoResponse [id=" + id + ", merchantCategoryCode=" + merchantCategoryCode + ", result="
				+ result + ", transaction=" + transaction + "]";
	}
}
