package com.tokio.Pagos.PendientesPortlet.bean;

public class Session {
	String aes256Key;
	int authenticationLimit;
	String id;
	String updateStatus;
	String version;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUpdateStatus() {
		return updateStatus;
	}
	public void setUpdateStatus(String updateStatus) {
		this.updateStatus = updateStatus;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	@Override
	public String toString() {
		return "Session [id=" + id + ", updateStatus=" + updateStatus + ", version=" + version + "]";
	}
}
