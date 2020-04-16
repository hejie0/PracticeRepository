package demo;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class JMSTopicProducer {

    public static void main(String[] args) throws JMSException {
        ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://192.168.50.144:61616");
        Connection connection = factory.createConnection();
        connection.start();

        Session session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createTopic("myTopic");   //创建目的地
        MessageProducer producer = session.createProducer(destination);    //创建发送者
        producer.setDeliveryMode(DeliveryMode.PERSISTENT); //设置为持久化

        TextMessage message = session.createTextMessage("全员接收：hello world"); //创建需要发送的消息
        producer.send(message);    //发送消息

        session.commit();
        connection.close();
    }
}
