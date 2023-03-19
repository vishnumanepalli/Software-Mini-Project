DROP SCHEMA public CASCADE;
CREATE SCHEMA public;

CREATE TABLE course (
  	code VARCHAR(5) PRIMARY KEY NOT NULL,
  	title VARCHAR(100) NOT NULL,
  	lec INTEGER NOT NULL ,
	tut INTEGER NOT NULL ,
  	per INTEGER NOT NULL ,
  	s_hrs INTEGER NOT NULL ,
  	credits INTEGER NOT NULL
);

CREATE TABLE department(
    dep VARCHAR(4) PRIMARY KEY,
    dep_name VARCHAR(100) NOT NULL
);
Insert into department VALUES('NNE','ectricalr Science'); 

CREATE TABLE programme (
    prog VARCHAR(100) PRIMARY KEY
);

INSERT INTO programme (prog) VALUES ('program_core');
INSERT INTO programme (prog) VALUES ('engineering_core');
INSERT INTO programme (prog) VALUES ('elective');
INSERT INTO programme (prog) VALUES ('btp');


CREATE TABLE st_creds (
    st_name VARCHAR(100) NOT NULL,
    st_entry_no VARCHAR(12) UNIQUE PRIMARY KEY,
    dept VARCHAR(5) NOT NULL,
    batch INTEGER NOT NULL,
    FOREIGN KEY (dept) REFERENCES department(dep)
);

CREATE TABLE pr_creds (
    pr_name VARCHAR(100) NOT NULL,
    pr_id VARCHAR(11) UNIQUE PRIMARY KEY,
    dept VARCHAR(4) NOT NULL,
    FOREIGN KEY (dept) REFERENCES department(dep)
);

create table pre_req(
    course_code VARCHAR(5) NOT NULL,
    pre_req_code VARCHAR(5) NOT NULL,
    FOREIGN KEY(course_code) REFERENCES course(code),
    FOREIGN KEY(pre_req_code) REFERENCES course(code)
);

create table course_off(
    off_id SERIAL PRIMARY KEY, 
    code VARCHAR(5),
    prof VARCHAR(11),
    year INTEGER NOT NULL,
    sem INTEGER NOT NULL,
    cg_req FLOAT NOT NULL,
    dep  VARCHAR(4) NOT NULL,
    type VARCHAR(20) NOT NULL,
    permit INTEGER DEFAULT 0,
    FOREIGN KEY (code) REFERENCES course(code),
    FOREIGN KEY (prof) REFERENCES pr_creds(pr_id),
    FOREIGN KEY (type) REFERENCES programme(prog),
    FOREIGN KEY (dep) REFERENCES department(dep)
);

CREATE TABLE student (
    user_id VARCHAR(12) PRIMARY KEY,
    password VARCHAR(50) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES st_creds(st_entry_no)
);

CREATE TABLE instructor (
    user_id VARCHAR(10) PRIMARY KEY,
    password VARCHAR(50) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES pr_creds(pr_id)
);

CREATE TABLE acad_office (
    user_id VARCHAR(10) PRIMARY KEY,
    password VARCHAR(50) NOT NULL
);
