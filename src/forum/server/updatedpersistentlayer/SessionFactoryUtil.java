package forum.server.updatedpersistentlayer;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class SessionFactoryUtil {

	private static org.hibernate.SessionFactory sessionFactory = null;

	public static SessionFactory getInstance() {
		if (sessionFactory == null) {
			sessionFactory =  new Configuration()
			.configure("forum/server/updatedpersistentlayer/hibernate.cfg.xml").buildSessionFactory();
		
			
		}
		return sessionFactory;
	}
	
	public static void reconnectToOtherDatabase(String databaseName) {
		SessionFactoryUtil.close();
		Configuration tConfig = new Configuration()
		.configure("forum/server/updatedpersistentlayer/hibernate.cfg.xml");
		tConfig.setProperty("hibernate.connection.url", "jdbc:mysql://localhost/" + databaseName);
		sessionFactory = tConfig.buildSessionFactory();
	}

	public Session openSession() {
		return sessionFactory.openSession();
	}

	public Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	public static void close() {
		if (sessionFactory != null)
			sessionFactory.close();
		sessionFactory = null;
	}
	
	// 0528586476

}
