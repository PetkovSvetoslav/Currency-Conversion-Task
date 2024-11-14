# Currency Conversion Application

This application is a self-contained RESTful service built using Spring Boot that provides currency exchange rate functionality, conversion services, and transaction history management. 
It integrates with an external exchange rate provider for real-time rates and supports both in-memory & H2 database operations for persistence and testing.
There are added (documented) settings for working with PostgreSQL DB, by default now work with H2 for easier testing.
We use exchangerate.host API key for exchange rate integration. 
---

## Features
- **Exchange Rate Endpoint**: Fetch the current exchange rate for a given currency pair.
- **Currency Conversion Endpoint**: Convert a specified amount from one currency to another.
- **Conversion History**: Retrieve transaction history, with support for filtering by date.
- **Error Handling**: Provides meaningful error messages and HTTP response codes.

---

## H2 Database Configuration
- **JDBC URL**: `jdbc:h2:mem:exchangeApp`
- **Console URL**: `http://localhost:8080/h2-console`
- **Username**: `sa`
- **Password**: (empty)

### Sample Query
```sql
SELECT * FROM currency_conversion;
```

## Application Structure

```
.
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── controller
│   │   │   │   └── CurrencyCodeService.java
│   │   │   │   └── CurrencyConversionController.java
│   │   │   │   └── ExchangeRateController.java
│   │   │   │   └── HomeController.java
│   │   │   ├── service
│   │   │   │   ├── CurrencyCodeService.java
│   │   │   │   ├── CurrencyConversionService.java
│   │   │   │   └── ExchangeRateService.java
│   │   │   ├── model
│   │   │   │   └── CurrencyCode.java
│   │   │   │   └── CurrencyCodeEnum.java
│   │   │   │   └── CurrencyConversion.java
│   │   │   │   └── ExchangeRate.java
│   │   │   ├── dto
│   │   │   │   ├── ConversionHistory.java
│   │   │   │   ├── ConversionRequest.java
│   │   │   │   ├── ConversionResponse.java
│   │   │   │   └── ExchangeRateResponse.java
│   │   │   ├── repository
│   │   │   │   └── CurrencyCodeRepository.java
│   │   │   │   └── CurrencyConversionRepository.java
│   │   │   │   └── ExchangeRateRepository.java
│   │   │   ├── exception
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   └── Application.java
│   │   ├── resources
│   │   │   ├── application.properties
│   │   └── static
│   │       └── index.html
│   └── test
│       └── java
│           └── ...
```

## Endpoints

### Exchange Rate Endpoint
**URL**: `/api/exchange-rate`
- **Method**: `GET`
- **Input**: Query parameters `fromCurrency` and `toCurrency`.
- **Output**: The current exchange rate between the specified currencies.
- **Example Request**:
  ```
  GET /api/exchange-rate?fromCurrency=USD&toCurrency=EUR
  ```
- **Example Response**:
  ```json
  {
      "fromCurrency": "USD",
      "toCurrency": "EUR",
      "rate": 0.85
  }
  ```

### Currency Conversion Endpoint
**URL**: `/api/conversion`
- **Method**: `POST`
- **Input**: JSON body containing `fromCurrency`, `toCurrency`, and `amount`.
- **Output**: Converted amount and transaction details.
- **Example Request**:
  ```json
  {
      "fromCurrency": "USD",
      "toCurrency": "EUR",
      "amount": 100
  }
  ```
- **Example Response**:
  ```json
  {
      "transactionId": "1234-5678-91011",
      "fromCurrency": "USD",
      "toCurrency": "EUR",
      "originalAmount": 100,
      "convertedAmount": 85,
      "conversionTime": "2024-11-14T15:00:00"
  }
  ```

### Get All Conversion History
**URL**: `/api/conversion/history`
- **Method**: `GET`
- **Input**: None.
- **Output**: List of all transaction history.
- **Example Response**:
  ```json
  [
      {
          "transactionId": "1234-5678-91011",
          "fromCurrency": "USD",
          "toCurrency": "EUR",
          "originalAmount": 100,
          "convertedAmount": 85,
          "conversionTime": "2024-11-14T15:00:00"
      }
  ]
  ```

### Get Conversion History By Date
**URL**: `/api/conversion/history/date`
- **Method**: `GET`
- **Input**: Query parameter `date` (ISO format, e.g., `2024-11-14`).
- **Output**: List of transactions filtered by the specified date.
- **Example Request**:
  ```
  GET /api/conversion/history/date?date=2024-11-14
  ```
- **Example Response**:
  ```json
  [
      {
          "transactionId": "1234-5678-91011",
          "fromCurrency": "USD",
          "toCurrency": "EUR",
          "originalAmount": 100,
          "convertedAmount": 85,
          "conversionTime": "2024-11-14T15:00:00"
      }
  ]
  ```
---

## How to Run the Application

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd <repository-folder>
   ```

2. Build the project:
   ```bash
   ./mvnw clean install
   ```

3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

4. Access the web application:
   - Web Frontend: `http://localhost:8080`
   - H2 Console: `http://localhost:8080/h2-console`

---

## Docker Support
1. Build Docker Image:
   ```bash
   docker build -t currency-conversion-app .
   ```

2. Run Docker Container:
   ```bash
   docker run -p 8080:8080 currency-conversion-app
   ```

