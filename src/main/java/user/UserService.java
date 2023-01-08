package user;


import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

@Repository
public class UserService {

    private final SessionFactory sessionFactory;
    private final UserDAO userDao;


    @Inject
    public UserService (final SessionFactory sessionFactory, final UserDAO userDao) {
        this.sessionFactory = requireNonNull(sessionFactory);
        this.userDao = requireNonNull(userDao);
    }

    public UserService () {
        this.sessionFactory = null;
        this.userDao = null;
    }

    public void save(final User user) {
        inTransaction(() -> userDao.insert(user));
    }

    public Optional<User> get(final int userId) {
        return inTransaction(() -> userDao.get(userId));
    }

    public Set<User> getAll() {
        return inTransaction(userDao::getAll);
    }

    public void update(final User user) {
        inTransaction(() -> userDao.update(user));
    }

    public void changeCredits(final int userId, final Integer credits) {
        inTransaction(() -> {
            final Optional<User> optionalUser = userDao.get(userId);
            if (!optionalUser.isPresent()) {
                throw new IllegalArgumentException("there is no user with id " + userId);
            }
            optionalUser.get().setCredits(credits);
            // there is no need to merge: hibernate detects changes and updates on commit
            // there is possibility of deadlock if two transactions get one user and then try to update it
            // to avoid it we can 'select for update' in userDAO.get above
            // also we can implement UserDAO.setFirstName(int userId, String firstName) that does 1 query instead of 2 (get, update on commit)
        });
    }

    public void delete(final int userId) {
        inTransaction(() -> userDao.delete(userId));
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
