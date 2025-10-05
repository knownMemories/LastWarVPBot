# 🤖 Last War: VP Bot

An automated bot for the strategy game **Last War: Survival Game** that applies for and performs **Vice President (VP)** duties within an alliance — fully automated using the **LDPlayer Android emulator**.

> ⚠️ **Note**: This project is **under active development**. Core navigation is implemented, but the actual VP logic (application, member management, etc.) is not yet complete.

---

## 🎯 Current Functionality

The bot currently performs the following steps:

1. **Launches the LDPlayer emulator** (instance index 0) with the Last War app.
2. **Detects the current screen state** using template matching (OpenCV).
3. **Closes obstructive pop-ups** (e.g., close or back buttons).
4. **Navigates** from the main screen → profile screen → Capitol/ministries screen.
5. **Prepares the environment** for future VP automation tasks.

> 🔜 **Planned Features**:
> - Automatic application for Vice President position
> - Alliance member scanning and automated kicking based on activity/rules
> - Stats tracking and recovery logic

---

## 🛠️ Requirements

Before running the bot, ensure the following setup:

### 1. **LDPlayer Emulator**
- Install [LDPlayer 9](https://www.ldplayer.net/) (or compatible version).
- Create at least one emulator instance (index `0` will be used by default).

### 2. **Last War: Survival Game**
- Install **Last War: Survival Game** (Google Play version) inside LDPlayer.
- Log in to an account that meets these criteria:
    - **Player level ≥ 16** (required to apply for VP roles).
    - Member of an alliance where VP positions are available.

### 3. **System**
- **Windows** (required for LDPlayer and Windows API access via JNA).
- Java 17+ (the project uses Java 17 features like records).
- Maven (for building).

---

