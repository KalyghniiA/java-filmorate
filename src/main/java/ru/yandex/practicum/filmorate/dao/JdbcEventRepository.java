package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.extractors.EventExtractor;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class JdbcEventRepository implements EventRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public void addEvent(Event event) {
        Map<String, Object> params = Map.of("userId", event.getUserId(),
                "eventType", event.getEventType(),
                "operation", event.getOperation(),
                "entityId", event.getEntityId(),
                "timestamp", event.getTimestamp());

        String sql = """
                INSERT INTO USERS_EVENTS (USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID, TIMESTAMP)
                VALUES (:userId, :eventType, :operation, :entityId, :timestamp)
                """;

        jdbc.update(sql, params);
    }

    @Override
    public List<Event> getUserEvents(Integer userId) {
        String sql = """
                SELECT USERS_EVENTS.EVENT_ID AS ID,
                       USERS_EVENTS.USER_ID,
                       USERS_EVENTS.EVENT_TYPE,
                       EVENT_TYPE.NAME AS EVENT_TYPE_NAME,
                       USERS_EVENTS.OPERATION,
                       OPERATION.NAME AS OPERATION_NAME,
                       ENTITY_ID,
                       TIMESTAMP
                FROM USERS_EVENTS
                JOIN EVENT_TYPE ON USERS_EVENTS.EVENT_TYPE = EVENT_TYPE.NAME
                JOIN OPERATION ON USERS_EVENTS.OPERATION = OPERATION.NAME
                WHERE USER_ID = :userId
                """;

        Map<String, Object> param = Map.of("userId", userId);

        return jdbc.query(sql, param, new EventExtractor());
    }
}
