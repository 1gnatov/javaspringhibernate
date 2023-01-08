package bills;

import bill.Bill;
import bill.BillDAO;
import bill.BillService;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import user.UserService;

import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

public class BillServiceTest extends HibernateTestBase {

  private static final BillDAO billDAO= new BillDAO(sessionFactory);
  private static final UserService userService = new UserService();
  private static final BillService billService = new BillService(sessionFactory, billDAO, userService);

  @Test
  public void saveShouldInsertBillInDBAndReturnBillWithId() throws Exception {

    final Bill bill1 = new Bill("Main bill");
    final Bill bill2 = new Bill("Reserve bill");

    billService.save(bill1);
    billService.save(bill2);

    assertEquals("Main bill", bill1.getTitle());
    assertEquals("Reserve bill", bill2.getTitle());
    assertEquals(bill1, billService.get(bill1.id()).get());
    assertEquals(bill2, billService.get(bill2.id()).get());
  }

  @Test
  public void getShouldReturnBillById() throws Exception {

    final Bill bill = new Bill("Main bill");
    bill.setCredits(100500);
    billService.save(bill);

    final Optional<Bill> billFromDB = billService.get(bill.id());

    assertEquals(bill, billFromDB.get());
    assertEquals(bill.getCredits(), billFromDB.get().getCredits());
  }

  @Test
  public void getShouldReturnEmptyOptionalIfNoBillWithSuchId() throws Exception {

    final int nonExistentBillId = 123;

    final Optional<Bill> user = billService.get(nonExistentBillId);

    assertFalse(user.isPresent());
  }

  @Test
  public void getAllShouldReturnAllBills() throws Exception {

    final Bill bill1 = new Bill("Main bill");
    final Bill bill2 = new Bill("Reserve bill");
    billService.save(bill1);
    billService.save(bill2);

    final Set<Bill> bills = billService.getAll();

    assertEquals(ImmutableSet.of(bill1, bill2), bills);
  }

  @Test
  public void changeTitleShouldChangeTitle() throws Exception {

    final Bill bill = new Bill("Main bill");
    billService.save(bill);

    billService.changeTitle(bill.id(), "Old main bill");

    final Bill billFromDB = billService.get(bill.id()).get();
    assertEquals("Old main bill", billFromDB.getTitle());
  }

  @Test(expected = IllegalArgumentException.class)
  public void changeTitleShouldThrowIllegalArgumentExceptionIfNoBillWithSuchId() throws Exception {

    final int nonExistentBillId = 123;
    assertFalse(billService.get(nonExistentBillId).isPresent());

    billService.changeTitle(nonExistentBillId, "New title");
  }

  @Test
  public void deleteShouldDeleteBillById() throws Exception {

    final Bill bill1 = new Bill("Main bill");
    final Bill bill2 = new Bill("Reserve bill");
    billService.save(bill1);
    billService.save(bill2);

    billService.delete(bill1.id());

    assertFalse(billService.get(bill1.id()).isPresent());
    assertTrue(billService.get(bill2.id()).isPresent());
  }
}
