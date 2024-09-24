package com.example.oliviadarien_remindapp

import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oliviadarien_remindapp.ui.theme.OliviaDarienRemindAppTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OliviaDarienRemindAppTheme {
                AppLayout()
            }
        }
    }
}

enum class OpenDialog {
    NONE,
    DATE,
    TIME
}

@Composable
fun RowScope.TopBarButton(label: String, onClick: () -> Unit) {
    Button(onClick=onClick, modifier=Modifier.weight(1f).padding(PaddingValues(8.dp, 0.dp))) {
        Text(text=label)
    }
}

@Composable
fun AddReminderBar(newValue: String, onValueChange: (String) -> Unit, date: Long, time: Pair<Int, Int>, onSetDate: () -> Unit, onSetTime: () -> Unit, onCreate: () -> Unit) {
    Column(
        modifier=Modifier
            .background(color=MaterialTheme.colorScheme.primaryContainer)
            .padding(PaddingValues(8.dp, 0.dp))
            .fillMaxWidth()

    ) {
        Spacer(modifier=Modifier.height(32.dp))
        Text(text="Add Reminder", color=MaterialTheme.colorScheme.primary, fontSize=24.sp,  modifier = Modifier.padding(start=8.dp))
        Spacer(modifier=Modifier.height(8.dp))
        OutlinedTextField(value=newValue, onValueChange=onValueChange, modifier=Modifier.padding(PaddingValues(8.dp, 0.dp)).fillMaxWidth())
        Spacer(modifier=Modifier.height(8.dp))
        Row() {
            TopBarButton("Set Date", onClick=onSetDate)
            TopBarButton("Set Time", onClick=onSetTime)
            TopBarButton("Create", onClick=onCreate)
        }
        Spacer(modifier=Modifier.height(8.dp))
        DateInfo(date)
        TimeInfo(time)
        Spacer(modifier=Modifier.height(8.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(onDateSelected: (Long?)->Unit, onDismiss: ()->Unit) {
    val datePickerState = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest=onDismiss,
        confirmButton={
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton={
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerModal(onTimeSelected: (Int, Int)->Unit, onDismiss: ()->Unit) {
    val timePickerState = rememberTimePickerState()
    AlertDialog(
        onDismissRequest=onDismiss,
        confirmButton={
            TextButton(onClick = {
                onTimeSelected(timePickerState.hour, timePickerState.minute)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton={
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        text={
            TimePicker(state = timePickerState)
        }
    )
}

@Composable
fun ReminderEntry(value: String, date: Long, time: Pair<Int, Int>, onClick: ()->Unit) {
    Card(modifier=Modifier.padding(8.dp)) {
        Row(verticalAlignment=Alignment.CenterVertically, modifier=Modifier.padding(8.dp)) {
            Column(modifier=Modifier.weight(1f)) {
                Text(text=value,  modifier = Modifier.padding(start=8.dp))
                DateInfo(date)
                TimeInfo(time)
            }
            Button(onClick=onClick,  modifier = Modifier.padding(start=8.dp)) {
                Text("Clear")
            }
        }
    }
}

@Composable
fun DateInfo(date: Long) {
    // display the date accurately based on the user's locale
    val format = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    format.timeZone = TimeZone.getDefault()
    Text(text="Date: " + format.format(Date(date+5*(3600000))), modifier = Modifier.padding(start=8.dp))
}

@Composable
fun TimeInfo(data: Pair<Int, Int>) {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, data.first)
        set(Calendar.MINUTE, data.second)
    }
    // 12 hour format
    val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
    Text(text="Time: " + format.format(calendar.time), modifier = Modifier.padding(start=8.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLayout() {
    var newReminderValue by remember {mutableStateOf("")}
    var openDialog by remember {mutableStateOf(OpenDialog.NONE)}
    var chosenDate by remember {mutableStateOf(Date().toInstant().toEpochMilli())}
    var chosenTime by remember {mutableStateOf(Pair<Int, Int>(0, 0))}

    var reminderSet by remember {mutableStateOf(false)}
    var reminderLabel by remember {mutableStateOf("")}
    var reminderDate by remember {mutableStateOf(Date().toInstant().toEpochMilli())}
    var reminderTime by remember {mutableStateOf(Pair<Int, Int>(0, 0))}

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    if (openDialog == OpenDialog.DATE) {
        DatePickerModal({
            if (it != null) {
                chosenDate = it
            }
        }, {openDialog = OpenDialog.NONE})
    }
    if (openDialog == OpenDialog.TIME) {
        TimePickerModal({hour, minute ->
            chosenTime = Pair(hour, minute)
        }, {openDialog = OpenDialog.NONE})
    }

    Scaffold(modifier = Modifier.fillMaxSize().displayCutoutPadding(), snackbarHost = { SnackbarHost(hostState = snackbarHostState) }, topBar = {
        if (reminderSet) {
            return@Scaffold
        }
        AddReminderBar(newReminderValue, {newReminderValue = it}, chosenDate, chosenTime, {openDialog=OpenDialog.DATE}, {openDialog=OpenDialog.TIME}, onCreate={
            if (newReminderValue.isNotEmpty()) {
                reminderLabel = newReminderValue
                reminderDate = chosenDate
                reminderTime = chosenTime
                reminderSet = true
                // clear text field when new reminder is set
                newReminderValue = ""
                scope.launch {
                    // dismiss current snackbar if active
                    snackbarHostState.currentSnackbarData?.dismiss()

                    snackbarHostState.showSnackbar("Reminder was set successfully!")
                }
            }
            else{
                scope.launch {
                    // dismiss current snackbar if active
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar("Please enter a valid reminder!")
                }
            }
        })
    }) { innerPadding ->
        LazyColumn(modifier=Modifier.padding(innerPadding).padding(8.dp)) {
            if (reminderSet) {
                item {
                    ReminderEntry(reminderLabel, reminderDate, reminderTime) {
                        reminderSet = false
                        scope.launch {
                            // dismiss current snackbar if active
                            snackbarHostState.currentSnackbarData?.dismiss()

                            snackbarHostState.showSnackbar("Reminder was cleared successfully!")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    OliviaDarienRemindAppTheme {
        AppLayout()
    }
}