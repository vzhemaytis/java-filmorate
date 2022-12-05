INSERT into FILMS (FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATE, MPA_ID)
VALUES ('FILM1', 'DESC1', '2022-10-11', 100, 4, 1),
       ('FILM2', 'DESC2', '2022-10-12', 101, 5, 2),
       ('FILM3', 'DESC3', '2022-10-13', 102, 3, 3),
       ('FILM4', 'DESC4', '2022-10-14', 100, 4, 4),
       ('FILM5', 'DESC5', '2022-10-15', 100, 4, 5),
       ('FILM6', 'DESC1', '2022-10-11', 100, 4, 1),
       ('FILM7', 'DESC2', '2022-10-12', 101, 5, 2),
       ('FILM8', 'DESC3', '2022-10-13', 102, 3, 3),
       ('FILM9', 'DESC4', '2022-10-14', 100, 4, 4),
       ('FILM10', 'DESC5', '2022-10-15', 100, 4, 5),
       ('FILM11', 'DESC1', '2022-10-11', 100, 4, 1),
       ('FILM12', 'DESC2', '2022-10-12', 101, 5, 2),
       ('FILM13', 'DESC3', '2022-10-13', 102, 3, 3),
       ('FILM14', 'DESC4', '2022-10-14', 100, 4, 4),
       ('FILM15', 'DESC5', '2022-10-15', 100, 4, 5);
INSERT INTO FILM_GENRES (FILM_ID, GENRE_ID)
VALUES (1, 1),
       (2, 2),
       (2, 3),
       (2, 4),
       (5, 5),
       (6, 6);
INSERT INTO USERS (EMAIL, LOGIN, USER_NAME, BIRTHDAY)
VALUES ('1@1.COM', 'LOGIN1', 'NAME1', '2000-10-10'),
       ('2@2.COM', 'LOGIN2', 'NAME2', '2000-10-10'),
       ('3@3.COM', 'LOGIN3', 'NAME3', '2000-10-10'),
       ('4@4.COM', 'LOGIN4', 'NAME4', '2000-10-10'),
       ('5@5.COM', 'LOGIN5', 'NAME5', '2000-10-10'),
       ('6@6.COM', 'LOGIN6', 'NAME6', '2000-10-10'),
       ('7@7.COM', 'LOGIN7', 'NAME7', '2000-10-10'),
       ('8@8.COM', 'LOGIN8', 'NAME8', '2000-10-10'),
       ('9@9.COM', 'LOGIN9', 'NAME9', '2000-10-10'),
       ('10@10.COM', 'LOGIN10', 'NAME10', '2000-10-10');
INSERT INTO FRIENDS (USER_ID, FRIEND_ID)
VALUES (1, 2),
       (2, 1),
       (3, 1),
       (5, 10),
       (4, 9);
INSERT INTO LIKES (FILM_ID, USER_ID)
VALUES (1, 1),
       (1, 2),
       (1, 3),
       (4, 1),
       (4, 2),
       (4, 3),
       (4, 4),
       (4, 5),
       (10, 10),
       (10, 9),
       (13, 9);

INSERT INTO REVIEWS (FILM_ID, USER_ID, CONTENT, IS_POSITIVE)
VALUES (1, 1, 'Ревью фильма 1 пользователем 1', TRUE),
       (2, 1, 'Ревью фильма 2 пользователем 1', FALSE),
       (3, 1, 'Ревью фильма 3 пользователем 1', TRUE),
       (1, 1, 'Ревью фильма 1 пользователем 2', TRUE),
       (2, 1, 'Ревью фильма 2 пользователем 2', FALSE),
       (3, 1, 'Ревью фильма 3 пользователем 2', TRUE),
       (1, 1, 'Ревью фильма 1 пользователем 3', TRUE),
       (2, 1, 'Ревью фильма 2 пользователем 3', FALSE),
       (3, 1, 'Ревью фильма 3 пользователем 3', TRUE),
       (3, 1, 'Ревью фильма 4 пользователем 1', TRUE),
       (3, 1, 'Ревью фильма 4 пользователем 2', TRUE),
       (3, 1, 'Ревью фильма 4 пользователем 2', FALSE),
       (3, 1, 'Ревью фильма 4 пользователем 4', FALSE);

INSERT INTO REVIEWS_REACTIONS (REVIEW_ID, USER_ID, IS_POSITIVE)
VALUES (1, 1, TRUE),
       (1, 2, TRUE),
       (1, 3, TRUE),
       (1, 4, TRUE),
       (2, 1, FALSE),
       (2, 2, FALSE),
       (2, 3, FALSE),
       (2, 4, FALSE);