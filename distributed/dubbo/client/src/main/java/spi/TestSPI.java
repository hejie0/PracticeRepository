package spi;

import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.rpc.Protocol;

import java.sql.Driver;
import java.sql.SQLException;
import java.util.ServiceLoader;

public class TestSPI {

    public static void main(String[] args) throws Exception {
//        jdkSPI();
//        dubboSPI();
        code();
    }

    /**
     * jdk的spi默认路径是META-INF/services
     */
    public static void jdkSPI() throws SQLException {
        ServiceLoader<Driver> drivers = ServiceLoader.load(Driver.class);
        for (Driver driver : drivers){
            if(driver instanceof DefineDriver){  //执行自己定义的driver
                driver.connect("helloworld", null);
            }
        }
    }

    /**
     * dubbo的spi默认路径是META-INF/dubbo
     */
    public static void dubboSPI(){
        Protocol protocol = ExtensionLoader.getExtensionLoader(Protocol.class)
                .getExtension("myProtocol");
        System.out.println(protocol.getDefaultPort());

        System.out.println(ExtensionLoader.getExtensionLoader(Protocol.class)
                .getDefaultExtension().getDefaultPort());
    }

    public static void code(){
        Protocol protocol = ExtensionLoader.getExtensionLoader(Protocol.class)
                .getAdaptiveExtension(); //得到Protocol$Adpative
    }
}
