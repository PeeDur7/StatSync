# StatSync

A comprehensive sports statistics and news web application designed for NBA and NFL enthusiasts, fantasy players, and sports bettors.

## 📋 Description

StatSync is a streamlined web application that provides real-time access to NBA and NFL player statistics and news. Built with simplicity and accessibility in mind, StatSync aggregates data from ESPN's API to deliver up-to-date information that refreshes daily. Whether you're tracking your favorite players, researching for fantasy sports, or analyzing stats for betting purposes, StatSync makes it easy to find the information you need in one centralized platform.

## ✨ Features

- **Player Search** - Quickly search for NBA and NFL players across the league
- **Favorite Players** - Save and track your favorite players for quick access
- **Advanced Filtering** - Filter players by position and team
- **Game Logs** - View detailed game-by-game performance statistics
- **Season Stats** - Access current and previous season statistics with league rankings
- **Live News Feed** - Stay updated with the latest player and team news
- **Secure Authentication** - JWT-based user authentication for personalized experience

## 🛠️ Tech Stack

**Frontend:**
- React.js
- React Router
- Lucide React (icons)

**Backend:**
- Java Spring Boot
- Maven

**Database:**
- MongoDB

**External APIs:**
- ESPN API (NBA & NFL data)

## 📦 Prerequisites

Before running this project, ensure you have the following installed:

- **Node.js** (v14 or higher)
- **npm** (comes with Node.js)
- **Java JDK** (version 11 or higher)
- **Maven** (for Spring Boot backend)
- **MongoDB** (local installation or cloud instance)

## 🚀 Installation

### Backend Setup

1. Clone the repository
```bash
git clone https://github.com/PeeDur7/statsync.git
cd statsync
```

2. Navigate to the backend directory
```bash
cd backend
```

3. Install dependencies using Maven
```bash
mvn clean install
```

4. Configure environment variables
Create an `application.properties` file in `src/main/resources/`:
```properties
spring.data.mongodb.uri=your_mongodb_connection_string
jwt.secret=your_jwt_secret_key
espn.api.key=your_espn_api_key
```

5. Run the Spring Boot application
```bash
mvn spring-boot:run
```

The backend server will start on `http://localhost:8080`

### Frontend Setup

1. Navigate to the frontend directory
```bash
cd frontend
```

2. Install dependencies
```bash
npm install
```

3. Start the development server
```bash
npm start
```

The frontend will start on `http://localhost:3000`

## 🔑 API Keys

This project requires access to ESPN's API. You'll need to:
1. Obtain ESPN API credentials
2. Add them to your backend environment variables

## 🎯 Future Features

- **AI Performance Predictions** - Implement machine learning models to analyze player performance and predict outcomes for upcoming games
- **Advanced Analytics Dashboard** - Add visualizations and trend analysis
- **Mobile App** - Develop iOS and Android applications

## 🤝 Contributing

Contributions, issues, and feature requests are welcome! Feel free to check the issues page.

## 📝 License

This project is open source and available under the [MIT License](LICENSE).

## 👤 Author

**Peter Tran**
- Email: petertrann24@gmail.com
- GitHub: [@PeeDur7](https://github.com/PeeDur7)

## 🙏 Acknowledgments

- ESPN for providing the sports data API
---
