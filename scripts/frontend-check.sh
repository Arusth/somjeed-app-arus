#!/bin/bash

# Frontend Quality & Unit Test Check Script
# Next.js 15 + TypeScript

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}🚀 Frontend Code Quality & Unit Test Check${NC}"
echo "============================================="

cd frontend

# TypeScript compilation check
echo -e "${YELLOW}🔄 Checking TypeScript compilation...${NC}"
npx tsc --noEmit
echo -e "${GREEN}✅ TypeScript compilation successful${NC}"

# ESLint check
echo -e "${YELLOW}🔄 Running ESLint code quality check...${NC}"
npm run lint
echo -e "${GREEN}✅ ESLint check passed${NC}"

# Run unit tests
echo -e "${YELLOW}🔄 Running unit tests...${NC}"
npm test -- --passWithNoTests --watchAll=false
echo -e "${GREEN}✅ Unit tests completed${NC}"

# Generate coverage report
echo -e "${YELLOW}🔄 Generating test coverage report...${NC}"
npm run test:coverage -- --passWithNoTests --watchAll=false
echo -e "${GREEN}✅ Coverage report generated${NC}"

# Build check
echo -e "${YELLOW}🔄 Testing production build...${NC}"
npm run build
echo -e "${GREEN}✅ Production build successful${NC}"

# Display coverage requirements
echo -e "\n${BLUE}📊 Coverage Requirements:${NC}"
echo "   - Components: 85% minimum"
echo "   - Custom hooks: 90% minimum"
echo "   - Utility functions: 95% minimum"
echo "   - API services: 80% minimum"

if [ -d "coverage" ]; then
    echo -e "\n${GREEN}✅ Coverage report available at: frontend/coverage/lcov-report/index.html${NC}"
fi

# Check TypeScript strict mode
if grep -q '"strict": true' tsconfig.json; then
    echo -e "${GREEN}✅ TypeScript strict mode is enabled${NC}"
else
    echo -e "${RED}❌ TypeScript strict mode is not enabled${NC}"
fi

echo -e "\n${GREEN}🎉 Frontend quality check completed successfully!${NC}"
