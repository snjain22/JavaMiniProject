# Introduction
This Java program presents a versatile password management application 
designed to securely store and efficiently manage website-specific passwords. 
Developed using JavaFX, this project offers a straightforward and intuitive 
user experience while emphasizing security and reliability.

It's salient features include
- Database Connection and Setup
  - Establishes a robust connection to a MySQL database, serving as a 
secure repository for website information, including usernames and 
encrypted passwords.

- User Authentication
 -  Upon successful authentication, users access the main application window constructed with 
JavaFX.

- Flexible Data Management
  - Users can add, modify and delete the entered data with ease.

- Robust Security Measures
  -  It utilizes AES encryption with GCM mode, 
ensuring maximum protection for stored passwords. Individual .key files for 
encryption keys enhance security. The application simplifies user interactions 
with an intuitive JavaFX-based interface, allowing effortless additions, 
updates, and deletions of website details. It prioritizes error handling, 
providing informative alerts for users. This password manager strikes a 
balance between security and user convenience, making it an exceptional 
choice for reliable and accessible website password management.

# How to Implement the Project?

1. Install JavaFX, Java, MySQL in your system along with a code editor of choice.
2. Create a project in any code editor of your choice and save the 3 java files.
3. Create the MySQL Database using the following query

  ```
    CREATE TABLE password (
    Serial_Number int NOT NULL AUTO_INCREMENT,
    Website varchar(200) NOT NULL,
    UserName varchar(50) NOT NULL,
    HashedPass varchar(500) NOT NULL,
    Alias varchar(100) NOT NULL,
    DateTime datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (Serial_Number)
);
```
4. In the `SQLLinker.java` file, change the user, url and password variables as it is for your database.
5. Run `App.java`
