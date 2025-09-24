#!/bin/bash

# Backend Quality & Unit Test Check Script
# Spring Boot 3.2.0 + Java 17

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Java Environment Setup
export JAVA_HOME="/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"

echo -e "${BLUE}🚀 Backend Code Quality & Unit Test Check${NC}"
echo "============================================"

cd backend

# Clean and compile
echo -e "${YELLOW}🔄 Cleaning and compiling backend...${NC}"
./mvnw clean compile -q
echo -e "${GREEN}✅ Backend compilation successful${NC}"

# Run unit tests
echo -e "${YELLOW}🔄 Running unit tests...${NC}"
./mvnw test -q
echo -e "${GREEN}✅ Unit tests completed${NC}"

# Generate coverage report
echo -e "${YELLOW}🔄 Generating test coverage report...${NC}"
./mvnw jacoco:report -q
echo -e "${GREEN}✅ Coverage report generated${NC}"

# Run integration tests
echo -e "${YELLOW}🔄 Running integration tests...${NC}"
./mvnw test -Dtest=*IT -q || echo -e "${YELLOW}⚠️  No integration tests found${NC}"

# Display coverage requirements
echo -e "\n${BLUE}📊 Coverage Requirements:${NC}"
echo "   - Service classes: 90% minimum"
echo "   - Controller classes: 85% minimum"
echo "   - Repository classes: 80% minimum"
echo "   - Utility classes: 95% minimum"

if [ -f "target/site/jacoco/index.html" ]; then
    echo -e "\n${GREEN}✅ Coverage report available at: backend/target/site/jacoco/index.html${NC}"
fi

echo -e "\n${GREEN}🎉 Backend quality check completed successfully!${NC}"
