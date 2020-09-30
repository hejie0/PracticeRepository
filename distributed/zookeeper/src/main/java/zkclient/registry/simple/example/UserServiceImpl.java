package zkclient.registry.simple.example;


import zkclient.registry.simple.Service;

/**
 * StudentServiceImpl
 * 
 */
@Service(UserService.class)
public class UserServiceImpl implements UserService {

	public User getInfo() {
		User person = new User();
		person.setAge(18);
		person.setName("arrylist");
		person.setSex("女");
		return person;
	}

	public boolean printInfo(User person) {
		if (person != null) {
			System.out.println(person);
			return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
		new Thread(()->{
			System.out.println("111");
		}).start();;
	}
}
