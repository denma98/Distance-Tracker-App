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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
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
        val lastStop = findViewById<TextView>(R.id.textView7)
//         totalDistLazy = 626,  1272
        val distLeft = findViewById<TextView>(R.id.textView10)
        val distCovered = findViewById<TextView>(R.id.textView8)
        val isMiles = findViewById<Switch>(R.id.switch1)
        mySwitch.setOnCheckedChangeListener { _, isChecked ->
            composeView.setContent {
                MyApp(isChecked, nextButton, distanceToNextStop, lastStop, distCovered, distLeft, isMiles)
            }
        }
    }
}

@SuppressLint("RememberReturnType", "SetTextI18n")
@Composable
fun MyApp(isChecked: Boolean, nextButton: Button, distanceToNextStop: TextView, lastStop: TextView, distCovered: TextView, distLeft: TextView, isMiles: Switch) {
    // This state holds the index of the currently selected item
    val selectedItemIndex = remember { mutableIntStateOf(0) }
    val total: Int=  736;

    val stops = listOf(
        Stop("lazy 1", 10),
        Stop("Stop 2", 23),
        Stop("Stop 3", 34),
        Stop("Stop 4", 47),
        Stop("Stop 5", 60),
        Stop("Stop 6", 69),
        Stop("Stop 7", 84),
        Stop("Stop 8", 100),
        Stop("Stop 9", 130),
        Stop("Stop 10", 169)
    )
    val dist = remember { mutableIntStateOf(stops[0].distance) }

    // Now you can access the actual distance of a stop in MyApp

    MaterialTheme {
        if (isChecked) {
            LazyList(selectedItemIndex, distanceToNextStop, lastStop, distCovered, dist, stops, isMiles, distLeft)
        }
        else {
            NormalList(selectedItemIndex, distanceToNextStop, lastStop, distCovered,dist, stops, isMiles, distLeft)
        }
    }

    // When the button is pressed, increment selectedItemIndex
    nextButton.setOnClickListener {
        selectedItemIndex.intValue = (selectedItemIndex.intValue + 1) % 10
        dist.intValue += stops[selectedItemIndex.intValue].distance
        distLeft.text = "${total - dist.intValue} km"
    }

}


@SuppressLint("SetTextI18n")
@Composable
fun LazyList(
    selectedItemIndex: MutableState<Int>,
    distanceToNextStop: TextView,
    lastStop: TextView,
    distCovered: TextView,
    dist: MutableState<Int>,
    stops: List<Stop>,
    @SuppressLint("UseSwitchCompatOrMaterialCode") isMiles: Switch,
    distLeft: TextView
) {


    val listState = rememberLazyListState()
    val miles = remember { mutableStateOf(false) }

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

                        isMiles.setOnCheckedChangeListener { _, isChecked ->
                            miles.value = isChecked

                        }

                        if(miles.value) {
                            val lastStop1 = stops[index]
                            val nextStop = stops[index + 1]
                            val distance = kmToMiles(nextStop.distance) - kmToMiles(stop.distance)

                            // Update the TextViews on the main thread
                            LaunchedEffect(Unit) {
                                withContext(Dispatchers.Main) {
                                    distanceToNextStop.text = "(${nextStop.name}): ${String.format("%.2f miles", distance.toFloat())}"

                                    lastStop.text = "(${lastStop1.name})"
                                    distCovered.text = String.format("%.2f miles",
                                        kmToMiles( dist.value).toFloat()
                                    )

                                }
                            }
                        }
                        else {
                            val lastStop1 = stops[index]
                            val nextStop = stops[index + 1]
                            val distance = nextStop.distance - stop.distance

                            // Update the TextViews on the main thread
                            LaunchedEffect(Unit) {
                                withContext(Dispatchers.Main) {
                                    distanceToNextStop.text = "(${nextStop.name}): $distance km"
                                    lastStop.text = "(${lastStop1.name})"
                                    distCovered.text = String.format("%.2f km", dist.value.toFloat())
                                    distLeft.text = "${(736 - dist.value)} km"
                                }
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


fun kmToMiles(kilometers: Int): Double {
    return kilometers * 0.62
}


@SuppressLint("SetTextI18n")
@Composable
fun NormalList(
    selectedItemIndex: MutableState<Int>,
    distanceToNextStop: TextView,
    lastStop: TextView,
    distCovered: TextView,
    dist: MutableState<Int>,
    stops: List<Stop>,
    @SuppressLint("UseSwitchCompatOrMaterialCode") isMiles: Switch,
    distLeft: TextView
) {
    val scrollState = rememberScrollState()
    val miles = remember { mutableStateOf(false) }

    BoxWithConstraints {
        val screenHeight = constraints.maxHeight/2
        val halfScreenHeight = 350.dp

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
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

                    isMiles.setOnCheckedChangeListener { _, isChecked ->
                        miles.value = isChecked

                    }

                    if (miles.value) {
                        val lastStop1 = stops[index]
                        val nextStop = stops[index + 1]
                        val distance = kmToMiles(nextStop.distance) - kmToMiles(stop.distance)

                        // Update the TextViews on the main thread
                        LaunchedEffect(Unit) {
                            withContext(Dispatchers.Main) {
                                distanceToNextStop.text = "(${nextStop.name}): ${
                                    String.format(
                                        "%.2f miles",
                                        distance.toFloat()
                                    )
                                }"

                                lastStop.text = "(${lastStop1.name})"
                                distCovered.text = String.format(
                                    "%.2f miles",
                                    kmToMiles(dist.value).toFloat()
                                )

                            }
                        }
                    } else {
                        val lastStop1 = stops[index]
                        val nextStop = stops[index + 1]
                        val distance = nextStop.distance - stop.distance

                        // Update the TextViews on the main thread
                        LaunchedEffect(Unit) {
                            withContext(Dispatchers.Main) {
                                distanceToNextStop.text = "(${nextStop.name}): $distance km"
                                lastStop.text = "(${lastStop1.name})"
                                distCovered.text = String.format("%.2f km", dist.value.toFloat())
                                distLeft.text = "${(736 - dist.value)} km"
                            }
                        }
                    }
                }
            }
        }
    }
}


