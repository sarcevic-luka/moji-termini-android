package com.example.mojitermini

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentView(viewModel: AppointmentsViewModel) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    var showBooking by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    fun filteredAppointments(): List<Appointment> {
        val formatter = SimpleDateFormat("dd.MM.yyyy")
        val today = formatter.format(Date())

        var result = mutableListOf<Appointment>()
        for (a in viewModel.appointments) {
            if (selectedTab == 0) {
                if (a.date > today) {
                    result.add(a)
                }
            } else {
                if (a.date < today) {
                    result.add(a)
                }
            }
        }
        if (searchText != "") {
            result = result.filter { it.doctorName.contains(searchText) }.toMutableList()
        }
        return result
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        Text(
            "Moji termini",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
        )

        // header
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Pozdrav, " + prefs.getString("patientName", null)!!,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "OIB: " + prefs.getString("patientOIB", null)!!,
                    fontSize = 13.sp,
                    color = Constants.gray
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                Icons.Filled.AccountCircle,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        // TODO profil
                    }
            )
        }

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            SegmentedButton(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
            ) { Text("Nadolazeći") }
            SegmentedButton(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
            ) { Text("Prošli") }
        }

        BasicTextField(
            value = searchText,
            onValueChange = { searchText = it },
            singleLine = true,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
                .background(Color(red = 0.95f, green = 0.95f, blue = 0.95f), RoundedCornerShape(8.dp))
                .padding(8.dp),
            decorationBox = { innerTextField ->
                if (searchText.isEmpty()) {
                    Text("Traži", color = Constants.gray)
                }
                innerTextField()
            }
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(filteredAppointments()) { appointment ->
                key(appointment.doctorName) {
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { value ->
                            if (value == SwipeToDismissBoxValue.EndToStart) {
                                val index = filteredAppointments().indexOf(filteredAppointments().find { it.id == appointment.id }!!)
                                viewModel.cancel(index)
                            }
                            true
                        }
                    )
                    SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = false,
                        backgroundContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Red)
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Text("Otkaži", color = Color.White)
                            }
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .clickable {
                                    // TODO: otvoriti detalje termina
                                    Log.d("ContentView", "tapped ${appointment.id}")
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(appointment.doctorName, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                                Text(appointment.specialty, fontSize = 14.sp, color = Constants.gray)
                                Text(formatDate(appointment.date), fontSize = 14.sp, color = Constants.blue)
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            if (appointment.status == "cancelled") {
                                Text("Otkazano", fontSize = 12.sp, color = Color.Red)
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Icon(
                                Icons.Filled.Phone,
                                contentDescription = null,
                                tint = Constants.blue,
                                modifier = Modifier.clickable {
                                    context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + appointment.phone)))
                                }
                            )
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }

        Button(
            onClick = { showBooking = true },
            colors = ButtonDefaults.buttonColors(containerColor = Constants.blue),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
                .width(350.dp)
                .height(50.dp)
        ) {
            Text("Naruči novi termin", color = Color.White)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadAppointments()
    }

    if (showBooking) {
        ModalBottomSheet(
            onDismissRequest = { showBooking = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            BookingView(onClose = { showBooking = false })
        }
    }
}

fun formatDate(string: String): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    val date = formatter.parse(string)!!

    val formatter2 = SimpleDateFormat("dd.MM.yyyy. 'u' HH:mm")
    return formatter2.format(date)
}
