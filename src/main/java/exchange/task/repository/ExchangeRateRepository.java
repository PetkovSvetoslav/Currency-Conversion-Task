package exchange.task.repository;

import exchange.task.model.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    Optional<ExchangeRate> findFirstByFromCurrencyAndToCurrencyOrderByFetchedAtDesc(String fromCurrency, String toCurrency);
}
