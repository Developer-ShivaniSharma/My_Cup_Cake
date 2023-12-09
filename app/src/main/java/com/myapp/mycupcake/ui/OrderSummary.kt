package com.myapp.mycupcake.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.myapp.mycupcake.R
import com.myapp.mycupcake.data.OrderUiState
import com.myapp.mycupcake.ui.components.FormattedPriceLabel

@Composable
fun OrderSummaryScreen(
    orderUIState: OrderUiState,
    onSendButtonClicked: (String, String) -> Unit,
    onCancelButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val resources = LocalContext.current.resources
    val numberOfCupcakes = resources.getQuantityString(
        R.plurals.cupcakes,
        orderUIState.quantity,
        orderUIState.quantity
    )
    val orderSummary = stringResource(
        R.string.order_details,
        numberOfCupcakes,
        orderUIState.flavor,
        orderUIState.date,
        orderUIState.quantity
    )
    val newOrder = stringResource(R.string.new_cupcake_order)

    /**
     *Create list of order summary to display
     */

    val items = listOf(
        Pair(stringResource(R.string.quantity), numberOfCupcakes),
        Pair(stringResource(R.string.flavor), orderUIState.flavor),
        Pair(stringResource(R.string.pickup_date), orderUIState.date)
    )
    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
        ) {
            items.forEach { item ->
                Text(item.first.uppercase())
                Text(text = item.second, fontWeight = FontWeight.Bold)
                Divider(thickness = 1.dp)

            }
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
            FormattedPriceLabel(
                subtotal = orderUIState.price,
                modifier = Modifier.align(Alignment.End)
            )

        }
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
        ) {
            Button(
                onClick = { onSendButtonClicked(newOrder, orderSummary) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.send))
            }
            OutlinedButton(
                onClick = { onCancelButtonClicked },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun OrderSummaryPreview(){
    OrderSummaryScreen(
        orderUIState = OrderUiState(0,"Test","Test","$300.0"),
        onSendButtonClicked = {subject:String, summary:String->},
        onCancelButtonClicked = {},
        modifier = Modifier.fillMaxHeight()
    )
}