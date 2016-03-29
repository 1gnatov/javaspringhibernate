package users;

import org.junit.Test;
import user.User;
import user.UserDAO;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;


public abstract class UserDAOTestBase extends DBTestBase {

  protected abstract UserDAO userDAO();

  @Test
  public void insertShouldInsertNewUserInDBAndReturnUserWithAssignedId() throws Exception {

    final User user1 = User.create("Ville" );
    final User user2 = User.create("Martin");

    user1.setPremuium(1);
    user2.setCredits(30);

    userDAO().insert(user1);
    userDAO().insert(user2);

    final User user1FromDB = userDAO().get(user1.getId()).get();
    assertEquals(user1, user1FromDB);

    final User user2FromDB = userDAO().get(user2.getId()).get();
    assertEquals(user2, user2FromDB);
  }

  @Test(expected = IllegalArgumentException.class)
  public void insertShouldThrowIllegalArgumentExceptionIfUserHasId() throws Exception {

    final User user = User.existing(1, "nickname", 20, 0);

    userDAO().insert(user);
  }

  @Test
  public void getShouldReturnUser() throws Exception {

    final User user = User.create("Ville");
    user.setCredits(50);
    assertEquals("50", user.getCredits().toString());
    userDAO().insert(user);

    final Optional<User> userFromDB = userDAO().get(user.getId());
    assertEquals("50", userFromDB.get().getCredits().toString());
    assertEquals(user, userFromDB.get());
  }


  @Test
  public void getShouldReturnEmptyOptionalIfNoUserWithSuchId() throws Exception {

    final int nonExistentUserId = 666;

    final Optional<User> userFromDB = userDAO().get(nonExistentUserId);

    assertFalse(userFromDB.isPresent());
  }

  @Test
  public void getAllShouldReturnAllUsers() throws Exception {

    assertTrue(userDAO().getAll().isEmpty());

    final User user1 = User.create("Joe");
    final User user2 = User.create("Martin");

    userDAO().insert(user1);
    userDAO().insert(user2);

    final Set<User> usersFromDB = userDAO().getAll();

    assertEquals(new HashSet<>(Arrays.asList(user1, user2)), usersFromDB);
  }

  @Test
  public void updateShouldUpdateUser() throws Exception {

    final User user = User.create("Ville");
    userDAO().insert(user);
    user.setNickname("Ivan");
    user.setPremuium(1);

    userDAO().update(user);

    final User userFromDB = userDAO().get(user.getId()).get();
    assertEquals(user, userFromDB);
    assertEquals("1", userFromDB.getPremuium().toString());
  }

  @Test
  public void deleteShouldDeleteUserById() throws Exception {

    final User user1 = User.create("Joe");
    final User user2 = User.create("Martin");

    userDAO().insert(user1);
    userDAO().insert(user2);

    userDAO().delete(user1.getId());

    assertFalse(userDAO().get(user1.getId()).isPresent());
    assertTrue(userDAO().get(user2.getId()).isPresent());
  }
}
