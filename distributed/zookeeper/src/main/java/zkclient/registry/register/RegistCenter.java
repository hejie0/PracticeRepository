package zkclient.registry.register;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 注册中心
 */
public class RegistCenter {
	// zookeeper客户端
	ZkClient client = new ZkClient("192.168.50.145:2181");
	
	// 注册中心父节点
	private String centerRootPath = "/regitCenter";
	
	// 初始化
	public RegistCenter() {
		client.setZkSerializer(new MyZkSerializer());
	}
	
	/**
	 * 注册服务
	 * 需要服务的信息，ip+port
	 * /root
	 * 	|_servers
	 * 		|_serverName
	 * 			|_ URL(json)
	 * 
	 * 
	 * @param serviceResource
	 */
	public void regist(ServiceResource serviceResource) {
		// 服务接口名称，作为服务zonde
		String serviceName = serviceResource.getServiceName();
		String servicePath = centerRootPath + "/"+serviceName+"/service";
		if(! client.exists(servicePath)) {
			client.createPersistent(servicePath, true);
		}
		
		// 把URL信息做成子节点、临时节点
		String uri = JsonMapper.toJsonString(serviceResource);
		try {
			uri = URLEncoder.encode(uri, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String uriPath = servicePath+"/"+uri;
		client.delete(uriPath);
		client.createEphemeral(uriPath);
	}
	
	/**
	 * 反注册、即注销
	 * @param serviceResource
	 */
	public void unregist(ServiceResource serviceResource) {
		String serviceName = serviceResource.getServiceName();
		String servicePath = centerRootPath + "/"+serviceName+"/service";
		String uri = JsonMapper.toJsonString(serviceResource);
		String uriPath = servicePath+"/"+uri;
		if(client.exists(uriPath)) {
			client.delete(uriPath);
		}
	}
	
	/**
	 * 加载配置中心中服务资源信息
	 * @param serviceName
	 * @return
	 */
	public List<ServiceResource> loadServiceResouces(String serviceName) {
		// 只关注我需要关注的接口
		String servicePath = centerRootPath + "/"+serviceName+"/service";
		
		// 一次性把所有的服务提供者信息全部拿过来
		List<String> children = client.getChildren(servicePath);
		List<ServiceResource> resources = new ArrayList<ServiceResource>();
		for(String ch : children) {
			try {
				String deCh = URLDecoder.decode(ch, "UTF-8");
				ServiceResource r = JsonMapper.fromJsonString(deCh, ServiceResource.class);
				resources.add(r);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return resources;
	}
	
	/**
	 * 客户订阅服务接口
	 * @param serviceName
	 * @param handler
	 */
	public void subscribe(String serviceName, ChangeHandler handler) {
		
		// 订阅，给我们的客户发通知
		String path = centerRootPath + "/"+serviceName+"/service";
		
		// 实时通知我们的客户端
		client.subscribeChildChanges(path, new IZkChildListener() {
			@Override
			public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
				List<ServiceResource> resource = loadServiceResouces(serviceName);
				handler.itemChange(resource);
			}
		});
		
	}
	
	/**
	 * 当服务提供节点发生变化，就会触发该事件
	 */
	public interface ChangeHandler {
		/**
		 * 发生变化后给一个完整的属性对象
		 * @param resource
		 */
		void itemChange(List<ServiceResource> resource);
	}
}

