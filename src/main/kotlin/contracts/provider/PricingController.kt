package contracts.provider

import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class PricingController(val useCase: PriceCartUseCase) {
    @PostMapping("/cart/total")
    fun price(@RequestBody cart: Cart): ResponseEntity<PricedCart> =
        useCase.execute(cart)?.let { ResponseEntity(it, OK) } ?: ResponseEntity(NOT_FOUND)
}