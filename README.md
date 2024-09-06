# **Fast Cash Portfolio Project**

**Fast Cash** is a cutting-edge digital banking solution designed to simplify and enhance financial management by providing users with seamless and automatic wallet creation for both local and international payments. This project demonstrates my expertise as a Senior Software Engineer 💻, integrating robust backend and frontend technologies to deliver a comprehensive and responsive web application.

## **🔭 Table of Contents**
- [Project Overview](#-project-overview)
- [Demo](#demo)
- [Tech Stack](#-tech-stack)
- [Getting Started](#-getting-started)
- [Usage Instructions](#usage-instructions)
- [API Documentation](#-api-documentation)
- [Features](#features)
- [Testing](#testing)
- [Docker Support](#docker-support)
- [Contributing](#contributing)
- [License](#license)
- [Connect With Me](#-connect-with-me)

## **🌱 Project Overview**
Fast Cash is a next-generation online bank that automates wallet creation and facilitates effortless transactions. Whether handling local or international payments, Fast Cash aims to provide users with a streamlined, secure, and user-friendly experience.

## **Demo**
Check out the live of Fast Cash here:

<p align="center">
   <a href="https://fast-cash.onrender.com/" target="_blank">https://fast-cash.onrender.com/</a>
</p>

<p align="center">
   <img
      width="500"
      height="300"
      alt="fast cash home"
      src="https://github.com/Olayiwola72/my-profile/blob/main/fastcash.png"
   />
</p>

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

## **🔧 Getting Started**
To set up the project locally:

1. **Clone the Repository:**

      ```sh
      git clone https://github.com/Olayiwola72/fastcash-backend
      ```

1. **Submodule Initialization:**

   The frontend code is included as a submodule within the backend repository. Navigate to src/main/resources/client to interact with the front end.

      ```sh 
      git submodule update --init --recursive
      ```

1. **Install Dependencies and Start the Application:**

   The project leverages Vite & Maven to bundle both React and Spring Boot together. Use the provided script to install all dependencies and run the application.

      ```sh
      ./prod.sh
      ```

   The application will be accessible at: [http://localhost:8086/](http://localhost:8086/)

## **Usage Instructions**
Once the application is running, open your browser and navigate to <a href="https://fast-cash.onrender.com/ " target="_blank">https://fast-cash.onrender.com/</a>. Create a user account and start exploring the features of Fast Cash.

## **📝 API Documentation**
For detailed API information and testing, access the Swagger UI:

<p align="center">
   <a href="https://fast-cash.onrender.com/swagger-ui" target="_blank">https://fast-cash.onrender.com/swagger-ui</a>
</p>

## **Features**

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

## **Testing**
To run the tests locally:

- **Backend Tests**

   ```sh
   ./mvnw test
   ```

## **Docker Support**
Fast Cash can be deployed using Docker and Docker Compose, which simplifies the process of setting up and running the application in a containerized environment. Follow these instructions to get the application running using Docker.

<br/>

🛠 Prerequisites
- **Docker: Install Docker**
- **Docker Compose: Install Docker Compose**

<br/>

🚀 Quick Start
1. **Clone the Repository:**

   If you haven't already cloned the repository, do so with:

      ```sh
      git clone https://github.com/Olayiwola72/fastcash-backend

1. **Build and Start the Application::**

   Use Docker Compose to build and start the application. This will build the Docker images and start the containers defined in docker-compose.yml in the root directory.

      ```sh 
      docker-compose up --build
      ```

   **Note:** --build forces Docker Compose to rebuild the images. You can omit this flag if you don't need to rebuild.

1. **📜 Notes:**

   Adjust environment variables and ports as needed for your local development or production setup. The application will be accessible at: [http://localhost:8086/](http://localhost:8086/)

## **Contributing**
Contributions to this project are welcome. Please follow the standard GitHub fork, branch, and pull request workflow. Feel free to raise issues or feature requests to enhance this project.

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

- 💬 If you have any questions or feedback, feel free to reach out! I'm also actively seeking exciting job opportunities that align with my skills and experience, so don't hesitate to contact me if you know of any suitable roles