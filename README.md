# Mobile Application for Efficient Travel Planning
 This repository contains the code for my Bachelor's thesis project, a mobile app designed to help users efficiently plan and manage their travel experiences.

 ## Features

- **User Authentication** - Uses *Firebase Authentication* for secure login and user registration with OAuth support.
- **Search for Accommodations** - Fetches real-time data from the *Amadeus for Developers API* for accommodations, prices, and availability.
- **Favorite and Reserve Accommodations** - Users can mark accommodations as favorites or reserve them(without actually making a request to the *Amadeus Booking API*)
- **Interactive Map Integration** - Displays locations on *Google Maps*, including pins for accommodations.
- **Image Fetching** - Retrieves high-quality images using the *Google Places API*.
- **Payment System** - Implements a complete payment solution using Stripe SDK, allowing users to securely process accommodation bookings with multiple payment methods.
- **Currency Conversion** - Features dynamic currency conversion functionality powered by the Exchange Rates API (RapidAPI), enabling users to view and compare prices in their preferred currency with real-time exchange rates.
- **Accommodation Recommendations** - Integrates AI-generated recommendations based on user preferences using *OpenAI* embeddings
- **Preferences Management** - Stores user preferences using *Preferences DataStore* for a seamless experience.


## Installation

- Clone the repository

```bash
  git clone https://github.com/AndreiHammer/bachelor-thesis-travel-companion
  cd bachelor-thesis-travel-companion

```
- Open Android Studio and choose the *Open an Existing Project* option. Navigate to the cloned repository and select it.
- Set up API Keys
    
    Create a file named *local.properties* in the root directory of the project(if it doesn't already exist) and add the following keys: 

```properties
  # Amadeus API Key
AMADEUS_API_KEY=your_amadeus_api_key

  # Amadeus API Secret
AMADEUS_API_SECRET=your_amadeus_api_secret

# Google Maps and Places API Key
GOOGLE_API_KEY=your_google_api_key

# OpenAI API Key
OPENAI_API_KEY=your_openai_api_key

# Rapid API Key
RAPID_API_KEY = your_rapid_api_key

#Stripe Publishable Key
STRIPE_PUBLISHABLE_KEY = your_stripe_publishable_key

#Stripe Secret Key
STRIPE_SECRET_KEY = your_stripe_secret_key

```
- Ensure you have Firebase configured in your project:
  - Follow the official **Firebase Android Setup**.
  - Download the *google-services.json* file from your Firebase project and place it in the projects's app directory.

## Tech Stack

**Frontend:** Kotlin with Jetpack Compose UI toolkit

**Backend/ Network Connectivity:** Ktor framework

**Database:** Cloud Firestore

**APIs:** Amadeus API; Google Maps SDK; Google Places API; Currency Conversion And Exchange Rates from RapidAPI

**Payment:** Stripe

**AI Integration:** OpenAI embeddings with Python via *Chaquopy*

**Authentication**: OAuth via Firebase


## License

This project is licensed under the [MIT License](https://choosealicense.com/licenses/mit/).

