package com.example.calc
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class Stop(val name: String, val distance: Int)

class MainActivity : AppCompatActivity() {
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mySwitch = findViewById<Switch>(R.id.switch3)
        val composeView = findViewById<ComposeView>(R.id.compose_view)

        val nextButton = findViewById<Button>(R.id.button10)
        val distanceToNextStop = findViewById<TextView>(R.id.textView)

        mySwitch.setOnCheckedChangeListener { _, isChecked ->
            composeView.setContent {
                MyApp(isChecked, nextButton, distanceToNextStop)
            }
        }
    }
}

@Composable
fun MyApp(isChecked: Boolean, nextButton: Button, distanceToNextStop: TextView) {
    // This state holds the index of the currently selected item
    val selectedItemIndex = remember { mutableIntStateOf(0) }

    val density = LocalDensity.current

    MaterialTheme {
        if (isChecked) {
            LazyList(selectedItemIndex, distanceToNextStop)
        }
        else {
            NormalList(selectedItemIndex, distanceToNextStop)
        }

    }

    // When the button is pressed, increment selectedItemIndex
    nextButton.setOnClickListener {
        selectedItemIndex.intValue = (selectedItemIndex.intValue + 1) % 10
    }
}

@Composable
fun LazyList(selectedItemIndex: MutableState<Int>, distanceToNextStop: TextView) {
    val stops = listOf(
        Stop("lazy 1", 10),
        Stop("Stop 2", 20),
        Stop("Stop 3", 30),
        Stop("Stop 4", 40),
        Stop("Stop 5", 50),
        Stop("Stop 6", 60),
        Stop("Stop 7", 70),
        Stop("Stop 8", 80),
        Stop("Stop 9", 90),
        Stop("Stop 10", 100)
    )

    val listState = rememberLazyListState()

    BoxWithConstraints {
        val screenHeight = constraints.maxHeight
        val halfScreenHeight = 350.dp
        Column {
            Spacer(modifier = Modifier.height(halfScreenHeight + 100.dp)) // Add 70.dp to the halfScreenHeight

            LazyColumn(state = listState, modifier = Modifier.height(halfScreenHeight)) { // This will take up the other half of the screen
                itemsIndexed(stops) { index, stop ->
                    // If this item is the selected item, highlight it
                    val color = if (index == selectedItemIndex.value) Color.LightGray else Color.White
                    ElevatedCard(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 6.dp
                        ),
                        modifier = Modifier
                            .background(color)
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "${stop.name}: ${stop.distance} km",
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    // Calculate the distance to the next stop
                    if (index == selectedItemIndex.value && index < stops.size - 1) {
                        val nextStop = stops[index + 1]
                        val distance = nextStop.distance - stop.distance

                        // Update the TextView on the main thread
                        LaunchedEffect(Unit) {
                            withContext(Dispatchers.Main) {
                                distanceToNextStop.text = "(${nextStop.name}): $distance km"
                            }
                        }
                    }
                }
            }

            LaunchedEffect(selectedItemIndex.value) {
                listState.animateScrollToItem(selectedItemIndex.value)
            }
        }
    }
}

@Composable
fun NormalList(selectedItemIndex: MutableState<Int>, distanceToNextStop: TextView) {
    val stops = listOf(
        Stop("stop 1", 10),
        Stop("Stop 2", 20),
        Stop("Stop 3", 30),
        Stop("Stop 4", 70),
        Stop("Stop 5", 100),
        Stop("Stop 6", 110),
        Stop("Stop 7", 170),
        Stop("Stop 8", 180),
        Stop("Stop 9", 290),
        Stop("Stop 10", 310)
    )

    val scrollState = rememberScrollState()

    BoxWithConstraints {
        val screenHeight = constraints.maxHeight/2
        val halfScreenHeight = 350.dp

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(halfScreenHeight + 100.dp))
            stops.forEachIndexed { index, stop ->
                // If this item is the selected item, highlight it
                val color = if (index == selectedItemIndex.value) Color.LightGray else Color.White
                ElevatedCard(
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    ),
                    modifier = Modifier
                        .background(color)
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "${stop.name}: ${stop.distance} km",
                        modifier = Modifier.padding(16.dp)
                    )
                }

                // Calculate the distance to the next stop
                if (index == selectedItemIndex.value && index < stops.size - 1) {
                    val nextStop = stops[index + 1]
                    val distance = nextStop.distance - stop.distance

                    // Update the TextView on the main thread
                    LaunchedEffect(Unit) {
                        withContext(Dispatchers.Main) {
                            distanceToNextStop.text = "(${nextStop.name}): $distance km"
                        }
                    }
                }
            }
        }
    }
}

