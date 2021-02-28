package contracts.consumer

import au.com.dius.pact.consumer.MockServer
import au.com.dius.pact.consumer.dsl.PactDslWithProvider
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.core.model.RequestResponsePact
import au.com.dius.pact.core.model.annotations.Pact
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import com.fasterxml.jackson.annotation.JsonCreator
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.postForObject
import java.math.BigDecimal

private class PricingClient(url: String) {
    private val restTemplate = RestTemplateBuilder().rootUri(url).build()

    fun priceCart(cart: Cart): PricedCart? = runCatching {
        restTemplate.postForObject<PricedCart>("/cart/total", cart)
    }.getOrElse { e ->
        when (e) {
            is HttpClientErrorException -> null
            else -> throw e
        }
    }
}

private data class Cart(val items: List<CartItem>)

private data class CartItem(val sku: String, val quantity: Int)

private data class Amount(val amount: BigDecimal) {
    companion object {
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        @JvmStatic
        fun create(amount: Double): Amount = Amount(amount.toBigDecimal())
    }

    override fun equals(other: Any?): Boolean =
        when {
            this === other -> true
            javaClass != other?.javaClass -> false
            else -> amount.compareTo((other as Amount).amount) == 0
        }

    override fun hashCode(): Int = amount.hashCode()
}

private data class PricedCart(val items: List<PricedCartItem>, val total: Amount)

private data class PricedCartItem(val sku: String, val quantity: Int, val unitPrice: Amount, val totalPrice: Amount)

private fun Double.toAmount(): Amount = Amount(this.toBigDecimal())

@ExtendWith(PactConsumerTestExt::class)
@PactTestFor(providerName = "pricing")
class PricingClientPactTest {
    @Pact(consumer = "shop-ui")
    fun `get cart total pact`(builder: PactDslWithProvider): RequestResponsePact =
        builder
            .given("cart with existing SKUs")
            .uponReceiving("get cart total")
            .method("POST")
            .path("/cart/total")
            .body(
                """{   
                    "items": [{
                        "sku": "croissants",
                        "quantity": 4
                     }, {
                        "sku": "baguettes",
                        "quantity": 5
                     }]
                }""".trimIndent()
            )
            .willRespondWith()
            .status(200)
            .body(
                """{   
                    "items": [{
                        "sku": "croissants",
                        "quantity": 3,
                        "unitPrice": 1.10,
                        "totalPrice": 2.65
                     }, {
                        "sku": "baguettes",
                        "quantity": 5,
                        "unitPrice": 0.75,
                        "totalPrice": 3.00
                     }],
                     "total": 5.65
                }""".trimIndent(),
                "application/json"
            )
            .toPact()

    @Pact(consumer = "shop-ui")
    fun `get cart total with some missing SKUs pact`(builder: PactDslWithProvider): RequestResponsePact =
        builder
            .given("some cart SKUs don't exist")
            .uponReceiving("get cart total")
            .method("POST")
            .path("/cart/total")
            .body(
                """{   
                    "items": [{
                        "sku": "croissants",
                        "quantity": 1
                     }]
                }""".trimIndent()
            )
            .willRespondWith()
            .status(404)
            .toPact()

    @Test
    @PactTestFor(pactMethod = "get cart total pact")
    fun `get cart total`(mockServer: MockServer) {
        val client = PricingClient(mockServer.getUrl())
        val cart = Cart(items = listOf(CartItem(sku = "croissants", quantity = 4), CartItem(sku = "baguettes", quantity = 5)))

        expect(client.priceCart(cart)).toBe(
            PricedCart(
                items = listOf(
                    PricedCartItem(
                        sku = "croissants",
                        quantity = 3,
                        unitPrice = 1.10.toAmount(),
                        totalPrice = 2.65.toAmount(),
                    ),
                    PricedCartItem(
                        sku = "baguettes",
                        quantity = 5,
                        unitPrice = 0.75.toAmount(),
                        totalPrice = 3.0.toAmount(),
                    ),
                ),
                total = 5.65.toAmount(),
            )
        )
    }

    @Test
    @PactTestFor(pactMethod = "get cart total with some missing SKUs pact")
    fun `get cart total with some missing SKUs`(mockServer: MockServer) {
        val client = PricingClient(mockServer.getUrl())
        val cart = Cart(items = listOf(CartItem(sku = "croissants", quantity = 1)))

        expect(client.priceCart(cart)).toBe(null)
    }
}
