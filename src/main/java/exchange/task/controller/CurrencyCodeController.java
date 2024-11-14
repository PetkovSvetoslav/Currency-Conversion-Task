package exchange.task.controller;

import exchange.task.model.CurrencyCode;
import exchange.task.service.CurrencyCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/currency-code")
@RequiredArgsConstructor
public class CurrencyCodeController {

    private final CurrencyCodeService currencyCodeService;

    @GetMapping
    public ResponseEntity<List<CurrencyCode>> getAllCurrencyCodes() {
        List<CurrencyCode> codes = currencyCodeService.getAllSupportedCurrencies();
        return ResponseEntity.ok(codes);
    }

    @PostMapping
    public ResponseEntity<CurrencyCode> addCurrencyCode(@RequestParam String code,
                                                        @RequestParam String description) {
        CurrencyCode savedCode = currencyCodeService.addCurrencyCode(code, description);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCode);
    }
}
