package exchange.task.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "currency_codes")
public class CurrencyCode {
    @Id
    @Enumerated(EnumType.STRING)
    private CurrencyCodeEnum code;
    private String description;
}

