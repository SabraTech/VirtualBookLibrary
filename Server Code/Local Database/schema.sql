CREATE SCHEMA Library ;
#Drop table Library.User;
CREATE TABLE IF NOT EXISTS Library.User (
    ID varchar(20)   PRIMARY KEY,
    pass varchar(20) NOT NULL,
    First_Name varchar(20) NOT NULL,
    Last_Name varchar(20) NOT NULL,
    Email varchar(20) NOT NULL
);
#Drop TABLE Library.Favourites;

CREATE TABLE IF NOT EXISTS Library.Favourites (
    User_ID varchar(20)   references User.ID,
    Book_ID varchar(20) ,
    Book_Title varchar(100),
	primary key (User_ID, Book_ID)
);

#Drop TABLE Library.History;


CREATE TABLE IF NOT EXISTS Library.History (
    User_ID varchar(20)   references User.ID,
    Book_ID varchar(20) ,
    Book_Title varchar(100),
	primary key (User_ID, Book_ID)
);

DROP TRIGGER IF EXISTS Library.user_deleted_delete_favourites;
CREATE TRIGGER Library.user_deleted_delete_favourites after delete on Library.User
FOR EACH ROW
DELETE FROM Library.Favourites
    WHERE Library.Favourites.User_ID=old.ID;

DROP TRIGGER IF EXISTS Library.user_deleted_delete_history;
CREATE TRIGGER Library.user_deleted_delete_history after delete on Library.User
FOR EACH ROW
DELETE FROM Library.History
    WHERE Library.History.User_ID = old.ID;

insert into Library.User value ("asdasd","asdasd","asdasdas","asdasdasd","asdasdas");
select * from Library.User;
insert into Library.Favourites value ("asdasd","asdasd","title");
select * from Library.Favourites;
delete from Library.User where Library.User.id = "asdasd";
delete from Library.Favourites where Favourites.User_ID = "asdasd";
select * from Library.History;
insert into Library.History value("matefff", "gzYfBQAAQBAJ", "Inside HBO's Game of Thrones");
