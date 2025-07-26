package com.example.b07proj.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class QuizData(
    val questions: Questions,
    val tips: Tips
)

@Serializable
data class Questions(
    val Warmup: Map<String, Question>,
    val BranchQuestions: Map<String, Map<String, Question>>,
    @SerialName("Follow-up")
    val FollowUp: Map<String, Question> = emptyMap()
)

@Serializable
data class Question(
    val id: Int,
    val question: String,
    val options: List<String>? = null,
    val type: String,
    val variable: String? = null,
    @SerialName("follow-up")
    val followUp: Map<String, FollowUp>? = null
)

@Serializable
data class FollowUp(
    val sub_question: String,
    val input_type: String,
    val variable: String? = null
)

@Serializable
data class Tips(
    val questionATips: Map<String, Tip> = emptyMap(),
    val questionBTips: Map<String, Map<String, Tip>> = emptyMap(),
    val questionCTips: Map<String, Tip> = emptyMap()
)

@Serializable
data class Tip(
    val id: Int? = null,
    val tip: String? = null,

    @SerialName("Still in a relationship")
    val StillInARelationship: String? = null,

    @SerialName("Planning to leave")
    val PlanningToLeave: String? = null,

    @SerialName("Post-separation")
    val PostSeparation: String? = null,

    @SerialName("Family/Roommates")
    val FamilyRoommates: String? = null,
    val Alone: String? = null,
    val Partner: String? = null,
    // For questionBTips and questionCTips with Yes/No
    val Yes: String? = null,
    val No: String? = null
)