package com.example.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FireTruck
import androidx.compose.material.icons.filled.Flood
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.LocalPostOffice
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Train
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.models.ServiceCategory

object CategoryIconHelper {

    fun getIcon(category: ServiceCategory): ImageVector {
        return when (category) {
            ServiceCategory.ALL -> Icons.Default.GridView
            ServiceCategory.HOSPITAL_HEALTH -> Icons.Default.LocalHospital
            ServiceCategory.AMBULANCE -> Icons.Default.MedicalServices
            ServiceCategory.POLICE_LAW -> Icons.Default.LocalPolice
            ServiceCategory.FIRE_SERVICE -> Icons.Default.FireTruck
            ServiceCategory.BLOOD_DONOR -> Icons.Default.Bloodtype
            ServiceCategory.PHARMACY -> Icons.Default.LocalPharmacy
            ServiceCategory.GOVT_OFFICE -> Icons.Default.AccountBalance
            ServiceCategory.ELECTRICITY_UTILITY -> Icons.Default.ElectricBolt
            ServiceCategory.GAS_UTILITY -> Icons.Default.LocalGasStation
            ServiceCategory.EDUCATION -> Icons.Default.School
            ServiceCategory.TRANSPORT_BUS -> Icons.Default.DirectionsBus
            ServiceCategory.RAILWAY -> Icons.Default.Train
            ServiceCategory.POST_COURIER -> Icons.Default.LocalPostOffice
            ServiceCategory.DISASTER_RELIEF -> Icons.Default.Flood
            ServiceCategory.VETERINARY -> Icons.Default.Pets
            ServiceCategory.AGRICULTURE_FISHERIES -> Icons.Default.Agriculture
            ServiceCategory.TOURISM -> Icons.Default.Park
            ServiceCategory.OTHER_ESSENTIAL -> Icons.Default.SupportAgent
        }
    }

    fun getAccentColor(category: ServiceCategory): Color {
        return when (category) {
            ServiceCategory.HOSPITAL_HEALTH -> Color(0xFF00796B)
            ServiceCategory.AMBULANCE -> Color(0xFFC62828)
            ServiceCategory.POLICE_LAW -> Color(0xFF1565C0)
            ServiceCategory.FIRE_SERVICE -> Color(0xFFD84315)
            ServiceCategory.BLOOD_DONOR -> Color(0xFFB71C1C)
            ServiceCategory.PHARMACY -> Color(0xFF00838F)
            ServiceCategory.GOVT_OFFICE -> Color(0xFF283593)
            ServiceCategory.ELECTRICITY_UTILITY -> Color(0xFFF57F17)
            ServiceCategory.GAS_UTILITY -> Color(0xFFEF6C00)
            ServiceCategory.EDUCATION -> Color(0xFF4527A0)
            ServiceCategory.TRANSPORT_BUS -> Color(0xFF0277BD)
            ServiceCategory.RAILWAY -> Color(0xFF2E7D32)
            ServiceCategory.POST_COURIER -> Color(0xFF6A1B9A)
            ServiceCategory.DISASTER_RELIEF -> Color(0xFF00695C)
            ServiceCategory.VETERINARY -> Color(0xFF558B2F)
            ServiceCategory.AGRICULTURE_FISHERIES -> Color(0xFF33691E)
            ServiceCategory.TOURISM -> Color(0xFF00897B)
            ServiceCategory.OTHER_ESSENTIAL, ServiceCategory.ALL -> Color(0xFF00695C)
        }
    }
}
