INSERT INTO USERS(LOGIN, NAME, EMAIL, BIRTHDAY)
VALUES('user1', 'user1', '@email1.ru', '2020-10-10'),
      ('user2', 'user2', '@email2.ru', '2020-10-11'),
      ('user3', 'user3', '@email3.ru', '2020-10-12'),
      ('user4', 'user4', '@email4.ru', '2020-10-13');
INSERT into RATINGS(rating_id, name) values ( 1, 'G' );
INSERT into RATINGS(rating_id, name) values ( 2, 'PG' );
INSERT into RATINGS(rating_id, name) values ( 3, 'PG-13' );
INSERT into RATINGS(rating_id, name) values ( 4, 'R' );
INSERT into RATINGS(rating_id, name) values ( 5, 'NC-17' );
INSERT into GENRES(genre_id, name) values ( 1, 'Комедия' );
INSERT into GENRES(genre_id, name) values ( 2, 'Драма' );
INSERT into GENRES(genre_id, name) values ( 3, 'Мультфильм' );
INSERT into GENRES(genre_id, name) values ( 4, 'Триллер' );
INSERT into GENRES(genre_id, name) values ( 5, 'Документальный' );
INSERT into GENRES(genre_id, name) values ( 6, 'Боевик' );
INSERT INTO FILMS(NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID)
VALUES ('film1', 'description', '2010-10-10', '100', 1);
INSERT INTO FILMS(NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID)
VALUES ('film2', 'description', '2010-10-10', '100', 1);
INSERT INTO FILMS(NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID)
VALUES ('film3', 'description', '2010-10-10', '100', 2);
INSERT INTO FILMS(NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID)
VALUES ('film4', 'description', '2010-10-10', '100', 3);
INSERT INTO FILMS(NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID)
VALUES ('film5', 'description', '2010-10-10', '100', 4);
INSERT INTO FILMS(NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID)
VALUES ('film6', 'description', '2010-10-10', '100', 5);
INSERT INTO FILM_GENRES(FILM_ID, GENRE_ID) VALUES (1, 1), (2, 2), (2, 3), (4, 2), (3, 2);
INSERT INTO LIKES(FILM_ID, USER_ID) VALUES (3, 1), (3, 2), (4, 1);


