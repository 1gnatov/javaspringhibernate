import bill.Bill;
import bill.BillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import user.User;
import user.UserService;

import java.util.Optional;


public class AppSpringIOC {

    private  static Logger log = LoggerFactory.getLogger(AppSpringIOC.class);

    public static void main(String[] args) {



        try (GenericApplicationContext springContext = new AnnotationConfigApplicationContext(AppConfigSpring.class)) {
            UserService userService = springContext.getBean(UserService.class);
            User user = User.create("Gordon");
            userService.save(user);
            log.info("persisted " + user);

            user.setNickname("Morgan");
            user.setCredits(50);
            user.setPremuium(1);
            userService.update(user);
            log.info("updated user to " + user);





            final BillService billService = springContext.getBean(BillService.class);
            Bill mainBill = new Bill("Main Bill");
            log.info("persisting " + mainBill);
            billService.save(mainBill);
            log.info("users in db: " + billService.getAll());

            log.info("changing title to ReserveBill, adding credits");
            billService.changeTitle(mainBill.id(), "ReserveBill");
            mainBill.setCredits(1000);
            billService.update(mainBill);
            log.info("users in db: " + billService.getAll());

            //transaction
            log.info("beginning transferCredits");
            billService.transferCreditsFromUserToBill(user.getId(), mainBill.id(), 49);
            //update obj
            log.info(userService.getAll().toString());
            log.info(billService.getAll().toString());


            //fail transaction
            log.info("beginning fail transaction");
            billService.transferCreditsFromUserToBill(user.getId(), mainBill.id(), 49);
            //update obj
            log.info(userService.getAll().toString());
            log.info(billService.getAll().toString());




            log.info("deleted " + userService.get(user.getId()).get());
            userService.delete(user.getId());

            final Optional<User> absentUser = userService.get(user.getId());
            log.info("tried to get user by " + user.getId() + " but got " + absentUser);
            log.info("users in db: " + userService.getAll());

            log.info("deleting " + billService.get(mainBill.id()));
            billService.delete(mainBill.id());
            log.info("bills in db: " + billService.getAll());


        } catch (Exception e) {
            log.info("Exception occurs in loading Spring context");
        }

        //ctx.close();
    }

}
