DROP SEQUENCE SIMPLE_SEQUENCE IF EXISTS CASCADE;
DROP TABLE CONTACT IF EXISTS CASCADE;
DROP TABLE PERSON IF EXISTS CASCADE;
DROP PROCEDURE NEW_PERSON IF EXISTS CASCADE;
DROP PROCEDURE NEW_PERSON_RET IF EXISTS CASCADE;
DROP FUNCTION AN_HOUR_BEFORE IF EXISTS CASCADE;
DROP TABLE DIGIDATA IF EXISTS CASCADE;

CREATE SEQUENCE SIMPLE_SEQUENCE AS BIGINT START WITH 100 INCREMENT BY 1;

CREATE TABLE PERSON
(
  ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL
, FIRST_NAME VARCHAR(100) NOT NULL 
, LAST_NAME VARCHAR(100) NOT NULL 
, DATE_OF_BIRTH DATE
, GENDER VARCHAR(1) NOT NULL
, SSN VARCHAR(100) 
);

COMMENT ON TABLE PERSON IS 'The personal information';
COMMENT ON COLUMN PERSON.ID IS 'The technical primary key';
COMMENT ON COLUMN PERSON.FIRST_NAME IS 'The first name';
COMMENT ON COLUMN PERSON.LAST_NAME IS 'The surname';
COMMENT ON COLUMN PERSON.DATE_OF_BIRTH IS 'The date of birth';
COMMENT ON COLUMN PERSON.GENDER IS 'The gender: M=male, F=female, 0=other';
COMMENT ON COLUMN PERSON.SSN IS 'The social security number';

ALTER TABLE PERSON ADD CONSTRAINT PK_PERSON
	PRIMARY KEY (ID)
;

ALTER TABLE PERSON ADD CONSTRAINT CHECK_PERSON_GENDER
    CHECK (GENDER IN ('M', 'F', '0'))
;

CREATE  INDEX IX_PERSON_LAST_NAME ON PERSON (LAST_NAME);

CREATE TABLE CONTACT
(
  ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL
, PERSON_ID BIGINT NOT NULL 
, TYPE INT NOT NULL 
, ADDRESS VARCHAR(100) NOT NULL 
, PHONE_NUMBER VARCHAR(100)
, X_NOTE VARCHAR(100)
);

COMMENT ON TABLE CONTACT IS 'The contact information';
COMMENT ON COLUMN CONTACT.ID IS 'The technical primary key';
COMMENT ON COLUMN CONTACT.PERSON_ID IS 'The owner of this contact';
COMMENT ON COLUMN CONTACT.TYPE IS 'The contact type: 0=home, 1=business, 2=abroad';
COMMENT ON COLUMN CONTACT.ADDRESS IS 'The address: city, street...';
COMMENT ON COLUMN CONTACT.PHONE_NUMBER IS 'The phone number';
COMMENT ON COLUMN CONTACT.X_NOTE IS 'Any notes and remarks';

ALTER TABLE CONTACT ADD CONSTRAINT PK_CONTACT
	PRIMARY KEY (ID)
;

ALTER TABLE CONTACT ADD CONSTRAINT FK_CONTACT_PERSON
	FOREIGN KEY (PERSON_ID) REFERENCES PERSON (ID) ON DELETE CASCADE
;

ALTER TABLE CONTACT ADD CONSTRAINT CHECK_CONTACT_TYPE
    CHECK (TYPE IN (0, 1, 2))
;

CREATE PROCEDURE NEW_PERSON(OUT newid INT, IN date_of_birth DATE, IN ssn VARCHAR(20), IN first_name VARCHAR(100), IN last_name VARCHAR(100), IN gender VARCHAR(1))
  MODIFIES SQL DATA
  BEGIN ATOMIC
    INSERT INTO PERSON VALUES (DEFAULT, first_name, last_name, date_of_birth, gender, ssn);
    SET newid = IDENTITY();
  END
    

CREATE PROCEDURE NEW_PERSON_RET_RS(IN date_of_birth DATE, IN ssn VARCHAR(20), IN first_name VARCHAR(100), IN last_name VARCHAR(100), IN gender VARCHAR(1))
  MODIFIES SQL DATA DYNAMIC RESULT SETS 1
  BEGIN ATOMIC
    DECLARE result CURSOR FOR SELECT * FROM PERSON WHERE ID = IDENTITY();
    INSERT INTO PERSON VALUES (DEFAULT, first_name, last_name, date_of_birth, gender, ssn);
    OPEN result;
  END
    
CREATE FUNCTION AN_HOUR_BEFORE(t TIMESTAMP)
  RETURNS TIMESTAMP
  RETURN t - 1 HOUR
