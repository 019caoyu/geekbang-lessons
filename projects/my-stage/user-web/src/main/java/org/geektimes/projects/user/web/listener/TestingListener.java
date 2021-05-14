package org.geektimes.projects.user.web.listener;

import org.eclipse.microprofile.config.Config;
import org.geektimes.configuration.microprofile.config.DefaultConfigProviderResolver;
import org.geektimes.context.ClassicComponentContext;
import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.management.UserManager;
import org.geektimes.projects.user.sql.DBConnectionManager;

import javax.management.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.lang.management.ManagementFactory;
import java.util.logging.Logger;

//import javax.jms.*;

/**
 * 测试用途
 */
@Deprecated
public class TestingListener implements ServletContextListener {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ClassicComponentContext context = ClassicComponentContext.getInstance();
        DBConnectionManager dbConnectionManager = context.getComponent("bean/DBConnectionManager");
        dbConnectionManager.getConnection();
        testPropertyFromServletContext(sce.getServletContext());
        testPropertyFromJNDI(context);
        //testUser(dbConnectionManager.getEntityManager());
        logger.info("所有的 JNDI 组件名称：[");
        context.getComponentNames().forEach(logger::info);
        logger.info("]");

        testJMXByJokoia();
        //testPropertyFromConfig();

        //ConnectionFactory connectionFactory = context.getComponent("jms/activemq-factory");
        //testJms(connectionFactory);
    }

    private void testPropertyFromConfig() {
        Config config = DefaultConfigProviderResolver.instance().getConfig();
        String propertyName = "application.name";
        logger.info("My Configuration Property[" + propertyName + "] : "
                + config.getConfigValue(propertyName));

    }

    private void testJMXByJokoia() {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

        try {
            // 为 UserMXBean 定义 ObjectName
            ObjectName objectName = new ObjectName("org.geektimes.projects.user.management:type=User");
            // 创建 UserMBean 实例
            mBeanServer.registerMBean(createUserMBean(getUser(1L)), objectName);
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }
    }

    private  Object createUserMBean(User user){
        return new UserManager(user);
    }

    private void testPropertyFromServletContext(ServletContext servletContext) {
        String propertyName = "application.name";
        logger.info("ServletContext Property[" + propertyName + "] : "
                + servletContext.getInitParameter(propertyName));
    }

    private void testPropertyFromJNDI(ClassicComponentContext context) {
        String propertyName = "maxValue";
        logger.info("JNDI Property[" + propertyName + "] : "
                + context.lookupComponent(propertyName));
    }

    private void testUser(EntityManager entityManager) {
        User user = new User();
        //user.setId(30L);
        user.setName("小马哥");
        user.setPassword("********");
        user.setEmail("mercyblitz@gmail.com");
        user.setPhoneNumber("abcdefg");

      /*  不使用ThreadLocal 方案时，解决方案，每次请求都是新的EntityManager
      DelegatingEntityManager delegatingEntityManager = null;
      if (entityManager instanceof DelegatingEntityManager){
            delegatingEntityManager = (DelegatingEntityManager)entityManager;
            entityManager = delegatingEntityManager.getEntityManager();
        }
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(user);
        //entityManager.flush();
        transaction.commit();
        System.out.println(entityManager.find(User.class, user.getId()));*/

        // ThreadLocal 方案
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(user);
        //entityManager.flush();
        transaction.commit();
        System.out.println(entityManager.find(User.class, user.getId()));
        //System.out.println(user);

    }

    private User getUser(long id) {
        ClassicComponentContext context = ClassicComponentContext.getInstance();
        DBConnectionManager dbConnectionManager = context.getComponent("bean/DBConnectionManager");
        EntityManager entityManager = dbConnectionManager.getEntityManager();
        return  entityManager.find(User.class, id);
    }



    /*private void testJms(ConnectionFactory connectionFactory) {
        ThrowableAction.execute(() -> {
//            testMessageProducer(connectionFactory);
            testMessageConsumer(connectionFactory);
        });
    }

    private void testMessageProducer(ConnectionFactory connectionFactory) throws JMSException {
        // Create a Connection
        Connection connection = connectionFactory.createConnection();
        connection.start();

        // Create a Session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Create the destination (Topic or Queue)
        Destination destination = session.createQueue("TEST.FOO");

        // Create a MessageProducer from the Session to the Topic or Queue
        MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        // Create a messages
        String text = "Hello world! From: " + Thread.currentThread().getName() + " : " + this.hashCode();
        TextMessage message = session.createTextMessage(text);

        // Tell the producer to send the message
        producer.send(message);
        System.out.printf("[Thread : %s] Sent message : %s\n", Thread.currentThread().getName(), message.getText());

        // Clean up
        session.close();
        connection.close();

    }

    private void testMessageConsumer(ConnectionFactory connectionFactory) throws JMSException {

        // Create a Connection
        Connection connection = connectionFactory.createConnection();
        connection.start();

        // Create a Session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Create the destination (Topic or Queue)
        Destination destination = session.createQueue("TEST.FOO");

        // Create a MessageConsumer from the Session to the Topic or Queue
        MessageConsumer consumer = session.createConsumer(destination);

        consumer.setMessageListener(m -> {
            TextMessage tm = (TextMessage) m;
            try {
                System.out.printf("[Thread : %s] Received : %s\n", Thread.currentThread().getName(), tm.getText());
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        });

        // Clean up
        // session.close();
        // connection.close();
    }*/


    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

}
