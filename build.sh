#!/bin/bash
# Render.com build script for Patient Appointment System

echo "🚀 Starting build process for Render.com deployment..."

# Clean and build the project
echo "📦 Building Maven project..."
./mvnw clean package -DskipTests

# Create data directory for H2 database
echo "📁 Creating data directory..."
mkdir -p data

echo "✅ Build completed successfully!"
