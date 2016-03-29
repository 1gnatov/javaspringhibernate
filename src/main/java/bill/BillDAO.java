package bill;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.requireNonNull;

@Repository
public class BillDAO {

  private final SessionFactory sessionFactory;

  @Inject
  public BillDAO(final SessionFactory sessionFactory) {
    this.sessionFactory = requireNonNull(sessionFactory);
  }

  public void save(final Bill bill) {

    // session.save inserts new user with different id even if user already has id
    // this is confusing, we'd better throw exception, unfortunately at runtime only
    if (bill.id() != null) {
      throw new IllegalArgumentException("can not save " + bill + " with assigned id");
    }
    session().save(bill); // see also saveOrUpdate and persist
  }

  public Optional<Bill> get(final int billId) {
    final Bill bill = (Bill) session().get(Bill.class, billId);
    return Optional.ofNullable(bill);
  }

  public Set<Bill> getAll() {
    final Criteria criteria = session().createCriteria(Bill.class);
    final List<Bill> bills = criteria.list();
    return new HashSet<>(bills);
  }

  public void update(final Bill bill) {
    session().update(bill);
    // session.update throws exception if current session already has User with same id
    // session.merge does not throw exception
  }

  public void delete(final int billId) {
    session().createQuery("DELETE Bill WHERE id = :id") // HQL
            .setInteger("id", billId)
            .executeUpdate();
    // see also session().delete(bill);
    // but first you will need to get this user from DB
    // also be aware that hibernate silently ignores the fact that user may not have id, this most likely an error
}

  private Session session() {
    return sessionFactory.getCurrentSession();
    // or sessionFactory.openSession(), but do not forget to close it
    // try-with-resource won't work because Session does not implement Autocloseable
  }
}
