import bill.Bill;
import org.hibernate.cfg.Configuration;
import user.UserService;

public class HibernateConfigFactory {

  public static Configuration prod() {
    return new Configuration()
            .addAnnotatedClass(Bill.class)
            .addAnnotatedClass(UserService.class)
            .setProperty("hibernate.connection.driver_class", "org.h2.Driver")
            .setProperty("hibernate.connection.url", "jdbc:h2:mem:test");
  };

  private HibernateConfigFactory() {
  }
}





