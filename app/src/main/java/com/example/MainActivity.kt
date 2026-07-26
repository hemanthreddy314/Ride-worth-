package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.database.RideWorthDatabase
import com.example.data.repository.ValuationRepositoryImpl
import com.example.ui.navigation.AppNavGraph
import com.example.ui.viewmodel.HistoryViewModel
import com.example.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            try {
                val database = RideWorthDatabase.getDatabase(applicationContext)
                val valuationRepository = ValuationRepositoryImpl(database.valuationDao())

                val homeViewModel: HomeViewModel = viewModel()
                val historyViewModel: HistoryViewModel = viewModel {
                    HistoryViewModel(valuationRepository)
                }

                AppNavGraph(
                    homeViewModel = homeViewModel,
                    historyViewModel = historyViewModel
                )
            } catch (e: Exception) {
                android.util.Log.e("MainActivity", "Error initializing app: ${e.message}", e)
                throw e
            }
        }
    }
}
