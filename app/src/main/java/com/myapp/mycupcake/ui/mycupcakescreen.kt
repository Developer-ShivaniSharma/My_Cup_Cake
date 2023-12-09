package com.myapp.mycupcake.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable

enum class cupcakeScreen(){
    start,
    flavor,
    pickup,
    summary

}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CupCakeApp() {
    Scaffold() { innnerPAdding->
     val uiState by viewModel.uiState.collectAsState()
        NavHost(
            navController = navController,


        ){}

    }
}