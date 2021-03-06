package contracts.provider

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PricingController {
    @PostMapping("/cart/total")
    fun price(): Any = TODO()
}