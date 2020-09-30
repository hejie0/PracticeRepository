package zkclient.registry.simple.example;
/**
 * 
 */
public interface UserService {
	/**
	   *   获取信息
	 * @return
	 */
	public User getInfo();
	
	public boolean printInfo(User user);
}

