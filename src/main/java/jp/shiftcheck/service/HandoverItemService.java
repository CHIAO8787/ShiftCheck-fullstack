package jp.shiftcheck.service;

import jp.shiftcheck.dto.*;
import jp.shiftcheck.entity.*;
import jp.shiftcheck.exception.ResourceNotFoundException;
import jp.shiftcheck.repository.HandoverItemRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class HandoverItemService {
  private final HandoverItemRepository repo;
  public HandoverItemService(HandoverItemRepository repo){this.repo=repo;}

  @Transactional(readOnly=true)
  public List<HandoverItemResponse> findAll(HandoverStatus status, Priority priority, String q) {
    Specification<HandoverItem> spec = (r, query, cb) -> cb.conjunction();
    if(status != null) spec = spec.and((r, query, cb) -> cb.equal(r.get("status"), status));
    if(priority != null) spec = spec.and((r, query, cb) -> cb.equal(r.get("priority"), priority));
    if(q != null && !q.isBlank()) {
      String p = "%" + q.trim().toLowerCase(Locale.ROOT) + "%";
      spec = spec.and((r, query, cb) -> cb.or(
        cb.like(cb.lower(r.get("title")), p),
        cb.like(cb.lower(r.get("assignee")), p),
        cb.like(cb.lower(r.get("category")), p)));
    }
    return repo.findAll(spec).stream().map(HandoverItemResponse::from).toList();
  }

  @Transactional(readOnly=true)
  public HandoverItemResponse findById(Long id){return HandoverItemResponse.from(get(id));}

  public HandoverItemResponse create(HandoverItemRequest req) {
    HandoverItem i = new HandoverItem();
    i.setTitle(req.title().trim());
    i.setAssignee(req.assignee()==null || req.assignee().isBlank()?null:req.assignee().trim());
    i.setCategory(req.category()==null || req.category().isBlank()?"申し送り":req.category().trim());
    i.setDueAt(req.dueAt()); i.setPriority(req.priority()==null?Priority.MEDIUM:req.priority());
    i.setStatus(HandoverStatus.TODO); i.setConfirmed(false);
    return HandoverItemResponse.from(repo.save(i));
  }

  public HandoverItemResponse update(Long id, StatusUpdateRequest req) {
    if(req==null || req.isEmpty()) throw new IllegalArgumentException("status または confirmed を指定してください");
    HandoverItem i=get(id);
    if(req.status()!=null) {
      i.setStatus(req.status());
      if(req.status()==HandoverStatus.DONE) i.setConfirmed(true);
    }
    if(req.confirmed()!=null) i.setConfirmed(req.confirmed());
    return HandoverItemResponse.from(repo.save(i));
  }
  public void delete(Long id){repo.delete(get(id));}
  private HandoverItem get(Long id){return repo.findById(id).orElseThrow(()->new ResourceNotFoundException("申し送りが見つかりません: id="+id));}
}
