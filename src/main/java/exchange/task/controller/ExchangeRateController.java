package exchange.task.controller;

import exchange.task.dto.ExchangeRateResponse;
import exchange.task.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/exchange-rate")
@RequiredArgsConstructor
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    @GetMapping
    public ResponseEntity<ExchangeRateResponse> getExchangeRate(@RequestParam String fromCurrency,
                                                                @RequestParam String toCurrency) {
        Optional<ExchangeRateResponse> exchangeRate = exchangeRateService.getLatestExchangeRate(fromCurrency, toCurrency);
        return exchangeRate.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ExchangeRateResponse(fromCurrency, toCurrency, null, LocalDateTime.now())));
    }

    @PostMapping
    public ResponseEntity<?> addExchangeRate(@RequestParam String fromCurrency,
                                             @RequestParam String toCurrency,
                                             @RequestParam Double rate) {
        try {
            if (rate <= 0) {
                throw new IllegalArgumentException("Exchange rate must be greater than 0");
            }

            ExchangeRateResponse savedRate = exchangeRateService.saveExchangeRate(fromCurrency, toCurrency, rate);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedRate);

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + ex.getMessage());
        }
    }

}

