# 🚀 CodeXShip Online Judge

**CodeXShip** is a modern **Online Judge platform** built with **Spring Boot**, **React.js**, and **relational databases (MySQL & PostgreSQL)**.  
It allows users to **register, log in (including Google OAuth 2.0), submit code**, and receive **real-time verdicts**.  
Designed for **competitive programming, coding contests, and practice platforms**, CodeXShip combines **modern authentication, gamification, and scalable architecture**.

---

## 📌 Features

### 🔐 User Authentication & Authorization
- Register, Login with **JWT-based authentication**  
- **Google OAuth 2.0** login support  
- Role-based access control with three roles:  
  - **NORMAL_USER** – Solve problems and participate in contests  
  - **PROBLEM_EDITOR** – Create and manage coding problems  
  - **ADMIN** – Full system access for management and contests  

### 📝 Problem Management
- Add, edit, and manage coding problems  
- Tagging, difficulty levels, and categories  
- Supports contest problem sets  

### 💻 Code Submission & Evaluation
- Submit code in **multiple programming languages**  
- **Secure Docker-based sandbox** for safe execution  
- **Live Testcase Judging Feedback**  See each testcase result update in real-time as code runs, powered by WebSocket streaming. 

### 🎯 Gamification & Rewards
- **Daily Coin Reward System**: Users earn coins once per day for logging in  
- Earn **coins** by solving problems or participating in contests  
- Redeem coins for **rewards** like T-shirts, hoodies, bags, money, and other goodies 🎁  
- Motivates daily engagement and competitive participation  

### 📊 Contest & Ranking System
- Create and manage coding contests  
- Real-time **leaderboards and user standings**  
- Scalable for multiple contests simultaneously
- Under Development

### 🌐 Global Rankings Page
- **All users can see their rank** on a dedicated page  
- Displays **overall standings, total coins, and problem-solving stats**  
- Allows users to **compare their performance with others globally**

### 🖥️ Modern Frontend
- **React.js + Tailwind CSS** for a responsive, clean UI  
- Smooth navigation with **dark mode** and mobile-friendly design  

### ⚡ Scalable Backend
- **Spring Boot** backend with layered architecture  
- Clean separation of **services, repositories, and controllers**  
- RESTful APIs for frontend-backend communication  
- Integrated **gamification logic** with daily coins and rewards  
- Secure authentication including **Google OAuth 2.0 + JWT**  

---

## 🏗️ Tech Stack
| Layer | Technology |
|-------|------------|
| Frontend | React.js, Tailwind CSS |
| Backend | Spring Boot (Java) |
| Database | MySQL & PostgreSQL (relational DB support) |
| Code Execution | Docker-based sandbox |
| Authentication | JWT + Google OAuth 2.0 |
| Gamification | Daily coin rewards & redemption system |
| Build Tools | Maven, npm |

---

### ✨ Why CodeXShip?
CodeXShip combines **real-time code evaluation**, **daily login rewards**, and **role-based gamification** to create an engaging platform for programmers of all levels.  
Whether you are a **NORMAL_USER**, a **PROBLEM_EDITOR**, or an **ADMIN**, CodeXShip motivates you to **log in daily, solve problems, create contests, and manage the system efficiently**.  

---

