Mobile Application for Efficient Travel Planning
This repository contains the code for my Bachelor's thesis project, a mobile app designed to help users efficiently plan and manage their travel experiences.

Features

User Authentication

Search for Accommodations

Fetches real-time data from the Amadeus for Developers API for accommodations, prices, and availability.

Favorite and Reserve Accommodations

Users can mark accommodations as favorites or reserve them (fictionally).

Interactive Map Integration

Displays locations on Google Maps, including pins for accommodations.

Image Fetching

Retrieves high-quality images from the Google Places API.

Accommodation Recommendations

Integrates AI-generated recommendations based on user preferences using OpenAI embeddings and ObjectBox on-device vector search.

Preferences Management

Stores user preferences using Preferences DataStore for a seamless experience.

Technologies Used
Frontend: Kotlin with Jetpack Compose
Backend: Ktor framework 
Database: ObjectBox with vector search capabilities
APIs:
Amadeus API
Google Maps API
Google Places API
AI Integration: OpenAI embeddings with Python via Chaquopy
Authentication: OAuth2 / JSON Web Tokens (JWT)


