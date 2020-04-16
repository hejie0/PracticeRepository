package demo;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * 实现topic类型 先|后订阅都能接收消息
 */
public class JMSPersistentTopicListenerConsumer {

    //先运行一次注册一下Unique-ID，  比喻：如果QQ都没有 怎么会有离线消息
    public static void main(String[] args) {
        Connection connection = null;
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://192.168.50.144:61616");
            connection = factory.createConnection();
            connection.setClientID("Unique-ID"); //设置唯一id，可以看作QQ号
            connection.start();

            Session session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
            Topic destination = session.createTopic("myTopic");   //创建目的地
            MessageConsumer consumer = session.createDurableSubscriber(destination, "Unique-ID"); //创建消费者

            MessageListener messageListener = new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    try {
                        System.out.println("message: " + ((TextMessage)message).getText());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            };
            while (true){ //循环接收消息
                consumer.setMessageListener(messageListener);
                session.commit();
            }
        }catch (JMSException e){
            e.printStackTrace();
        }finally {
            if(connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
