import bill.Bill;
import bill.BillDAO;
import bill.BillService;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import user.User;
import user.UserDAO;
import user.UserService;
import user.UserSpringJDBCDAO;

import javax.sql.DataSource;
import java.util.Optional;

public class App {

    public static void main(String[] args) {


        EmbeddedDatabase database = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("create-tables.sql")
                .build();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(database);

        UserDAO userDAO = new UserSpringJDBCDAO(database);

        DataSource dataSource = database;

        final User user = User.create("Gordon");
        userDAO.insert(user);
        System.out.println("persisted " + user);

        user.setNickname("Morgan");
        user.setCredits(50);
        user.setPremuium(1);
        userDAO.update(user);
        System.out.println("updated user to " + user);

        userDAO.delete(user.getId());
        System.out.println("deleted " + user);

        final Optional<User> absentUser = userDAO.get(user.getId());
        System.out.println("tried to get user by " + user.getId() + " but got " + absentUser);

        //END
        jdbcTemplate.update("DELETE FROM users");
        //jdbcTemplate.update("DELETE FROM bills");

        ///////////////////////////////////////

        final SessionFactory sessionFactory = createSessionFactory(dataSource);

        try {

            final BillService billService = createUserService(sessionFactory, userDAO);

            final Bill mainBill = new Bill("Main Bill");
            System.out.println("persisting " + mainBill);
            billService.save(mainBill);
            System.out.println("users in db: " + billService.getAll());


            System.out.println("changing title to ReserveBill, adding credits");
            billService.changeTitle(mainBill.id(), "ReserveBill");
            mainBill.setCredits(1000);
            billService.update(mainBill);
            System.out.println("users in db: " + billService.getAll());


            System.out.println("deleting " + mainBill);
            billService.delete(mainBill.id());
            System.out.println("bills in db: " + billService.getAll());

        } finally {
            sessionFactory.close();
        }
    }

    private static SessionFactory createSessionFactory(DataSource dataSource) {

        //Configuration conf = HibernateConfigFactory.prod();

        return HibernateConfigFactory.prod().buildSessionFactory(new StandardServiceRegistryBuilder()
        //       .applySettings(conf.getProperties())
                .applySetting(Environment.DATASOURCE, dataSource)
                .build());
    }

    private static BillService createUserService(final SessionFactory sessionFactory, UserDAO userDAO) {
        final BillDAO billDao = new BillDAO(sessionFactory);
        final UserService userService = new UserService(sessionFactory, userDAO);
        return new BillService(sessionFactory, billDao, userService);
    }


}