DROP TABLE IF EXISTS films CASCADE;
DROP TABLE IF EXISTS mpa CASCADE;
DROP TABLE IF EXISTS genre CASCADE;
DROP TABLE IF EXISTS genres CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS friends CASCADE;
DROP TABLE IF EXISTS likes CASCADE;
DROP TABLE IF EXISTS FILM_GENRES CASCADE;
DROP TABLE IF EXISTS reviews_reactions CASCADE;
DROP TABLE IF EXISTS reviews CASCADE;


create table if not exists USERS
(
    USER_ID   LONG auto_increment,
    EMAIL     CHARACTER VARYING(50) not null,
    LOGIN     CHARACTER VARYING(50) not null,
    USER_NAME CHARACTER VARYING(50) not null,
    BIRTHDAY  DATE                  not null,
    constraint "USERS_pk"
    primary key (USER_ID)
    );
create table if not exists FRIENDS
(
    USER_ID   LONG not null,
    FRIEND_ID LONG not null,
    constraint "FRIENDS_pk"
        primary key (USER_ID, FRIEND_ID),
    constraint "FRIENDS_USERS_1_fk"
        foreign key (USER_ID) references USERS,
    constraint "FRIENDS_USERS_2_fk"
        foreign key (FRIEND_ID) references USERS
);
create table if not exists MPA_RATING
(
    MPA_ID INTEGER auto_increment,
    MPA_NAME CHARACTER VARYING(10) not null,
    constraint "MPA_RATING_pk"
    primary key (MPA_ID)
    );
create table if not exists FILMS
(
    FILM_ID      LONG auto_increment,
    FILM_NAME    CHARACTER VARYING(50) not null,
    DESCRIPTION  CHARACTER VARYING(200),
    RELEASE_DATE DATE                  not null,
    DURATION     INTEGER,
    RATE         INTEGER,
    MPA_ID       INTEGER,
    constraint "FILMS_pk"
    primary key (FILM_ID),
    constraint FILMS_MPA_RATING_MPA_ID_FK
    foreign key (MPA_ID) references MPA_RATING
    );
create table if not exists GENRES
(
    GENRE_ID   INTEGER auto_increment,
    GENRE_NAME CHARACTER VARYING(30) not null,
    constraint "GENRES_pk"
    primary key (GENRE_ID)
    );
create table if not exists FILM_GENRES
(
    FILM_ID  LONG not null,
    GENRE_ID INTEGER not null,
    constraint "FILM_GENRES_pk"
        primary key (FILM_ID, GENRE_ID),
    constraint "FILM_CATEGORIES_1_fk"
        foreign key (FILM_ID) references FILMS,
    constraint "FILM_CATEGORIES_2_fk"
        foreign key (GENRE_ID) references GENRES
);
create table if not exists LIKES
(
    FILM_ID LONG not null,
    USER_ID LONG not null,
    constraint "LIKES_pk"
        primary key (FILM_ID, USER_ID),
    constraint "LIKES_1_fk"
        foreign key (FILM_ID) references FILMS,
    constraint "LIKES_2_fk"
        foreign key (USER_ID) references USERS
);
CREATE TABLE IF NOT EXISTS reviews
(
    review_id   bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_id     bigint  NOT NULL REFERENCES films (film_id) ON DELETE CASCADE,
    user_id     bigint  NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
    content     text    NOT NULL,
    is_positive boolean NOT NULL
);
-- CREATE UNIQUE INDEX  IF NOT EXISTS REVIEWS_FILM_ID_USER_ID ON reviews (film_id, user_id);

CREATE TABLE IF NOT EXISTS reviews_reactions
(
    review_id  bigint  NOT NULL REFERENCES reviews (review_id) ON DELETE CASCADE,
    user_id    bigint  NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
    is_positive boolean NOT NULL,
    PRIMARY KEY (review_id, user_id)
);

-- CREATE UNIQUE INDEX  IF NOT EXISTS REVIEWS_REACTIONS_FILM_ID_USER_ID ON reviews (review_id, user_id);
