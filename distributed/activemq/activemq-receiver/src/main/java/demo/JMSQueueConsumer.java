package demo;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * 主动接收队列消息
 */
public class JMSQueueConsumer {

    public static void main(String[] args) throws JMSException {
        ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://192.168.50.144:61616");
        Connection connection = factory.createConnection();
        connection.start();

        Session session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue("myQueue");   //创建目的地
        MessageConsumer consumer = session.createConsumer(destination);    //创建消费者

        TextMessage message = (TextMessage) consumer.receive(); //接收消息
        System.out.println("message: " + message.getText());

        //message.acknowledge(); 确认消息被签收，会签收所有之前消费的message
        session.commit();
        connection.close();
    }
}
