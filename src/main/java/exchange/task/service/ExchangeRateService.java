package exchange.task.service;

import exchange.task.dto.ExchangeRateResponse;
import exchange.task.exception.ExchangeRateNotFoundException;
import exchange.task.exception.ExternalApiException;
import exchange.task.model.ExchangeRate;
import exchange.task.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateService.class);

    @Value("${external.api.key}")
    private String apiKey;

    public Optional<ExchangeRateResponse> getLatestExchangeRate(String fromCurrency, String toCurrency) {
        Optional<ExchangeRate> optionalRate = exchangeRateRepository
                .findFirstByFromCurrencyAndToCurrencyOrderByFetchedAtDesc(fromCurrency, toCurrency);

        if (optionalRate.isPresent()) {
            ExchangeRate rate = optionalRate.get();
            return Optional.of(new ExchangeRateResponse(
                    rate.getFromCurrency(),
                    rate.getToCurrency(),
                    rate.getRate(),
                    rate.getFetchedAt()
            ));
        } else {
            ExchangeRateResponse newRate = fetchAndSaveExchangeRate(fromCurrency, toCurrency);
            return Optional.of(newRate);
        }
    }

    public ExchangeRateResponse fetchAndSaveExchangeRate(String fromCurrency, String toCurrency) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("API Access Key is missing. Please configure 'external.api.key' in application.properties.");
        }

        String apiUrl = "https://api.exchangerate.host/latest?base=" + fromCurrency + "&symbols=" + toCurrency + "&access_key=" + apiKey;

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> body = response.getBody();

            if (body == null || !body.containsKey("rates")) {
                logger.error("No rates found in the API response for currencies: {} to {}", fromCurrency, toCurrency);
                throw new ExchangeRateNotFoundException("Exchange rate not available for " + fromCurrency + " to " + toCurrency);
            }

            @SuppressWarnings("unchecked")
            Map<String, Double> rates = (Map<String, Double>) body.get("rates");

            if (rates == null || !rates.containsKey(toCurrency)) {
                logger.error("Exchange rate not found in the 'rates' section of the response for: {} to {}", fromCurrency, toCurrency);
                throw new ExchangeRateNotFoundException("Exchange rate not available for " + fromCurrency + " to " + toCurrency);
            }

            Double rate = rates.get(toCurrency);

            ExchangeRate exchangeRate = ExchangeRate.builder()
                    .fromCurrency(fromCurrency)
                    .toCurrency(toCurrency)
                    .rate(rate)
                    .fetchedAt(LocalDateTime.now())
                    .build();

            exchangeRateRepository.save(exchangeRate);

            logger.info("Exchange rate for {} to {} successfully saved with rate: {}", fromCurrency, toCurrency, rate);

            return new ExchangeRateResponse(fromCurrency, toCurrency, rate, LocalDateTime.now());
        } else {
            logger.error("Failed to fetch exchange rate from external API. HTTP Status: {}", response.getStatusCode());
            throw new ExternalApiException("Failed to fetch exchange rate from external API");
        }
    }
    public ExchangeRateResponse saveExchangeRate(String fromCurrency, String toCurrency, Double rate) {
        ExchangeRate exchangeRate = ExchangeRate.builder()
                .fromCurrency(fromCurrency)
                .toCurrency(toCurrency)
                .rate(rate)
                .fetchedAt(LocalDateTime.now())
                .build();

        exchangeRateRepository.save(exchangeRate);

        return new ExchangeRateResponse(
                fromCurrency,
                toCurrency,
                rate,
                LocalDateTime.now()
        );
    }
}
