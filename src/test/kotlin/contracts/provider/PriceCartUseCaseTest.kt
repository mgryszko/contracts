package contracts.provider

import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

class PriceCartUseCaseTest {
    val inventory = mockk<Inventory>()
    val pricingEngine = mockk<PricingEngine>()
    val useCase = PriceCartUseCase(inventory, pricingEngine)

    val cartToPrice = Cart(listOf(CartItem("::sku1::", 2), CartItem("::sku2::", 1)))
    val availableCart = Cart(listOf(CartItem("::sku1::", 1), CartItem("::sku2::", 0)))
    val unavailableCart = Cart(listOf(CartItem("::sku1::", 0), CartItem("::sku2::", 0)))
    val pricedCard = PricedCart(emptyList(), 0.toBigDecimal())

    @Test
    fun `some cart items available`() {
        every { inventory.check(cartToPrice) } returns availableCart
        every { pricingEngine.price(availableCart) } returns pricedCard

        expect(useCase.execute(cartToPrice)).toBe(pricedCard)
    }

    @Test
    fun `none of cart items available`() {
        every { inventory.check(cartToPrice) } returns unavailableCart

        expect(useCase.execute(cartToPrice)).toBe(null)
    }
}

