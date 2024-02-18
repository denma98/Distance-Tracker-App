package com.example.calc
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
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
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class Stop(val name: String, var distance: Double, var unit: String)

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
        val bar = findViewById<ProgressBar>(R.id.progressBar)
        mySwitch.setOnCheckedChangeListener { _, isChecked ->
            composeView.setContent {
                MyApp(isChecked, nextButton, distanceToNextStop, lastStop, distCovered, distLeft, isMiles, bar)
            }
        }

    }
}

var miles =mutableStateOf(false)



@SuppressLint("RememberReturnType", "SetTextI18n")
// ...
@Composable
fun MyApp(
    isChecked: Boolean,
    nextButton: Button,
    distanceToNextStop: TextView,
    lastStop: TextView,
    distCovered: TextView,
    distLeft: TextView,
    @SuppressLint(
        "UseSwitchCompatOrMaterialCode"
    ) isMiles: Switch,
    bar: ProgressBar
) {
    val selectedItemIndex = remember { mutableIntStateOf(0) }


    val stops = remember {
        mutableStateListOf(
            Stop("lazy 1", 10.0, "km"),
            Stop("Stop 2", 23.0, "km"),
            Stop("Stop 3", 34.0, "km"),
            Stop("Stop 4", 47.0, "km"),
            Stop("Stop 5", 60.0, "km"),
            Stop("Stop 6", 69.0, "km"),
            Stop("Stop 7", 84.0, "km"),
            Stop("Stop 8", 100.0, "km"),
            Stop("Stop 9", 130.0, "km"),
            Stop("Stop 10", 169.0, "km")
        )
    }
    val total: MutableState<Double> = remember { mutableStateOf(calculateTotalDistance(stops)) }


    // Store original distances in a separate list
    var dist = remember { mutableDoubleStateOf(stops[0].distance) }

    val originalDistances = stops.map { it.distance }.toList()
    total.value = calculateTotalDistance(stops)
    isMiles.setOnCheckedChangeListener { _, isChecked ->
        miles.value = isChecked
        if (isChecked) {
            stops.forEachIndexed { index, stop ->
                stop.distance = kmToMiles(originalDistances[index])
                stop.unit = "miles"
            }
            dist.doubleValue = kmToMiles(dist.doubleValue)
            total.value = kmToMiles(total.value)
        } else {
            miles.value = false
            stops.forEachIndexed { index, stop ->
                stop.distance = originalDistances[index]
                stop.unit = "km"
            }
            dist.doubleValue = milesToKm(dist.doubleValue)
            total.value = milesToKm(total.value)
        }
        selectedItemIndex.intValue = selectedItemIndex.intValue
    }


    MaterialTheme {
        if (isChecked) {
            LazyList(selectedItemIndex, distanceToNextStop, lastStop, distCovered, dist, stops, isMiles, distLeft, miles.value, total.value)
        } else {
            NormalList(selectedItemIndex, distanceToNextStop, lastStop, distCovered, dist, stops, isMiles, distLeft, miles.value, total.value)
        }
    }


    nextButton.setOnClickListener {
        selectedItemIndex.intValue = (selectedItemIndex.intValue + 1) % 10
        dist.doubleValue += stops[selectedItemIndex.intValue].distance

        // Update the progress bar
        val progress = (dist.doubleValue / total.value * 100).toInt()
        bar.progress = progress
    }
}

private fun calculateTotalDistance(stops: List<Stop>): Double {
    return stops.sumOf { it.distance }
}


@SuppressLint("SetTextI18n")
@Composable
fun LazyList(
    selectedItemIndex: MutableState<Int>,
    distanceToNextStop: TextView,
    lastStop: TextView,
    distCovered: TextView,
    dist: MutableState<Double>,
    stops: List<Stop>,
    @SuppressLint("UseSwitchCompatOrMaterialCode") isMiles: Switch,
    distLeft: TextView,
    value: Boolean,
    total: Double
) {
    val listState = rememberLazyListState()

    BoxWithConstraints {
        val screenHeight = constraints.maxHeight
        val halfScreenHeight = 350.dp
        Column {
            Spacer(modifier = Modifier.height(halfScreenHeight + 100.dp))

            LazyColumn(state = listState, modifier = Modifier.height(halfScreenHeight)) {
                itemsIndexed(stops) { index, stop ->
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
                            text = "${stop.name}: ${stop.distance} ${stop.unit}",
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    if (index == selectedItemIndex.value && index < stops.size - 1) {
                        val lastStop1 = stops[index]
                        val nextStop = stops[index + 1]
                        val distance = nextStop.distance - stop.distance

                        // Convert the distance if necessary
//                        if (value) {
//                            distance = kmToMiles(distance)
//                        }

                        // Update the TextViews on the main thread
                        LaunchedEffect(value) {
                            withContext(Dispatchers.Main) {
                                distanceToNextStop.text = "${String.format("%.2f", distance)} ${nextStop.unit}"
                                lastStop.text = "${lastStop1.name}"
                                distCovered.text = String.format("%.2f ${nextStop.unit}", dist.value)
                                distLeft.text = String.format("%.2f ${nextStop.unit}", (total) - dist.value)
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




fun kmToMiles(kilometers: Double): Double {
    return kilometers * 0.62
}
fun milesToKm(miles: Double): Double {
    return miles * 1.61
}


@SuppressLint("SetTextI18n")
@Composable
fun NormalList(
    selectedItemIndex: MutableState<Int>,
    distanceToNextStop: TextView,
    lastStop: TextView,
    distCovered: TextView,
    dist: MutableState<Double>,
    stops: List<Stop>,
    @SuppressLint("UseSwitchCompatOrMaterialCode") isMiles: Switch,
    distLeft: TextView,
    value: Boolean,
    total: Double
) {
    val scrollState = rememberScrollState()

    BoxWithConstraints {
        val screenHeight = constraints.maxHeight / 2
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
                        text = "${stop.name}: ${stop.distance} ${stop.unit}",
                        modifier = Modifier.padding(16.dp)
                    )
                }

                // Calculate the distance to the next stop
                if (index == selectedItemIndex.value && index < stops.size - 1) {
                    val lastStop1 = stops[index]
                    val nextStop = stops[index + 1]
                    var distance = nextStop.distance - stop.distance

                    // Convert the distance if necessary
//                    if (value) {
//                        distance = kmToMiles(distance)
//                    }

                    // Update the TextViews on the main thread
                    LaunchedEffect(value) { // Use the updated state value
                        withContext(Dispatchers.Main) {
                            distanceToNextStop.text = "${String.format("%.2f", distance)} ${nextStop.unit}"
                            lastStop.text = "${lastStop1.name}"
                            distCovered.text = String.format("%.2f ${nextStop.unit}", dist.value)
                            if( value) {
                                distLeft.text = String.format("%.2f ${nextStop.unit}", (total) - dist.value)
                            } else {
                                distLeft.text =
                                    String.format("%.2f ${nextStop.unit}", (450.12) - dist.value)
                            }
                        }
                    }
                }
            }
        }
    }
}