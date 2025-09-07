#!/bin/bash
# Start script for Render.com deployment

echo "🚀 Starting Patient Appointment System on Render.com..."

# Set JVM options for Render.com free tier (512MB memory limit)
export JAVA_OPTS="-Xms256m -Xmx512m -Dspring.profiles.active=production"

# Start the application
echo "▶️ Starting application with optimized JVM settings..."
java $JAVA_OPTS -jar target/patient-appointment-system-1.0.0.jar --server.port=${PORT:-8080}

echo "✅ Application started successfully!"
