package jp.shiftcheck.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jp.shiftcheck.dto.*;
import jp.shiftcheck.entity.*;
import jp.shiftcheck.exception.ResourceNotFoundException;
import jp.shiftcheck.service.HandoverItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HandoverItemController.class)
class HandoverItemControllerTest {
  @Autowired MockMvc mvc;
  @MockBean HandoverItemService service;

  private HandoverItemResponse sample(){
    LocalDateTime t=LocalDateTime.of(2030,1,1,9,0);
    return new HandoverItemResponse(1L,"備品を確認する","夜勤チーム","物品確認",t,
      Priority.HIGH,HandoverStatus.TODO,false,t,t,false);
  }

  @Test void listReturnsArray() throws Exception {
    when(service.findAll(null,null,null)).thenReturn(List.of(sample()));
    mvc.perform(get("/api/handover-items")).andExpect(status().isOk())
      .andExpect(jsonPath("$[0].title").value("備品を確認する"));
  }

  @Test void validCreateReturns201() throws Exception {
    when(service.create(any(HandoverItemRequest.class))).thenReturn(sample());
    mvc.perform(post("/api/handover-items").contentType(MediaType.APPLICATION_JSON)
      .content("{\"title\":\"備品を確認する\",\"priority\":\"HIGH\"}"))
      .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(1));
  }

  @Test void blankTitleReturns400() throws Exception {
    mvc.perform(post("/api/handover-items").contentType(MediaType.APPLICATION_JSON)
      .content("{\"title\":\"   \"}"))
      .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
  }

  @Test void missingItemReturns404() throws Exception {
    when(service.findById(999L)).thenThrow(new ResourceNotFoundException("not found"));
    mvc.perform(get("/api/handover-items/999")).andExpect(status().isNotFound())
      .andExpect(jsonPath("$.status").value(404));
  }

  @Test void patchReturnsUpdatedRecord() throws Exception {
    HandoverItemResponse updated = new HandoverItemResponse(1L, "備品を確認する", "夜勤チーム", "物品確認", LocalDateTime.of(2030,1,1,9,0), Priority.HIGH, HandoverStatus.DOING, false, LocalDateTime.of(2030,1,1,9,0), LocalDateTime.of(2030,1,1,9,0), false);
    when(service.update(eq(1L), any(StatusUpdateRequest.class))).thenReturn(updated);
    mvc.perform(patch("/api/handover-items/1").contentType(MediaType.APPLICATION_JSON)
      .content("{\"status\":\"DOING\"}"))
      .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("DOING"));
  }
}
