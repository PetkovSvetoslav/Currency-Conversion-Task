package exchange.task.service;

import exchange.task.model.CurrencyCode;
import exchange.task.model.CurrencyCodeEnum;
import exchange.task.repository.CurrencyCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CurrencyCodeService {

    private final CurrencyCodeRepository currencyCodeRepository;

    public List<CurrencyCode> getAllSupportedCurrencies() {
        return currencyCodeRepository.findAll();
    }

    public boolean existsByCode(String code) {
        try {
            CurrencyCodeEnum codeEnum = CurrencyCodeEnum.valueOf(code);
            return currencyCodeRepository.existsByCode(codeEnum);
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    @Transactional
    public CurrencyCode addCurrencyCode(String code, String description) {
        try {
            CurrencyCodeEnum codeEnum = CurrencyCodeEnum.valueOf(code);
            if (currencyCodeRepository.existsByCode(codeEnum)) {
                throw new IllegalArgumentException("Currency code already exists: " + code);
            }
            CurrencyCode currencyCode = new CurrencyCode();
            currencyCode.setCode(codeEnum);
            currencyCode.setDescription(description);
            return currencyCodeRepository.save(currencyCode);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid currency code: " + code);
        }
    }
}

