package jp.shiftcheck.dto;
import jp.shiftcheck.entity.HandoverItem;
import jp.shiftcheck.entity.HandoverStatus;
import jp.shiftcheck.entity.Priority;
import java.time.LocalDateTime;

public record HandoverItemResponse(Long id, String title, String assignee, String category,
  LocalDateTime dueAt, Priority priority, HandoverStatus status, boolean confirmed,
  LocalDateTime createdAt, LocalDateTime updatedAt, boolean overdue) {
  public static HandoverItemResponse from(HandoverItem i) {
    boolean late = i.getStatus() != HandoverStatus.DONE && i.getDueAt() != null
      && i.getDueAt().isBefore(LocalDateTime.now());
    return new HandoverItemResponse(i.getId(), i.getTitle(), i.getAssignee(), i.getCategory(),
      i.getDueAt(), i.getPriority(), i.getStatus(), i.isConfirmed(), i.getCreatedAt(), i.getUpdatedAt(), late);
  }
}
