package jp.shiftcheck.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jp.shiftcheck.entity.Priority;
import java.time.LocalDateTime;

public record HandoverItemRequest(
  @NotBlank(message="title は必須です") @Size(max=180, message="title は180文字以内です") String title,
  @Size(max=80, message="assignee は80文字以内です") String assignee,
  @Size(max=40, message="category は40文字以内です") String category,
  LocalDateTime dueAt,
  Priority priority
) {}
