package demo;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * 事务 createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE)
 *      true表示启用事务
 *      auto_acknowledge表示自动提交
 *      false && auto_acknowledge 不启用事务 自动提交
 * producer端
 *      session.commit 提交message到activemq上
 *      session.rollback 回滚还没有提交到activemq上的message
 * consumer端
 *      session.commit 确认activemq上的message已被消费，如果没有commit就会一直被重复消费
 *      session.rollback 回滚还没有（提交/确认）到activemq上的message
 */
public class JMSQueueProducer {

    public static void main(String[] args) throws JMSException {
        ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://192.168.50.144:61616");
        Connection connection = factory.createConnection();
        connection.start();

        Session session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue("myQueue");   //创建目的地
        MessageProducer producer = session.createProducer(destination);    //创建发送者
        producer.setDeliveryMode(DeliveryMode.PERSISTENT); //设置为持久化

        TextMessage message = session.createTextMessage("Hello World"); //创建需要发送的消息
        //TextMessage、BytesMessage、ObjectMessage、MapMessage、StreamMessage   5种消息体
        //message.setJMS...()  设置消息头
        //message.set...Property()  设置消息属性
        producer.send(message);    //发送消息

        session.commit();
        connection.close();
    }
}
