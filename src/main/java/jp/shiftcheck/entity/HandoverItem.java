package jp.shiftcheck.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "handover_items")
public class HandoverItem {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false, length = 180) private String title;
  @Column(length = 80) private String assignee;
  @Column(nullable = false, length = 40) private String category = "申し送り";
  @Column(name = "due_at") private LocalDateTime dueAt;
  @Enumerated(EnumType.STRING) @Column(nullable = false, length = 10) private Priority priority = Priority.MEDIUM;
  @Enumerated(EnumType.STRING) @Column(nullable = false, length = 10) private HandoverStatus status = HandoverStatus.TODO;
  @Column(nullable = false) private boolean confirmed = false;
  @Column(name = "created_at", nullable = false, updatable = false) private LocalDateTime createdAt;
  @Column(name = "updated_at", nullable = false) private LocalDateTime updatedAt;

  @PrePersist void prePersist() {
    LocalDateTime now = LocalDateTime.now();
    if (createdAt == null) createdAt = now;
    updatedAt = now;
  }
  @PreUpdate void preUpdate() { updatedAt = LocalDateTime.now(); }

  public Long getId(){return id;} public String getTitle(){return title;}
  public String getAssignee(){return assignee;} public String getCategory(){return category;}
  public LocalDateTime getDueAt(){return dueAt;} public Priority getPriority(){return priority;}
  public HandoverStatus getStatus(){return status;} public boolean isConfirmed(){return confirmed;}
  public LocalDateTime getCreatedAt(){return createdAt;} public LocalDateTime getUpdatedAt(){return updatedAt;}
  public void setTitle(String v){title=v;} public void setAssignee(String v){assignee=v;}
  public void setCategory(String v){category=v;} public void setDueAt(LocalDateTime v){dueAt=v;}
  public void setPriority(Priority v){priority=v;} public void setStatus(HandoverStatus v){status=v;}
  public void setConfirmed(boolean v){confirmed=v;}
}
