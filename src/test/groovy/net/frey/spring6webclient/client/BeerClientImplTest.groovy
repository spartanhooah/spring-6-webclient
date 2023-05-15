package net.frey.spring6webclient.client

import net.frey.spring6webclient.model.BeerDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.test.StepVerifier
import spock.lang.Specification

@SpringBootTest
class BeerClientImplTest extends Specification {
    static final def NAME = "New Name"

    @Autowired
    BeerClient client

    def "test list beers"() {
        when:
        def response = client.listBeers()

        then:
        StepVerifier.create(response)
            .expectNextMatches { it.contains("id") }
            .verifyComplete()
    }

    def "test list beers map version"() {
        when:
        def response = client.listBeersMap()

        then:
        StepVerifier.create(response)
            .expectNextCount(3)
            .verifyComplete()
    }

    def "test list beers using JsonNode"() {
        when:
        def response = client.listBeersJsonNode()

        then:
        StepVerifier.create(response)
            .expectNextMatches { beer ->
                {
                    println beer.toPrettyString()
                    true
                }
            }
            .expectNextCount(2)
            .verifyComplete()
    }

    def "test list beers with pojo"() {
        when:
        def response = client.listBeersDto()

        then:
        StepVerifier.create(response)
            .expectNextCount(3)
            .verifyComplete()
    }

    def "get beer by ID"() {
        given:
        def beerMono = client.listBeersDto().next()

        when:
        def response = beerMono.flatMap { client.getBeerById(it.id) }

        then:
        StepVerifier.create(response)
            .expectNextMatches {
                {
                    println it.beerName
                    true
                }
            }
            .verifyComplete()
    }

    def "get beers by style"() {
        when:
        def response = client.getBeersByStyle("Pale Ale")

        then:
        StepVerifier.create(response)
            .expectNextMatches {
                {
                    println it.beerName
                    true
                }
            }
            .expectNextCount(1)
            .verifyComplete()
    }

    def "create new beer"() {
        given:
        def newBeer = BeerDTO.builder()
            .beerName("Saporous")
            .beerStyle("Sour")
            .upc("123456")
            .price(BigDecimal.TEN)
            .quantityOnHand(0)
            .build()

        when:
        def response = client.createBeer(newBeer)

        then:
        StepVerifier.create(response)
            .expectNextMatches { it.beerName == "Saporous" }
            .verifyComplete()
    }

    def "update a beer"() {
        given:
        def updatedBeer = client.listBeersDto().next().doOnNext { it.beerName = NAME }

        when:
        def response = updatedBeer.flatMap { client.updateBeer(it) }

        then:
        StepVerifier.create(response)
            .expectNextMatches { it.beerName == NAME }
            .verifyComplete()
    }

    def "patch a beer"() {
        given:
        def patchBody = client.listBeersDto().next()
            .map { BeerDTO.builder().id(it.id).beerName(NAME).build() }

        when:
        def response = patchBody.flatMap { client.patchBeer(it) }

        then:
        StepVerifier.create(response)
            .expectNextMatches { it.beerName == NAME }
            .verifyComplete()
    }

    def "delete a beer"() {
        given:
        def newBeer = BeerDTO.builder()
            .beerName("Saporous")
            .beerStyle("Sour")
            .upc("123456")
            .price(BigDecimal.TEN)
            .quantityOnHand(0)
            .build()

        def createResponse = client.createBeer(newBeer)

        when:
        def deleteResponse = createResponse.flatMap { client.deleteBeer(it.id) }

        then:
        StepVerifier.create(deleteResponse)
            .verifyComplete()
    }
}
