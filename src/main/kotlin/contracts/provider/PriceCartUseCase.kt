package contracts.provider

class PriceCartUseCase(private val inventory: Inventory, private val pricingEngine: PricingEngine) {
    fun execute(cartToPrice: Cart): PricedCart? {
        val inventoryCheckedCart = inventory.check(cartToPrice)
        return if (inventoryCheckedCart.anyItemAvailable())
            pricingEngine.price(inventoryCheckedCart)
        else null
    }
}

private fun Cart.anyItemAvailable(): Boolean = this.items.any { it.quantity > 0 }
