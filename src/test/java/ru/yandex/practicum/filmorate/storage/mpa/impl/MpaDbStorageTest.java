package ru.yandex.practicum.filmorate.storage.mpa.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorageTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaDbStorageTest extends MpaStorageTest<MpaDbStorage> {

    private final MpaStorage mpaStorage;

    @Test
    @DisplayName("1) Получение списка категорий")
    void getMpasTest() {
        List<Mpa> mpas = mpaStorage.getMpas();
        assertAll(
                () -> assertEquals(5, mpas.size()),
                () -> assertEquals(1, mpas.get(0).getId()),
                () -> assertEquals("G", mpas.get(0).getName()),
                () -> assertEquals(2, mpas.get(1).getId()),
                () -> assertEquals("PG", mpas.get(1).getName()),
                () -> assertEquals(3, mpas.get(2).getId()),
                () -> assertEquals("PG-13", mpas.get(2).getName()),
                () -> assertEquals(4, mpas.get(3).getId()),
                () -> assertEquals("R", mpas.get(3).getName()),
                () -> assertEquals(5, mpas.get(4).getId()),
                () -> assertEquals("NC-17", mpas.get(4).getName())
        );
    }

    @Test
    @DisplayName("2) Получение категории по ID")
    void findMpaTest() {
        Mpa mpa = mpaStorage.findMpa(1);
        assertAll(
                () -> assertEquals(1, mpa.getId()),
                () -> assertEquals("G", mpa.getName())
        );
    }

    @Test
    @DisplayName("3) Получение ошибки при запросе несуществующей категории")
    void findMpaTest2() {
        assertThrows(
                EntityNotFoundException.class,
                () -> mpaStorage.findMpa(6)
        );
    }
}
