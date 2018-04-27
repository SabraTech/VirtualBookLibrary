CREATE SCHEMA Library;
CREATE TABLE IF NOT EXISTS Library.User (
    ID varchar(20)  UNIQUE PRIMARY KEY,
    pass varchar(20) NOT NULL,
    First_Name varchar(20) NOT NULL,
    Last_Name varchar(20) NOT NULL,
    Email varchar(20) NOT NULL
);

# insert into Library.User value ("asdasd","asdasd","asdasdas","asdasdasd","asdasdas");
# select * from Library.User where User.id = "asdasd";