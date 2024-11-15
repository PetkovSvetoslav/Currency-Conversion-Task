package exchange.task;

import exchange.task.model.CurrencyCode;
import exchange.task.model.CurrencyCodeEnum;
import exchange.task.repository.CurrencyCodeRepository;
import exchange.task.service.CurrencyCodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrencyCodeServiceTest {

    @Mock
    private CurrencyCodeRepository currencyCodeRepository;

    @InjectMocks
    private CurrencyCodeService currencyCodeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllSupportedCurrencies_ShouldReturnListOfCurrencyCodes() {
        CurrencyCode currencyCode1 = new CurrencyCode(CurrencyCodeEnum.USD, "United States Dollar");
        CurrencyCode currencyCode2 = new CurrencyCode(CurrencyCodeEnum.EUR, "Euro");
        when(currencyCodeRepository.findAll()).thenReturn(List.of(currencyCode1, currencyCode2));

        List<CurrencyCode> result = currencyCodeService.getAllSupportedCurrencies();

        assertEquals(2, result.size());
        assertEquals("USD", result.get(0).getCode().toString());
        assertEquals("EUR", result.get(1).getCode().toString());
        verify(currencyCodeRepository, times(1)).findAll();
    }

    @Test
    void existsByCode_ShouldReturnTrue_WhenCodeExists() {
        when(currencyCodeRepository.existsByCode(CurrencyCodeEnum.USD)).thenReturn(true);

        boolean result = currencyCodeService.existsByCode("USD");

        assertTrue(result);
        verify(currencyCodeRepository, times(1)).existsByCode(CurrencyCodeEnum.USD);
    }

    @Test
    void existsByCode_ShouldReturnFalse_WhenCodeDoesNotExist() {
        when(currencyCodeRepository.existsByCode(CurrencyCodeEnum.USD)).thenReturn(false);

        boolean result = currencyCodeService.existsByCode("USD");

        assertFalse(result);
        verify(currencyCodeRepository, times(1)).existsByCode(CurrencyCodeEnum.USD);
    }

    @Test
    void existsByCode_ShouldReturnFalse_WhenInvalidCodeIsProvided() {
        boolean result = currencyCodeService.existsByCode("INVALID_CODE");

        assertFalse(result);
        verifyNoInteractions(currencyCodeRepository);
    }

    @Test
    void addCurrencyCode_ShouldAddAndReturnCurrencyCode_WhenValidCodeAndDescriptionProvided() {
        CurrencyCode currencyCode = new CurrencyCode(CurrencyCodeEnum.USD, "United States Dollar");
        when(currencyCodeRepository.existsByCode(CurrencyCodeEnum.USD)).thenReturn(false);
        when(currencyCodeRepository.save(any(CurrencyCode.class))).thenReturn(currencyCode);

        CurrencyCode result = currencyCodeService.addCurrencyCode("USD", "United States Dollar");

        assertNotNull(result);
        assertEquals(CurrencyCodeEnum.USD, result.getCode());
        assertEquals("United States Dollar", result.getDescription());
        verify(currencyCodeRepository, times(1)).existsByCode(CurrencyCodeEnum.USD);
        verify(currencyCodeRepository, times(1)).save(any(CurrencyCode.class));
    }

    @Test
    void addCurrencyCode_ShouldThrowException_WhenCodeAlreadyExists() {
        when(currencyCodeRepository.existsByCode(CurrencyCodeEnum.USD)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                currencyCodeService.addCurrencyCode("USD", "United States Dollar"));
        assertEquals("Invalid currency code: USD", exception.getMessage());
        verify(currencyCodeRepository, times(1)).existsByCode(CurrencyCodeEnum.USD);
        verifyNoMoreInteractions(currencyCodeRepository);
    }

    @Test
    void addCurrencyCode_ShouldThrowException_WhenInvalidCodeIsProvided() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                currencyCodeService.addCurrencyCode("INVALID_CODE", "Invalid Code Description"));
        assertEquals("Invalid currency code: INVALID_CODE", exception.getMessage());
        verifyNoInteractions(currencyCodeRepository);
    }
}

