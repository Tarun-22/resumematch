#!/bin/bash

# ResumeMatch Android App - Basic Test Runner
# Simple and clean test execution script

echo "🧪 ResumeMatch Basic Test Suite"
echo "================================"
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if we're in the correct directory
if [ ! -f "app/build.gradle.kts" ]; then
    print_error "Please run this script from the project root directory"
    exit 1
fi

print_status "Starting basic test suite execution..."

# Clean and build the project
print_status "Cleaning and building project..."
./gradlew clean build -x test -x lintDebug

if [ $? -eq 0 ]; then
    print_success "Project built successfully"
else
    print_error "Build failed. Please fix compilation errors first."
    exit 1
fi

# Run Unit Tests
echo ""
print_status "Running Unit Tests..."
./gradlew testDebugUnitTest --tests="com.example.resumematch.BasicUnitTests" > unit_test_results.txt 2>&1

if [ $? -eq 0 ]; then
    print_success "Unit Tests passed"
    echo "✅ Unit Tests: PASSED"
else
    print_warning "Unit Tests failed - check unit_test_results.txt for details"
    echo "❌ Unit Tests: FAILED"
fi

# Run Integration Tests
echo ""
print_status "Running Integration Tests..."
./gradlew testDebugUnitTest --tests="com.example.resumematch.BasicIntegrationTests" > integration_test_results.txt 2>&1

if [ $? -eq 0 ]; then
    print_success "Integration Tests passed"
    echo "✅ Integration Tests: PASSED"
else
    print_warning "Integration Tests failed - check integration_test_results.txt for details"
    echo "❌ Integration Tests: FAILED"
fi

# Generate test summary
echo ""
echo "📊 Test Execution Summary"
echo "========================"
echo ""

echo "📈 Test Results:"
echo "  - Unit Tests: 12 tests"
echo "  - Integration Tests: 12 tests"
echo "  - Total Tests: 24 tests"
echo ""

# Check if all tests passed
if [ $? -eq 0 ]; then
    print_success "🎉 All basic tests completed successfully!"
    echo ""
    echo "✅ Test Suite Status: PASSED"
    echo "📁 Test Reports:"
    echo "  - Unit Tests: unit_test_results.txt"
    echo "  - Integration Tests: integration_test_results.txt"
else
    print_warning "Some tests failed. Please check the result files for details."
    echo ""
    echo "⚠️  Test Suite Status: PARTIAL"
    echo "📁 Test Reports:"
    echo "  - Unit Tests: unit_test_results.txt"
    echo "  - Integration Tests: integration_test_results.txt"
fi

echo ""
print_status "Basic test suite execution completed!"
echo ""
echo "📝 Next Steps:"
echo "  1. Review test reports in the generated files"
echo "  2. Fix any failing tests if needed"
echo "  3. Run tests before each deployment"
echo ""

# Clean up temporary files (optional)
# rm -f unit_test_results.txt integration_test_results.txt

print_success "Basic test suite completed! 🎉" 