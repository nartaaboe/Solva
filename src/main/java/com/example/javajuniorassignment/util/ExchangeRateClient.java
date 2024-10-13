package com.example.javajuniorassignment.util;

import com.example.javajuniorassignment.entity.ExchangeRate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;


@Component
@RequiredArgsConstructor
public class ExchangeRateClient {
    private final WebClient webClient;
    @Value("${twelve.data.api-key}")
    private String apikey;
    /**
     * Получает последние курсы валют для указанных пар.
     * <p>
     * Выполняет GET-запрос к API и возвращает реактивный объект {@link Mono}, содержащий карту с ключами
     * (символами валютных пар) и значениями типа {@link ExchangeRate}.
     * </p>
     *
     * @param symbols список символов валютных пар (например, ["USD/KZT", "EUR/USD"]).
     * @return {@link Mono} с картой курсов валют, где ключ — символ пары, а значение — {@link ExchangeRate}.
     */
    public Mono<Map<String, ExchangeRate>> getLastExchangeRates(List<String> symbols){
        String currencyPairs = String.join(",", symbols);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/exchange_rate")
                        .queryParam("symbol", currencyPairs)
                        .queryParam("interval", "1day")
                        .queryParam("apikey", apikey)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
}
