package exchange.task.controller;

import exchange.task.dto.ConversionRequest;
import exchange.task.dto.ConversionResponse;
import exchange.task.service.CurrencyConversionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/conversion")
@RequiredArgsConstructor
public class CurrencyConversionController {

    private final CurrencyConversionService currencyConversionService;

    @PostMapping
    public ResponseEntity<ConversionResponse> convertCurrency(@RequestBody ConversionRequest conversionRequest) {
        ConversionResponse response = currencyConversionService.convertCurrency(conversionRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<ConversionResponse> getConversionByTransactionId(@PathVariable String transactionId) {
        Optional<ConversionResponse> conversion = currencyConversionService.getConversionByTransactionId(transactionId);
        return conversion.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/history/date")
    public ResponseEntity<List<ConversionResponse>> getConversionHistoryByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<ConversionResponse> conversions = currencyConversionService.getConversionsByDate(startOfDay, endOfDay);

        if (conversions.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        return ResponseEntity.ok(conversions);
    }

    @GetMapping("/history")
    public ResponseEntity<List<ConversionResponse>> getConversionHistory() {
        List<ConversionResponse> conversions = currencyConversionService.getAllConversions();
        if (conversions.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(conversions);
    }


}
