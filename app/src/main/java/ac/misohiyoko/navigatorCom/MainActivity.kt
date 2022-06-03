package ac.misohiyoko.navigatorCom

import ac.misohiyoko.navigatorCom.ui.theme.Gray700
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions


import androidx.compose.foundation.text.selection.SelectionContainer

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat


class MainActivity : AppCompatActivity() {
    companion object {
        private const val PERMISSION_REQUEST_CODE = 1234
        public const val INTENT_LATITUDE = "latitude"
        public const val INTENT_LONGITUDE = "longitude"
        public const val INTENT_DEST= "destination"
    }

    private var namedLocation:NamedLocation = NamedLocation(0.0, 0.0,"")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var isNavActive:Boolean = ForeGroundNav.isActive(this)

        setContent {
            mainScaffold(isNavActive){value ->
                isNavActive = value
                if(isNavActive){
                    val intent = Intent(this, ForeGroundNav::class.java)
                    intent.putExtra(INTENT_LATITUDE, namedLocation.latitude)
                    intent.putExtra(INTENT_LONGITUDE, namedLocation.longitude)
                    intent.putExtra(INTENT_DEST, namedLocation.name)
                    startService(intent)
                }else{
                    val intent = Intent(this, ForeGroundNav::class.java)

                    stopService(intent)
                }
            }
        }
        requestPermission()

        createNotificationChannel()
    }
    private fun requestPermission() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {

                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true -> {
                    // Precise location access granted.
                    val backGroundGPSApproved = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    if(backGroundGPSApproved == PackageManager.PERMISSION_GRANTED){

                    }else{
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                        PERMISSION_REQUEST_CODE)
                    }
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                    // Only approximate location access granted.

                }
                else -> {
                    // No location access granted.

                }
            }
        }
        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,

        ))



    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                ForeGroundNav.CHANNEL_ID,
                "お知らせ",
                NotificationManager.IMPORTANCE_HIGH).apply {
                    description = "通知"
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }
    }
}




@Preview
@Composable
fun mainScaffold(isNavStartedFirst: Boolean = false,buttonOnClick:(Boolean)->Unit = {}){
    var selectedMenu = rememberSaveable { mutableStateOf("Home") }
    var isNavStarted = rememberSaveable { mutableStateOf(isNavStartedFirst) }
    Scaffold (
        bottomBar = {
            BottomBar(selectedMenu.value){
                value -> selectedMenu.value = value;
            }
        }
    ){
        if(selectedMenu.value == "Home"){
            HomeMenu(isNavStarted = isNavStarted.value){
                isNavStarted.value = !isNavStarted.value
                buttonOnClick(isNavStarted.value)
            }
        }else{

        }
    }
}


@Composable
fun HomeMenu(destName:String = "ちえりあ", destAddress:String = "札幌市西区課長五城二兆",isNavStarted:Boolean, buttonOnClick:()->Unit){



            Card(elevation = 5.dp, modifier = Modifier.padding(16.dp).fillMaxWidth().height(IntrinsicSize.Min)){
                Column() {
                    Row (horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                        ){
                        Icon(Icons.Filled.LocationOn,"", modifier = Modifier.wrapContentHeight().padding(2.dp))
                        SelectionContainer {
                            Column {

                                    Text(
                                        text = stringResource(R.string.destination),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 30.sp,
                                        textAlign = TextAlign.Left,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                    Text(
                                        text = destName,
                                        fontSize = 20.sp,
                                        textAlign = TextAlign.Left,
                                        modifier = Modifier.padding(10.dp,0.dp,5.dp,5.dp),
                                        color = Gray700
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
                            fontSize = 15.sp,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.padding(8.dp),
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis);
                    }
                    Button(
                        onClick = {

                            buttonOnClick()
                        },
                        modifier = Modifier.padding(10.dp).align(alignment = Alignment.End),




                    ){
                        if(!isNavStarted){
                            Icon(Icons.Filled.PlayArrow,"")
                            Text("ナビを開始")
                        }else{
                            Icon(Icons.Filled.Close,"")
                            Text("ナビを停止")
                        }
                    }


                }
            }


}

@Composable
fun MapMenu(){
    var text = remember { mutableStateOf(TextFieldValue("")) }
    TextField(
        value = text.value,
        onValueChange = {
            text.value = it
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        label = { Text(text = stringResource(R.string.destination)) },
        placeholder = { Text(text = stringResource(R.string.write_your_destination)) }
    )
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