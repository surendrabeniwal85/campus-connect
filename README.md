# Placement Assistant — AI-Powered Campus Placement Platform

Placement Assistant is a web-based platform designed to simplify campus placements by connecting students, faculty, and placement teams through role-based portals, placement opportunities, eligibility tracking, and application management.

### Key Features
- Separate portals for students, teachers, HODs, and placement administrators.
- Secure authentication, account registration, and role-based access control.
- Placement drives, student eligibility checks, and application tracking.
- Student academic profiles, placement statistics, and recruitment management.
- Responsive, interactive UI with dark and light themes.

### Tech Stack
**Frontend:** React, TypeScript, Vite, Bootstrap 5  
**Backend:** Java 25, Spring Boot 4.1.1, Spring Security, Spring Data JPA  
**Database:** MySQL 8  
**Build & Deployment:** Maven, Docker, Render

### Live Demo
**Frontend:** https://placement-assistant-frontend.onrender.com

The live frontend is deployed on Render. Backend APIs and database-dependent features require the backend service and database to be configured and running.

### Local Setup
1. Clone the repository and install the frontend dependencies using `npm install` inside the `frontend` directory.
2. Start the frontend using `npm run dev`.
3. Configure the backend database connection and required environment variables in your local environment.
4. Start the Java backend using the Maven Wrapper from the `backend` directory.

### Deployment
The frontend can be deployed on Render as a Static Site using `npm install && npm run build` and the publish directory `dist` with the root directory set to `frontend`.

Deploy the backend separately as a Docker-based web service. Configure the database connection and security secrets through environment variables, and ensure the frontend API configuration points to the deployed backend.

### Project Status
The project is under active development. Features and integrations are being tested as development progresses.

**Repository:** https://github.com/surendrabeniwal85/placement-assistant
