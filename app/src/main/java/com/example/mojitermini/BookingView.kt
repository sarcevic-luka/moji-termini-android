package com.example.mojitermini

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingView(onClose: () -> Unit) {
    var doctors by remember { mutableStateOf(listOf<Doctor>()) }
    var selectedDoctor by remember { mutableStateOf(0) }
    var selectedSlot by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var showAlert by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Novi termin", fontSize = 34.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = { onClose() }) {
                Text("Zatvori")
            }
        }

        Text("Liječnik", fontSize = 14.sp, color = Constants.gray, modifier = Modifier.padding(top = 16.dp, bottom = 4.dp))
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value = if (doctors.isNotEmpty()) doctors[selectedDoctor].name + " - " + doctors[selectedDoctor].specialty else "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Liječnik") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                for (i in 0 until doctors.size) {
                    DropdownMenuItem(
                        text = { Text(doctors[i].name + " - " + doctors[i].specialty) },
                        onClick = {
                            selectedDoctor = i
                            expanded = false
                        }
                    )
                }
            }
        }

        Text("Termin", fontSize = 14.sp, color = Constants.gray, modifier = Modifier.padding(top = 16.dp, bottom = 4.dp))
        // TODO: prikazati slobodne termine odabranog liječnika
        Text("Odaberi termin", color = Constants.gray, modifier = Modifier.padding(vertical = 8.dp))

        Text("Napomena", fontSize = 14.sp, color = Constants.gray, modifier = Modifier.padding(top = 16.dp, bottom = 4.dp))
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        )

        TextButton(
            onClick = {
                NetworkManager.bookAppointment(doctors[selectedDoctor], selectedSlot) { success ->
                    showAlert = true
                }
            },
            modifier = Modifier.padding(top = 16.dp, bottom = 32.dp)
        ) {
            Text("Rezerviraj")
        }
    }

    LaunchedEffect(Unit) {
        NetworkManager.getDoctors { result ->
            doctors = result
        }
    }

    if (showAlert) {
        AlertDialog(
            onDismissRequest = { showAlert = false },
            title = { Text("Uspjeh") },
            text = { Text("Termin je rezerviran!") },
            confirmButton = {
                TextButton(onClick = { showAlert = false }) { Text("OK") }
            }
        )
    }
}
