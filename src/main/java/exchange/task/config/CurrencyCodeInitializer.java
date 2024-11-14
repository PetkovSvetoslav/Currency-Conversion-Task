package exchange.task.config;

import exchange.task.model.CurrencyCodeEnum;
import exchange.task.service.CurrencyCodeService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class CurrencyCodeInitializer {

    private final CurrencyCodeService currencyCodeService;

    public CurrencyCodeInitializer(CurrencyCodeService currencyCodeService) {
        this.currencyCodeService = currencyCodeService;
    }

    @PostConstruct
    public void init() {
        for (CurrencyCodeEnum codeEnum : CurrencyCodeEnum.values()) {
            if (!currencyCodeService.existsByCode(codeEnum.name())) {
                currencyCodeService.addCurrencyCode(codeEnum.name(), codeEnum.getDescription());
            }
        }
    }
}


