package exchange.task.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "currency_codes")
public class CurrencyCode {
    @Id
    @Enumerated(EnumType.STRING)
    private CurrencyCodeEnum code;
    private String description;
}

