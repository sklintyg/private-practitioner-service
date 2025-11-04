package se.inera.intyg.privatlakarportal.auth;

import java.io.Serial;
import java.util.Collections;
import java.util.Objects;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class FakeAuthenticationToken extends AbstractAuthenticationToken {

  @Serial
  private static final long serialVersionUID = 1L;

  private final PrivatlakarUser user;

  public FakeAuthenticationToken(PrivatlakarUser user) {
    super(Collections.emptyList());
    this.user = user;
    setAuthenticated(true);
  }

  @Override
  public Object getCredentials() {
    return user.getPersonalIdentityNumber();
  }

  @Override
  public Object getPrincipal() {
    return user;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    final var that = (FakeAuthenticationToken) o;
    return Objects.equals(user, that.user);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), user);
  }
}