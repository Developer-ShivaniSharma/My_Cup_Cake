package com.myapp.mycupcake.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.myapp.mycupcake.R
import com.myapp.mycupcake.data.DataSource
import javax.security.auth.Subject

enum class CupcakeScreen() {
    start, flavor, pickup, summary

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CupCakeApp(
    viewModel: OrderViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = CupcakeScreen.valueOf(
        backStackEntry?.destination?.route ?: CupcakeScreen.start.name
    )
    Scaffold() { innnerPadding ->
        val uiState by viewModel.uiState.collectAsState()
        NavHost(
            navController = navController,
            startDestination = CupcakeScreen.start.name,
            modifier = Modifier.padding(innnerPadding)
        ) {
            composable(route = CupcakeScreen.start.name) {
                CakeOrderScreen(
                    quantityOptions = DataSource.quantityOption,
                    onNextButtonClicked = {
                        viewModel.setQuantity(it)
                        navController.navigate(CupcakeScreen.flavor.name)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(R.dimen.padding_small))

                )

            }
            composable(route = CupcakeScreen.flavor.name) {
                val context = LocalContext.current
                SelectOptionScreen(
                    subtotal = uiState.price,
                    onNextButtonClicked = { navController.navigate(CupcakeScreen.pickup.name) },
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    },
                    options = DataSource.flavourOfCake.map { id -> context.resources.getString(id) },
                    onSelectionChanged = { viewModel.setFlavour(it) },
                    modifier = Modifier.fillMaxHeight()
                )
            }
            composable(route = CupcakeScreen.pickup.name) {
                SelectOptionScreen(
                    subtotal = uiState.date,
                    onNextButtonClicked = { navController.navigate(CupcakeScreen.summary.name) },
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(
                            viewModel,
                            navController
                        )
                    },
                    options = uiState.pickupOptions,
                    onSelectionChanged = { viewModel.setPickupDate(it) },
                    modifier = Modifier.fillMaxHeight()
                )

            }
            composable(route = CupcakeScreen.summary.name) {
                OrderSummaryScreen(
                    orderUIState = uiState,
                    onSendButtonClicked = { subject: String, summary: String ->
                        shareOrder(context, subject = subject, summary = summary)
                    },
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(
                            viewModel,
                            navController
                        )
                    },
                    modifier = Modifier.fillMaxHeight()
            }

        }

    }
}

fun cancelOrderAndNavigateToStart(viewModel: OrderViewModel, navController: NavHostController) {

    viewModel.resetOrder()
    navController.popBackStack(CupcakeScreen.start.name, inclusive = false)
}

fun shareOrder(context: Context, subject: Subject, summary: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plan"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, summary)
    }
    context.startActivity(
        Intent.createChooser(intent, "New CupCakeOrder")
    )
}
