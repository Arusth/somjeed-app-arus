# Scripts สำหรับตรวจสอบ Code Quality และ Unit Test

โฟลเดอร์นี้ประกอบด้วยสคริปต์สำหรับตรวจสอบคุณภาพโค้ดและการทดสอบของโปรเจค ChatBot Application

## 📋 รายการสคริปต์

### 1. `quality-check.sh` - การตรวจสอบแบบครบถ้วน
สคริปต์หลักสำหรับตรวจสอบคุณภาพโค้ดและการทดสอบของทั้งโปรเจค

**การใช้งาน:**
```bash
# ให้สิทธิ์ execute (ครั้งแรกเท่านั้น)
chmod +x scripts/quality-check.sh

# รันการตรวจสอบ
./scripts/quality-check.sh
```

**สิ่งที่ตรวจสอบ:**
- ✅ Backend: Compilation, Unit Tests, Integration Tests, Coverage Report
- ✅ Frontend: TypeScript Compilation, ESLint, Unit Tests, Coverage Report, Build
- ✅ Test Coverage Requirements ตามมาตรฐานโปรเจค
- ✅ Code Quality Standards

### 2. `backend-check.sh` - ตรวจสอบ Backend เท่านั้น
สำหรับนักพัฒนา Backend ที่ต้องการตรวจสอบเฉพาะส่วน Backend

**การใช้งาน:**
```bash
chmod +x scripts/backend-check.sh
./scripts/backend-check.sh
```

**สิ่งที่ตรวจสอบ:**
- ✅ Maven Compilation
- ✅ Unit Tests (JUnit 5 + Mockito)
- ✅ Integration Tests
- ✅ JaCoCo Coverage Report

### 3. `frontend-check.sh` - ตรวจสอบ Frontend เท่านั้น
สำหรับนักพัฒนา Frontend ที่ต้องการตรวจสอบเฉพาะส่วน Frontend

**การใช้งาน:**
```bash
chmod +x scripts/frontend-check.sh
./scripts/frontend-check.sh
```

**สิ่งที่ตรวจสอบ:**
- ✅ TypeScript Compilation
- ✅ ESLint Code Quality
- ✅ Unit Tests (Jest + React Testing Library)
- ✅ Coverage Report
- ✅ Production Build Test

### 4. `quick-test.sh` - การทดสอบแบบเร็ว
สำหรับการทดสอบระหว่างการพัฒนา (ไม่รวม coverage และ build)

**การใช้งาน:**
```bash
chmod +x scripts/quick-test.sh
./scripts/quick-test.sh
```

**สิ่งที่ตรวจสอบ:**
- ⚡ Backend Unit Tests เท่านั้น
- ⚡ Frontend Unit Tests เท่านั้น

## 🎯 มาตรฐานการทดสอบ

### Backend Coverage Requirements:
- **Service classes**: 90% minimum
- **Controller classes**: 85% minimum
- **Repository classes**: 80% minimum
- **Utility classes**: 95% minimum

### Frontend Coverage Requirements:
- **Components**: 85% minimum
- **Custom hooks**: 90% minimum
- **Utility functions**: 95% minimum
- **API services**: 80% minimum

## 📊 รายงานผล Coverage

### Backend Coverage Report:
```
backend/target/site/jacoco/index.html
```

### Frontend Coverage Report:
```
frontend/coverage/lcov-report/index.html
```

## 🚀 การใช้งานใน Development Workflow

### 1. ระหว่างการพัฒนา (Development)
```bash
# ทดสอบแบบเร็วระหว่างเขียนโค้ด
./scripts/quick-test.sh
```

### 2. ก่อน Commit
```bash
# ตรวจสอบครบถ้วนก่อน commit
./scripts/quality-check.sh
```

### 3. ตรวจสอบเฉพาะส่วน
```bash
# Backend เท่านั้น
./scripts/backend-check.sh

# Frontend เท่านั้น
./scripts/frontend-check.sh
```

## 🔧 การแก้ไขปัญหาที่พบบ่อย

### Backend Issues:
```bash
# ถ้า Maven tests fail
cd backend
mvn clean test

# ถ้า compilation error
mvn clean compile
```

### Frontend Issues:
```bash
# ถ้า TypeScript errors
cd frontend
npx tsc --noEmit

# ถ้า ESLint errors
npm run lint --fix

# ถ้า tests fail
npm test
```

## 📝 หมายเหตุ

- สคริปต์เหล่านี้ใช้ `set -e` จึงจะหยุดทำงานทันทีเมื่อเจอ error
- ใช้สีในการแสดงผลเพื่อให้อ่านง่าย
- รองรับการรันจาก root directory ของโปรเจค
- ตรวจสอบตามมาตรฐานที่กำหนดใน `.windsurf/rules/unittest.md`

## 🎨 Exit Codes

- **0**: ผ่านการตรวจสอบทั้งหมด
- **1**: มีการตรวจสอบที่ไม่ผ่าน

สามารถใช้ exit codes เหล่านี้ใน CI/CD pipeline ได้
