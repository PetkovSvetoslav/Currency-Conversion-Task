package exchange.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeRateResponse {
    private String fromCurrency;
    private String toCurrency;
    private Double rate;
    private LocalDateTime fetchedAt;
}
