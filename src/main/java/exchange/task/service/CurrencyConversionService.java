package exchange.task.service;

import exchange.task.dto.ConversionRequest;
import exchange.task.dto.ConversionResponse;
import exchange.task.exception.ExchangeRateNotFoundException;
import exchange.task.exception.ExternalApiException;
import exchange.task.model.CurrencyConversion;
import exchange.task.repository.CurrencyConversionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class CurrencyConversionService {

    private final CurrencyConversionRepository currencyConversionRepository;
    private final ExchangeRateService exchangeRateService;

    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(CurrencyConversionService.class);
    private static final String SUCCESS_STATUS = "SUCCESS";

    @Value("${external.api.key}")
    private String apiKey;

    public ConversionResponse convertCurrency(ConversionRequest conversionRequest) {
        String fromCurrency = conversionRequest.getFromCurrency();
        String toCurrency = conversionRequest.getToCurrency();
        Double amount = conversionRequest.getAmount();

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("API Access Key is missing. Please configure 'external.api.key' in application.properties.");
        }

        String apiUrl = "https://api.exchangerate.host/convert?from=" + fromCurrency
                + "&to=" + toCurrency
                + "&amount=" + amount
                + "&access_key=" + apiKey;

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> body = response.getBody();

            if (body == null || body.get("result") == null) {
                logger.error("Exchange rate not available for {} to {}", fromCurrency, toCurrency);
                throw new ExchangeRateNotFoundException("Exchange rate not available for " + fromCurrency + " to " + toCurrency);
            }

            Double convertedAmount = Double.valueOf(body.get("result").toString());
            String transactionId = UUID.randomUUID().toString();

            CurrencyConversion conversion = CurrencyConversion.builder()
                    .transactionId(transactionId)
                    .fromCurrency(fromCurrency)
                    .toCurrency(toCurrency)
                    .originalAmount(amount)
                    .convertedAmount(convertedAmount)
                    .conversionTime(LocalDateTime.now())
                    .build();

            currencyConversionRepository.save(conversion);

            logger.info("Transaction ID: {}, Converted Amount: {}", transactionId, convertedAmount);

            return ConversionResponse.builder()
                    .transactionId(transactionId)
                    .fromCurrency(fromCurrency)
                    .toCurrency(toCurrency)
                    .originalAmount(amount)
                    .convertedAmount(convertedAmount)
                    .conversionTime(LocalDateTime.now())
                    .status(SUCCESS_STATUS)
                    .build();
        } else {
            logger.error("Failed to fetch exchange rate from external API. HTTP Status: {}", response.getStatusCode());
            throw new ExternalApiException("Failed to fetch exchange rate from external API");
        }
    }
    
    public Optional<ConversionResponse> getConversionByTransactionId(String transactionId) {
        return currencyConversionRepository.findByTransactionId(transactionId)
                .map(conversion -> ConversionResponse.builder()
                        .transactionId(conversion.getTransactionId())
                        .fromCurrency(conversion.getFromCurrency())
                        .toCurrency(conversion.getToCurrency())
                        .originalAmount(conversion.getOriginalAmount())
                        .convertedAmount(conversion.getConvertedAmount())
                        .conversionTime(conversion.getConversionTime())
                        .status(SUCCESS_STATUS)
                        .build());
    }

    public List<ConversionResponse> getConversionsByDate(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            startDate = LocalDateTime.MIN;
            endDate = LocalDateTime.MAX;
        }

        List<CurrencyConversion> conversions = currencyConversionRepository.findAllByConversionTimeBetween(startDate, endDate);
        return conversions.stream()
                .map(conversion -> ConversionResponse.builder()
                        .transactionId(conversion.getTransactionId())
                        .fromCurrency(conversion.getFromCurrency())
                        .toCurrency(conversion.getToCurrency())
                        .originalAmount(conversion.getOriginalAmount())
                        .convertedAmount(conversion.getConvertedAmount())
                        .conversionTime(conversion.getConversionTime())
                        .status(SUCCESS_STATUS)
                        .build())
                .toList();
    }


    public List<ConversionResponse> getAllConversions() {
        List<CurrencyConversion> conversions = currencyConversionRepository.findAll();
        return conversions.stream()
                .map(conversion -> ConversionResponse.builder()
                        .transactionId(conversion.getTransactionId())
                        .fromCurrency(conversion.getFromCurrency())
                        .toCurrency(conversion.getToCurrency())
                        .originalAmount(conversion.getOriginalAmount())
                        .convertedAmount(conversion.getConvertedAmount())
                        .conversionTime(conversion.getConversionTime())
                        .status(SUCCESS_STATUS)
                        .build())
                .toList();
    }

}
