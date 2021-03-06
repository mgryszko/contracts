package contracts.provider

import java.math.BigDecimal

data class Cart(val items: List<CartItem>)

data class CartItem(val sku: String, val quantity: Int)

data class PricedCart(val items: List<PricedCartItem>, val total: BigDecimal)

data class PricedCartItem(val sku: String, val quantity: Int, val unitPrice: BigDecimal, val totalPrice: BigDecimal)
