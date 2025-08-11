# CricMania – Cricket Fantasy App

## Introduction
**CricMania** is a feature-rich mobile application designed for cricket enthusiasts to create fantasy teams, join contests, track live scores, and play mini-games for additional rewards. The app ensures secure user access with two-step verification, real-time match data, and a smooth transaction system for deposits, withdrawals, and contest entries. It blends fantasy sports excitement with interactive gaming, delivering a comprehensive cricket experience.

---

## Table of Contents
1. [Features](#features)  
2. [Tech Stack](#tech-stack)  
3. [Installation](#installation)  
4. [Usage](#usage)  
5. [Modules Overview](#modules-overview)  
6. [Configuration](#configuration)  
7. [Screenshots](#screenshots)  
8. [Future Work](#future-work)  
9. [Contributors](#contributors)  
10. [License](#license)  

---

## Features
- **Secure Login & Sign-up** – Two-step verification with Firebase Authentication.
- **Live Match Scores** – Real-time cricket match data, including ball-by-ball updates.
- **Fantasy Contests** – Join various contests, select teams, and compete for prizes.
- **Mini Games** – Tic Tac Toe, Snake & Ladder, and Mines for extra rewards.
- **Player Suggestions** – AI-powered recommendations based on performance stats.
- **Transactions & Wallet** – Add funds, withdraw winnings, and view transaction history.
- **Leaderboard** – Global rankings based on fantasy team performance.

---

## Tech Stack
- **Frontend:** Java (Android), XML layouts  
- **Backend:** Java & Python  
- **Database:** Firebase Firestore  
- **Authentication:** Firebase Auth (Email Verification)  
- **Development Tools:** Android Studio, VS Code, Git  
- **Payment Gateway:** UPI, Wallets, Net Banking, Card Support  

---

## Installation
1. **Clone the repository**  
   ```bash
   git clone https://github.com/<your-username>/CricMania.git
   cd CricMania
   ```

2. **Open in Android Studio**  
   - Import the project folder.
   - Sync Gradle files.

3. **Setup Firebase**  
   - Create a Firebase project.
   - Add `google-services.json` to your app’s `/app` directory.
   - Enable Firebase Authentication and Firestore in the Firebase Console.

4. **Configure Payment Gateway**  
   - Add your payment provider credentials in the configuration file.

5. **Run the App**  
   - Connect an Android device or start an emulator.
   - Click **Run** in Android Studio.

---

## Usage
1. **Sign up** with email verification.  
2. **Create your fantasy team** by selecting 11 real players.  
3. **Join contests** with an entry fee to compete for prizes.  
4. **Track live match scores** while managing your team.  
5. **Play mini-games** to win virtual currency.  
6. **Withdraw earnings** securely to your bank or wallet.

---

## Modules Overview
- **Login & Sign-up Module** – Secure user authentication with profile management.  
- **Live Score Module** – Fetch and display ongoing match details with notifications.  
- **Join Contest Module** – Contest listing, team selection, and preview features.  
- **Mini Games Module** – Reward-based interactive games for single and two players.  
- **Player Suggestion Module** – Performance-based player recommendations.  
- **Payment Module** – Multi-method transactions with detailed history.

---

## Configuration
- **Firebase Auth & Firestore** credentials in `google-services.json`  
- **API Keys** for live scores from cricket data providers  
- **Payment Gateway** environment variables  

---

## Screenshots
*(Include images from your `/screenshots` directory or captured from the app)*  
1. **User Verification**  
2. **Live Match Score**  
3. **Team Selection & Captain/Vice-Captain**  
4. **Contest & Rankings**  
5. **Transaction History**  
6. **Mini Games**  

---

## Future Work
- Expand to other sports like Football and Kabaddi.  
- Advanced analytics for team and player performance on various pitches.  
- “Refer & Earn” program to increase user engagement.  

---

## Contributors
- **Mihir Dhami** – Backend Development  
- **Alish Narsidani** – Frontend Development  
- **Swapnil Verma** – UI/UX & Game Modules  

---

## License
This project is licensed under the MIT License – see the [LICENSE](LICENSE) file for details.
