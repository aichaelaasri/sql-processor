-- Create normal entities
    
CREATE TABLE MEDIA (
  ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  TITLE VARCHAR(100) NOT NULL,
  CREATEDDATE TIMESTAMP,
  CREATEDBY VARCHAR(50),
  LASTUPDATED TIMESTAMP,
  LASTUPDATEDBY VARCHAR(50),
  VERSION BIGINT NOT NULL
);


CREATE TABLE BOOK (
  ISBN VARCHAR(20) NOT NULL,
  MEDIA BIGINT NOT NULL
);


CREATE TABLE CONTACT (
  ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  ADRESS VARCHAR(100),
  CITY VARCHAR(100),
  ZIP VARCHAR(100),
  STATE VARCHAR(100),
  UUID VARCHAR(36) NOT NULL,
  CREATEDDATE TIMESTAMP,
  CREATEDBY VARCHAR(50),
  LASTUPDATED TIMESTAMP,
  LASTUPDATEDBY VARCHAR(50),
  VERSION BIGINT NOT NULL,
  PERSONNAME_FIRST VARCHAR(100),
  PERSONNAME_LAST VARCHAR(100),
  PHONE_NUMBER VARCHAR(100)
);


CREATE TABLE PERSON (
  ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  BIRTHDATE DATE NOT NULL,
  CREATEDDATE TIMESTAMP,
  CREATEDBY VARCHAR(50),
  LASTUPDATED TIMESTAMP,
  LASTUPDATEDBY VARCHAR(50),
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
  ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  ROLE VARCHAR(100) NOT NULL,
  UUID VARCHAR(36) NOT NULL,
  PERSON BIGINT NOT NULL,
  MEDIA BIGINT NOT NULL
);


CREATE TABLE LIBRARY (
  ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  NAME VARCHAR(100) NOT NULL,
  CREATEDDATE TIMESTAMP,
  CREATEDBY VARCHAR(50),
  LASTUPDATED TIMESTAMP,
  LASTUPDATEDBY VARCHAR(50),
  VERSION BIGINT NOT NULL
);


CREATE TABLE MEDIACHARACTER (
  ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
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
  ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  STATUS VARCHAR(3) NOT NULL,
  LOCATION VARCHAR(100) NOT NULL,
  UUID VARCHAR(36) NOT NULL,
  CREATEDDATE TIMESTAMP,
  CREATEDBY VARCHAR(50),
  LASTUPDATED TIMESTAMP,
  LASTUPDATEDBY VARCHAR(50),
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


-- Unique constraints

ALTER TABLE BOOK
	ADD CONSTRAINT UQ_BOOK UNIQUE (ISBN)
;

ALTER TABLE CONTACT
    ADD CONSTRAINT UQ_CONTACT UNIQUE (UUID)
;

ALTER TABLE PERSON
	ADD CONSTRAINT UQ_PERSON UNIQUE (SSN_NUMBER, SSN_COUNTRY)
;

ALTER TABLE ENGAGEMENT
    ADD CONSTRAINT UQ_ENGAGEMENT UNIQUE (UUID)
;

ALTER TABLE LIBRARY
	ADD CONSTRAINT UQ_LIBRARY UNIQUE (NAME)
;

ALTER TABLE MEDIACHARACTER
    ADD CONSTRAINT UQ_MEDIACHARACTER UNIQUE (UUID)
;

ALTER TABLE MOVIE
	ADD CONSTRAINT UQ_MOVIE UNIQUE (URLIMDB)
;

ALTER TABLE PHYSICALMEDIA
    ADD CONSTRAINT UQ_PHYSICALMEDIA UNIQUE (UUID)
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

-- Create pk sequence
    
CREATE SEQUENCE SQLPROC_SEQUENCE START WITH 100 INCREMENT BY 1;

-- types

CREATE TABLE TYPES 
(
  ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL
, T_INT INT 
, T_LONG BIGINT 
, T_BYTE TINYINT 
, T_SHORT SMALLINT 
, T_FLOAT FLOAT 
, T_DOUBLE DOUBLE 
, T_CHAR CHAR(1) 
, T_STRING VARCHAR(255) 
, T_DATE DATE 
, T_TIME TIME 
, T_DATETIME DATETIME 
, T_TIMESTAMP TIMESTAMP 
, T_BOOLEAN BOOLEAN 
, T_BIG_INTEGER DECIMAL 
, T_BIG_DECIMAL DECIMAL(10,1) 
, A_BYTE LONGVARBINARY 
, A_TEXT LONGVARCHAR 
, A_CLOB CLOB 
, A_BLOB BLOB 
);

-- more joins

CREATE TABLE SUBSCRIBER (
  ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  CREATEDDATE TIMESTAMP,
  CREATEDBY VARCHAR(50),
  LASTUPDATED TIMESTAMP,
  LASTUPDATEDBY VARCHAR(50),
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
  ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  CREATEDDATE TIMESTAMP,
  CREATEDBY VARCHAR(50),
  LASTUPDATED TIMESTAMP,
  LASTUPDATEDBY VARCHAR(50),
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
