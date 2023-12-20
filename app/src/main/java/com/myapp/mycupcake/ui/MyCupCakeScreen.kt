package com.myapp.mycupcake.ui

import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.myapp.mycupcake.R
import com.myapp.mycupcake.data.DataSource
import javax.security.auth.Subject

enum class CupcakeScreen(@StringRes val title: Int) {
    start(title = R.string.app_name),
    flavor(title = R.string.choose_flavor),
    pickup(title = R.string.choose_pickup_date),
    summary(title = R.string.order_summary)

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CupcakeAppBar(
    currentScreen: CupcakeScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}


@Composable
fun CupCakeApp(
    viewModel: OrderViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = CupcakeScreen.valueOf(
        backStackEntry?.destination?.route ?: CupcakeScreen.start.name
    )
    Scaffold(
       topBar ={
         CupcakeAppBar(
             currentScreen = currentScreen ,
             canNavigateBack = navController.previousBackStackEntry!=null,
             navigateUp = {navController.navigateUp()}
         )
       }
    ) { inerPadding ->
        val uiState by viewModel.uiState.collectAsState()
        NavHost(
            navController = navController,
            startDestination = CupcakeScreen.start.name,
            modifier = Modifier.padding(inerPadding)
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
                val context = LocalContext.current
                OrderSummaryScreen(
                    orderUIState = uiState,
                    onSendButtonClicked = { subject: String, summary: String ->
                        shareOrder(context = context, subject = subject, summary = summary)
                    },
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(
                            viewModel,
                            navController
                        )
                    },
                    modifier = Modifier.fillMaxHeight()
                )
            }

        }

    }
}

fun cancelOrderAndNavigateToStart(viewModel: OrderViewModel, navController: NavHostController) {

    viewModel.resetOrder()
    navController.popBackStack(CupcakeScreen.start.name, inclusive = false)
}

fun shareOrder(context: Context, subject: String, summary: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plan"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, summary)
    }
    context.startActivity(
        Intent.createChooser(intent, "New CupCakeOrder")
    )
}
