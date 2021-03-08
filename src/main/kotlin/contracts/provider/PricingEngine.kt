package contracts.provider

interface PricingEngine {
    fun price(cart: Cart): PricedCart
}
