package bill;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import user.User;
import user.UserService;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

@Repository
public class BillService {

  private final SessionFactory sessionFactory;
  private final BillDAO billDAO;
  private final UserService userService;

  @Inject
  public BillService(final SessionFactory sessionFactory, final BillDAO billDAO, final UserService userService) {
    this.sessionFactory = requireNonNull(sessionFactory);
    this.billDAO = requireNonNull(billDAO);
    this.userService = requireNonNull(userService);
  }

  public void save(final Bill bill) {
    inTransaction(() -> billDAO.save(bill));
  }

  public Optional<Bill> get(final int billId) {
    return inTransaction(() -> billDAO.get(billId));
  }

  public Set<Bill> getAll() {
    return inTransaction(billDAO::getAll);
  }

  public void update(final Bill bill) {
    inTransaction(() -> billDAO.update(bill));
  }

  public void changeTitle(final int userId, final String title) {
    inTransaction(() -> {
      final Optional<Bill> optionalUser = billDAO.get(userId);
      if (!optionalUser.isPresent()) {
        throw new IllegalArgumentException("there is no user with id " + userId);
      }
      optionalUser.get().setTitle(title);
      // there is no need to merge: hibernate detects changes and updates on commit
      // there is possibility of deadlock if two transactions get one user and then try to update it
      // to avoid it we can 'select for update' in userDAO.get above
      // also we can implement UserDAO.setFirstName(int userId, String firstName) that does 1 query instead of 2 (get, update on commit)
    });
  }

  public void delete(final int billId) {
    inTransaction(() -> billDAO.delete(billId));
  }



  public void transferCreditsFromUserToBill (final int userId, final int billId, final int creditsAmount) throws SQLException {

    Session session = null;
    Transaction tx = null;
    try {

      session = sessionFactory.openSession();
      tx = session.beginTransaction();

      try {

        //prepare User
        final Optional<User> optionalCustomer = userService.get(userId);
        if (!optionalCustomer.isPresent()) {
          throw new IllegalArgumentException("there is no user with id " + userId);
        }
        final User customer = userService.get(userId).get();

        //prepare Bill
        final Optional<Bill> optionalBill = get(billId);
        if (!optionalCustomer.isPresent()) {
          throw new IllegalArgumentException("there is no bill with id " + userId);
        }
        final Bill bill = get(billId).get();


        //check user have credits to pay
        if (customer.getCredits() < creditsAmount) {
          throw new IllegalArgumentException("User with id " + userId + " have not enough credits + " + customer.getCredits());
        }
        //check illegal creditsAmount
        if (creditsAmount <= 0) {
          throw new IllegalArgumentException("Invalid payment, creditsAmount " + creditsAmount);
        }

        //do credit transfer

        customer.setCredits(customer.getCredits() - creditsAmount);
        userService.update(customer);
        bill.setCredits(bill.getCredits() + creditsAmount);
        update(bill);

        System.out.println("Trying transaction from User " + customer + " to bill " + bill);
        tx.commit();
        System.out.println("Commit is done, user = " + userService.get(customer.getId()) + ", bill = " + get(bill.id()));
      }
      catch (IllegalArgumentException e) {
        tx.rollback();
        System.out.println("Transaction failed: rollback");
      }
      catch (RuntimeException e) {
        tx.rollback();
        sessionFactory.close();
        throw e;
      }
    } finally {
      if (session != null) {
        session.close();
      }
    }
  }


  private <T> T inTransaction(final Supplier<T> supplier) {
    final Optional<Transaction> transaction = beginTransaction();
    try {
      final T result = supplier.get();
      transaction.ifPresent(Transaction::commit);
      return result;
    } catch (RuntimeException e) {
      transaction.ifPresent(Transaction::rollback);
      throw e;
    }
  }

  private void inTransaction(final Runnable runnable) {
    inTransaction(() -> {
      runnable.run();
      return null;
    });
  }

  private Optional<Transaction> beginTransaction() {
    final Transaction transaction = sessionFactory.getCurrentSession().getTransaction();
    if (!transaction.isActive()) {
      transaction.begin();
      return Optional.of(transaction);
    }
    return Optional.empty();
  }
}
