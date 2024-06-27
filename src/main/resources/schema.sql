create table if not exists USERS
(
    USER_ID  INTEGER auto_increment
        primary key,
    LOGIN    CHARACTER VARYING(255) not null,
    NAME     CHARACTER VARYING(255),
    EMAIL    CHARACTER VARYING(255) not null,
    BIRTHDAY DATE                   not null,
    constraint "USERS_pk"
        unique (EMAIL)
);

create table if not exists RATINGS
(
    RATING_ID INTEGER               not null
        primary key,
    NAME      CHARACTER VARYING(10) not null
);

create table if not exists DIRECTORS
(
    DIRECTOR_ID INTEGER auto_increment not null primary key ,
    NAME        CHARACTER VARYING(255) not null
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
        foreign key (RATING) references RATINGS
);

create table if not exists FILM_DIRECTORS
(
    FILM_ID     INTEGER not null,
    DIRECTOR_ID INTEGER not null,
    constraint "FILM_DIRECTOR_pk"
        primary key (FILM_ID, DIRECTOR_ID)
);

create table if not exists GENRES
(
    GENRE_ID INTEGER                not null
        primary key,
    NAME     CHARACTER VARYING(255) not null
);

create table IF NOT EXISTS FRIENDS
(
    USER_ID   INTEGER not null,
    FRIEND_ID INTEGER not null,
    constraint FK_FRIENDS_USER_ID
        foreign key (USER_ID) references USERS ON DELETE CASCADE,
    constraint FK_FRIENDS_FRIEND_ID
        foreign key (FRIEND_ID) references USERS ON DELETE CASCADE
);

create table if not exists FILM_GENRES
(
    FILM_ID INTEGER NOT NULL,
    GENRE_ID INTEGER NOT NULL,
    constraint FK_FILMGENRES_FILM_ID
        foreign key (FILM_ID) references FILMS ON DELETE CASCADE,
    constraint FK_FILMGENRES_GENRE_ID
        foreign key (GENRE_ID) references GENRES ON DELETE CASCADE
);

create table IF NOT EXISTS LIKES
(
    FILM_ID INTEGER not null,
    USER_ID INTEGER not null,
    constraint FK_LIKES_FILM_ID
        foreign key (FILM_ID) references FILMS ON DELETE CASCADE,
    constraint FK_LIKES_USER_ID
        foreign key (USER_ID) references USERS ON DELETE CASCADE
);

create table if not exists REVIEWS
(
    REVIEW_ID   INTEGER auto_increment
        primary key,
    CONTENT     CHARACTER VARYING(1024) not null,
    IS_POSITIVE BOOLEAN                 not null,
    USER_ID     INTEGER                 not null,
    FILM_ID     INTEGER                 not null,
    constraint "REVIEWS_FILMS_FILM_ID_fk"
        foreign key (FILM_ID) references FILMS
            on update cascade on delete cascade,
    constraint "REVIEWS_USERS_USER_ID_fk"
        foreign key (USER_ID) references USERS
            on update cascade on delete cascade
);

CREATE TABLE IF NOT EXISTS REVIEWS_LIKES
(
    REVIEW_ID INTEGER NOT NULL,
    USER_ID INTEGER NOT NULL,
    IS_LIKE BOOLEAN NOT NULL,
    constraint "REVIEWS_LIKES_pk"
        primary key (REVIEW_ID, USER_ID, IS_LIKE)
);