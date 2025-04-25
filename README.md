# CivicFix - Civic Complaint Management System

CivicFix is an Android application designed to streamline the process of managing civic complaints. It provides a platform for users to submit, track, and resolve civic issues efficiently.

## Features

- **User Authentication**: Secure login and registration system
- **Complaint Management**:
  - Submit complaints with descriptions and images
  - Track complaint status (pending/resolved)
  - View detailed complaint information
- **User Profile**:
  - Personal information management
  - Track total, pending, and resolved complaints
  - Building and flat information
- **Admin Dashboard**:
  - User-wise complaint monitoring
  - Service management system
  - Complaint resolution tracking

## Technology Stack

- **Platform**: Android (Kotlin)
- **Backend**: Firebase Realtime Database
- **Storage**: Firebase Storage (for complaint images)
- **UI**: Native Android XML layouts with ViewBinding

## Setup Instructions

1. Clone the repository
2. Open the project in Android Studio
3. Configure Firebase:
   - Add your `google-services.json` to the app directory
   - Enable Authentication, Realtime Database, and Storage in Firebase Console
4. Build and run the application

## Project Structure

- `user_complaint_detailed_view.kt`: Handles detailed view of complaints
- `user_profile_view.kt`: Manages user profile information
- `services.kt`: Handles service management functionality
- `admin_view_complaint_user_wise.kt`: Admin dashboard for user-wise complaint monitoring

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.




