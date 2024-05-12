package ma.enset.projetmobile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.libraries.maps.model.CameraPosition
import com.google.android.libraries.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import ma.enset.projetmobile.ui.theme.ProjetMobileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            ProjetMobileTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var authenticated by remember { mutableStateOf(false) }

                    NavHost(navController = navController, startDestination = if (authenticated) "my_app_screen" else "authentication_screen") {
                        composable("authentication_screen") {
                            AuthenticationScreen(onAuthenticationSuccess = { authenticated = true })
                        }
                        composable("my_app_screen") {
                            MyApp(navController = navController)
                        }
                        composable("add_destination_screen") {
                            AddDestinationScreen(
                                onDestinationAdded = { /* Ajouter la nouvelle destination */ },
                                onBackClick = { navController.popBackStack() },
                                activity = this@MainActivity
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun AuthenticationScreen(onAuthenticationSuccess: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nom d'utilisateur") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mot de passe") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        Button(
            onClick = {
                if (username.isNotEmpty() && password.isNotEmpty()) {
                    onAuthenticationSuccess()
                } else {
                    errorMessage = "Veuillez remplir tous les champs"
                }
            }
        ) {
            Text("Se connecter")
        }
    }
}

@Composable
fun MyApp(navController: NavHostController) {
    var selectedDestination by remember { mutableStateOf<Destination?>(null) }

    val destinations = listOf(
        Destination("Destination 1", "Description de la destination 1", 48.8566, 2.3522, R.drawable.destination1),
        Destination("Destination 2", "Description de la destination 2", 34.0522, -118.2437, R.drawable.destination5),
        Destination("Destination 3", "Description de la destination 3", 41.8781, -87.6298, R.drawable.destination3),
        Destination("Destination 4", "Description de la destination 4", 25.7617, -80.1918, R.drawable.destination4),
        Destination("Destination 5", "Description de la destination 5", 51.5074, -0.1278, R.drawable.destination5)
    )


    Column {
        Text(
            text = "Destinations Naturelles",
            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(16.dp)
        )
        DestinationList(destinations = destinations) { destination ->
            selectedDestination = destination
        }
        selectedDestination?.let { selectedDestination ->
            DestinationDetail(selectedDestination) {

            }
        }
        FloatingActionButton(
            onClick = {
                navController.navigate("add_destination_screen")
            },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.End)
        ) {
            Text(
                text = "Ajouter une nouvelle destination"
            )
        }
    }
}

@Composable
fun DestinationList(destinations: List<Destination>, onDestinationClick: (Destination) -> Unit) {
    LazyColumn {
        items(destinations) { destination ->
            DestinationItem(destination = destination) {
                onDestinationClick(destination)
            }
        }
    }
}

@Composable
fun DestinationItem(destination: Destination, onDestinationClick: (Destination) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDestinationClick(destination) }
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = destination.imageResource),
            contentDescription = destination.name,
            modifier = Modifier.size(64.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = destination.name, fontWeight = FontWeight.Bold)
            Text(text = destination.description, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun DestinationDetail(destination: Destination, onBackClick: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = destination.name,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = destination.description,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        // Affichage de la carte
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            initialCameraPosition = CameraPosition(
                target = LatLng(destination.latitude, destination.longitude),
                zoom = 10f // Zoom initial
            ),
            markers = listOf(
                Marker(
                    position = LatLng(destination.latitude, destination.longitude),
                    title = destination.name
                )
            )
        )
        Button(onClick = { onBackClick() }) {
            Text(text = "Retour")
        }
    }
}


@Composable
fun AddDestinationScreen(
    onDestinationAdded: (Destination) -> Unit,
    onBackClick: () -> Unit,
    activity: Activity
) {
    var destinationName by remember { mutableStateOf("") }
    var destinationDescription by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = destinationName,
            onValueChange = { destinationName = it },
            label = { Text("Nom de la destination") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = destinationDescription,
            onValueChange = { destinationDescription = it },
            label = { Text("Description de la destination") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val newDestination = Destination(destinationName, destinationDescription,34.0522, -118.2437, R.drawable.destination5)
                onDestinationAdded(newDestination)
                onBackClick()
            }
        ) {
            Text("Ajouter la destination")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                dispatchTakePictureIntent(activity)
            }
        ) {
            Text("Prendre une photo")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onBackClick() }
        ) {
            Text("Annuler")
        }
    }
}

fun dispatchTakePictureIntent(activity: Activity) {
    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
}
data class Destination(
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val imageResource: Int // Ajout de l'identifiant de l'image
)

private const val REQUEST_IMAGE_CAPTURE = 1
