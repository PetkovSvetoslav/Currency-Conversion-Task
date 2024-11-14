package exchange.task.repository;

import exchange.task.model.CurrencyCode;
import exchange.task.model.CurrencyCodeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CurrencyCodeRepository extends JpaRepository<CurrencyCode, CurrencyCodeEnum> {

    boolean existsByCode(CurrencyCodeEnum code);
}
