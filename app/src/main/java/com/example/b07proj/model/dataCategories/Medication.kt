package com.example.b07proj.model.dataCategories;

import com.google.firebase.firestore.DocumentId

// Stores the data for a medication in firebase
data class Medication (
    // firestore gives id a "DocumentId" for each contact
    @DocumentId val id: String = "",
    // basic information for medication
    var medicationName: String = "",
    var medicationDosage: String = "",
    var medicationExpiry: String = "",
)
