# 🌍 Wanderly – Trip Manager App

**Wanderly** is a personal trip management Android app built using Kotlin and Jetpack Compose. Each user can register, create private trips, upload documents and images, view trip details, and easily share trips with friends.

---

## ✨ Features

- 🔐 **User Authentication** – using Firebase Authentication
- 🧳 **Trip Management** – add, view, and delete trips (user-specific)
- 📝 **Trip Notes** – add and edit personal notes per trip
- 📎 **Upload Files & Photos** – store files per trip via Cloudinary
- 🗺️ **View Destination on Map** – using Google Maps SDK
- 🔗 **Share Trips with Friends** – share notes and files via Share Intent
- 👤 **User Profile with Avatar** – update profile photo via Cloudinary
- 🚫 **Per-User Data Isolation** – each user sees only their own trips & files

---

## 🛠️ Technologies Used

- 💻 Kotlin + Jetpack Compose
- ☁️ Firebase (Authentication & Firestore)
- ☁️ Cloudinary – for file/image uploads
- 🗺️ Google Maps SDK for Android
- 📤 Android Share Intent

---

## 🧪 How to Run

1. Open the project in **Android Studio**
2. Ensure your `google-services.json` file is correctly linked to Firebase
3. Add your **Cloudinary credentials** to `CloudinaryUploader.kt` if needed
4. Run on an emulator or a real Android device

---

## 📂 Main Screens

- `AuthScreen.kt` – Login / Register
- `MenuScreen.kt` – Main menu
- `AddTripScreen.kt` – Add a new trip
- `MyTripsScreen.kt` – View your trips
- `TripDetailsScreen.kt` – Trip details + sharing 
- `UploadDocumentScreen.kt` – Upload files/images
- `TripFilesScreen.kt` – View uploaded trip files
- `TripMapScreen.kt` – Show trip destination on map
- `ProfileScreen.kt` – View/update profile photo
- `SelectTripForUploadScreen.kt` – Choose a trip before uploading files

---

## 👩‍💻 Developed By

**Lusia Ruvinski**  
Final project for the Mobile App Development course (Kotlin-based).

---
## 📸 Screenshots

<img src="263cb7a3-6a14-498e-81d4-339d2326c3e2.jpeg" width="300"/>
<img src="47c595a5-59dd-4d82-b12d-47ddb87a2dff2.jpeg" width="300"/>
<img src="4b9cc3a6-d3e6-48f1-8c66-2b1d238f8dd1.jpeg" width="300"/>
<img src="58c7d5df-0f6f-4bec-be1a-fc51adb627e7.jpeg" width="300"/>
<img src="8b5f515e-10f5-4b28-8131-d7f3f0e30dca.jpeg" width="300"/>
<img src="b1feda22-850e-4cd2-819c-a2f3c45810a2.jpeg" width="300"/>
<img src="ba2b5a11-5a23-407e-84d4-7f3da1ed9f31.jpeg" width="300"/>
<img src="dcb22644-0901-4fa4-a23c-d4538e4fe08e.jpeg" width="300"/>
<img src="e8d02939-8879-4cef-9ab5-793c3d2ed478.jpeg" width="300"/>

## 🎬 Demo Video


