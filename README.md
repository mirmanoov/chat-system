# Chat system

### Overview
The Distributed Chat System is a collaborative chat application developed using the Akka actor model and Scala programming language. The system allows users to engage in real-time conversations, create groups for collaborative discussions, and experience a user-friendly interface powered by ScalaFX.

### Features

- **Scalable:** Built on Akka's actor model, the system is designed to scale horizontally, handling an increasing number of users. 
- **User Interface:** A user-friendly interface created using ScalaFX and FXML for a seamless user experience.
- **Group Messaging:** Users can create and join groups for collaborative discussions.

### Prerequisites

Ensure you have the following software installed:

- [Java Development Kit (JDK)](https://adoptium.net/)
- [Scala](https://www.scala-lang.org/) (Version 2.12.16)
- [SBT (Scala Build Tool)](https://www.scala-sbt.org/) for managing the project build


### Installation

1. Clone the repository:

    ```bash
    git clone https://github.com/mirmanoov/distributed-chat-system.git
    ```

2. Navigate to the project directory:

    ```bash
    cd distributed-chat-system
    ```

3. Compile the project:

    ```bash
    sbt compile
    ```

4. Run the application:

    ```bash
    sbt run
    ```

### Usage

1. Launch the application by following the installation instructions.
2. Enter your name to join the chat.
3. Explore features such as group creation, messaging, and real-time updates.




### How to run locally
#### Run a server
To run server, please set like this. Change the application.conf inside resource directory. <br />
Canonical host: This please set to your local IPV4 address (not public IP)  
Canonical port: Try use 25520  
Bind host: Use the same local IPV4 address  
Bind port: 25520  
 
##### YOU ARE REQUIRED TO CHANGE APPLICATION.CONF
#### Run a client
To run client change application.conf in resource folder  
Canonical host: This please set to your local IPV4 address (not public IP)  
Canonical port: 0  
Bind host: "" (Use empty string)  
Bind port: 0  
