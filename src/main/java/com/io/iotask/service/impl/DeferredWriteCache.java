package com.io.iotask.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.io.iotask.repository.Record;
import com.io.iotask.repository.RecordRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.util.ConcurrentLruCache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DeferredWriteCache<K, V> {

    private final Cache<K, V> cache; // Кэш от Caffeine
    private final Set<K> dirtyKeys = ConcurrentHashMap.newKeySet(); // "Грязные" ключи
    private final ScheduledExecutorService scheduler;

    private final DatabaseSaver<K, V> databaseSaver; // Интерфейс для сохранения в базу

    public DeferredWriteCache(int maxSize, DatabaseSaver<K, V> databaseSaver) {
        this.cache = Caffeine.newBuilder()
                .maximumSize(maxSize) // Максимальное количество записей
                .build();
        this.databaseSaver = databaseSaver;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();

        // Запускаем периодическое сохранение
        scheduler.scheduleAtFixedRate(this::flushDirtyKeys, 5, 5, TimeUnit.SECONDS);
    }

    // Добавление или обновление элемента в кэше
    public void put(K key, V value) {
        cache.put(key, value);
        dirtyKeys.add(key); // Помечаем ключ как измененный
    }

    // Получение элемента из кэша
    public V get(K key) {
        return cache.getIfPresent(key);
    }

    // Принудительное сохранение "грязных" записей в базу
    public synchronized void flushDirtyKeys() {
        if (dirtyKeys.isEmpty()) {
            return;
        }

        // Копируем список "грязных" ключей и очищаем его
        Set<K> keysToSave = new HashSet<>(dirtyKeys);
        dirtyKeys.clear();

        // Сохраняем данные для всех ключей
        Map<K, V> entriesToSave = new HashMap<>();
        for (K key : keysToSave) {
            V value = cache.getIfPresent(key);
            if (value != null) {
                entriesToSave.put(key, value);
            }
        }

        // Вызываем метод сохранения в базу
        databaseSaver.save(entriesToSave);
    }

    // Завершение работы
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }

    // Интерфейс для реализации сохранения в базу
    public interface DatabaseSaver<K, V> {
        void save(Map<K, V> entries);
    }
}