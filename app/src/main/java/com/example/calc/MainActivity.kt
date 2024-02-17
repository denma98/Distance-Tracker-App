package com.example.calc
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp

data class Stop(val name: String, val distance: Int)

class MainActivity : AppCompatActivity() {
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mySwitch = findViewById<Switch>(R.id.switch2)
        val composeView = findViewById<ComposeView>(R.id.compose_view)

        val nextButton = findViewById<Button>(R.id.button10)

        mySwitch.setOnCheckedChangeListener { _, isChecked ->
            composeView.setContent {
                MyApp(isChecked, nextButton)
            }
        }
    }
}

@Composable
fun MyApp(isChecked: Boolean, nextButton: Button) {
    // This state holds the index of the currently selected item
    val selectedItemIndex = remember { mutableStateOf(0) }

    MaterialTheme {
        if (isChecked) {
            LazyList(selectedItemIndex)
        } else {
            NormalList()
        }
    }

    // When the button is pressed, increment selectedItemIndex
    nextButton.setOnClickListener {
        selectedItemIndex.value = (selectedItemIndex.value + 1) % 10
    }
}

@Composable
fun LazyList(selectedItemIndex: MutableState<Int>) {
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

    BoxWithConstraints {
        val screenHeight = constraints.maxHeight
        val halfScreenHeight = 350.dp
        Column {
            Spacer(modifier = Modifier.height(halfScreenHeight + 100.dp)) // Add 70.dp to the halfScreenHeight

            LazyColumn(Modifier.height(halfScreenHeight)) { // This will take up the other half of the screen
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
                }
            }
        }
    }
}

@Composable
fun NormalList() {
    val stops = listOf(
        Stop("stop 1", 10),
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
    BoxWithConstraints {

        val screenHeight = constraints.maxHeight
        val halfScreenHeight = 350.dp
        val scrollState = rememberScrollState()
        Column {
            Spacer(modifier = Modifier.height(halfScreenHeight + 100.dp)) // Add 70.dp to the halfScreenHeight

            Column(
                modifier = Modifier
//                    .height(halfScreenHeight + 100.dp) // Add 70.dp to the halfScreenHeight
                    .verticalScroll(scrollState)
            ) { // This will take up the other half of the screen
                stops.forEach { stop ->
                    ElevatedCard(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 6.dp
                        ),
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "${stop.name}: ${stop.distance} km",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
//            Spacer(modifier = Modifier.height(70.dp)) // Add a Spacer at the end to leave space for a button
        }
    }
}
