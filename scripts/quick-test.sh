#!/bin/bash

# Quick Test Script - Fast unit tests only
# For rapid development feedback

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}⚡ Quick Test Run - ChatBot Application${NC}"
echo "========================================"

# Backend quick tests
echo -e "\n${BLUE}📋 Backend Quick Tests${NC}"
echo "----------------------"
cd backend
echo -e "${YELLOW}🔄 Running backend unit tests...${NC}"
mvn test -q
echo -e "${GREEN}✅ Backend tests passed${NC}"
cd ..

# Frontend quick tests
echo -e "\n${BLUE}📋 Frontend Quick Tests${NC}"
echo "-----------------------"
cd frontend
echo -e "${YELLOW}🔄 Running frontend unit tests...${NC}"
npm test -- --passWithNoTests --watchAll=false --silent
echo -e "${GREEN}✅ Frontend tests passed${NC}"
cd ..

echo -e "\n${GREEN}🎉 All quick tests passed! Ready to continue development.${NC}"
