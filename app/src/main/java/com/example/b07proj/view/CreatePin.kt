package com.example.b07proj.view
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.b07proj.R
import com.example.b07proj.ui.theme.Primary40
import com.example.b07proj.ui.theme.backgroundAccent


// renders Create PIN screen
@Composable
fun CreatePin(navController: NavHostController) {
    UICreatePin(navController)
}
// renders title for PIN screen
@Composable
fun TitleText() {
    val myFont = FontFamily(Font(R.font.afacad))
    Text(
        text = stringResource(R.string.pinPromptTitle),
        color = backgroundAccent,
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = myFont
    )
}
// renders information about the PIN
@Composable
fun SetUpPinInfo() {
    val myFont = FontFamily(Font(R.font.afacad))
    Text(
        text = stringResource(R.string.pinPromptInfo),
        modifier = Modifier.width(333.dp),
        fontFamily = myFont


    )
}
// renders header for text field
@Composable
fun InputPinText() {
    val myFont = FontFamily(Font(R.font.afacad))

    Text(
        text = stringResource(R.string.inputPinInfo),
        fontWeight = FontWeight.Bold,
        fontFamily = myFont
    )

}

// renders input field and check if input is valid or not
@Composable
fun InputPinField(
    pinValue: String,
    onPinValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = pinValue,
        // we only change the value if its less than 6 and all digits
        onValueChange = { newValue ->
            if (newValue.length <= 6 && newValue.all { it.isDigit() }) {
                onPinValueChange(newValue)
            }
        },
        modifier = modifier,
        label = { InputPinText() },
        // convert keyboard PIN keyboard
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        singleLine = true,

        )
}

@Composable
fun ContinueButton(pinValue: String, isPinValid: Boolean, modifier: Modifier, navController: NavHostController) {
    Button(
        onClick = {
            // send to logcat the pin and its validity
            Log.d("valid pin", "pin is: $pinValue, Valid?: $isPinValid")
            navController.navigate("landing_page") // this is temporary for now
        },
        // button will be glowing depending on if the pin is valid
        enabled = isPinValid,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = Primary40),
    ) {
        Text(
            text = stringResource(R.string.continueButtonText),
            modifier = Modifier.padding(end = 30.dp)
            )
        Image(
            painter = painterResource(id = R.drawable.sendhorizontal),
            contentDescription = stringResource(id = R.string.arrow_content_description),
            modifier = Modifier.scale(2.5F),
            )

    }


}

@Composable
fun MainBody(modifier: Modifier = Modifier, navController: NavHostController) {
    var pin by remember { mutableStateOf("") }
    // generally layout is centered column, this takes up the whole screen and has padding
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 45.dp)
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // rendering all components
        TitleText()
        Spacer(modifier = Modifier.height(16.dp))
        SetUpPinInfo()
        Spacer(modifier = Modifier.height(24.dp))
        InputPinField(
            pinValue = pin,
            onPinValueChange = { pin = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))
        val isPinValid = pin.length in 4..6
        // light up the continue button if they typed 4-6 digits
        ContinueButton(
            pinValue = pin,
            isPinValid = isPinValid,
            modifier =  Modifier.align(Alignment.End),
            navController = navController
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UICreatePin(navController: NavHostController) {
    // this layout has header + main content
    Scaffold(
        topBar = {
            // ordered as a column for horizontal line below the header
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.pinPromptHeaderText),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Primary40
                        )
                    }
                )
                HorizontalDivider(
                    color = Color.Gray,
                    thickness = 0.5.dp
                )
            }

        }, floatingActionButton = {
            ExitButton(
                modifier = Modifier
                    .padding(5.dp)    // placement
            )
        }
    ) { innerPadding ->
        // render in main content
        MainBody(
            modifier = Modifier.padding(innerPadding),
            navController = navController
        )
    }
}