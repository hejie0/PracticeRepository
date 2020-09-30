package zkclient.registry.register;
/**
 * ServiceUri
 * 服务资源信息
 * 
 */
public class ServiceResource {
	private String host;
	private int port;
	private String serviceName;		// 需要暴露的接口名
	private String methods;		// 需要暴露的方法名称
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getMethods() {
		return methods;
	}
	public void setMethods(String methods) {
		this.methods = methods;
	}
	@Override
	public String toString() {
		return "ServiceResource [host=" + host + ", port=" + port + ", serviceName=" + serviceName + ", methods="
				+ methods + "]";
	}
	
}

