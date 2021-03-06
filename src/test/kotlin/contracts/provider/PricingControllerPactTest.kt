package contracts.provider

import au.com.dius.pact.provider.junit5.PactVerificationContext
import au.com.dius.pact.provider.junitsupport.Provider
import au.com.dius.pact.provider.junitsupport.State
import au.com.dius.pact.provider.junitsupport.loader.PactBroker
import au.com.dius.pact.provider.spring.junit5.MockMvcTestTarget
import au.com.dius.pact.provider.spring.junit5.PactVerificationSpringProvider
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Provider("pricing")
@PactBroker(host = "localhost", port = "9292")
@WebMvcTest
class PricingControllerPactTest {
    @MockkBean
    lateinit var useCase: PriceCartUseCase

    @TestTemplate
    @ExtendWith(PactVerificationSpringProvider::class)
    fun pactVerifications(context: PactVerificationContext) {
        context.verifyInteraction()
    }

    @BeforeEach
    fun before(context: PactVerificationContext) {
        context.target = MockMvcTestTarget().apply {
            controllers = listOf(PricingController(useCase))
        }
    }

    @State("cart with existing SKUs")
    fun `cart with existing SKUs`() {
        val cart = Cart(listOf(CartItem("croissants", 4), CartItem("baguettes", 5)))
        every { useCase.execute(cart) } returns
            PricedCart(
                items = listOf(
                    PricedCartItem(
                        sku = "croissants",
                        quantity = 3,
                        unitPrice = 1.10.toBigDecimal(),
                        totalPrice = 2.65.toBigDecimal(),
                    ),
                    PricedCartItem(
                        sku = "baguettes",
                        quantity = 5,
                        unitPrice = 0.75.toBigDecimal(),
                        totalPrice = 3.0.toBigDecimal(),
                    ),
                ),
                total = 5.65.toBigDecimal(),
            )
    }

    @State("some cart SKUs don't exist")
    fun `some cart SKUs don't exist`() {
        every { useCase.execute(any()) } returns null
    }
}