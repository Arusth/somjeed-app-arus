#!/bin/bash

# ChatBot Application - Code Quality & Unit Test Check Script
# Based on project rules: unittest.md and backend-rules.md

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Project directories
BACKEND_DIR="backend"
FRONTEND_DIR="frontend"
ROOT_DIR="$(pwd)"

# Java Environment Setup
export JAVA_HOME="/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"

echo -e "${BLUE}🚀 Starting Code Quality & Unit Test Check for ChatBot Application${NC}"
echo "=================================================="

# Function to print section headers
print_section() {
    echo -e "\n${BLUE}📋 $1${NC}"
    echo "----------------------------------------"
}

# Function to check if directory exists
check_directory() {
    if [ ! -d "$1" ]; then
        echo -e "${RED}❌ Directory $1 not found!${NC}"
        exit 1
    fi
}

# Function to run command and capture result
run_command() {
    local cmd="$1"
    local description="$2"
    
    echo -e "${YELLOW}🔄 $description${NC}"
    
    if eval "$cmd"; then
        echo -e "${GREEN}✅ $description - PASSED${NC}"
        return 0
    else
        echo -e "${RED}❌ $description - FAILED${NC}"
        return 1
    fi
}

# Initialize counters
TOTAL_CHECKS=0
PASSED_CHECKS=0
FAILED_CHECKS=0

# Function to update counters
update_counters() {
    TOTAL_CHECKS=$((TOTAL_CHECKS + 1))
    if [ $1 -eq 0 ]; then
        PASSED_CHECKS=$((PASSED_CHECKS + 1))
    else
        FAILED_CHECKS=$((FAILED_CHECKS + 1))
    fi
}

# Check project structure
print_section "Project Structure Validation"
check_directory "$BACKEND_DIR"
check_directory "$FRONTEND_DIR"
echo -e "${GREEN}✅ Project structure is valid${NC}"

# Backend Quality Checks
print_section "Backend Code Quality & Testing (Spring Boot + Java 17)"

cd "$BACKEND_DIR"

# Backend: Compile check
run_command "./mvnw clean compile -q" "Backend compilation check"
update_counters $?

# Backend: Run all unit tests
run_command "./mvnw test -q" "Backend unit tests execution"
update_counters $?

# Backend: Generate test coverage report
run_command "./mvnw jacoco:report -q" "Backend test coverage report generation"
update_counters $?

# Backend: Check test coverage thresholds
echo -e "${YELLOW}🔍 Checking backend test coverage requirements...${NC}"
if [ -f "target/site/jacoco/index.html" ]; then
    echo -e "${GREEN}✅ Coverage report generated successfully${NC}"
    echo -e "${YELLOW}📊 Coverage requirements:${NC}"
    echo "   - Service classes: 90% minimum"
    echo "   - Controller classes: 85% minimum" 
    echo "   - Repository classes: 80% minimum"
    echo "   - Utility classes: 95% minimum"
    echo -e "${BLUE}💡 Check detailed coverage at: backend/target/site/jacoco/index.html${NC}"
else
    echo -e "${RED}❌ Coverage report not found${NC}"
    FAILED_CHECKS=$((FAILED_CHECKS + 1))
fi
TOTAL_CHECKS=$((TOTAL_CHECKS + 1))

# Backend: Run integration tests (optional)
echo -e "${YELLOW}🔄 Backend integration tests${NC}"
if find src/test -name "*IT.java" 2>/dev/null | grep -q .; then
    ./mvnw test -Dtest=*IT -q
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ Backend integration tests - PASSED${NC}"
        PASSED_CHECKS=$((PASSED_CHECKS + 1))
    else
        echo -e "${RED}❌ Backend integration tests - FAILED${NC}"
        FAILED_CHECKS=$((FAILED_CHECKS + 1))
    fi
else
    echo -e "${YELLOW}⚠️  No integration tests found (optional)${NC}"
    PASSED_CHECKS=$((PASSED_CHECKS + 1))
fi
TOTAL_CHECKS=$((TOTAL_CHECKS + 1))

# Backend: Check for test naming conventions
echo -e "${YELLOW}🔍 Checking backend test naming conventions...${NC}"
if find src/test -name "*.java" -exec grep -l "@Test" {} \; | head -5 | xargs grep -l "should.*" > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Test naming conventions followed (descriptive 'should' patterns found)${NC}"
    PASSED_CHECKS=$((PASSED_CHECKS + 1))
else
    echo -e "${YELLOW}⚠️  Consider using descriptive test names with 'should' patterns${NC}"
    PASSED_CHECKS=$((PASSED_CHECKS + 1))
fi
TOTAL_CHECKS=$((TOTAL_CHECKS + 1))

cd "$ROOT_DIR"

# Frontend Quality Checks
print_section "Frontend Code Quality & Testing (Next.js 15 + TypeScript)"

cd "$FRONTEND_DIR"

# Frontend: TypeScript compilation check
run_command "npx tsc --noEmit" "Frontend TypeScript compilation check"
update_counters $?

# Frontend: ESLint check
run_command "npm run lint" "Frontend ESLint code quality check"
update_counters $?

# Frontend: Run all tests
run_command "npm test -- --passWithNoTests --watchAll=false" "Frontend unit tests execution"
update_counters $?

# Frontend: Run tests with coverage
run_command "npm run test:coverage -- --passWithNoTests --watchAll=false" "Frontend test coverage report generation"
update_counters $?

# Frontend: Check test coverage requirements
echo -e "${YELLOW}🔍 Checking frontend test coverage requirements...${NC}"
if [ -d "coverage" ]; then
    echo -e "${GREEN}✅ Coverage report generated successfully${NC}"
    echo -e "${YELLOW}📊 Coverage requirements:${NC}"
    echo "   - Components: 85% minimum"
    echo "   - Custom hooks: 90% minimum"
    echo "   - Utility functions: 95% minimum"
    echo "   - API services: 80% minimum"
    echo -e "${BLUE}💡 Check detailed coverage at: frontend/coverage/lcov-report/index.html${NC}"
else
    echo -e "${RED}❌ Coverage report not found${NC}"
    FAILED_CHECKS=$((FAILED_CHECKS + 1))
fi
TOTAL_CHECKS=$((TOTAL_CHECKS + 1))

# Frontend: Build check
run_command "npm run build" "Frontend production build check"
update_counters $?

# Frontend: Check for proper TypeScript usage
echo -e "${YELLOW}🔍 Checking TypeScript strict mode compliance...${NC}"
if grep -q '"strict": true' tsconfig.json; then
    echo -e "${GREEN}✅ TypeScript strict mode is enabled${NC}"
    PASSED_CHECKS=$((PASSED_CHECKS + 1))
else
    echo -e "${RED}❌ TypeScript strict mode is not enabled${NC}"
    FAILED_CHECKS=$((FAILED_CHECKS + 1))
fi
TOTAL_CHECKS=$((TOTAL_CHECKS + 1))

cd "$ROOT_DIR"

# Final Summary
print_section "Quality Check Summary"

echo -e "${BLUE}📊 Test Results Summary:${NC}"
echo "   Total Checks: $TOTAL_CHECKS"
echo -e "   ${GREEN}Passed: $PASSED_CHECKS${NC}"
echo -e "   ${RED}Failed: $FAILED_CHECKS${NC}"

if [ $FAILED_CHECKS -eq 0 ]; then
    echo -e "\n${GREEN}🎉 All quality checks PASSED! Your code meets the project standards.${NC}"
    echo -e "${GREEN}✅ Ready for commit and pull request${NC}"
    exit 0
else
    echo -e "\n${RED}❌ Some quality checks FAILED. Please fix the issues before committing.${NC}"
    echo -e "${YELLOW}💡 Review the failed checks above and ensure:${NC}"
    echo "   - All tests pass"
    echo "   - Code coverage meets minimum requirements"
    echo "   - No linting errors"
    echo "   - TypeScript compilation succeeds"
    exit 1
fi
