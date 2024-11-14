package exchange.task.repository;

import exchange.task.model.CurrencyConversion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CurrencyConversionRepository extends JpaRepository<CurrencyConversion, Long> {

    Optional<CurrencyConversion> findByTransactionId(String transactionId);

    List<CurrencyConversion> findAllByConversionTimeBetween(LocalDateTime startDate, LocalDateTime endDate);
}
