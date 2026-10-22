package jp.shiftcheck.repository;
import jp.shiftcheck.entity.HandoverItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
public interface HandoverItemRepository extends JpaRepository<HandoverItem, Long>, JpaSpecificationExecutor<HandoverItem> {}
