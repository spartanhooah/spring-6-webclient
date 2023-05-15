package net.frey.spring6webclient.client;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import net.frey.spring6webclient.model.BeerDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BeerClientImpl implements BeerClient {
    public static final String BEER_PATH = "/api/v3/beer";
    public static final String ID_PATH = BEER_PATH + "/{id}";
    private final WebClient client;

    public BeerClientImpl(WebClient.Builder builder) {
        this.client = builder.build();
    }

    @Override
    public Flux<String> listBeers() {
        return client.get().uri(BEER_PATH).retrieve().bodyToFlux(String.class);
    }

    @Override
    public Flux<Map> listBeersMap() {
        return client.get().uri(BEER_PATH).retrieve().bodyToFlux(Map.class);
    }

    @Override
    public Flux<JsonNode> listBeersJsonNode() {
        return client.get().uri(BEER_PATH).retrieve().bodyToFlux(JsonNode.class);
    }

    @Override
    public Flux<BeerDTO> listBeersDto() {
        return client.get().uri(BEER_PATH).retrieve().bodyToFlux(BeerDTO.class);
    }

    @Override
    public Mono<BeerDTO> getBeerById(String id) {
        return client.get()
                .uri(uriBuilder -> uriBuilder.path(ID_PATH).build(id))
                .retrieve()
                .bodyToMono(BeerDTO.class);
    }

    @Override
    public Flux<BeerDTO> getBeersByStyle(String style) {
        return client.get()
                .uri(builder ->
                        builder.path(BEER_PATH).queryParam("style", style).build())
                .retrieve()
                .bodyToFlux(BeerDTO.class);
    }

    @Override
    public Mono<BeerDTO> createBeer(BeerDTO beerDTO) {
        return client.post()
                .uri(BEER_PATH)
                .bodyValue(beerDTO)
                .retrieve()
                .toBodilessEntity()
                .flatMap(created ->
                        Mono.just(created.getHeaders().get("location").get(0)))
                .map(path -> path.split("/")[path.split("/").length - 1])
                .flatMap(this::getBeerById);
    }

    @Override
    public Mono<BeerDTO> updateBeer(BeerDTO beerDTO) {
        return client.put()
                .uri(builder -> builder.path(ID_PATH).build(beerDTO.getId()))
                .bodyValue(beerDTO)
                .retrieve()
                .toBodilessEntity()
                .flatMap(empty -> getBeerById(beerDTO.getId()));
    }

    @Override
    public Mono<BeerDTO> patchBeer(BeerDTO beerDTO) {
        return client.patch()
                .uri(builder -> builder.path(ID_PATH).build(beerDTO.getId()))
                .bodyValue(beerDTO)
                .retrieve()
                .toBodilessEntity()
                .flatMap(empty -> getBeerById(beerDTO.getId()));
    }

    @Override
    public Mono<Void> deleteBeer(String id) {
        return client.delete()
                .uri(builder -> builder.path(ID_PATH).build(id))
                .retrieve()
                .toBodilessEntity()
                .then();
    }
}
