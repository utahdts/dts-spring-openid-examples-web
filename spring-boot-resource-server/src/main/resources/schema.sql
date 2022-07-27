create table member
(
    user_id numeric(20) identity not null constraint member_pkey primary key,
    email varchar(255),
    inactive boolean,
    first_name varchar(255),
    last_name varchar(255),
    utah_id varchar(255),
    role varchar(255)
)
;

create table role
(
    id numeric(20) identity not null constraint role_pkey primary key,
    name varchar(255)
);



