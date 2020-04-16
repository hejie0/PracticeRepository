package demo;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * queue和topic的区别：
 * 1、queue类型 多个consumer只能有1个接收，topic类型 多个consumer可以全部接收
 * 2、queue类型 consumer先|后订阅都能接收，topic类型 必须在发布消息前订阅 才能接收
 *
 */
public class JMSTopicListenerConsumer {

    public static void main(String[] args) {
        Connection connection = null;
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://192.168.50.144:61616");
            connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createTopic("myTopic");   //创建目的地
            MessageConsumer consumer = session.createConsumer(destination);    //创建消费者

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
