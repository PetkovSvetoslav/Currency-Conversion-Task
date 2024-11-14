package exchange.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversionRequest {
    private String fromCurrency;
    private String toCurrency;
    private Double amount;
}
