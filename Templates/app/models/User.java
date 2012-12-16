package models;

public class User {

  public String firstName;
  public String lastName;
  public String email;

  public User() {
  }

  public User(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }

  private static User demoUser = new User("Max", "Musterbro");

  public static User forName(String name) {
    return demoUser;
  }
}
