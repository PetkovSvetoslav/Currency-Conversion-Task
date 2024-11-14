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
public class ConversionHistory {
    private String transactionId;
    private String fromCurrency;
    private String toCurrency;
    private Double originalAmount;
    private Double convertedAmount;
    private LocalDateTime conversionTime;
}

