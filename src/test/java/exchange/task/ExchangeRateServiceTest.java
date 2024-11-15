package exchange.task;

import exchange.task.dto.ExchangeRateResponse;
import exchange.task.exception.ExternalApiException;
import exchange.task.model.ExchangeRate;
import exchange.task.repository.ExchangeRateRepository;
import exchange.task.service.ExchangeRateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ExchangeRateServiceTest {

    @InjectMocks
    private ExchangeRateService exchangeRateService;

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @Mock
    private RestTemplate restTemplate;

    @Captor
    private ArgumentCaptor<ExchangeRate> exchangeRateCaptor;

    @BeforeEach
    void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        setPrivateApiKeyField("testApiKey");
    }

    private void setPrivateApiKeyField(String apiKey) throws Exception {
        Field apiKeyField = ExchangeRateService.class.getDeclaredField("apiKey");
        apiKeyField.setAccessible(true);
        apiKeyField.set(exchangeRateService, apiKey);
    }

    @Test
    void getLatestExchangeRate_shouldReturnCachedRate_whenAvailable() {
        String fromCurrency = "USD";
        String toCurrency = "EUR";
        ExchangeRate cachedRate = ExchangeRate.builder()
                .fromCurrency(fromCurrency)
                .toCurrency(toCurrency)
                .rate(0.85)
                .fetchedAt(LocalDateTime.now())
                .build();
        when(exchangeRateRepository.findFirstByFromCurrencyAndToCurrencyOrderByFetchedAtDesc(fromCurrency, toCurrency))
                .thenReturn(Optional.of(cachedRate));

        Optional<ExchangeRateResponse> response = exchangeRateService.getLatestExchangeRate(fromCurrency, toCurrency);

        assertTrue(response.isPresent());
        assertEquals(0.85, response.get().getRate());
        verify(exchangeRateRepository, times(1))
                .findFirstByFromCurrencyAndToCurrencyOrderByFetchedAtDesc(fromCurrency, toCurrency);
    }

    @Test
    void fetchAndSaveExchangeRate_shouldThrowException_whenApiFails() {
        String fromCurrency = "USD";
        String toCurrency = "JPY";
        String apiUrl = "https://api.exchangerate.host/latest?base=USD&symbols=JPY&access_key=testApiKey";

        when(restTemplate.exchange(eq(apiUrl), eq(org.springframework.http.HttpMethod.GET), isNull(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

        assertThrows(ExternalApiException.class, () ->
                exchangeRateService.fetchAndSaveExchangeRate(fromCurrency, toCurrency));
    }

    @Test
    void saveExchangeRate_shouldSaveRateSuccessfully() {
        String fromCurrency = "USD";
        String toCurrency = "CAD";
        Double rate = 1.25;

        ExchangeRateResponse response = exchangeRateService.saveExchangeRate(fromCurrency, toCurrency, rate);

        verify(exchangeRateRepository).save(exchangeRateCaptor.capture());
        ExchangeRate savedRate = exchangeRateCaptor.getValue();
        assertEquals(fromCurrency, savedRate.getFromCurrency());
        assertEquals(toCurrency, savedRate.getToCurrency());
        assertEquals(rate, savedRate.getRate());
        assertEquals(fromCurrency, response.getFromCurrency());
        assertEquals(toCurrency, response.getToCurrency());
        assertEquals(rate, response.getRate());
    }
}

