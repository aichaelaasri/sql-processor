-- Drop many to many relations

DROP TABLE MEDIA_PHYSICALMEDIA;

DROP TABLE MEDIACHARACTER_PLAYEDBY;

DROP TABLE EXISTSINMEDIA_MEDIACHARACTER;


-- Drop normal entities

DROP TABLE BILLING_DETAILS;

DROP TABLE SUBSCRIBER;

DROP TABLE PHYSICALMEDIA;

DROP TABLE MOVIE;

DROP TABLE MEDIACHARACTER;

DROP TABLE LIBRARY;

DROP TABLE ENGAGEMENT;

DROP TABLE PERSON;

DROP TABLE CONTACT;

DROP TABLE BOOK;

DROP TABLE MEDIA;

DROP TABLE TYPES;


-- Drom procedures a functions

DROP PROCEDURE new_person;

DROP PROCEDURE new_person_ret;

DROP FUNCTION an_hour_before;


-- Create normal entities
    
CREATE TABLE MEDIA (
  ID BIGINT IDENTITY NOT NULL PRIMARY KEY,
  TITLE NVARCHAR(100) NOT NULL,
  LASTUPDATED DATETIME,
  LASTUPDATEDBY VARCHAR(50),
  CREATEDDATE DATETIME,
  CREATEDBY VARCHAR(50),
  VERSION BIGINT NOT NULL
);


CREATE TABLE BOOK (
  ISBN VARCHAR(20) NOT NULL,
  MEDIA BIGINT NOT NULL
);


CREATE TABLE CONTACT (
  ID BIGINT IDENTITY NOT NULL PRIMARY KEY,
  ADRESS VARCHAR(100),
  CITY VARCHAR(100),
  ZIP VARCHAR(100),
  STATE VARCHAR(100),
  UUID VARCHAR(36) NOT NULL,
  LASTUPDATED DATETIME,
  LASTUPDATEDBY VARCHAR(50),
  CREATEDDATE DATETIME,
  CREATEDBY VARCHAR(50),
  VERSION BIGINT NOT NULL,
  PERSONNAME_FIRST VARCHAR(100),
  PERSONNAME_LAST VARCHAR(100),
  PHONE_NUMBER VARCHAR(100)
);


CREATE TABLE PERSON (
  ID BIGINT IDENTITY NOT NULL PRIMARY KEY,
  BIRTHDATE DATE NOT NULL,
  LASTUPDATED DATETIME,
  LASTUPDATEDBY VARCHAR(50),
  CREATEDDATE DATETIME,
  CREATEDBY VARCHAR(50),
  VERSION BIGINT NOT NULL,
  CONTACT BIGINT,
  SSN_NUMBER VARCHAR(20) NOT NULL,
  SSN_COUNTRY VARCHAR(100) NOT NULL,
  NAME_FIRST VARCHAR(100) NOT NULL,
  NAME_LAST VARCHAR(100) NOT NULL,
  SEX VARCHAR(100) NOT NULL,
  CLOTHES_SIZE INT
);


CREATE TABLE ENGAGEMENT (
  ID BIGINT IDENTITY NOT NULL PRIMARY KEY,
  ROLE VARCHAR(100) NOT NULL,
  UUID VARCHAR(36) NOT NULL,
  PERSON BIGINT NOT NULL,
  MEDIA BIGINT NOT NULL
);


CREATE TABLE LIBRARY (
  ID BIGINT IDENTITY NOT NULL PRIMARY KEY,
  NAME VARCHAR(100) NOT NULL,
  LASTUPDATED DATETIME,
  LASTUPDATEDBY VARCHAR(50),
  CREATEDDATE DATETIME,
  CREATEDBY VARCHAR(50),
  VERSION BIGINT NOT NULL
);


CREATE TABLE MEDIACHARACTER (
  ID BIGINT IDENTITY NOT NULL PRIMARY KEY,
  NAME VARCHAR(100) NOT NULL,
  UUID VARCHAR(36) NOT NULL
);


CREATE TABLE MOVIE (
  URLIMDB VARCHAR(100) NOT NULL,
  PLAYLENGTH INT NOT NULL,
  CATEGORY VARCHAR(40),
  MEDIA BIGINT NOT NULL
);


CREATE TABLE PHYSICALMEDIA (
  ID BIGINT IDENTITY NOT NULL PRIMARY KEY,
  STATUS VARCHAR(3) NOT NULL,
  LOCATION VARCHAR(100) NOT NULL,
  UUID VARCHAR(36) NOT NULL,
  LASTUPDATED DATETIME,
  LASTUPDATEDBY VARCHAR(50),
  CREATEDDATE DATETIME,
  CREATEDBY VARCHAR(50),
  VERSION BIGINT NOT NULL,
  LIBRARY BIGINT
);


-- Create many to many relations
    
CREATE TABLE EXISTSINMEDIA_MEDIACHARACTER (
  MEDIACHARACTER BIGINT NOT NULL,
  EXISTSINMEDIA BIGINT NOT NULL
);


CREATE TABLE MEDIACHARACTER_PLAYEDBY (
  PLAYEDBY BIGINT NOT NULL,
  MEDIACHARACTER BIGINT NOT NULL
);


CREATE TABLE MEDIA_PHYSICALMEDIA (
  PHYSICALMEDIA BIGINT NOT NULL,
  MEDIA BIGINT NOT NULL
);


-- Primary keys
    
ALTER TABLE EXISTSINMEDIA_MEDIACHARACTER ADD CONSTRAINT PK_EXISTSINMEDIA_MEDIACHARACTER
  PRIMARY KEY (MEDIACHARACTER, EXISTSINMEDIA)
;

ALTER TABLE MEDIACHARACTER_PLAYEDBY ADD CONSTRAINT PK_MEDIACHARACTER_PLAYEDBY
  PRIMARY KEY (PLAYEDBY, MEDIACHARACTER)
;

ALTER TABLE MEDIA_PHYSICALMEDIA ADD CONSTRAINT PK_MEDIA_PHYSICALMEDIA
  PRIMARY KEY (PHYSICALMEDIA, MEDIA)
;


-- Unique constraints

ALTER TABLE BOOK ADD CONSTRAINT UQ_BOOK
  UNIQUE (ISBN)
;

ALTER TABLE CONTACT ADD CONSTRAINT UQ_CONTACT
  UNIQUE (UUID)
;

ALTER TABLE PERSON ADD CONSTRAINT UQ_PERSON
  UNIQUE (SSN_NUMBER, SSN_COUNTRY)
;

ALTER TABLE ENGAGEMENT ADD CONSTRAINT UQ_ENGAGEMENT
  UNIQUE (UUID)
;

ALTER TABLE LIBRARY ADD CONSTRAINT UQ_LIBRARY
  UNIQUE (NAME)
;

ALTER TABLE MEDIACHARACTER ADD CONSTRAINT UQ_MEDIACHARACTER
  UNIQUE (UUID)
;

ALTER TABLE MOVIE ADD CONSTRAINT UQ_MOVIE
  UNIQUE (URLIMDB)
;

ALTER TABLE PHYSICALMEDIA ADD CONSTRAINT UQ_PHYSICALMEDIA
  UNIQUE (UUID)
;


-- Foreign key constraints

ALTER TABLE BOOK ADD CONSTRAINT FK_BOOK_MEDIA
  FOREIGN KEY (MEDIA) REFERENCES MEDIA (ID) ON DELETE CASCADE
;

ALTER TABLE MOVIE ADD CONSTRAINT FK_MOVIE_MEDIA
  FOREIGN KEY (MEDIA) REFERENCES MEDIA (ID) ON DELETE CASCADE
;
  
ALTER TABLE PERSON ADD CONSTRAINT FK_PERSON_CONTACT
  FOREIGN KEY (CONTACT) REFERENCES CONTACT (ID) ON DELETE CASCADE
;
  
ALTER TABLE ENGAGEMENT ADD CONSTRAINT FK_ENGAGEMENT_PERSON
  FOREIGN KEY (PERSON) REFERENCES PERSON (ID) ON DELETE CASCADE
;

ALTER TABLE ENGAGEMENT ADD CONSTRAINT FK_ENGAGEMENT_MEDIA
  FOREIGN KEY (MEDIA) REFERENCES MEDIA (ID) ON DELETE CASCADE
;

ALTER TABLE PHYSICALMEDIA ADD CONSTRAINT FK_PHYSICALMEDIA_LIBRARY
  FOREIGN KEY (LIBRARY) REFERENCES LIBRARY (ID) ON DELETE CASCADE
;

ALTER TABLE EXISTSINMEDIA_MEDIACHARACTER ADD CONSTRAINT FK_EXISTSINMEDIA_MEDIACHARAC53
  FOREIGN KEY (MEDIACHARACTER) REFERENCES MEDIACHARACTER (ID)
;

ALTER TABLE EXISTSINMEDIA_MEDIACHARACTER ADD CONSTRAINT FK_EXISTSINMEDIA_MEDIACHARAC27
  FOREIGN KEY (EXISTSINMEDIA) REFERENCES MEDIA (ID)
;
  
ALTER TABLE MEDIACHARACTER_PLAYEDBY ADD CONSTRAINT FK_MEDIACHARACTER_PLAYEDBY_P03
  FOREIGN KEY (PLAYEDBY) REFERENCES PERSON (ID) ON DELETE CASCADE
;

ALTER TABLE MEDIACHARACTER_PLAYEDBY ADD CONSTRAINT FK_MEDIACHARACTER_PLAYEDBY_M76
  FOREIGN KEY (MEDIACHARACTER) REFERENCES MEDIACHARACTER (ID)
;
  
ALTER TABLE MEDIA_PHYSICALMEDIA ADD CONSTRAINT FK_MEDIA_PHYSICALMEDIA_PHYSI64
  FOREIGN KEY (PHYSICALMEDIA) REFERENCES PHYSICALMEDIA (ID)
;

ALTER TABLE MEDIA_PHYSICALMEDIA ADD CONSTRAINT FK_MEDIA_PHYSICALMEDIA_MEDIA
  FOREIGN KEY (MEDIA) REFERENCES MEDIA (ID)
;


-- types

CREATE TABLE TYPES 
(
  ID BIGINT IDENTITY NOT NULL PRIMARY KEY
, T_INT INT
, T_LONG BIGINT
, T_BYTE TINYINT
, T_SHORT SMALLINT
, T_FLOAT FLOAT
, T_DOUBLE FLOAT
, T_CHAR CHAR(1)
, T_STRING VARCHAR(255)
, T_DATE DATE
, T_TIME DATETIME
, T_DATETIME DATETIME
, T_TIMESTAMP DATETIME
, T_BOOLEAN BIT
, T_BIG_INTEGER BIGINT
, T_BIG_DECIMAL DECIMAL(13,1)
, A_BYTE VARBINARY(30)
, A_TEXT TEXT
, A_CLOB TEXT
, A_BLOB IMAGE
);


-- More inheritance

CREATE TABLE SUBSCRIBER (
  ID BIGINT IDENTITY NOT NULL PRIMARY KEY,
  LASTUPDATED DATETIME,
  LASTUPDATEDBY VARCHAR(50),
  CREATEDDATE DATETIME,
  CREATEDBY VARCHAR(50),
  VERSION BIGINT NOT NULL,
  LIBRARY BIGINT NOT NULL,
  CONTACT BIGINT,
  NAME_FIRST VARCHAR(100) NOT NULL,
  NAME_LAST VARCHAR(100) NOT NULL
);

ALTER TABLE SUBSCRIBER ADD CONSTRAINT FK_SUBSCRIBER_LIBRARY
  FOREIGN KEY (LIBRARY) REFERENCES LIBRARY (ID) ON DELETE CASCADE
;

CREATE TABLE BILLING_DETAILS (
  ID BIGINT IDENTITY NOT NULL PRIMARY KEY,
  LASTUPDATED DATETIME,
  LASTUPDATEDBY VARCHAR(50),
  CREATEDDATE DATETIME,
  CREATEDBY VARCHAR(50),
  VERSION BIGINT NOT NULL,
  SUBSCRIBER BIGINT NOT NULL,
  TYPE VARCHAR(2) NOT NULL,
  CC_NUMBER BIGINT,
  CC_EXP_MONTH SMALLINT,
  CC_EXP_YEAR SMALLINT,
  BA_ACCOUNT_NUMBER VARCHAR(100),
  BA_BANK_NAME VARCHAR(100),
  BA_SWIFT VARCHAR(20)
);

ALTER TABLE BILLING_DETAILS ADD CONSTRAINT FK_BILLING_DETAILS_SUBSCRIBER
  FOREIGN KEY (SUBSCRIBER) REFERENCES SUBSCRIBER (ID) ON DELETE CASCADE
;


-- Procedures
CREATE PROCEDURE new_person(@newid INT OUTPUT, @birthdate DATE, @ssn_number VARCHAR(20), @ssn_country VARCHAR(100), @name_first VARCHAR(100), @name_last VARCHAR(100), @sex VARCHAR(100) OUTPUT) AS
BEGIN
  SET @sex = ISNULL(@sex,'M');

  INSERT INTO PERSON (BIRTHDATE, LASTUPDATED, LASTUPDATEDBY, CREATEDDATE, CREATEDBY, VERSION, CONTACT, SSN_NUMBER, SSN_COUNTRY, NAME_FIRST, NAME_LAST, SEX, CLOTHES_SIZE) 
    VALUES (@birthdate, CURRENT_TIMESTAMP, 'test', NULL, NULL, 1, NULL, @ssn_number, @ssn_country, @name_first, @name_last, @sex, NULL);
  SET @newid = SCOPE_IDENTITY();
END;

CREATE PROCEDURE new_person_ret(@birthdate DATE, @ssn_number VARCHAR(20), @ssn_country VARCHAR(100), @name_first VARCHAR(100), @name_last VARCHAR(100), @sex VARCHAR(100)) AS
BEGIN
  DECLARE @temp_id INT;
  DECLARE @sex1 VARCHAR(100);
  SET @sex1 = ISNULL(@sex,'M');

  INSERT INTO PERSON (BIRTHDATE, LASTUPDATED, LASTUPDATEDBY, CREATEDDATE, CREATEDBY, VERSION, CONTACT, SSN_NUMBER, SSN_COUNTRY, NAME_FIRST, NAME_LAST, SEX, CLOTHES_SIZE) 
    VALUES (@birthdate, CURRENT_TIMESTAMP, 'test', NULL, NULL, 1, NULL, @ssn_number, @ssn_country, @name_first, @name_last, @sex1, NULL);
  SET @temp_id = SCOPE_IDENTITY();
  SELECT * FROM PERSON WHERE ID = @temp_id;
END;

CREATE FUNCTION an_hour_before(@t DATETIME) RETURNS DATETIME AS
BEGIN
  RETURN DATEADD(hour, -1, @t);
END;