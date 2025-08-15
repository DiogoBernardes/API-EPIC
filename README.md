# EPIC-API
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=JSON%20web%20tokens&logoColor=white)
![Node.js](https://img.shields.io/badge/Node%20js-339933?style=for-the-badge&logo=nodedotjs&logoColor=white)
![Flutter](https://img.shields.io/badge/Flutter-02569B?style=for-the-badge&logo=flutter&logoColor=white)
![React](https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)
![ChatGPT](https://img.shields.io/badge/ChatGPT-74aa9c?style=for-the-badge&logo=openai&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=for-the-badge&logo=github-actions&logoColor=white)
![JUnit 5](https://img.shields.io/badge/Junit5-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![Jest](https://img.shields.io/badge/Jest-C21325?style=for-the-badge&logo=jest&logoColor=white)

---
## Description
**EPIC** is an application that aims to integrate **financial management** and **personal organization** into a single platform.
With features such as income and expense tracking, scheduling important events, OCR invoice recognition, and intelligent recommendations via AI, the goal is to automate repetitive tasks and provide useful insights to improve users' financial health.

---

## Key Features
- User management with JWT authentication.
- Automatic recording and categorization of financial transactions.
- Event scheduling with personalized notifications.
- Invoice upload with automatic data extraction via OCR.
- Financial dashboard with graphs and outlier detection.
- PDF report generation with personalized analysis.
- Intelligent savings and spending optimization suggestions via AI.
- Cryptocurrency and ETF market overview with the option to set up notifications and reminders.

---
## Architecture
The system consists of independent microservices communicating via REST APIs, managed by a central API Gateway.

- **API Gateway:** Single entry point for the frontend, handles authentication, authorization, and routing.
- **Microservices:**
  - Finance Service (financial management)
  - OCR Service (invoice processing)
  - AI Service (insights and suggestions)
  - Report Service (reports and dashboards)
  - Planner Service (personal organization and notifications)

---

## Technology Stack
- **Backend:** Spring Boot, Hibernate, PostgreSQL, JWT
- **Frontend:** Flutter / React (if the web part moves forward)
- **OCR:** Tesseract.js (Node.js)
- **AI:** OpenAI 
- **Reports:** Flying Saucer (PDF)
- **Testing:** JUnit, Mockito, Jest
- **CI/CD:** GitHub Actions

---


### **Thank you for visiting the EPIC project!**

For questions, suggestions, or contributions, please contact the owner.


[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/diogo-bernardes/)