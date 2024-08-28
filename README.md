# **Fast Cash Portfolio Project**

**Fast Cash** is a cutting-edge digital banking solution designed to simplify and enhance financial management by providing users with seamless and automatic wallet creation for both local and international payments. This project demonstrates my expertise as a Senior Software Engineer 💻, integrating robust backend and frontend technologies to deliver a comprehensive and responsive web application.

## **🔭 Table of Contents**
- [Project Overview](#-project-overview)
- [Tech Stack](#-tech-stack)
- [Getting Started](#getting-started)
- [Usage Instructions](#-usage-instructions)
- [API Documentation](#-api-documentation)
- [Features](#-features)
- [Testing](#-testing)
- [Demo](#-demo)
- [Contributing](#-contributing)
- [License](#license)
- [Connect With Me](#-connect-with-me)

## **🌱 Project Overview**
Fast Cash is a next-generation online bank that automates wallet creation and facilitates effortless transactions. Whether handling local or international payments, Fast Cash aims to provide users with a streamlined, secure, and user-friendly experience.

## **💼 Tech Stack**
### **Front-End:**
- **![](https://img.shields.io/badge/Code-React-informational?style=flat&logo=react&color=61DAFB)**
- **![](https://img.shields.io/badge/Code-Redux-informational?style=flat&logo=Redux&color=764ABC)**
- **![](https://img.shields.io/badge/Code-TypeScript-informational?style=flat&logo=TypeScript&color=F7DF1E)**
- **![](https://img.shields.io/badge/Style-Bootstrap-informational?style=flat&logo=Bootstrap&color=7952B3)**
- **![](https://img.shields.io/badge/Style-CSS3-informational?style=flat&logo=CSS3&color=1572B6)**
- **![](https://img.shields.io/badge/Code-HTML5-informational?style=flat&logo=HTML5&color=E34F26)**
- **![](https://img.shields.io/badge/Tools-Vite-informational?style=flat&logo=Vite&color=F24E1E)**
- **![](https://img.shields.io/badge/Tools-NPM-informational?style=flat&logo=NPM&color=CB3837)**
- **![](https://img.shields.io/badge/Tools-Git-informational?style=flat&logo=Git&color=F05032)**
- **![](https://img.shields.io/badge/Tools-GitHub-informational?style=flat&logo=GitHub&color=181717)**
### **Back-End:**
- **![](https://img.shields.io/badge/Code-Spring_Boot-informational?style=flat&logo=Spring-Boot&color=DB7093)**
- **![](https://img.shields.io/badge/Code-Spring_JPA-informational?style=flat&logo=Spring-JPA&color=CC342D)**
- **![](https://img.shields.io/badge/Code-Spring_Security-informational?style=flat&logo=Spring-Security&color=CC0000)**
- **![](https://img.shields.io/badge/Test-JUnit5-informational?style=flat&logo=JUnit5&color=003B57)**
- **![](https://img.shields.io/badge/Tools-Maven-informational?style=flat&logo=Maven&color=430098)**
- **![](https://img.shields.io/badge/Code-Thymeleaf-informational?style=flat&logo=Thymeleaf&color=E34F26)**

### **Database:**
- **![](https://img.shields.io/badge/Code-MySQL-informational?style=flat&logo=MySQL&color=336791)**

## **Getting Started**
To set up the project locally:

1. **✅ Clone the Repository:**

      ```sh
      git clone https://github.com/Olayiwola72/fastcash-backend

1. **✅ Submodule Initialization:**

   The frontend code is included as a submodule within the backend repository. Navigate to src/main/resources/client to interact with the front end.

      ```sh 
      git submodule update --init --recursive

1. **✅ Install Dependencies and Start the Application:**

   The project leverages Vite & Maven to bundle both React and Spring Boot together. Use the provided script to install all dependencies and run the application.

      ```sh
   ./prod.sh

The application will be accessible at: <a href="http://localhost:8086/" target="_blank">http://localhost:8086/</a>

## **✅ Usage Instructions**
Once the application is running, open your browser and navigate to <a href="http://localhost:8086/" target="_blank">http://localhost:8086/</a>. Create a user account and start exploring the features of Fast Cash.

## **📝 API Documentation**
For detailed API information and testing, access the Swagger UI:

<p align="center">
   <a href="http://localhost:8086/swagger-ui" target="_blank">http://localhost:8086/swagger-ui</a>
</p>

## **✅ Features**

Fast Cash is packed with a variety of features aimed at providing a comprehensive digital banking experience:

- **JWT User Authentication**
- **Sign in with Google (OAUTH)**
- **Transaction History**
- **Wallets**
- **Make Transfers**
- **Recent Transactions**
- **Change Password**
- **Reset Password**
- **Update Profile**
- **Email Notifications**
- **Delete Account**
- **Transaction List PDF and Export Actions**
- **Responsive Web Design**

## **✅ Testing**
Backend tests can be run from the root folder using the command:

<p align="center">
   ./mvnw test
</p>

## **✅ Demo**
Check out the live  of Fast Cash here:

<p align="center">
   <a href="http://fastcash.com/test" target="_blank">http://fastcash.com/test</a>
</p>

<p align="center">
   <img
      width="500"
      height="300"
      alt="fast cash home"
      src="https://private-user-images.githubusercontent.com/40915435/361814001-8fbbc93e-c226-4f9e-a429-aa425fb7e49f.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3MjQ3NjQwMDYsIm5iZiI6MTcyNDc2MzcwNiwicGF0aCI6Ii80MDkxNTQzNS8zNjE4MTQwMDEtOGZiYmM5M2UtYzIyNi00ZjllLWE0MjktYWE0MjVmYjdlNDlmLnBuZz9YLUFtei1BbGdvcml0aG09QVdTNC1ITUFDLVNIQTI1NiZYLUFtei1DcmVkZW50aWFsPUFLSUFWQ09EWUxTQTUzUFFLNFpBJTJGMjAyNDA4MjclMkZ1cy1lYXN0LTElMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjQwODI3VDEzMDE0NlomWC1BbXotRXhwaXJlcz0zMDAmWC1BbXotU2lnbmF0dXJlPTg2ZTNhZGY1NzBkNjQxMWIwYWZjOTMxZDdiOTlmNGU4MzkxZTNiMmVmZmFlNzIyMWRlYjFkY2IzM2ZlZjEyZDQmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0JmFjdG9yX2lkPTAma2V5X2lkPTAmcmVwb19pZD0wIn0.WDYqWKKWe8kgc9C7SpoThCXNAKK3jRNvtqGHtyo9sZc"
   />
</p>

## **✅ Contributing**
Contributions to this project are welcome. Please follow the standard GitHub fork, branch, and pull request workflow. Feel free to raise issues or feature requests to enhance this project.

## **License**
N/A

## **📈 GitHub Stats**

[![Top Langs](https://github-readme-stats.vercel.app/api/top-langs/?username=Olayiwola72&layout=compact)](https://github.com/Olayiwola72)

## **🤝 Connect With Me**

<a href="https://www.linkedin.com/in/olayiwola-akinnagbe-371686172/" target="_blank">
   <img 
      align="left" 
      src="https://github.com/Olayiwola72/my-profile/blob/main/linkedin.png" 
      alt="Olayiwola Akinnagbe | LinkedIn" 
      width="21px"
   />
</a>

<a href="https://twitter.com/OlayiwolaAkinn1" target="_blank">
   <img 
      align="left" 
      src="https://github.com/Olayiwola72/my-profile/blob/main/twitter.png" 
      alt="Olayiwola Akinnagbe | Twitter" 
      width="21px"
   />
</a>

<a href="https://drive.google.com/file/d/10uXqISOnCIMsoLgmxOJor1kdrrflcnAy/view?usp=sharing" target="_blank">
   <img 
      align="left" 
      src="https://github.com/Olayiwola72/my-profile/blob/main/cv.png" 
      alt="Olayiwola Akinnagbe | Resume" 
      width="21px"
   />
</a>

<br>

<br>

- 💬 If you have any question/feedback, please do not hesitate to reach out to me!
