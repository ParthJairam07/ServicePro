# Safety planning Android app

Along with working with the [National Domestic Violence Hotline](https://www.thehotline.org/) organization, we developed a mobile app to help individuals navigate abusive relationships by 
providing personalized guidance, secure information storage, and access to local support resources. 
The app is expected to offer flexible planning, privacy features, 
and regular reminders to ensure users can manage and update their safety plans as their situations change.


### Requirements
- Ensure google-services.json is in the app folder
- AGP version 8.11.1
- All dependencies are installed from the build.gradle.kts
- A clean pull from the github will install all that is required( will remove google-services.json after 1 month before repo goes public)


### Tech stack:
- Kotlin
- Jetpack compose
- Firebase auth, firestore to store user information, and firebase cloud storage to store document upload
- Andriod Keystore to hold the pin from the user securely
- Gradle for dependencies
- Mockito and JUnit for testing
  
### We implemented the following features:
- Login and Signup page using Firebase
- Tailored questionnaire based on users relationship status
- A list of tips based on questionnaire given
- Storage of Emergency Info
  - Upload important documents or photos
  - Create a list of emergency contacts, medications, and any safe locations
- Direct Links to local support services
- Reminders & Plan Review Notifications
- Emergency Exit Button

### Sample Folder structure
```
.
├── build.gradle.kts
├── google-services.json
├── proguard-rules.pro
└── src
    ├── main
    │   ├── AndroidManifest.xml
    │   ├── java
    │   │   └── com
    │   │       └── example
    │   │           └── b07proj
    │   │               ├── MainActivity.kt
    │   │               ├── MyApplication.kt
    │   │               ├── model
    │   │               │   ├── DirectLinksModel.kt
    │   │               │   ├── HandleAuth.kt
    │   │               │   ├── IAuthService.kt
    │   │               │   ├── PinManager.kt
    │   │               │   ├── QuizData.kt
    │   │               │   ├── QuizDataSource.kt
    │   │               │   ├── QuizSaveData.kt
    │   │               │   └── dataCategories
    │   │               │       ├── DocumentSaveData.kt
    │   │               │       ├── EmergencyContact.kt
    │   │               │       ├── Medication.kt
    │   │               │       └── SafeLocation.kt
    │   │               ├── notifs
    │   │               │   ├── Notification.kt
    │   │               │   ├── NotificationReciever.kt
    │   │               │   └── PeriodicReminder.kt
    │   │               ├── presenter
    │   │               │   ├── AuthPresenter.kt
    │   │               │   ├── DirectLinksPresenter.kt
    │   │               │   ├── DocumentPresenter.kt
    │   │               │   ├── QuizPresenter.kt
    │   │               │   └── dataItems
    │   │               │       ├── AddDataItemContract.kt
    │   │               │       ├── AddDataItemPresenter.kt
    │   │               │       ├── ViewDataItemContract.kt
    │   │               │       └── ViewDataItemPresenter.kt
    │   │               ├── ui
    │   │               │   └── theme
    │   │               │       ├── Color.kt
    │   │               │       ├── Theme.kt
    │   │               │       └── Type.kt
    │   │               └── view
    │   │                   ├── AddContactsView.kt
    │   │                   ├── AddDocumentsPage.kt
    │   │                   ├── AddMedicationPage.kt
    │   │                   ├── AddSafeLocationsPage.kt
    │   │                   ├── Common.kt
    │   │                   ├── CreatePin.kt
    │   │                   ├── DirectLinks.kt
    │   │                   ├── DocumentsView.kt
    │   │                   ├── EditQuizAnswers.kt
    │   │                   ├── EmailLogin.kt
    │   │                   ├── EmergencyContactsView.kt
    │   │                   ├── HomePage.kt
    │   │                   ├── LandingPage.kt
    │   │                   ├── LoggedInTopBar.kt
    │   │                   ├── LoginPage.kt
    │   │                   ├── MedicationsView.kt
    │   │                   ├── PinPage.kt
    │   │                   ├── QuizComposables.kt
    │   │                   ├── SafeLocationsView.kt
    │   │                   ├── SafetyPlanQuizPage1.kt
    │   │                   ├── SafetyPlanQuizPage2.kt
    │   │                   ├── SafetyPlanQuizPage3.kt
    │   │                   ├── SettingsPage.kt
    │   │                   ├── SignUpPage.kt
    │   │                   ├── SignUpView.kt
    │   │                   ├── Storage.kt
    │   │                   └── TipsPage.kt
    │   └── res
    │       ├── drawable
    │       │   ├── appimage.png
    │       │   ├── baseline_mail_24.xml
    │       │   ├── drive_folder_upload.png
    │       │   ├── ic_google_logo.xml
    │       │   ├── ic_launcher_background.xml
    │       │   ├── ic_launcher_foreground.xml
    │       │   ├── optionpageimage.png
    │       │   ├── passwordicon.png
    │       │   ├── pincode.png
    │       │   ├── relationshipssimple.png
    │       │   ├── sendhorizontal.png
    │       │   ├── sendorizontal.png
    │       │   ├── templogo.png
    │       │   └── whitearrowgoback.png
    │       ├── font
    │       │   ├── afacad.ttf
    │       │   ├── afacad_bold.ttf
    │       │   ├── afacad_regular.ttf
    │       │   └── font.xml
    │       ├── layout
    │       │   └── landing_activity.xml
    │       ├── raw
    │       │   ├── answers.json
    │       │   ├── headings.json
    │       │   ├── questions.json
    │       │   └── services.json
    │       ├── values
    │       │   ├── colors.xml
    │       │   ├── strings.xml
    │       │   └── themes.xml
    │       └── xml
    │           ├── backup_rules.xml
    │           └── data_extraction_rules.xml
    └── test
        └── java
            └── com
                └── example
                    └── b07proj
                        ├── AddDataItemPresenterTest.kt
                        ├── AuthPresenterTest.kt
                        ├── DirectLinksPresenterTest.kt
                        ├── DocumentPresenterTest.kt
                        ├── ExampleUnitTest.kt
                        ├── QuizPresenterTest.kt
                        └── ViewDataItemPresenterTest.kt
```
### Here are some snapshots

<img width="202" height="454" alt="image" src="https://github.com/user-attachments/assets/eafeb40e-dce2-4fe4-a01b-944758d3f4b5" />
<img width="202" height="454" alt="image" src="https://github.com/user-attachments/assets/c2912154-3084-4697-b628-46dffd3f3faf" />
<br/>
<img width="202" height="454" alt="image" src="https://github.com/user-attachments/assets/9998be17-5366-489c-836f-e267d2648228" />
<img width="202" height="454" alt="image" src="https://github.com/user-attachments/assets/cc66fb9e-d0aa-4580-b162-4471fb0de8a1" />



This app was for a CSCBO7, the people worked on this are Parth Rupesh Jairam, Abinash Nagendran, Akshayan Prabaharan, Ayyash Anhardeen, Krish Patel.
