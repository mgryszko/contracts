package contracts.provider

import au.com.dius.pact.provider.junit5.PactVerificationContext
import au.com.dius.pact.provider.junitsupport.Provider
import au.com.dius.pact.provider.junitsupport.State
import au.com.dius.pact.provider.junitsupport.loader.PactBroker
import au.com.dius.pact.provider.spring.junit5.MockMvcTestTarget
import au.com.dius.pact.provider.spring.junit5.PactVerificationSpringProvider
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
    @TestTemplate
    @ExtendWith(PactVerificationSpringProvider::class)
    fun pactVerifications(context: PactVerificationContext) {
        context.verifyInteraction()
    }

    @BeforeEach
    fun before(context: PactVerificationContext) {
        context.target = MockMvcTestTarget().apply {
            controllers = listOf(PricingController())
        }
    }

    @State("cart with existing SKUs")
    fun `cart with existing SKUs`() {
    }

    @State("some cart SKUs don't exist")
    fun `some cart SKUs don't exist`() {
    }
}