package jp.shiftcheck.controller;
import jakarta.validation.Valid;
import jp.shiftcheck.dto.*;
import jp.shiftcheck.entity.*;
import jp.shiftcheck.service.HandoverItemService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/handover-items")
public class HandoverItemController {
  private final HandoverItemService service;
  public HandoverItemController(HandoverItemService service){this.service=service;}

  @GetMapping public List<HandoverItemResponse> findAll(
    @RequestParam(required=false) HandoverStatus status,
    @RequestParam(required=false) Priority priority,
    @RequestParam(required=false) String q) {
    return service.findAll(status, priority, q);
  }
  @GetMapping("/{id}") public HandoverItemResponse findById(@PathVariable Long id){return service.findById(id);}
  @PostMapping @ResponseStatus(HttpStatus.CREATED)
  public HandoverItemResponse create(@Valid @RequestBody HandoverItemRequest req){return service.create(req);}
  @PatchMapping("/{id}") public HandoverItemResponse update(@PathVariable Long id,@RequestBody StatusUpdateRequest req){return service.update(id,req);}
  @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id){service.delete(id);}
}
