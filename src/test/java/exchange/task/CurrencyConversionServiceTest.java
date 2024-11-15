package exchange.task;

import exchange.task.dto.ConversionRequest;
import exchange.task.dto.ConversionResponse;
import exchange.task.exception.ExternalApiException;
import exchange.task.model.CurrencyConversion;
import exchange.task.repository.CurrencyConversionRepository;
import exchange.task.service.CurrencyConversionService;
import exchange.task.service.ExchangeRateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrencyConversionServiceTest {

    @Mock
    private CurrencyConversionRepository currencyConversionRepository;

    @Mock
    private ExchangeRateService exchangeRateService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CurrencyConversionService currencyConversionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConvertCurrency_Success() throws Exception {
        ConversionRequest request = new ConversionRequest("USD", "EUR", 100.0);
        String apiUrl = "https://api.exchangerate.host/convert?from=USD&to=EUR&amount=100.0&access_key=testApiKey";
        String transactionId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        Map<String, Object> apiResponse = Map.of(
                "result", 85.5
        );

        CurrencyConversion conversion = CurrencyConversion.builder()
                .transactionId(transactionId)
                .fromCurrency("USD")
                .toCurrency("EUR")
                .originalAmount(100.0)
                .convertedAmount(85.5)
                .conversionTime(now)
                .build();

        Field apiKeyField = CurrencyConversionService.class.getDeclaredField("apiKey");
        apiKeyField.setAccessible(true);
        apiKeyField.set(currencyConversionService, "testApiKey");

        when(restTemplate.exchange(eq(apiUrl), eq(HttpMethod.GET), isNull(), ArgumentMatchers.<ParameterizedTypeReference<Map<String, Object>>>any()))
                .thenReturn(new ResponseEntity<>(apiResponse, HttpStatus.OK));
        when(currencyConversionRepository.save(any(CurrencyConversion.class))).thenReturn(conversion);

        ConversionResponse response = currencyConversionService.convertCurrency(request);

        assertNotNull(response);
        assertEquals("USD", response.getFromCurrency());
        assertEquals("EUR", response.getToCurrency());
        assertEquals(100.0, response.getOriginalAmount());
        assertEquals(85.5, response.getConvertedAmount());
        assertEquals("SUCCESS", response.getStatus());
    }

    @Test
    void testGetConversionByTransactionId_Found() {
        String transactionId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        CurrencyConversion conversion = CurrencyConversion.builder()
                .transactionId(transactionId)
                .fromCurrency("USD")
                .toCurrency("EUR")
                .originalAmount(100.0)
                .convertedAmount(85.5)
                .conversionTime(now)
                .build();

        when(currencyConversionRepository.findByTransactionId(transactionId)).thenReturn(Optional.of(conversion));

        Optional<ConversionResponse> response = currencyConversionService.getConversionByTransactionId(transactionId);

        assertTrue(response.isPresent());
        assertEquals("USD", response.get().getFromCurrency());
        assertEquals("EUR", response.get().getToCurrency());
    }

    @Test
    void testGetConversionByTransactionId_NotFound() {
        String transactionId = UUID.randomUUID().toString();

        when(currencyConversionRepository.findByTransactionId(transactionId)).thenReturn(Optional.empty());

        Optional<ConversionResponse> response = currencyConversionService.getConversionByTransactionId(transactionId);

        assertFalse(response.isPresent());
    }

    @Test
    void testGetConversionsByDate() {
        LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 12, 31, 23, 59);

        CurrencyConversion conversion1 = CurrencyConversion.builder()
                .transactionId(UUID.randomUUID().toString())
                .fromCurrency("USD")
                .toCurrency("EUR")
                .originalAmount(100.0)
                .convertedAmount(85.5)
                .conversionTime(LocalDateTime.of(2024, 6, 15, 12, 0))
                .build();

        CurrencyConversion conversion2 = CurrencyConversion.builder()
                .transactionId(UUID.randomUUID().toString())
                .fromCurrency("GBP")
                .toCurrency("USD")
                .originalAmount(200.0)
                .convertedAmount(250.0)
                .conversionTime(LocalDateTime.of(2024, 7, 20, 14, 0))
                .build();

        when(currencyConversionRepository.findAllByConversionTimeBetween(startDate, endDate))
                .thenReturn(List.of(conversion1, conversion2));

        List<ConversionResponse> responses = currencyConversionService.getConversionsByDate(startDate, endDate);

        assertEquals(2, responses.size());
        assertEquals("USD", responses.get(0).getFromCurrency());
        assertEquals("GBP", responses.get(1).getFromCurrency());
    }
}

