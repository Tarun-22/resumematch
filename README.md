# ResumeMatch - AI-Powered Resume Analysis App

A comprehensive Android application for employers to scan, analyze, and score resumes using AI technology.

## 🚀 Features

### Core Functionality
- **AI Resume Analysis** - Uses OpenAI GPT-4 for intelligent resume parsing
- **Google Maps Integration** - Location-based distance calculation
- **OCR Text Recognition** - Extract text from resume images
- **Smart Scoring System** - Multi-category scoring with distance calculation
- **Store Profile Management** - Configure store details with Google Places
- **Recent Resumes View** - Browse and analyze all scanned resumes

### Technical Features
- **Room Database** - Local data persistence
- **AsyncTask Operations** - Background processing
- **Material Design 3** - Modern UI/UX
- **Multi-language Support** - English (US/UK)
- **Photo Zoom** - Enhanced image viewing
- **Secure API Key Management** - Environment-based configuration

## 📋 Requirements

### API Keys Required
1. **OpenAI API Key** - For AI resume analysis
   - Get from: https://platform.openai.com/api-keys
   - Required for: GPT-4 resume analysis and scoring

2. **Google Maps API Key** - For location services
   - Get from: https://console.cloud.google.com/
   - Required for: Address autocomplete, distance calculation

## 🛠️ Setup Instructions

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/resumematch.git
cd resumematch
```

### 2. Configure API Keys

#### Option A: Environment File (Recommended for Development)
1. **Run the setup script:**
   ```bash
   ./setup-env.sh
   ```

2. **Edit the environment file:**
   ```bash
   # Edit app/src/main/assets/env.properties
   OPENAI_API_KEY=your-actual-openai-key-here
   GOOGLE_MAPS_API_KEY=your-actual-google-maps-key-here
   DEV_MODE=true
   ```

3. **The app will automatically load your API keys from the environment file**

#### Option B: Through App Settings
1. Build and install the app
2. Go to **Profile & Settings** → **Settings**
3. Enter your API keys:
   - OpenAI API Key
   - Google Maps API Key
4. Keys are stored securely on your device

#### Option C: Direct Configuration
1. Open `app/src/main/java/com/example/resumematch/Config.java`
2. Replace the default values:
   ```java
   private static final String DEFAULT_OPENAI_API_KEY = "your-openai-key-here";
   private static final String DEFAULT_GOOGLE_MAPS_API_KEY = "your-google-maps-key-here";
   ```

### 3. Build and Run
```bash
./gradlew assembleDebug
```

## 🎯 Usage Guide

### Initial Setup
1. **Configure Store Profile**
   - Go to Profile & Settings → Edit Profile
   - Enter store details with Google Places autocomplete
   - Use "Current Location" for automatic address detection

2. **Configure API Keys**
   - Use environment file for development (recommended)
   - Or use app settings for production deployment

### Main Workflow
1. **Create Jobs**
   - Use "Create New Job" from main screen
   - Choose template or create custom job
   - Jobs are stored locally

2. **Scan Resumes**
   - Select "Scan New Resume"
   - Choose job from list
   - Take photo or upload image
   - AI analyzes and scores automatically

3. **View Results**
   - See detailed analysis with category scores
   - View distance calculation and recommendations
   - Access all resumes from "Recent Resumes"

## 🔧 Technical Architecture

### Database Schema
- **JobEntity** - Job postings with descriptions
- **ResumeEntity** - Scanned resumes with analysis data
- **StoreProfile** - Store information for distance calculation

### Key Components
- **GPTApiService** - OpenAI API integration
- **GoogleMapsDistanceCalculator** - Distance calculation
- **DataRepository** - Database operations
- **Config** - API key management with environment support

### Security Features
- API keys stored in environment files (development)
- Keys masked in UI for security
- No hardcoded secrets in source code
- Environment-based configuration
- SharedPreferences for user configuration

## 📱 Screenshots

### Main Features
- Home screen with quick actions
- Job creation and management
- Resume scanning with OCR
- AI analysis results
- Store profile configuration

## 🚨 Important Notes

### API Key Security
- **Never commit API keys** to version control
- Use environment files for development
- Use app settings for production deployment
- Keys are stored locally on device
- Consider using environment variables for production

### Environment File Setup
- **Development**: Use `app/src/main/assets/env.properties`
- **Template**: `app/src/main/assets/env.properties.template`
- **Setup Script**: `./setup-env.sh`
- **Security**: Environment files are in `.gitignore`

### Permissions Required
- **Camera** - For taking resume photos
- **Storage** - For accessing uploaded images
- **Location** - For current location detection
- **Internet** - For API calls

## 🐛 Troubleshooting

### Common Issues
1. **API Key Errors**
   - Verify keys are correctly entered in environment file
   - Check internet connection
   - Ensure API quotas are not exceeded

2. **Environment File Issues**
   - Run `./setup-env.sh` to create environment file
   - Check that `env.properties` exists in `app/src/main/assets/`
   - Verify API keys are properly formatted

3. **Location Services**
   - Grant location permissions
   - Enable GPS for accurate location

4. **OCR Issues**
   - Ensure good lighting for photos
   - Use clear, high-resolution images
   - Check camera permissions

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## 📞 Support

For issues and questions:
- Create an issue on GitHub
- Check the troubleshooting section
- Review the setup instructions

---

**Note**: This app requires valid API keys to function properly. Make sure to configure them before testing the AI features. 