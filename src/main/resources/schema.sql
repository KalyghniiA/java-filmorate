create table if not exists FRIENDS
(
    USER_ID   INTEGER not null,
    FRIEND_ID INTEGER not null
);

create table if not exists GENRES_TITLE
(
    GENRE_ID INTEGER                not null
        primary key,
    NAME     CHARACTER VARYING(255) not null
);

create table if not exists GENRES
(
    FILM_ID INTEGER NOT NULL,
    GENRE_ID INTEGER NOT NULL
);

create table if not exists LIKES
(
    FILM_ID INTEGER not null,
    USER_ID INTEGER not null
);

create table if not exists RATING
(
    RATING_ID INTEGER               not null
        primary key,
    NAME      CHARACTER VARYING(10) not null
);

create table if not exists FILMS
(
    FILM_ID      INTEGER auto_increment
        primary key,
    NAME         CHARACTER VARYING(255)  not null,
    DESCRIPTION  CHARACTER VARYING(1024) not null,
    RELEASE_DATE DATE                    not null,
    DURATION     INTEGER                 not null,
    RATING       INTEGER,
    constraint "FILMS_RATING_RATING_ID_fk"
        foreign key (RATING) references RATING
);

create table if not exists USERS
(
    USER_ID  INTEGER auto_increment
        primary key,
    LOGIN    CHARACTER VARYING(255) not null,
    NAME     CHARACTER VARYING(255),
    EMAIL    CHARACTER VARYING(255) not null,
    BIRTHDAY DATE                   not null,
    constraint "USERS_pk"
        unique (LOGIN, EMAIL)
);