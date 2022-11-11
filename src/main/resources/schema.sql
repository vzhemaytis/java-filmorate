create table if not exists USERS
(
    USER_ID   INTEGER auto_increment,
    EMAIL     CHARACTER VARYING(50) not null,
    LOGIN     CHARACTER VARYING(50) not null,
    USER_NAME CHARACTER VARYING(50) not null,
    BIRTHDAY  DATE                  not null,
    constraint "USERS_pk"
    primary key (USER_ID)
    );
create table if not exists FRIENDS
(
    USER_ID   INTEGER not null,
    FRIEND_ID INTEGER not null,
    STATUS    BOOLEAN not null,
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
    FILM_ID      INTEGER auto_increment,
    FILM_NAME    CHARACTER VARYING(50) not null,
    DESCRIPTION  CHARACTER VARYING(200),
    RELEASE_DATE DATE                  not null,
    DURATION     INTEGER,
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
    FILM_ID     INTEGER not null,
    GENRE_ID INTEGER not null,
    constraint "FILM_GENRES_pk"
        primary key (FILM_ID, GENRE_ID),
    constraint "FILM_CATEGORIES_1_fk"
        foreign key (FILM_ID) references FILMS,
    constraint "FILM_CATEGORIES_2_fk"
        foreign key (GENRE_ID) references GENRES
);
create table if not exists FILM_USERS
(
    FILM_ID INTEGER not null,
    USER_ID INTEGER not null,
    constraint "FILM_USERS_pk"
        primary key (FILM_ID, USER_ID),
    constraint "FILM_USERS_1_fk"
        foreign key (FILM_ID) references FILMS,
    constraint "FILM_USERS_2_fk"
        foreign key (USER_ID) references USERS
);