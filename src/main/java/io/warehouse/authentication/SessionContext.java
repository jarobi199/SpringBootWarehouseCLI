package io.warehouse.authentication;

import io.warehouse.model.User;

public class SessionContext {

  private static User currentUser =  null;

  public static void setCurrentUser(User currentUser) {
    SessionContext.currentUser = currentUser;
  }

  public static User getCurrentUser() {
    return currentUser;
  }

}
