package com.myapp.mycupcake.ui

import androidx.lifecycle.ViewModel
import com.myapp.mycupcake.data.OrderUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/** Price per cupcake*/
private const val PRICE_PER_CUPCAKE = 2.00

/** Additional price for same day pickup*/
private const val PRICE_FOR_SAME_DAY_PICKUP = 3.00

/** OrderViewModel holds the information of cupcake quantity, flavour and pickupDate.
It also knows how to order the total price based on these order detail
 */
class OrderViewModel : ViewModel() {
    /**
     * CupCake State for this order
     */

    private val _uiState = MutableStateFlow(OrderUiState(pickupOptions = pickupOption()))
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()

    /**
     *Set the quantity of cupCake
     * and update the price
     */

    fun setQuantity(numberCupCake: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                quantity = numberCupCake,
                price = calculatePrice(quantity = numberCupCake)
            )
        }
    }

    /**
     * Set the falavour of the cupcake for the order
     * The user can select only one flavour for the whole order
     */

    fun setFlavour(desiredFlavour: String) {
        _uiState.update { currentState ->
            currentState.copy(flavor = desiredFlavour)
        }
    }

    /**
     * Set the pickup date of the order
     */
    fun setPickupDate(pickupDate: String) {
        _uiState.update { currentState ->
            currentState.copy(
                date = pickupDate,
                price = calculatePrice(pickupDate = pickupDate)
            )
        }
    }

    fun resetOrder(){
        _uiState.value=OrderUiState(pickupOptions = pickupOption())
    }

    /**
     * The price will be calculated on the basis of no. of cakes quantity and the pickup date
     * so we pass these two parameters.
     */

     private fun calculatePrice(
        quantity: Int = _uiState.value.quantity,
        pickupDate: String = _uiState.value.date
    ): String {
        var calculatePrice = quantity * PRICE_PER_CUPCAKE
        if (pickupOption()[0] == pickupDate) {
            calculatePrice += PRICE_FOR_SAME_DAY_PICKUP
        }
        val formatPrice = NumberFormat.getCurrencyInstance().format(calculatePrice)
        return formatPrice
    }

    private fun pickupOption(): List<String> {
        val dateOption = mutableListOf<String>()
        val formatter = SimpleDateFormat("E MMM D", Locale.getDefault())
        val calendar = Calendar.getInstance()
        repeat(4) {
            dateOption.add(formatter.format(calendar.time))
            calendar.add(Calendar.DATE, 1)
        }
        return dateOption
    }

}