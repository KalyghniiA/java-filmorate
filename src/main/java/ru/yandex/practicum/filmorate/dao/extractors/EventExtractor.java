package ru.yandex.practicum.filmorate.dao.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class EventExtractor implements ResultSetExtractor<List<Event>> {

    @Override
    public List<Event> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<Event> events = new ArrayList<>();
        while (rs.next()) {
            Operation operation = new Operation(rs.getInt("OPERATION_ID"), rs.getString("OPERATION_NAME"));
            EventType eventType = new EventType(rs.getInt("EVENT_TYPE_ID"), rs.getString("EVENT_TYPE_NAME"));
            Event event = new Event(
                    rs.getInt("USER_ID"),
                    eventType,
                    operation,
                    rs.getInt("EVENT_TYPE"),
                    rs.getTimestamp("TIMESTAMP").toLocalDateTime()
            );
            event.setEventId(rs.getInt("EVENT_ID"));
            events.add(event);
        }
        return events;
    }
}
