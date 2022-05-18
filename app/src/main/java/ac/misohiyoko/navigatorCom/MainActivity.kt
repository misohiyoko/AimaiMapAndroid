package ac.misohiyoko.navigatorCom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            mainScaffold()
        }
    }
}


@Composable
fun mainScaffold(){
    var selectedMenu = rememberSaveable { mutableStateOf("Home") }
    Scaffold (
        bottomBar = {
            BottomBar(selectedMenu.value){
                value -> selectedMenu.value = value;
            }
        }
    ){
        if(selectedMenu.value == "Home"){
            HomeMenu()
        }else{

        }
    }
}

@Preview
@Composable
fun HomeMenu(destName:String = "ちえりあ", destAddress:String = "札幌市西区課長五城二兆"){
    Surface(color = MaterialTheme.colors.background,modifier = Modifier.fillMaxWidth().fillMaxHeight()) {

            Card(elevation = 2.dp, modifier = Modifier.padding(16.dp).fillMaxWidth().aspectRatio(1.4f)){
                Column {
                    Row (horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                        ){
                        Icon(Icons.Filled.LocationOn,"", modifier = Modifier.wrapContentHeight().padding(2.dp).size(60.dp))
                        SelectionContainer {
                            Column {

                                    Text(
                                        text = "目的地",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 30.sp,
                                        textAlign = TextAlign.Left,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                    Text(
                                        text =destName,
                                        fontSize = 20.sp,
                                        textAlign = TextAlign.Left,
                                        modifier = Modifier.padding(10.dp,5.dp,5.dp,5.dp)
                                    )

                            }
                        }

                    }
                    Row (
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                            ){
                        Text(
                            text = destAddress,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.padding(5.dp),
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis);
                    }

                }
            }

    }
}


@Composable
fun BottomBar(selectedName:String, onClick:(String) -> Unit){

    val home = "Home"
    val map = "Map"
    BottomNavigation {
        BottomNavigationItem(
            icon = {Icon(Icons.Filled.Home, contentDescription = home)},
            label = {Text(home)},
            alwaysShowLabel = false,
            selected = selectedName == home,
            onClick = {
                onClick(home)
            }
        )
        BottomNavigationItem(
            icon = {Icon(Icons.Filled.LocationOn, contentDescription = map)},
            label = {Text(map)},
            alwaysShowLabel = false,
            selected = selectedName == map,
            onClick = {
                onClick(map)
            }
        )
    }

}