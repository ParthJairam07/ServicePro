package com.example.b07proj.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.core.net.toUri

@Composable
fun ExitButton(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val activity = context as? Activity // in requirements that context is an activity

    Button(
        onClick = {
            // Launch google.com
            Intent(Intent.ACTION_VIEW, "https://www.google.com".toUri())
                .apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                .also { context.applicationContext.startActivity(it) }

            // terminate
            activity?.finishAndRemoveTask()
        },
        modifier = modifier, // in other pages
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFF86E6E),
            contentColor   = Color.Black
        )
    ) {
        Text("Exit", fontWeight = FontWeight.Bold)
    }
}

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

    Scaffold(
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    ExitButton()
                }
            }
        }
    ) { scaffoldPadding ->
        ModalNavigationDrawer(
            modifier = Modifier.padding(scaffoldPadding),
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Spacer(Modifier.height(12.dp))
                    Text("Menu Options", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                    HorizontalDivider()
                    Text("Account", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
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
                    NavigationDrawerItem(
                        label = { Text("Logout") },
                        selected = false,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                navController.navigate("login_page")
                            }
                        }
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    Column {
                        TopBar(scope, drawerState, navController)
                        HorizontalDivider(
                            color = Color.Gray,
                            thickness = 0.5.dp
                        )
                    }
                }
            ) { innerPadding ->
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
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun DialogBox(
    goToEditAfterDialog: MutableState<Boolean>,
    editAccountDialog: MutableState<Boolean>,
    editAccountInfo: MutableState<Boolean>,
    fullQuizRestart: MutableState<Boolean>
) {
    Dialog(
        onDismissRequest = { editAccountDialog.value = false }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Has your relationship status changed?",
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 5.dp, bottom = 5.dp),
                )
                Text(
                    text = "Warning: Clicking Yes would prompt you to redo the questionnaire.",
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 9.dp, bottom = 14.dp),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
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
val myList = mutableListOf<String>()
@SuppressLint("DiscouragedApi")
@Composable
fun ViewData() {
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
        for ((key, value) in newMap) {
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
                                append(" and ")
                        }
                    }.trim('"')
                    val combined2 = buildString {
                        for ((key2, value2) in newMap2) {
                            append(value2.toString().trim('"'))
                            append("\n")
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

@Composable
fun Parsable(innerPadding: PaddingValues) {
    ViewData()
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = BackgroundColor)
            .padding(innerPadding)
            .padding(top = 30.dp, start = 15.dp, end = 15.dp)
            .consumeWindowInsets(innerPadding),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(myList.chunked(2)) { pair ->
            val text1 = pair.getOrNull(0) ?: ""
            val text2 = pair.getOrNull(1) ?: ""
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
                }
            }
        }
    }
}

var outputMap: Map<String, Any> = mutableMapOf<String, Any>()
@SuppressLint("UnrememberedMutableState")
@Composable
fun EditParsable(quizScreenTrigger: MutableState<Boolean>, innerPadding: PaddingValues, navController: NavHostController) {
    val isDataLoaded = remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        myList.clear()
        val inputStream2 = context.resources.openRawResource(
            context.resources.getIdentifier("headings", "raw", context.packageName)
        )
        val jsonData2 = inputStream2.bufferedReader().use { it.readText() }
        val outputJsonString2 = JSONObject(jsonData2)
        val posts2 = outputJsonString2.getJSONObject("answers")

        val newMap: Map<String, JsonElement> = getUserAnswers()
        var counter: Int = 0
        for ((key, value) in newMap) {
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
                                append(" and ")
                        }
                    }.trim('"')
                    counter = 0
                    val combined2 = buildString {
                        for ((key2, value2) in newMap2) {
                            append(value2.toString().trim('"'))
                            append("\n")
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
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .background(color = BackgroundColor)
                .padding(innerPadding)
                .padding(top = 30.dp, start = 15.dp, end = 15.dp)
                .consumeWindowInsets(innerPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(myList.chunked(2)) { pair ->
                if ("Relationship Status" !in pair.toString()) {
                    val text1 = pair.getOrNull(0) ?: ""
                    val text2 = pair.getOrNull(1) ?: ""
                    val presenter = remember { QuizPresenter() }
                    if (quizScreenTrigger.value)
                        navController.navigate("edit_quiz_screen")
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
                            val context = LocalContext.current
                            val editOk = remember { mutableStateOf(false) }
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = stringResource(R.string.editIcon),
                                modifier = Modifier.align(Alignment.End).clickable {
                                    editOk.value = true
                                }
                            )
                            if (editOk.value) {
                                LaunchedEffect(Unit) {
                                    outputMap = getId(context, text1)
                                    Log.d(outputMap.keys.toString(), outputMap.keys.toString())
                                    Log.d("AAAAAAAAA", "AAAAAAAAAAAAAAA")
                                    presenter.prefillMap = outputMap
                                    quizScreenTrigger.value = true
                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
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
suspend fun getId(context: Context, text: String): Map<String, String> {
    val inputStream2 = context.resources.openRawResource(
        context.resources.getIdentifier("headings", "raw", context.packageName)
    )
    val jsonData2 = inputStream2.bufferedReader().use { it.readText() }
    val outputJsonString2 = JSONObject(jsonData2)
    val posts2 = outputJsonString2.getJSONObject("answers")
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(scope: CoroutineScope, drawerState: DrawerState, navController: NavHostController ) {
    CenterAlignedTopAppBar(
        title = {
            Image(
                painter = painterResource(R.drawable.templogo),
                contentDescription = stringResource(id = R.string.logoDescription),
            )
        },
        navigationIcon = {
            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = stringResource(R.string.menuIcon),
                modifier = Modifier.clickable {
                    scope.launch {
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
            IconButton(onClick = { navController.navigate("settings_page") }) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Settings",
                    tint = BackgroundColor
                )
            }
        }
    )
}