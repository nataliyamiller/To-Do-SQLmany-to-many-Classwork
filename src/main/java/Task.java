import java.util.List;
import org.sql2o.*;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Task {
  private int id;
  private String description;
  private boolean completed;
  private String dueDate;
  private String createdAt;

  public String getDescription() {
    return description;
  }

  public int getId() {
    return id;
  }

  public String createdAt() {
    // Date date = new Date();
    // createdAt = String.format("%1$s %2$tB %2$td, %2$tY", "Date created:", date);
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    Date date = new Date();
    createdAt = dateFormat.format(date);
    return createdAt;
  }

  public String getDueDate() {
    return dueDate;
  }

  public boolean isCompleted() {
    return completed;
  }

  public Task(String description) {
    this.description = description;
    createdAt = createdAt();
    completed = false;
  }


  @Override
  public boolean equals(Object otherTask) {
    if(!(otherTask instanceof Task)) {
      return false;
    } else {
      Task newTask = (Task) otherTask;
      return this.getDescription().equals(newTask.getDescription()) &&
             this.getId() == newTask.getId();
    }
  }

  public static List<Task> all() {
    String sql = "SELECT id, description FROM tasks";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql).executeAndFetch(Task.class);
    }
  }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO tasks (description) VALUES (:description)";
      this.id = (int) con.createQuery(sql, true)
        .addParameter("description", this.description)
        .executeUpdate()
        .getKey();
    }
  }

  public static Task find(int id) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT * FROM tasks where id=:id";
      Task task = con.createQuery(sql)
          .addParameter("id", id)
          .executeAndFetchFirst(Task.class);
          return task;
    }
  }

  public void addCategory(Category category) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO categories_tasks (category_id, task_id) VALUES (:category_id, :task_id)";
      con.createQuery(sql)
        .addParameter("category_id", category.getId())
        .addParameter("task_id", this.getId())
        .executeUpdate();
    }
  }

  public ArrayList<Category> getCategories() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT category_id FROM categories_tasks WHERE task_id = :task_id";
      List<Integer> categoryIds = con.createQuery(sql)
        .addParameter("task_id", this.getId())
        .executeAndFetch(Integer.class);

        ArrayList<Category> categories = new ArrayList<Category>();

        for(Integer categoryId : categoryIds) {
          String taskQuery = "SELECT * FROM categories WHERE id = :categoryId";
          Category category = con.createQuery(taskQuery)
            .addParameter("categoryId", categoryId)
            .executeAndFetchFirst(Category.class);
          categories.add(category);
        }
      return categories;
    }
  }

  public void delete() {
    try(Connection con = DB.sql2o.open()) {
      String deleteQuery = "DELETE FROM tasks WHERE id = :id";
        con.createQuery(deleteQuery)
        .addParameter("id", id)
        .executeUpdate();

      String joinDeleteQuery = "DELETE FROM categories_tasks WHERE task_id = :taskId";
        con.createQuery(joinDeleteQuery)
          .addParameter("taskId", this.getId())
          .executeUpdate();
    }
  }

  public void markCompleted() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "UPDATE tasks SET completed = true WHERE id = :taskId";
        con.createQuery(sql)
        .addParameter("taskId", this.getId())
        .executeUpdate();

    }
  }


  public void update(String description) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "UPDATE tasks SET description = :description WHERE id = :id";
      con.createQuery(sql)
        .addParameter("description", description)
        .addParameter("id", id)
        .executeUpdate();
    }
  }

}
