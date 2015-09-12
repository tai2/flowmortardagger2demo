package net.tai2.flowmortardagger2demo.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import java.security.SecureRandom;
import java.util.Date;

public class Todo extends RealmObject {

  @PrimaryKey private String id;
  private String content;
  private Date addedDate;
  private boolean done;

  public static Todo create() {
    Todo todo = new Todo();
    todo.setId(generateRandomId());
    return todo;
  }

  private static String generateRandomId() {
    SecureRandom sr = new SecureRandom();
    byte[] bytes = new byte[16];
    sr.nextBytes(bytes);
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
      sb.append(String.format("%x", b));
    }
    return sb.toString();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Date getAddedDate() {
    return addedDate;
  }

  public void setAddedDate(Date addedDate) {
    this.addedDate = addedDate;
  }

  public boolean isDone() {
    return done;
  }

  public void setDone(boolean done) {
    this.done = done;
  }
}
