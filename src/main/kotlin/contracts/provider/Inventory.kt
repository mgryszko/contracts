package contracts.provider

interface Inventory {
    fun check(cart: Cart): Cart
}