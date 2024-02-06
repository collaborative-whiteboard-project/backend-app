# backend-app
Backend for sketching application on a virtual board that supports group work

Backend application is written in Spring Boot. It also uses PostgresSQL database and Socket.IO library to provide real-time features. Its main function is to enable multiple users to work
simultaneously on a virtual whiteboard. The whiteboard allows for creating shapes (rectangle, circle, line), adding text and free-hand sketching. All changes made by the users working on the board are synchronized to mantain consistency of content of virtual whteboard for all users.
