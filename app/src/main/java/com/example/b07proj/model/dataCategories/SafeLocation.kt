package com.example.b07proj.model.dataCategories

import com.google.firebase.firestore.DocumentId

// Stores the data for a safe location in firebase
data class SafeLocation(
    // firestore gives id a "DocumentId" for each contact
    @DocumentId val id: String = "",
    // basic information for safe locations
    var safeLocationAddress: String = "",
    var safeLocationName: String = "",
    var safeLocationDescription: String = "",
)