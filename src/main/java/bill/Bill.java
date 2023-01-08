package bill;


import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "bills")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bill_id")
    private Integer id;  // problem: id is null for new user and not null for existing user
    // seems that NewUser and PersistedUser are distinct classes
    @Column(name = "title")
    private String title;

    @Column(name = "credits")
    private Integer credits;

    public Bill(final String title, final Integer credits) {
        this.title = title;
        this.credits = credits;
    }

    public Bill(final String title) {
        this.title = title;
        this.credits = 0;
    }

    /** for Hibernate only */
    @Deprecated
    Bill() {}  // problem: somebody can use this constructor and create inconsistent instance



    public Integer id() {
        return id;
    }

    // no setId, Hibernate uses reflection to set field

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public Integer getCredits() { return credits; }

    public void setCredits(Integer credits) { this.credits = credits; }

    @Override
    public boolean equals(final Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;

        final Bill thatBill = (Bill) that;
        if (title != null ? !title.equals(thatBill.title) : thatBill.title != null) return false;
        if (id != null ? !id.equals(thatBill.id) : thatBill.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s{id=%d, title='%s', credits='%d'}",
                getClass().getSimpleName(), id, title, credits);
    }
}