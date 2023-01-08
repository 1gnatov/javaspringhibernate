package user;

import java.util.Objects;

public final class User {

  private Integer id;  // problem: id is null for new user and not null for existing user
                       // seems that NewUser and PersistedUser are distinct classes
                       // or you can generate id outside database, in order not to rely on database sequence,
                       // which is good for distributed system

  private String nickname;
  private Integer credits;
  private Integer premuium;

  // factory method to create new user
  // can be constructor, but factory method has name that helps to understand its purpose
  public static User create(final String nickname) {
    return new User(null, nickname, 0, 0);
  }

//   factory method to load user from db
//   only IUserDAO in the same package should use it, that is why it case package private visibility
//   id parameter is int - not Integer - existing user should always have id
  public static User existing(final int id, final String nickname, final Integer credits, final Integer premium) {
    return new User(id, nickname, credits, premium);
  }

  // private constructor, only factory methods can use it
  private User(final Integer id, final String nickname, final Integer credits, final Integer premuium) {
    this.id = id;
    this.nickname = nickname;
    this.credits = credits;
    this.premuium = premuium;
  }

  public Integer getId() {
    return id;
  }

  // setter is package private - not public - to prevent changing id from outside
  // also id parameter is int, not Integer to prevent setting null
  void setId(final int id) {
    this.id = id;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(final String nickname) {
    this.nickname = nickname;
  }

  public Integer getCredits() {
    return credits;
  }

  public void setCredits(Integer credits) {
    this.credits = credits;
  }

  public Integer getPremuium() {
    return premuium;
  }

  public void setPremuium(Integer premuium) {
    this.premuium = premuium;
  }

  @Override
  public boolean equals(final Object that) {
    if (this == that) return true;
    if (that == null || getClass() != that.getClass()) return false;

    final User thatUser = (User) that;
    return Objects.equals(id, thatUser.id) && Objects.equals(nickname, thatUser.nickname);
  }

  @Override
  public int hashCode() {
    // all new users will have the same hashCode, which might lead to poor Map and Set performance
    // on the other side this hashCode implementation is super fast
    return id != null ? id.hashCode() : 0;
  }

  @Override
  public String toString() {
    return String.format(
            "%s{id=%d, nickname='%s', credits='%d', premium='%d'}",
            getClass().getSimpleName(), id, nickname, credits, premuium
    );
  }
}
