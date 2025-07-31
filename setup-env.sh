#!/bin/bash

# ResumeMatch Environment Setup Script
echo "🔧 Setting up ResumeMatch environment..."

# Check if env.properties already exists
if [ -f "app/src/main/assets/env.properties" ]; then
    echo "⚠️  env.properties already exists!"
    echo "Current content:"
    cat app/src/main/assets/env.properties
    echo ""
    read -p "Do you want to overwrite it? (y/n): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "❌ Setup cancelled"
        exit 1
    fi
fi

# Copy template to env.properties
cp app/src/main/assets/env.properties.template app/src/main/assets/env.properties

echo "✅ Created env.properties from template"
echo ""
echo "📝 Please edit app/src/main/assets/env.properties and add your API keys:"
echo "   - OpenAI API Key: https://platform.openai.com/api-keys"
echo "   - Google Maps API Key: https://console.cloud.google.com/"
echo ""
echo "🔒 The env.properties file is in .gitignore and will not be committed"
echo "🎯 Your API keys will be automatically loaded for development" 