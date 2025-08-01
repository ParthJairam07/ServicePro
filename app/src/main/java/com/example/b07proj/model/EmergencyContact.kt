package com.example.b07proj.model

import com.google.firebase.firestore.DocumentId
// data class for emergency contacts
data class EmergencyContact(
    // firestore gives id a "DocumentId" for each contact
    @DocumentId val id: String = "",
    // basic information for contacts
    var contactName: String = "",
    var contactPhoneNumber: String = "",
    var contactRelation: String = "",
    var contactEmail: String = ""
)