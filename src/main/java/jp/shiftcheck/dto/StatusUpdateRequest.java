package jp.shiftcheck.dto;
import jp.shiftcheck.entity.HandoverStatus;
public record StatusUpdateRequest(HandoverStatus status, Boolean confirmed) {
  public boolean isEmpty(){ return status == null && confirmed == null; }
}
