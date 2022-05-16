package ac.misohiyoko.navigatorCom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ac.misohiyoko.navigatorCom.ui.theme.NavComTheme

import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment

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
@Preview
fun HomeMenu(){
    Surface(color = MaterialTheme.colors.background) {
        Row (horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
            ){
            Text(TextsLang.getText("Destination"))
            Icon(Icons.Filled.KeyboardArrowRight,"")
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