package contracts.provider

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.web.client.postForObject

class InventoryRepository(url: String) : Inventory {
    private val restTemplate = RestTemplateBuilder().rootUri(url).build()

    override fun check(cart: Cart): Cart =
        restTemplate.postForObject("/inventory/check", cart)
}