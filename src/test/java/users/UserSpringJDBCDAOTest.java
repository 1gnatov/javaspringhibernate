package users;

import user.UserDAO;
import user.UserSpringJDBCDAO;

public class UserSpringJDBCDAOTest extends UserDAOTestBase {

    private static final UserSpringJDBCDAO userSpringJDBCDAO = new UserSpringJDBCDAO(database);

    @Override
    protected UserDAO userDAO() {
        return userSpringJDBCDAO;
    }
}
