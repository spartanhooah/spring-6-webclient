package net.frey.spring6webclient.client;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import net.frey.spring6webclient.model.BeerDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BeerClient {
    Flux<String> listBeers();

    Flux<Map> listBeersMap();

    Flux<JsonNode> listBeersJsonNode();

    Flux<BeerDTO> listBeersDto();

    Mono<BeerDTO> getBeerById(String id);

    Flux<BeerDTO> getBeersByStyle(String style);

    Mono<BeerDTO> createBeer(BeerDTO beerDTO);

    Mono<BeerDTO> updateBeer(BeerDTO beerDTO);

    Mono<BeerDTO> patchBeer(BeerDTO beerDTO);

    Mono<Void> deleteBeer(String id);
}
