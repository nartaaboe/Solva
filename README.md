# Transaction and Limit Management API

## Описание
Это REST API для управления транзакциями и лимитами расходов. API предоставляет следующие возможности:

- Создание и просмотр транзакций с фильтрацией и постраничной навигацией.
- Установка, удаление и получение текущих лимитов расходов.

Все данные хранятся в **PostgreSQL**, а API документирован с помощью **Swagger**.

---

## Стек технологий
- **Backend:** Java, Spring Boot
- **База данных:** PostgreSQL
- **API Документация:** Swagger (OpenAPI 3)
- **Контейнеризация:** Docker, Docker Compose

---

## Требования
- **Java** 17+
- **Maven**
- **Docker и Docker Compose**

---

## Установка и запуск

1. **Клонирование репозитория:**
   ```bash
   git clone https://github.com/nartaaboe/Solva.git


## Запустите PostgreSQL с помощью Docker Compose:
docker-compose up -d

## Убедитесь, что все зависимости установлены:
./mvnw clean install

## Запуск
./mvnw spring-boot:run

Для просмотра документации Swagger перейдите по ссылке:
http://localhost:8080/swagger-ui/index.html

## Примеры запросов
## 1. Получение текущего лимита по категории (продукты и сервисы)
GET /api/limit?expenseCategory=products
ответ:
   {
   "id": 1,
   "limitSum": 500.0,
   "limitDateTime": "2024-10-13T23:38:41.000Z",
   "expirationDateTime": "2024-10-13T23:38:41.000Z",
   "expenseCategory": "products",
   "limitCurrencyShortname": "USD"
   }
## 2. Создание нового лимита для категории
POST /api/limit
Тело запроса:
```
   {
   "limitSum": 500.0,
   "expenseCategory": "services",
   "limitCurrencyShortname": "USD"
   }
```
## 3. Снятие лимита с категории
DELETE /api/limit?expenseCategory=products
```
   {
   "id": 0,
   "limitSum": 0,
   "limitDateTime": "2024-10-13T23:42:20.046Z",
   "expirationDateTime": "2024-10-13T23:42:20.046Z",
   "expenseCategory": "string",
   "limitCurrencyShortname": "string"
   }
```
в expirationDateTime устанавливается текущее время и так оно убирается но не удаляется с дб
## 4. Создание транзакции
POST /api/transaction
Тело запроса:
```
   {
   "accountFrom": "accountA",
   "accountTo": "accountB",
   "currencyShortname": "KZT",
   "sum": 400000.0,
   "expenseCategory": "products"
   }
```
ответ:
```
   {
   "id": 1,
   "accountFrom": "accountA",
   "accountTo": "accountB",
   "currencyShortname": "KZT",
   "sum": 400000.0,
   "sumInUSD": 880.0,
   "expenseCategory": "products",
   "transactionDateTime": "2024-10-13T23:44:59.278Z",
   "limitExceeded": false
   }
```
## 5. Получить транзакции превышающий лимит
GET /api/transaction/exceeding-limit?expenseCategory=services
ответ:
```
[
   {
   "id": 1,
   "accountFrom": "accountA",
   "accountTo": "accountB",
   "currencyShortname": "KZT",
   "sum": 400000.0,
   "sumInUSD": 880.0,
   "expenseCategory": "products",
   "transactionDateTime": "2024-10-13T23:44:59.278Z",
   "limitExceeded": false
   },
   {
   "id": 2,
   "accountFrom": "accountA",
   "accountTo": "accountB",
   "currencyShortname": "KZT",
   "sum": 400000.0,
   "sumInUSD": 880.0,
   "expenseCategory": "products",
   "transactionDateTime": "2024-10-13T23:44:59.278Z",
   "limitExceeded": true
   }
]
```