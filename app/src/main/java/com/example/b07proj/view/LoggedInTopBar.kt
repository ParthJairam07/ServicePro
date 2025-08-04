package com.example.b07proj.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.b07proj.R
import com.example.b07proj.presenter.QuizPresenter
import com.example.b07proj.ui.theme.BackgroundColor
import com.example.b07proj.ui.theme.Primary40
import com.example.b07proj.ui.theme.Primary50
import com.example.b07proj.view.AnswersProvider.getUserAnswers
import com.google.android.play.integrity.internal.b
import com.google.gson.JsonElement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.collections.mutableListOf

@SuppressLint("CoroutineCreationDuringComposition", "UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class) // allow usage of experimental Material3 APIs like Scaffold
@Composable
fun LoggedInTopBar(navController: NavHostController, content: @Composable (PaddingValues) -> Unit) {
    val drawerState = rememberDrawerState(DrawerValue.Closed) // keep track of the drawer state
    val scope = rememberCoroutineScope() // needed to launch drawer actions
    val editAccountDialog = mutableStateOf(false) // mutable bool for edit account dialog
    val viewAccount = mutableStateOf(false) // mutable bool for view account screen
    val editAccountInfo = mutableStateOf(false) // mutable bool for quiz edit confirmation
    val quizScreenTrigger = remember { mutableStateOf(false) } // used to trigger quiz edit navigation
    val fullQuizRestart = remember { mutableStateOf(false) } // used to trigger new quiz navigation
    val goToEditAfterDialog = remember { mutableStateOf(false) }
    // create a modalNavigationDrawer in order to store the menu bar
    ModalNavigationDrawer(
        // keep state variable which will change according to when the user clicks the bar open or dismisses
        drawerState = drawerState,
        // content in drawer
        drawerContent = {
            // menu bar in the form of ModalDrawerSheet
            ModalDrawerSheet {
                // add some space between options
                Spacer(Modifier.height(12.dp))
                // create options
                Text("Menu Options", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                HorizontalDivider()

                // create an Account heading, with two drawer items for the user to go to
                Text("Account", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                //NavigationDrawerItem(
                //    label = { Text("View Account") },
                //    selected = false,
                //    onClick = {
                 //       scope.launch {
                //            drawerState.close()
                //            viewAccount.value = true
                 //       }
                 //   }
                //)

                NavigationDrawerItem(
                    label = { Text("Edit Account") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            editAccountDialog.value = true
                        }
                    }
                )

            }
        }
    ) {
        // create a Scaffold to house the TopAppBar and other parts of the page
        Scaffold(
            // create a topBar element which will consist of the logo
            topBar = {
                Column {
                    TopBar(scope, drawerState)
                    HorizontalDivider(
                        color = Color.Gray,
                        thickness = 0.5.dp
                    )

                }
            }
        ) { innerPadding -> // pass in padding to allow fields within the UI to be spaced from the topBar
            LaunchedEffect(goToEditAfterDialog.value) {
                if (goToEditAfterDialog.value) {
                    editAccountInfo.value = true
                    goToEditAfterDialog.value = false
                }
            }
            when {
                editAccountDialog.value -> DialogBox(goToEditAfterDialog,editAccountDialog, editAccountInfo, fullQuizRestart)

                viewAccount.value -> {
                    Parsable(innerPadding)
                }

                editAccountInfo.value -> {
                    EditParsable(quizScreenTrigger, innerPadding, navController)
                }

                quizScreenTrigger.value -> {
                    LaunchedEffect(Unit) {
                        navController.navigate("edit_quiz_screen")
                    }
                }

                fullQuizRestart.value -> {
                    LaunchedEffect(Unit) {
                        navController.navigate("safety_plan_quiz")
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp)
                    ) {
                        content(innerPadding)
                    }
                }
            }
            // call the function here for our code
        }

    }

}

@SuppressLint("UnrememberedMutableState")
// DialogBox function that takes in innerPadding values for right spacing, editAccountDialog for dismissing the box, editAccountInfo to open a new edit info, and full restart
@Composable
fun DialogBox(
    goToEditAfterDialog: MutableState<Boolean>,
    editAccountDialog: MutableState<Boolean>,
    editAccountInfo: MutableState<Boolean>,
    fullQuizRestart: MutableState<Boolean>
) {
    // display the dialog when triggered. It can be dismissed manually.
    Dialog(
        onDismissRequest = { editAccountDialog.value = false }  // dialog closes when user taps outside
    ) {
        // create a card UI with rounded corners inside the dialog box
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            // create a column to arrange the dialog box elements
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // main prompt asking the user if their relationship status has changed
                Text(
                    text = "Has your relationship status changed?",
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 5.dp, bottom = 5.dp),
                )

                // warning message about the consequences of clicking "Yes"
                Text(
                    text = "Warning: Clicking Yes would prompt you to redo the questionnaire.",
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 9.dp, bottom = 14.dp),
                )

                // buttons row: with yes no and cancel
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    // "Yes" option: close dialog and open up the quiz
                    TextButton(
                        onClick = {
                            editAccountDialog.value = false
                            fullQuizRestart.value = true
                        },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Yes")
                    }

                    TextButton(
                        onClick = {
                            editAccountDialog.value = false
                            goToEditAfterDialog.value = true

                        },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("No")
                    }

                    // "Cancel" option: just close the dialog without doing anything else and returning to the menu box
                    TextButton(
                        onClick = {
                            editAccountDialog.value = false
                        },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}
//create a global list to store all the JSON data
val myList = mutableListOf<String>()

// viewData is responsible for converting the JSON data of the user into a machine legible ListOf<String>
@SuppressLint("DiscouragedApi")
@Composable
fun ViewData() {
    // take the current context (necessary for accessing files)
    val context = LocalContext.current
    val inputStream2 = context.resources.openRawResource(
        context.resources.getIdentifier("headings", "raw", context.packageName)
    )
    val jsonData2 = inputStream2.bufferedReader().use { it.readText() }
    val outputJsonString2 = JSONObject(jsonData2)
    val posts2 = outputJsonString2.getJSONObject("answers")


    LaunchedEffect(Unit) {


        val newMap: Map<String, JsonElement> = getUserAnswers()
        var counter: Int = 0

        // parse through the keys in the JSON object from the headings.json
        for ((key, value) in newMap) {
            // if the specific text has children in it and the i is 5, then set to the outputMap accordingly
            if (key == "5" || key == "12"|| key == "14" || key == "15") {
                if (!value.isJsonObject) {
                    var combined1 : String = ""
                    val thisObject = posts2.getJSONObject(key)
                    val keysforThis = thisObject.keys()
                    while (keysforThis.hasNext()) {
                        val key = keysforThis.next()
                        combined1 = buildString {
                            append(thisObject.get(key).toString())
                        }
                    }
                    myList.add(combined1)
                    myList.add(value.toString().trim('"'))
                } else {
                    val newMap2: Map<String, Any> =
                        value.asJsonObject.asMap() as Map<String, String>

                    val combined1 = buildString {
                        for ((key2, value2) in newMap2) {
                            append(posts2.getJSONObject(key).getString(key2).trim('"'))
                            counter = counter + 1
                            if (counter < newMap2.size)
                                append(" and ") // add some grammar
                        }
                    }.trim('"')

                    // do the same for the keys in answers.json
                    val combined2 = buildString {
                        for ((key2, value2) in newMap2) {
                            append(value2.toString().trim('"'))
                            append("\n") //this time add a newline
                        }
                    }.trim('"')

                    myList.add(combined1)
                    myList.add(combined2)
                }

            } else {
                myList.add(posts2.getString(key))
                myList.add(value.toString().trim('"'))
            }
        }
    }


}

// create a function called Parsable that takes in the innerPadding values to ensure no issues with the topBar
@Composable
fun Parsable(innerPadding: PaddingValues) {
    // call ViewData to get all the user answers
    ViewData()
    // create a LazyColumn to harness all the answers, making it scrollable by default and only loads whatever is visible on the page
    LazyColumn(
        // set the modifier values, color and so on to match padding values
        modifier = Modifier
            .fillMaxWidth()
            .background(color = BackgroundColor)
            .padding(innerPadding)
            .padding(top = 30.dp, start = 15.dp, end = 15.dp)
            .consumeWindowInsets(innerPadding),
        // spacing between the items
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // loop through the list in chunks of two, where the first index is the title and second is the user answer
        items(myList.chunked(2)) { pair ->
            val text1 = pair.getOrNull(0) ?: "" // set text1 to be the title, or 0th index, else empty string
            val text2 = pair.getOrNull(1) ?: "" // set text2 to be the answers, or 1st index, else empty string

            // create a boxy-sort of element to harness each answer
            Surface(
                // set colors, shadow, modifier values
                color = Primary50,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                // create column to harness both texts
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    // set text1
                    Text(
                        text = text1,
                        fontSize = 18.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        textAlign = TextAlign.Start
                    )
                    // set text2 with smaller font and in DarkGray
                    Text(
                        text = text2,
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
    }
}

// create a global mutableMap so that specific user editing options can be passed to the QuizPage
var outputMap: Map<String, Any> = mutableMapOf<String, Any>()

// EditParsable takes in quizScreenTrigger to open a screen to change the answer, padding values, navController to go to the screen
@SuppressLint("UnrememberedMutableState")
@Composable
fun EditParsable(quizScreenTrigger: MutableState<Boolean>, innerPadding: PaddingValues, navController: NavHostController) {
    // call ViewData again to get all the answers entered
    val isDataLoaded = remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        myList.clear() // Clear any existing data
        val inputStream2 = context.resources.openRawResource(
            context.resources.getIdentifier("headings", "raw", context.packageName)
        )
        val jsonData2 = inputStream2.bufferedReader().use { it.readText() }
        val outputJsonString2 = JSONObject(jsonData2)
        val posts2 = outputJsonString2.getJSONObject("answers")

        val newMap: Map<String, JsonElement> = getUserAnswers()
        var counter: Int = 0

        // parse through the keys in the JSON object from the headings.json
        for ((key, value) in newMap) {
            // if the specific text has children in it and the i is 5, then set to the outputMap accordingly
            if (key == "5" || key == "12"|| key == "14" || key == "15") {
                if (!value.isJsonObject) {
                    var combined1 : String = ""
                    val thisObject = posts2.getJSONObject(key)
                    val keysforThis = thisObject.keys()
                    while (keysforThis.hasNext()) {
                        val key = keysforThis.next()
                        combined1 = buildString {
                            append(thisObject.get(key).toString())
                        }
                    }
                    myList.add(combined1)
                    myList.add(value.toString().trim('"'))
                } else {
                    val newMap2: Map<String, Any> =
                        value.asJsonObject.asMap() as Map<String, String>

                    val combined1 = buildString {
                        for ((key2, value2) in newMap2) {
                            append(posts2.getJSONObject(key).getString(key2).trim('"'))
                            counter = counter + 1
                            if (counter < newMap2.size)
                                append(" and ") // add some grammar
                        }
                    }.trim('"')
                    counter = 0
                    // do the same for the keys in answers.json
                    val combined2 = buildString {
                        for ((key2, value2) in newMap2) {
                            append(value2.toString().trim('"'))
                            append("\n") //this time add a newline
                        }
                    }.trim('"')

                    myList.add(combined1)
                    myList.add(combined2)
                }

            } else {
                myList.add(posts2.getString(key))
                myList.add(value.toString().trim('"'))
            }
        }

        isDataLoaded.value = true
    }

    if (isDataLoaded.value) {
        // set a LazyColumn for harnessing all the answers, to be scrollable
        LazyColumn(
            // set modifier with the padding values to not interfere with topBar
            modifier = Modifier
                .fillMaxHeight()
                .background(color = BackgroundColor)
                .padding(innerPadding)
                .padding(top = 30.dp, start = 15.dp, end = 15.dp)
                .consumeWindowInsets(innerPadding),
            // spacing between items
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // loop through items through chunks of 2
            items(myList.chunked(2)) { pair ->
                if ("Relationship Status" !in pair.toString()) {
                    val text1 = pair.getOrNull(0) ?: "" // set text1 to be the title, or 0th index, else empty string
                    val text2 = pair.getOrNull(1) ?: "" // set text2 to be the answers, or 1st index, else empty string

                    // set a presenter variable which calls the QuizPresenter() kotlin class which has specific functions needed for the QuizPage
                    val presenter = remember { QuizPresenter() }

                    //Column(modifier = Modifier.heightIn(max = 2500.dp)) {
                    // if this value is triggered, go to the screen to edit that specific question
                    if (quizScreenTrigger.value)
                        navController.navigate("edit_quiz_screen")
                    //}
                    // create a boxy-item for each answer
                    Surface(
                        color = Primary50,
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = 6.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                        ) {
                            // set text1 and text2 in a column for the user to see their information
                            Text(
                                text = text1,
                                fontSize = 18.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                textAlign = TextAlign.Start
                            )
                            Text(
                                text = text2,
                                fontSize = 14.sp,
                                color = Color.DarkGray,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Start
                            )
                            // set context and editOk variables to trigger the screen to edit the specific answer
                            val context = LocalContext.current
                            val editOk = remember { mutableStateOf(false) }
                            // edit icon
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = stringResource(R.string.editIcon),
                                modifier = Modifier.align(Alignment.End).clickable {
                                    editOk.value = true // opens the screen
                                }
                            )
                            // if the editOk value is true, this sets up the outputMap and actually opens the screen
                            if (editOk.value) {
                                LaunchedEffect(Unit) {
                                    // call function to get the user's ID or related data
                                    outputMap = getId(context, text1)
                                    Log.d(outputMap.keys.toString(), outputMap.keys.toString())
                                    Log.d("AAAAAAAAA", "AAAAAAAAAAAAAAA")
                                    // this might not be needed???
                                    presenter.prefillMap = outputMap
                                    // trigger the quiz screen to show
                                    quizScreenTrigger.value = true
                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
        // show loading indicator while data is being loaded
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}




// this function getID takes in specific context to access resources, a text of the specific answer to set to outputMap and navController?
// New getId function - suspend, pure logic, no @Composable
suspend fun getId(context: Context, text: String): Map<String, String> {
    val inputStream2 = context.resources.openRawResource(
        context.resources.getIdentifier("headings", "raw", context.packageName)
    )
    val jsonData2 = inputStream2.bufferedReader().use { it.readText() }
    val outputJsonString2 = JSONObject(jsonData2)
    val posts2 = outputJsonString2.getJSONObject("answers")

    // Find matching key
    var ok: String = "0"
    for (i in posts2.keys()) {
        if (posts2[i] == text) {
            ok = i
        }
    }

    val result = mutableMapOf<String, String>()
    val newMap: Map<String, JsonElement> = getUserAnswers()
    for ((key, value) in newMap) {
        if (key == "5" && "Code" in text) {
            result["5"] = value.toString()
            break
        } else if (key == "12" && ("Shelter" in text || "Place" in text)) {
            result["12"] = value.toString()
            break
        } else if (key == "14" && ("Legal" in text || "Protection" in text)) {
            result["14"] = value.toString()
            break
        } else if (key == "15" && ("Equipment" in text || "Safety" in text)) {
            result["15"] = value.toString()
            break
        }
        else if (key == ok) {
            result[key] = value.toString()
            break
        }
    }

    return result
}



// this function is the TopBar, taking in the scope and state of menu bar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(scope: CoroutineScope, drawerState: DrawerState ) {
    CenterAlignedTopAppBar(
        title = {
            // display the logo image in the center of the top bar
            Image(
                painter = painterResource(R.drawable.templogo),
                contentDescription = stringResource(id = R.string.logoDescription),
            )
        },
        navigationIcon = {
            // display the menu icon on the left; clicking toggles the drawer open/close
            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = stringResource(R.string.menuIcon),
                modifier = Modifier.clickable {
                    scope.launch {
                        // open the drawer if it's closed, or close it if it's open
                        if (drawerState.isClosed) {
                            drawerState.open()
                        } else {
                            drawerState.close()
                        }
                    }
                }
            )
        },
        actions = {
            // placeholder for a settings icon on the right side (does nothing for now)
            IconButton(onClick = { /* erm */ }) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Settings",
                    tint = BackgroundColor
                )
            }
        }
    )
}




