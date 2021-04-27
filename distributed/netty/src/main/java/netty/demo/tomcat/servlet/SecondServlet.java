package netty.demo.tomcat.servlet;

import netty.demo.tomcat.biotomcat.http.Request;
import netty.demo.tomcat.biotomcat.http.Response;
import netty.demo.tomcat.biotomcat.http.Servlet;

public class SecondServlet extends Servlet {

	public void doGet(Request request, Response response) throws Exception {
		this.doPost(request, response);
	}

	public void doPost(Request request, Response response) throws Exception {
		response.write("This is Second Serlvet");
	}

}
