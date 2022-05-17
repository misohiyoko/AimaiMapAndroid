package ac.misohiyoko.navigatorCom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ac.misohiyoko.navigatorCom.ui.theme.NavComTheme
import androidx.compose.foundation.layout.*

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NavComTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {

                }
            }
        }
    }
}


@Composable
@Preview(showBackground = true)
fun HomeMenu(){
    Surface(color = MaterialTheme.colors.background,modifier = Modifier.fillMaxWidth().fillMaxHeight()) {

            Card(elevation = 2.dp, modifier = Modifier.padding(16.dp).fillMaxWidth().aspectRatio(1.4f)){
                Column {
                    Row (horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                        ){
                        Text(TextsLang.getText("Destination"), fontSize = 25.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(10.dp))
                        Icon(Icons.Filled.Lock,"")
                    }

                }
            }

    }
}


@Composable
fun BottomBar(){
    var selectedItem = remember { mutableStateOf("Home") }
    val home = "Home"
    val map = "Map"
    BottomNavigation {
        BottomNavigationItem(
            icon = {Icon(Icons.Filled.Home, contentDescription = home)},
            label = {Text(home)},
            alwaysShowLabel = false,
            selected = selectedItem.value == home,
            onClick = {
                selectedItem.value = home
            }
        )
        BottomNavigationItem(
            icon = {Icon(Icons.Filled.LocationOn, contentDescription = map)},
            label = {Text(map)},
            alwaysShowLabel = false,
            selected = selectedItem.value == map,
            onClick = {
                selectedItem.value = map
            }
        )
    }

}