package contracts.provider

import au.com.dius.pact.consumer.MockServer
import au.com.dius.pact.consumer.dsl.PactDslWithProvider
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.core.model.RequestResponsePact
import au.com.dius.pact.core.model.annotations.Pact
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(PactConsumerTestExt::class)
@PactTestFor(providerName = "inventory")
class InventoryRepositoryPactTest {
    @Pact(consumer = "pricing")
    fun `some items available pact`(builder: PactDslWithProvider): RequestResponsePact =
        builder
            .given("some items are available")
            .uponReceiving("inventory check")
            .method("POST")
            .path("/inventory/check")
            .body(
                """{   
                    "items": [{
                        "sku": "::sku1::",
                        "quantity": 2
                     }, {
                        "sku": "::sku2::",
                        "quantity": 1
                     }]
                }""".trimIndent()
            )
            .willRespondWith()
            .status(200)
            .body(
                """{   
                    "items": [{
                        "sku": "::sku1::",
                        "quantity": 1
                     }, {
                        "sku": "::sku2::",
                        "quantity": 0
                     }]
                }""".trimIndent(),
                "application/json"
            )
            .toPact()

    @Pact(consumer = "pricing")
    fun `no items available pact`(builder: PactDslWithProvider): RequestResponsePact =
        builder
            .given("no items available")
            .uponReceiving("inventory check")
            .method("POST")
            .path("/inventory/check")
            .body(
                """{   
                    "items": [{
                        "sku": "::sku1::",
                        "quantity": 2
                     }, {
                        "sku": "::sku2::",
                        "quantity": 1
                     }]
                }""".trimIndent()
            )
            .willRespondWith()
            .status(200)
            .body(
                """{   
                    "items": [{
                        "sku": "::sku1::",
                        "quantity": 0
                     }, {
                        "sku": "::sku2::",
                        "quantity": 0
                     }]
                }""".trimIndent(),
                "application/json"
            )
            .toPact()

    @Test
    @PactTestFor(pactMethod = "some items available pact")
    fun `some items available`(mockServer: MockServer) {
        val repository = InventoryRepository(mockServer.getUrl())
        val cart = Cart(listOf(CartItem("::sku1::", 2), CartItem("::sku2::", 1)))

        expect(repository.check(cart)).toBe(
            Cart(listOf(CartItem("::sku1::", 1), CartItem("::sku2::", 0)))
        )
    }

    @Test
    @PactTestFor(pactMethod = "no items available pact")
    fun `no items available`(mockServer: MockServer) {
        val repository = InventoryRepository(mockServer.getUrl())
        val cart = Cart(listOf(CartItem("::sku1::", 2), CartItem("::sku2::", 1)))

        expect(repository.check(cart)).toBe(
            Cart(listOf(CartItem("::sku1::", 0), CartItem("::sku2::", 0)))
        )
    }
}
